/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.template;

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
@Buildable(editableEnabled = false, validationEnabled = true, generateBuilderPackage = true, builderPackage = "me.snowdrop.istio.api.builder", inline = @Inline(type = Doneable.class, prefix = "Doneable", value = "done"))
public class Quota extends IstioBaseSpec {
    @JsonDeserialize(using = TypedValueMapDeserializer.class)
    private Map<String, TypedValue> dimensions;

    @Override
    public String getKind() {
        return "quota";
    }

    public Map<String, TypedValue> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, TypedValue> dimensions) {
        this.dimensions = dimensions;
    }
}
