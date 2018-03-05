/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.Doneable;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;
import io.sundr.builder.annotations.Inline;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.snowdrop.istio.api.internal.IstioDeserializer;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "apiVersion",
        "kind",
        "metadata",
        "spec"
})
@ToString
@EqualsAndHashCode
@Buildable(builderPackage = "io.fabric8.kubernetes.api.builder", generateBuilderPackage = true, editableEnabled = false, refs = {@BuildableReference(ObjectMeta.class)}, inline = @Inline(type = Doneable.class, prefix = "Doneable", value = "done"))
@JsonDeserialize(using = IstioDeserializer.class)
public class IstioResource implements HasMetadata, Serializable {
    private ObjectMeta metadata;
    private String kind;
    private String apiVersion = "config.istio.io/v1alpha2";
    private IstioSpec spec;

    public IstioResource() {
    }

    public IstioResource(String apiVersion, String kind, ObjectMeta metadata, IstioSpec spec) {
        this.metadata = metadata;
        this.kind = kind;
        this.apiVersion = apiVersion;
        this.spec = spec;
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
        if (kind == null && spec != null) {
            kind = spec.getKind();
        }
        
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

    public IstioSpec getSpec() {
        return spec;
    }

    public void setSpec(IstioSpec spec) {
        this.spec = spec;
    }
}
