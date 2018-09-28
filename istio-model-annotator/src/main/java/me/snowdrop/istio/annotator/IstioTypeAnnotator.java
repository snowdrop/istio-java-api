/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.annotator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.sun.codemodel.JAnnotationUse;
import io.sundr.builder.annotations.BuildableReference;
import io.sundr.transform.annotations.VelocityTransformations;
import io.sundr.transform.annotations.VelocityTransformation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.Inline;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.snowdrop.istio.api.IstioSpec;
import me.snowdrop.istio.api.internal.ClassWithInterfaceFieldsDeserializer;
import me.snowdrop.istio.api.internal.IstioApiVersion;
import me.snowdrop.istio.api.internal.IstioKind;
import me.snowdrop.istio.api.internal.IstioSpecRegistry;
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

        final Iterator<Map.Entry<String, JsonNode>> fields = propertiesNode.fields();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            if (!"apiVersion".equals(key) && !"kind".equals(key) && !"metadata".equals(key)) {
                annotationValue.param(key);
            }
        }

        final Optional<String> kind = IstioSpecRegistry.getIstioKind(clazz.name());
        if (kind.isPresent()) {
            clazz._implements(IstioSpec.class);
            clazz.annotate(IstioKind.class).param("name", kind.get());
        }
        final Optional<String> version = IstioSpecRegistry.getIstioApiVersion(clazz.name());
        if (version.isPresent()) {
            clazz.annotate(IstioApiVersion.class).param("value", version.get());
        }
        clazz.annotate(ToString.class);
        clazz.annotate(EqualsAndHashCode.class);
        try {

        JAnnotationUse buildable = clazz.annotate(Buildable.class)
                .param("editableEnabled", false)
                .param("validationEnabled", true)
                .param("generateBuilderPackage", true)
                .param("builderPackage", BUILDER_PACKAGE);

            buildable.paramArray("inline").annotate(Inline.class)
                    .param("type", new JCodeModel()._class("io.fabric8.kubernetes.api.model.Doneable"))
                    .param("prefix", "Doneable")
                    .param("value", "done");

           buildable.paramArray("refs").annotate(BuildableReference.class)
                   .param("value", new JCodeModel()._class("io.fabric8.kubernetes.api.model.ObjectMeta"));

        } catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
        }

        if (clazz.name().endsWith("Spec")) {
            JAnnotationArrayMember arrayMember= clazz.annotate(VelocityTransformations.class)
                    .paramArray("value");
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-resource.vm");
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-resource-list.vm");
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-manifest.vm").param("outputPath", "crd.properties").param("gather", true);
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-mappings-provider.vm").param("outputPath", "me/snowdrop/istio/api/model/IstioResourceMappingsProvider.java").param("gather", true);
        }
    }

    @Override
    public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        super.propertyField(field, clazz, propertyName, propertyNode);
        if (propertyNode.hasNonNull("isInterface")) {
            field.annotate(JsonUnwrapped.class);

            // todo: fix me, this won't work if a type has several fields using interfaces
            String interfaceFQN = propertyNode.get("javaType").asText();

            // create interface if we haven't done it yet
            try {
                final JDefinedClass fieldInterface = clazz._interface(interfaceFQN.substring(interfaceFQN.lastIndexOf('.') + 1));
                fieldInterface._extends(Serializable.class);
            } catch (JClassAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
            annotateIfNotDone(clazz, ClassWithInterfaceFieldsDeserializer.class);
        }
    }

    private Set<JDefinedClass> annotated = new HashSet<>();

    private void annotateIfNotDone(JDefinedClass clazz, Class<? extends JsonDeserializer> deserializerClass) {
        if (!annotated.contains(clazz)) {
            clazz.annotate(JsonDeserialize.class).param("using", deserializerClass);
            annotated.add(clazz);
        }
    }
}
