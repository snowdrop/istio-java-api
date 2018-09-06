/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.annotator;

import java.util.Iterator;
import java.util.Optional;

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
import me.snowdrop.istio.api.internal.IstioKind;
import me.snowdrop.istio.api.internal.IstioSpecRegistry;
import me.snowdrop.istio.api.model.IstioSpec;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioTypeAnnotator extends Jackson2Annotator {

    private static final String BUILDER_PACKAGE = "io.fabric8.kubernetes.api.builder";
    private static final String DONEABLE_CLASS_NAME = "io.fabric8.kubernetes.api.model.Doneable";
    private final JDefinedClass doneableClass;

    static {
        final String strict = System.getenv("ISTIO_STRICT");
        if ("true".equals(strict)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                final String unvisitedCRDs = IstioSpecRegistry.unvisitedCRDNames();
                if (!unvisitedCRDs.isEmpty()) {
                    throw new IllegalStateException("The following CRDs were not visited:\n" + unvisitedCRDs);
                }
            }));
        }
    }

    public IstioTypeAnnotator(GenerationConfig generationConfig) {
        super(generationConfig);
        try {
            doneableClass = new JCodeModel()._class(DONEABLE_CLASS_NAME);
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException("Couldn't load " + DONEABLE_CLASS_NAME);
        }
    }

    @Override
    public void propertyOrder(JDefinedClass clazz, JsonNode propertiesNode) {
        JAnnotationArrayMember annotationValue = clazz.annotate(JsonPropertyOrder.class).paramArray("value");
        annotationValue.param("apiVersion");
        annotationValue.param("kind");
        annotationValue.param("metadata");
        for (Iterator<String> properties = propertiesNode.fieldNames(); properties.hasNext(); ) {
            String next = properties.next();
            if (!"apiVersion".equals(next) && !"kind".equals(next) && !"metadata".equals(next)) {
                annotationValue.param(next);
            }
        }

        final Optional<String> kind = IstioSpecRegistry.getIstioKind(clazz.name());
        if (kind.isPresent()) {
            clazz._implements(IstioSpec.class);
            clazz.annotate(IstioKind.class).param("name", kind.get());
        }

        //We just want to make sure we avoid infinite loops
        clazz.annotate(JsonDeserialize.class)
                .param("using", JsonDeserializer.None.class);
        clazz.annotate(ToString.class);
        clazz.annotate(EqualsAndHashCode.class);
        clazz.annotate(Buildable.class)
                .param("editableEnabled", false)
                .param("validationEnabled", true)
                .param("generateBuilderPackage", true)
                .param("builderPackage", BUILDER_PACKAGE)
                .annotationParam("inline", Inline.class)
                .param("type", doneableClass)
                .param("prefix", "Doneable")
                .param("value", "done");
    }
}
