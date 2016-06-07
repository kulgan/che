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
package org.eclipse.che.ide.extension.machine.client.perspective;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.PartStack;
import org.eclipse.che.ide.extension.machine.client.machine.console.MachineConsolePresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.MachineAppliancePresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.panel.MachinePanelPresenter;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.recipe.RecipePartPresenter;
import org.eclipse.che.ide.workspace.PartStackPresenterFactory;
import org.eclipse.che.ide.workspace.PartStackViewFactory;
import org.eclipse.che.ide.workspace.WorkBenchControllerFactory;
import org.eclipse.che.ide.workspace.perspectives.general.AbstractPerspective;
import org.eclipse.che.ide.workspace.perspectives.general.PerspectiveViewImpl;

import javax.validation.constraints.NotNull;

import static org.eclipse.che.ide.api.constraints.Constraints.FIRST;
import static org.eclipse.che.ide.api.parts.PartStackType.EDITING;
import static org.eclipse.che.ide.api.parts.PartStackType.INFORMATION;
import static org.eclipse.che.ide.api.parts.PartStackType.NAVIGATION;

/**
 * Special view perspective which defines how must main window be displayed when we choose machine perspective.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
public class OperationsPerspective extends AbstractPerspective {

    public final static String OPERATIONS_PERSPECTIVE_ID = "Operations Perspective";

    @Inject
    public OperationsPerspective(PerspectiveViewImpl view,
                                 PartStackViewFactory partViewFactory,
                                 WorkBenchControllerFactory controllerFactory,
                                 PartStackPresenterFactory stackPresenterFactory,
                                 MachineConsolePresenter console,
                                 MachinePanelPresenter machinePanel,
                                 RecipePartPresenter recipePanel,
                                 NotificationManager notificationManager,
                                 MachineAppliancePresenter infoContainer,
                                 EventBus eventBus) {
        super(OPERATIONS_PERSPECTIVE_ID, view, stackPresenterFactory, partViewFactory, controllerFactory, eventBus);

        notificationManager.addRule(OPERATIONS_PERSPECTIVE_ID);

        //central panel
        partStacks.put(EDITING, infoContainer);

        addPart(console, INFORMATION);
        addPart(notificationManager, INFORMATION, FIRST);
        addPart(machinePanel, NAVIGATION);
        addPart(recipePanel, NAVIGATION);

        setActivePart(machinePanel);
    }

    /** {@inheritDoc} */
    @Override
    public void go(@NotNull AcceptsOneWidget container) {
        PartStack information = getPartStack(INFORMATION);
        PartStack navigation = getPartStack(NAVIGATION);
        PartStack editing = getPartStack(EDITING);

        if (information == null || navigation == null || editing == null) {
            return;
        }

        information.updateStack();

        information.go(view.getInformationPanel());
        navigation.go(view.getNavigationPanel());
        editing.go(view.getEditorPanel());

        container.setWidget(view);

        openActivePart(INFORMATION);
        openActivePart(NAVIGATION);
    }
}
