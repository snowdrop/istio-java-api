/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.template;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class LogEntry extends IstioBaseSpec {
    /*
    apiVersion: "config.istio.io/v1alpha2"
kind: logentry
metadata:
  name: accesslog
  namespace: istio-config-default
spec:
  severity: '"Default"'
  timestamp: request.time
  variables:
    sourceIp: source.ip | ip("0.0.0.0")
    destinationIp: destination.ip | ip("0.0.0.0")
    sourceUser: source.user | ""
    method: request.method | ""
    url: request.path | ""
    protocol: request.scheme | "http"
    responseCode: response.code | 0
    responseSize: response.size | 0
    requestSize: request.size | 0
    latency: response.duration | "0ms"
  monitoredResourceType: '"UNSPECIFIED"'
     */

    /**
     * Variables that are delivered for each log entry.
     */
    @JsonDeserialize(using = TypedValueMapDeserializer.class)
    private Map<String, TypedValue> variables;
    /**
     * Timestamp is the time value for the log entry.
     */
    private Date timestamp;
    /**
     * Severity indicates the importance of the log entry.
     */
    private String severity;
    /**
     * Optional. An expression to compute the type of the monitored resource this log entry is being recorded on. If the logging backend supports monitored resources, these fields are used to populate that resource. Otherwise these fields will be ignored by the adapter.
     */
    private String monitoredResourceType;
    /**
     * Optional. A set of expressions that will form the dimensions of the monitored resource this log entry is being recorded on. If the logging backend supports monitored resources, these fields are used to populate that resource. Otherwise these fields will be ignored by the adapter.
     */
    @JsonDeserialize(using = TypedValueMapDeserializer.class)
    private Map<String, TypedValue> monitoredResourceDimensions;


    @Override
    public String getKind() {
        return "logentry";
    }

    public Map<String, TypedValue> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, TypedValue> variables) {
        this.variables = variables;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
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
