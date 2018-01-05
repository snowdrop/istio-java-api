/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@Buildable(builderPackage = "me.snowdrop.istio.api.builder", generateBuilderPackage = true, editableEnabled = false, refs = {@BuildableReference(ObjectMeta.class)})
public class IstioBaseResource implements IstioResource {
    private ObjectMeta metadata;
    private final String kind = getClass().getSimpleName();
    private String apiVersion = "config.istio.io/v1alpha2";

    public IstioBaseResource() {
    }

    @Override
    public ObjectMeta getMetadata() {
        return metadata;
    }

    @Override
    public void setMetadata(ObjectMeta metadata) {
        this.metadata = metadata;
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
