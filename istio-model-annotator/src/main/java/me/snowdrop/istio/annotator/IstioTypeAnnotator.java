/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.annotator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.Inline;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioTypeAnnotator extends Jackson2Annotator {

    private static final String BUILDER_PACKAGE = "me.snowdrop.istio.api.builder";
    private static final Set<String> CLASSES_WITHOUT_METADATA = new HashSet<>(
            Arrays.asList("MeshConfig", "ProxyConfig")
    );

    public IstioTypeAnnotator(GenerationConfig generationConfig) {
        super(generationConfig);
    }

    @Override
    public void propertyOrder(JDefinedClass clazz, JsonNode propertiesNode) {
        JAnnotationArrayMember annotationValue = clazz.annotate(JsonPropertyOrder.class).paramArray("value");
        annotationValue.param("apiVersion");
        annotationValue.param("kind");
        annotationValue.param("metadata");
        for (Iterator<String> properties = propertiesNode.fieldNames(); properties.hasNext(); ) {
            String next = properties.next();
            if (!next.equals("apiVersion") && !next.equals("kind") && !next.equals("metadata")) {
                annotationValue.param(next);
            }
        }

        try {
            if (!CLASSES_WITHOUT_METADATA.contains(clazz.name())) {
                clazz._extends(new JCodeModel()._class("me.snowdrop.istio.api.model.IstioBaseResource"));
            }
        } catch (JClassAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        //We just want to make sure we avoid infinite loops
        clazz.annotate(JsonDeserialize.class)
                .param("using", JsonDeserializer.None.class);
        clazz.annotate(ToString.class);
        clazz.annotate(EqualsAndHashCode.class);
        try {
            clazz.annotate(Buildable.class)
                    .param("editableEnabled", false)
                    .param("validationEnabled", true)
                    .param("generateBuilderPackage", true)
                    .param("builderPackage", BUILDER_PACKAGE)
                    .annotationParam("inline", Inline.class)
                    .param("type", new JCodeModel()._class("io.fabric8.kubernetes.api.model.Doneable"))
                    .param("prefix", "Doneable")
                    .param("value", "done");
        } catch (JClassAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }
}
