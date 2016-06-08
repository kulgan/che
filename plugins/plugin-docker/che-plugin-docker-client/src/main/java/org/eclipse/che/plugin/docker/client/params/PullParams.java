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
package org.eclipse.che.plugin.docker.client.params;

import org.eclipse.che.plugin.docker.client.ProgressMonitor;
import org.eclipse.che.plugin.docker.client.dto.AuthConfigs;

import javax.validation.constraints.NotNull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Arguments holder for {@link org.eclipse.che.plugin.docker.client.DockerConnector#pull(PullParams, ProgressMonitor)}.
 *
 * @author Mykola Morhun
 */
public class PullParams {

    private String      image;
    private String      tag;
    private String      registry;
    private String      namespace;
    private AuthConfigs authConfigs;

    /**
     * Creates arguments holder with required parameters.
     *
     * @param image
     *          name of the image to pull
     * @return arguments holder with required parameters
     * @throws NullPointerException
     *         if {@code image} is null
     */
    public static PullParams create(@NotNull String image) {
        return new PullParams().withImage(image);
    }

    private PullParams() {}

    /**
     * Adds image to this parameters.
     *
     * @param image
     *         name of the image to pull
     * @return this params instance
     * @throws NullPointerException
     *         if {@code image} is null
     */
    public PullParams withImage(@NotNull String image) {
        requireNonNull(image);
        this.image = image;
        return this;
    }

    /**
     * Adds tag to this parameters.
     *
     * @param tag
     *         tag of the image
     * @return this params instance
     */
    public PullParams withTag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Adds registry to this parameters.
     *
     * @param registry
     *         host and port of registry, e.g. localhost:5000.
     *         If it is not set, default value "hub.docker.com" will be used
     * @return this params instance
     */
    public PullParams withRegistry(String registry) {
        this.registry = registry;
        return this;
    }

    /**
     * Adds namespace to this parameters.
     * In case of registry it is the same with username.
     *
     * @param namespace
     *         namespace to which images belongs to
     * @return this params instance
     */
    public PullParams withNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * Adds auth configuration to this parameters.
     *
     * @param authConfigs
     *         authentication configuration for registries
     * @return this params instance
     */
    public PullParams withAuthConfigs(AuthConfigs authConfigs) {
        this.authConfigs = authConfigs;
        return this;
    }

    public String getImage() {
        return image;
    }

    public String getTag() {
        return tag;
    }

    public String getRegistry() {
        return registry;
    }

    public String getNamespace() {
        return namespace;
    }

    public AuthConfigs getAuthConfigs() {
        return authConfigs;
    }

    /**
     * Returns FQN of image for pull.
     * It has following format: [registry/][namespace/]image
     */
    public String getImageFqn() {
        if (registry != null) {
            if (namespace != null) {
                return registry + '/' + namespace + '/' + image;
            } else {
                return registry + '/' + image; // for case if image contains namespace
            }
        } else {
            if (namespace != null) {
                return namespace + '/' + image;
            } else {
                return image;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PullParams that = (PullParams)o;
        return Objects.equals(image, that.image) &&
               Objects.equals(tag, that.tag) &&
               Objects.equals(registry, that.registry) &&
               Objects.equals(namespace, that.namespace) &&
               Objects.equals(authConfigs, that.authConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, tag, registry, namespace, authConfigs);
    }

}
