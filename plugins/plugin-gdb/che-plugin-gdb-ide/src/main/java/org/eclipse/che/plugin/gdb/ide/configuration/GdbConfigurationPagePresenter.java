/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.gdb.ide.configuration;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.recipe.RecipeDescriptor;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationPage;
import org.eclipse.che.ide.api.machine.MachineServiceClient;
import org.eclipse.che.ide.api.machine.RecipeServiceClient;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.command.valueproviders.CurrentProjectPathProvider;
import org.eclipse.che.ide.json.JsonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page allows to edit GDB debug configuration.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class GdbConfigurationPagePresenter implements GdbConfigurationPageView.ActionDelegate, DebugConfigurationPage<DebugConfiguration> {

    public static final String BIN_PATH_CONNECTION_PROPERTY   = "BINARY";
    public static final String DEFAULT_EXECUTABLE_TARGET_NAME = "a.out";

    private final GdbConfigurationPageView   view;
    private final MachineServiceClient       machineServiceClient;
    private final AppContext                 appContext;
    private final RecipeServiceClient        recipeServiceClient;
    private final DtoFactory                 dtoFactory;
    private final CurrentProjectPathProvider currentProjectPathProvider;

    private DebugConfiguration editedConfiguration;
    private String             originHost;
    private int                originPort;
    private String             originBinaryPath;
    private DirtyStateListener listener;

    @Inject
    public GdbConfigurationPagePresenter(GdbConfigurationPageView view,
                                         MachineServiceClient machineServiceClient,
                                         AppContext appContext,
                                         DtoFactory dtoFactory,
                                         RecipeServiceClient recipeServiceClient,
                                         CurrentProjectPathProvider currentProjectPathProvider) {
        this.view = view;
        this.machineServiceClient = machineServiceClient;
        this.appContext = appContext;
        this.recipeServiceClient = recipeServiceClient;
        this.dtoFactory = dtoFactory;
        this.currentProjectPathProvider = currentProjectPathProvider;

        view.setDelegate(this);
    }

    private String getBinaryPath(DebugConfiguration debugConfiguration) {
        if (debugConfiguration == null) {
            return getDefaultBinaryPath();
        }

        Map<String, String> connectionProperties = debugConfiguration.getConnectionProperties();
        String binaryPath = connectionProperties.get(BIN_PATH_CONNECTION_PROPERTY);
        return binaryPath == null ? getDefaultBinaryPath() : binaryPath;
    }

    private String getDefaultBinaryPath() {
        return currentProjectPathProvider.getKey() + "/" + DEFAULT_EXECUTABLE_TARGET_NAME;
    }

    @Override
    public void resetFrom(DebugConfiguration configuration) {
        editedConfiguration = configuration;

        originHost = configuration.getHost();
        originPort = configuration.getPort();
        originBinaryPath = getBinaryPath(configuration);
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        view.setHost(editedConfiguration.getHost());
        view.setPort(editedConfiguration.getPort());
        view.setBinaryPath(getBinaryPath(editedConfiguration));

        setHostsList();
    }

    private void setHostsList() {
        machineServiceClient.getMachines(appContext.getWorkspaceId()).then(new Operation<List<MachineDto>>() {
            @Override
            public void apply(List<MachineDto> machines) throws OperationException {
                @SuppressWarnings("unchecked")
                Promise<RecipeDescriptor>[] recipePromises = (Promise<RecipeDescriptor>[])new Promise[machines.size()];

                for (int i = 0; i < machines.size(); i++) {
                    String location = machines.get(i).getConfig().getSource().getLocation();
                    String recipeId = getRecipeId(location);
                    recipePromises[i] = recipeServiceClient.getRecipe(recipeId);
                }

                setHostsList(recipePromises, machines);
            }
        });
    }

    private void setHostsList(final Promise<RecipeDescriptor>[] recipePromises, final List<MachineDto> machines) {
        Promises.all(recipePromises).then(new Operation<JsArrayMixed>() {
            @Override
            public void apply(JsArrayMixed recipes) throws OperationException {
                Map<String, String> hosts = new HashMap<>();

                for (int i = 0; i < recipes.length(); i++) {
                    String recipeJson = recipes.getObject(i).toString();
                    RecipeDescriptor recipeDescriptor = dtoFactory.createDtoFromJson(recipeJson, RecipeDescriptor.class);

                    String script = recipeDescriptor.getScript();

                    String host;
                    try {
                        Map<String, String> m = JsonHelper.toMap(script);
                        host = m.containsKey("host") ? m.get("host") : "localhost";
                    } catch (Exception e) {
                        host = "localhost";
                    }
                    String description = host + " (" + machines.get(i).getConfig().getName() + ")";
                    hosts.put(host, description);
                }

                view.setHostsList(hosts);
            }
        });
    }

    @Override
    public boolean isDirty() {
        return !originHost.equals(editedConfiguration.getHost())
               || originPort != editedConfiguration.getPort()
               || !originBinaryPath.equals(getBinaryPath(editedConfiguration));
    }

    @Override
    public void setDirtyStateListener(DirtyStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onHostChanged() {
        editedConfiguration.setHost(view.getHost());
        listener.onDirtyStateChanged();
    }

    @Override
    public void onPortChanged() {
        editedConfiguration.setPort(view.getPort());
        listener.onDirtyStateChanged();
    }

    @Override
    public void onBinaryPathChanged() {
        final Map<String, String> connectionProperties = editedConfiguration.getConnectionProperties();
        connectionProperties.put(BIN_PATH_CONNECTION_PROPERTY, view.getBinaryPath());

        editedConfiguration.setConnectionProperties(connectionProperties);
        listener.onDirtyStateChanged();
    }

    private String getRecipeId(String location) {
        location = location.substring(0, location.lastIndexOf("/"));
        return location.substring(location.lastIndexOf("/") + 1);
    }

}
