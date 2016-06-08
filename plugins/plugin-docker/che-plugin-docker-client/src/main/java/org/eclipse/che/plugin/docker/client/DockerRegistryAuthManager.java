package org.eclipse.che.plugin.docker.client;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.commons.json.JsonHelper;
import org.eclipse.che.plugin.docker.client.dto.AuthConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing
 *
 * @author Mykola Morhun
 */
public class DockerRegistryAuthManager {

    private static final String DEFAULT_REGISTRY = "";

    private final InitialAuthConfig initialAuthConfig;

    public DockerRegistryAuthManager(InitialAuthConfig initialAuthConfig) {
        this.initialAuthConfig = initialAuthConfig;
    }

    /**
     * Looks for auth header for specified registry.
     * First searches in the params and then in the initial auth config.
     * If nothing found empty json will be returned.
     *
     * @param registry
     *         registry to which API call will be applied
     * @param paramsAuthConfig
     *         auth data for current API call
     * @return base64 encoded X-Registry-Auth header value
     */
    public String getXRegistryAuthHeaderValue(String registry, @Nullable Map<String,AuthConfig> paramsAuthConfig) {
        if (DEFAULT_REGISTRY.equals(registry)) {
            XRegistryAuthUnit auth = new XRegistryAuthUnit(initialAuthConfig.getDefaultUsername(), initialAuthConfig.getDefaultPassword());
            return Base64.encodeBase64String(JsonHelper.toJson(auth).getBytes());
        }

        AuthConfig authConfig = null;
        if (paramsAuthConfig != null) {
            for(Map.Entry<String, AuthConfig> entry : paramsAuthConfig.entrySet()) {
                AuthConfig value = entry.getValue();
                if (value.getServeraddress().contains(registry)) {
                    authConfig = value;
                    break;
                }
            }
        }

        if (authConfig == null) {
            for(Map.Entry<String, AuthConfig> entry : initialAuthConfig.getAuthConfigs().getConfigs().entrySet()) {
                AuthConfig value = entry.getValue();
                if (value.getServeraddress().contains(registry)) {
                    authConfig = value;
                    break;
                }
            }
        }

        if (authConfig != null) {
            XRegistryAuthUnit auth = new XRegistryAuthUnit(authConfig.getUsername(), authConfig.getPassword());
            return Base64.encodeBase64String(JsonHelper.toJson(auth).getBytes());
        }

        return Base64.encodeBase64String("{}".getBytes());
    }

    /**
     * Looks for auth header for specified registry.
     * First searches in the params and then in the initial auth config.
     * If nothing found empty json will be returned.
     *
     * @param serverAddress
     *         registry to which API call will be applied
     * @param paramsAuthConfig
     *         auth data for current API call
     * @return base64 encoded X-Registry-Auth header value
     */
    public String getXRegistryAuthHeaderValueByServerName(String serverAddress, @Nullable Map<String,AuthConfig> paramsAuthConfig) {
        if (DEFAULT_REGISTRY.equals(serverAddress)) {
            XRegistryAuthUnit auth = new XRegistryAuthUnit(initialAuthConfig.getDefaultUsername(), initialAuthConfig.getDefaultPassword());
            return Base64.encodeBase64String(JsonHelper.toJson(auth).getBytes());
        }

        AuthConfig authConfig = null;
        if (paramsAuthConfig != null) {
            authConfig = paramsAuthConfig.get(serverAddress);
        }
        if (authConfig == null) {
            authConfig = initialAuthConfig.getAuthConfigs().getConfigs().get(serverAddress);
        }

        if (authConfig != null) {
            XRegistryAuthUnit auth = new XRegistryAuthUnit(authConfig.getUsername(), authConfig.getPassword());
            return Base64.encodeBase64String(JsonHelper.toJson(auth).getBytes());
        }

        return Base64.encodeBase64String("{}".getBytes());
    }

    /**
     * Builds list of auth configs.
     * Adds auth configs from current API call and from initial auth config.
     *
     * @param paramsAuthConfig
     *         auth config for current API call
     * @return base64 encoded X-Registry-Config header value
     */
    public String getXRegistryConfigHeaderValue(@Nullable Map<String,AuthConfig> paramsAuthConfig) {
        Map<String, XRegistryAuthUnit> authConfigs = new HashMap<>();

        for(Map.Entry<String, AuthConfig> entry : initialAuthConfig.getAuthConfigs().getConfigs().entrySet()) {
            AuthConfig value = entry.getValue();
            authConfigs.put(value.getServeraddress(),
                            new XRegistryAuthUnit(value.getUsername(), value.getPassword()));
        }

        if (paramsAuthConfig != null) {
            for(Map.Entry<String, AuthConfig> entry : paramsAuthConfig.entrySet()) {
                AuthConfig value = entry.getValue();
                authConfigs.put(entry.getKey(),
                                new XRegistryAuthUnit(value.getUsername(), value.getPassword()));
            }
        }

        return Base64.encodeBase64String(JsonHelper.toJson(authConfigs).getBytes());
    }

    /** This class is used for generate X-Registry-Auth and X-Registry-Config */
    // protected is needed for JsonHelper
    protected static class XRegistryAuthUnit {
        private String username;
        private String password;

        public XRegistryAuthUnit(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

}
