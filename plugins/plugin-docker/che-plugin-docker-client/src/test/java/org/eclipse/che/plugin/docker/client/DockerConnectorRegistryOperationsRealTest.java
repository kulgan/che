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
package org.eclipse.che.plugin.docker.client;

import org.eclipse.che.inject.ConfigurationProperties;
import org.eclipse.che.plugin.docker.client.connection.DockerConnectionFactory;
import org.eclipse.che.plugin.docker.client.helper.DefaultNetworkFinder;
import org.eclipse.che.plugin.docker.client.helper.NetworkFinder;
import org.eclipse.che.plugin.docker.client.params.BuildImageParams;
import org.eclipse.che.plugin.docker.client.params.PullParams;
import org.eclipse.che.plugin.docker.client.params.PushParams;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@Listeners(MockitoTestNGListener.class)
public class DockerConnectorRegistryOperationsRealTest {

    private static final String CONFIGURATION_PREFIX_PATTERN = "docker\\.registry\\.auth\\..+";

    private static final String DOCKER_REGISTRY_AUTH_URL_KEY      = "url";
    private static final String DOCKER_REGISTRY_AUTH_EMAIL_KEY    = "email";
    private static final String DOCKER_REGISTRY_AUTH_USERNAME_KEY = "username";
    private static final String DOCKER_REGISTRY_AUTH_PASSWORD_KEY = "password";

    private static final String DOCKER_REGISTRY_URL_VALUE           = "https://index.docker.io/v1/";
    private static final String DOCKER_REGISTRY_AUTH_URL_VALUE      = "index.docker.io";
    private static final String DOCKER_REGISTRY_AUTH_EMAIL_VALUE    = "mmorhun@codenvy.com";
    private static final String DOCKER_REGISTRY_AUTH_USERNAME_VALUE = "mm4eche";
    private static final String DOCKER_REGISTRY_AUTH_PASSWORD_VALUE = "4dtests";

    private static final String REPOSITORY_NAME = "testprivate";
    private static final String TAG_NAME = "mtag";
    private static final String ADDITIONAL_TAG_NAME = "newtest";

    @Mock
    private ConfigurationProperties configurationProperties;

    private Map<String, String>          configurationPropertiesMap;
    private DockerConnectorConfiguration dockerConnectorConfiguration;
    private InitialAuthConfig            initialAuthConfig;
    private NetworkFinder                networkFinder;
    private DockerConnectionFactory      dockerConnectionFactory;

    private DockerConnector dockerConnector;

    @BeforeMethod
    public void setup() {
        configurationPropertiesMap = new HashMap<>();
        configurationPropertiesMap.put(DOCKER_REGISTRY_AUTH_URL_KEY, DOCKER_REGISTRY_URL_VALUE);
        configurationPropertiesMap.put(DOCKER_REGISTRY_AUTH_EMAIL_KEY, DOCKER_REGISTRY_AUTH_EMAIL_VALUE);
        configurationPropertiesMap.put(DOCKER_REGISTRY_AUTH_USERNAME_KEY, DOCKER_REGISTRY_AUTH_USERNAME_VALUE);
        configurationPropertiesMap.put(DOCKER_REGISTRY_AUTH_PASSWORD_KEY, DOCKER_REGISTRY_AUTH_PASSWORD_VALUE);

        when(configurationProperties.getProperties(CONFIGURATION_PREFIX_PATTERN)).thenReturn(configurationPropertiesMap);
        initialAuthConfig = new InitialAuthConfig(configurationProperties);

        networkFinder = new DefaultNetworkFinder();
        dockerConnectorConfiguration = new DockerConnectorConfiguration(initialAuthConfig, networkFinder);
        dockerConnectionFactory = new DockerConnectionFactory(dockerConnectorConfiguration);

        dockerConnector = new DockerConnector(dockerConnectorConfiguration, dockerConnectionFactory);
    }

    @Test
    public void pullFromPrivateRegistry() throws IOException, InterruptedException {
        final ProgressLineFormatterImpl progressLineFormatter = new ProgressLineFormatterImpl();

        dockerConnector.pull(PullParams.create(DOCKER_REGISTRY_AUTH_USERNAME_VALUE + '/' + REPOSITORY_NAME)
                                       .withRegistry(DOCKER_REGISTRY_AUTH_URL_VALUE),
                             currentProgressStatus -> {
                                 System.out.println(progressLineFormatter.format(currentProgressStatus));
                             });
    }

    @Test
    public void pushToPrivateRegistry() throws IOException, InterruptedException {
        final ProgressLineFormatterImpl progressLineFormatter = new ProgressLineFormatterImpl();

        dockerConnector.push(PushParams.create(DOCKER_REGISTRY_AUTH_USERNAME_VALUE + '/' + REPOSITORY_NAME)
                                       .withRegistry(DOCKER_REGISTRY_AUTH_URL_VALUE)
                                       .withTag(ADDITIONAL_TAG_NAME),
                             currentProgressStatus -> {
                                 System.out.println(progressLineFormatter.format(currentProgressStatus));
                             });
    }

    @Test
    public void buildImageFromPrivateRepository() throws IOException, InterruptedException {
        final ProgressLineFormatterImpl progressLineFormatter = new ProgressLineFormatterImpl();
        final ClassLoader classLoader = getClass().getClassLoader();

        dockerConnector.buildImage(BuildImageParams.create(new File(classLoader.getResource("Dockerfile").getFile()))
                                                   .withRepository(DOCKER_REGISTRY_AUTH_USERNAME_VALUE + '/' + REPOSITORY_NAME)
                                                   .withTag(TAG_NAME),
                                   currentProgressStatus -> {
                                       System.out.println(progressLineFormatter.format(currentProgressStatus));
                                   });
    }

}
