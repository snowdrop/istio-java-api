/*
 * *
 *  * Copyright (C) 2018 Red Hat, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */
package me.snowdrop.istio.api;

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
