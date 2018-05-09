/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.template;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.Doneable;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.Inline;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.snowdrop.istio.api.internal.TypedValueMapDeserializer;
import me.snowdrop.istio.api.model.IstioBaseSpec;
import me.snowdrop.istio.api.model.v1.cexl.TypedValue;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize
@ToString
@EqualsAndHashCode
@Buildable(editableEnabled = false, validationEnabled = true, generateBuilderPackage = true, builderPackage = "io.fabric8.kubernetes.api.builder", inline = @Inline(type = Doneable.class, prefix = "Doneable", value = "done"))
public class Metric extends IstioBaseSpec {
    /**
     * The value being reported.
     */
    private TypedValue value;
    /**
     * The unique identity of the particular metric to report.
     */
    @JsonDeserialize(using = TypedValueMapDeserializer.class)
    private Map<String, TypedValue> dimensions;

    /**
     * Optional. An expression to compute the type of the monitored resource this log entry is being recorded on. If the logging backend supports monitored resources, these fields are used to populate that resource. Otherwise these fields will be ignored by the adapter.
     */
    @JsonProperty(value = "monitored_resource_type")
    private String monitoredResourceType;
    /**
     * Optional. A set of expressions that will form the dimensions of the monitored resource this log entry is being recorded on. If the logging backend supports monitored resources, these fields are used to populate that resource. Otherwise these fields will be ignored by the adapter.
     */
    @JsonProperty(value = "monitored_resource_dimensions")
    @JsonDeserialize(using = TypedValueMapDeserializer.class)
    private Map<String, TypedValue> monitoredResourceDimensions;


    @Override
    public String getKind() {
        return "metric";
    }

    public TypedValue getValue() {
        return value;
    }

    public void setValue(TypedValue value) {
        this.value = value;
    }

    public Map<String, TypedValue> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, TypedValue> dimensions) {
        this.dimensions = dimensions;
    }

    public String getMonitoredResourceType() {
        return monitoredResourceType;
    }

    public void setMonitoredResourceType(String monitoredResourceType) {
        this.monitoredResourceType = monitoredResourceType;
    }

    public Map<String, TypedValue> getMonitoredResourceDimensions() {
        return monitoredResourceDimensions;
    }

    public void setMonitoredResourceDimensions(Map<String, TypedValue> monitoredResourceDimensions) {
        this.monitoredResourceDimensions = monitoredResourceDimensions;
    }
}
