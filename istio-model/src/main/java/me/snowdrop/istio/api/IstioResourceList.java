
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.Doneable;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.Inline;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.snowdrop.istio.api.internal.IstioListDeserializer;


/**
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "apiVersion",
        "kind",
        "metadata",
        "items"
})
@JsonDeserialize(using = IstioListDeserializer.class)
@ToString
@EqualsAndHashCode
@Buildable(editableEnabled = false, validationEnabled = true, generateBuilderPackage = true, builderPackage = "io.fabric8.kubernetes.api.builder", inline = @Inline(type = Doneable.class, prefix = "Doneable", value = "done"))
public class IstioResourceList implements KubernetesResource, KubernetesResourceList
{

    /**
     *
     * (Required)
     *
     */
    @NotNull
    @JsonProperty("apiVersion")
    private String apiVersion = "config.istio.io/v1alpha2";
    /**
     *
     *
     */
    @JsonProperty("items")
    @Valid
    private List<IstioResource> items = new ArrayList<IstioResource>();
    /**
     *
     * (Required)
     *
     */
    @NotNull
    @JsonProperty("kind")
    private String kind = "IstioResourceList";
    /**
     *
     *
     */
    @JsonProperty("metadata")
    @Valid
    private ListMeta metadata;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public IstioResourceList() {
    }

    /**
     *
     * @param metadata
     * @param apiVersion
     * @param kind
     * @param items
     */
    public IstioResourceList(String apiVersion, List<IstioResource> items, String kind, ListMeta metadata) {
        this.apiVersion = apiVersion;
        this.items = items;
        this.kind = kind;
        this.metadata = metadata;
    }

    /**
     *
     * (Required)
     *
     * @return
     *     The apiVersion
     */
    @JsonProperty("apiVersion")
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     *
     * (Required)
     *
     * @param apiVersion
     *     The apiVersion
     */
    @JsonProperty("apiVersion")
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     *
     *
     * @return
     *     The items
     */
    @JsonProperty("items")
    public List<IstioResource> getItems() {
        return items;
    }

    /**
     *
     *
     * @param items
     *     The items
     */
    @JsonProperty("items")
    public void setItems(List<IstioResource> items) {
        this.items = items;
    }

    /**
     *
     * (Required)
     *
     * @return
     *     The kind
     */
    @JsonProperty("kind")
    public String getKind() {
        return kind;
    }

    /**
     *
     * (Required)
     *
     * @param kind
     *     The kind
     */
    @JsonProperty("kind")
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     *
     *
     * @return
     *     The metadata
     */
    @JsonProperty("metadata")
    public ListMeta getMetadata() {
        return metadata;
    }

    /**
     *
     *
     * @param metadata
     *     The metadata
     */
    @JsonProperty("metadata")
    public void setMetadata(ListMeta metadata) {
        this.metadata = metadata;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
