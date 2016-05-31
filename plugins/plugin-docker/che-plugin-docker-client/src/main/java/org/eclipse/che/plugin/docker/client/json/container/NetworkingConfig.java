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
package org.eclipse.che.plugin.docker.client.json.container;

import org.eclipse.che.plugin.docker.client.json.network.EndpointConfig;

import java.util.Map;

/**
 * author Alexander Garagatyi
 */
public class NetworkingConfig {
    private Map<String, EndpointConfig> endpointsConfig;

    public Map<String, EndpointConfig> getEndpointsConfig() {
        return endpointsConfig;
    }

    public void setEndpointsConfig(Map<String, EndpointConfig> endpointsConfig) {
        this.endpointsConfig = endpointsConfig;
    }

    public NetworkingConfig withEndpointsConfig(Map<String, EndpointConfig> endpointsConfig) {
        this.endpointsConfig = endpointsConfig;
        return this;
    }

    @Override
    public String toString() {
        return "NetworkingConfig{" +
               "endpointsConfig=" + endpointsConfig +
               '}';
    }
}
