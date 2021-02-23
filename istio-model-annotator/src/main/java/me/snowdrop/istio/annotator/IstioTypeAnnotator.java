/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.annotator;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Version;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;
import io.sundr.transform.annotations.VelocityTransformation;
import io.sundr.transform.annotations.VelocityTransformations;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.snowdrop.istio.api.IstioSpec;
import me.snowdrop.istio.api.internal.ClassWithInterfaceFieldsDeserializer;
import me.snowdrop.istio.api.internal.IstioApiVersion;
import me.snowdrop.istio.api.internal.IstioKind;
import me.snowdrop.istio.api.internal.IstioSpecRegistry;
import me.snowdrop.istio.api.internal.MixerAdapter;
import me.snowdrop.istio.api.internal.MixerResourceDeserializer;
import me.snowdrop.istio.api.internal.MixerTemplate;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioTypeAnnotator extends Jackson2Annotator {

    private static final String BUILDER_PACKAGE = "io.fabric8.kubernetes.api.builder";

    private static final String OBJECT_META_CLASS_NAME = "io.fabric8.kubernetes.api.model.ObjectMeta";

    private static final String IS_INTERFACE_FIELD = "isInterface";

    private static final String EXISTING_JAVA_TYPE_FIELD = "existingJavaType";
    public static final String INSTANCE_PARAMS_FQN = "me.snowdrop.istio.api.policy.v1beta1.InstanceParams";
    public static final String HANDLER_PARAMS_FQN = "me.snowdrop.istio.api.policy.v1beta1.HandlerParams";
    public static final String INSTANCE_SPEC_DESERIALIZER_FQN = "me.snowdrop.istio.api.policy.v1beta1.InstanceSpecDeserializer";
    public static final String HANDLER_SPEC_DESERIALIZER_FQN = "me.snowdrop.istio.api.policy.v1beta1.HandlerSpecDeserializer";
    public static final String SUPPORTED_TEMPLATES_FQN = "me.snowdrop.istio.api.policy.v1beta1.SupportedTemplates";
    public static final String SUPPORTED_ADAPTERS_FQN = "me.snowdrop.istio.api.policy.v1beta1.SupportedAdapters";

    private final JDefinedClass objectMetaClass;

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
            objectMetaClass = new JCodeModel()._class(OBJECT_META_CLASS_NAME);
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException("Couldn't load " + OBJECT_META_CLASS_NAME);
        }
    }

    @Override
    public void typeInfo(JDefinedClass clazz, JsonNode node) {
        super.typeInfo(clazz, node);
        final JsonNode template = node.get("template");
        if (template != null) {
            clazz.annotate(MixerTemplate.class).param(MixerResourceDeserializer.INSTANCE_TYPE_FIELD, template.textValue());
        }
        final JsonNode adapter = node.get("adapter");
        if (adapter != null) {
            clazz.annotate(MixerAdapter.class).param(MixerResourceDeserializer.HANDLER_TYPE_FIELD, adapter.textValue());
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
            switch (key) {
                case "kind":
                case "metadata":
                case "apiVersion":
                    break;
                case "deprecatedAllowOrigin":
                    key = "allowOrigin";
                default:
                    annotationValue.param(key);
            }
        }

        final JPackage pkg = clazz.getPackage();
        final String pkgName = pkg.name();
        final int i = pkgName.lastIndexOf('.');
        final String version = pkgName.substring(i + 1);
        if (version.startsWith("v")) {
            final Optional<IstioSpecRegistry.CRDInfo> kind = IstioSpecRegistry.getCRDInfo(clazz.name(), version);
            kind.ifPresent(k -> {
                clazz._implements(IstioSpec.class);
                final String plural = k.getPlural();
                clazz.annotate(IstioKind.class).param("name", k.getKind()).param("plural",
                    plural);
                final String apiVersion = k.getAPIVersion();
                clazz.annotate(IstioApiVersion.class).param("value", apiVersion);
                clazz.annotate(Version.class).param("value", k.getVersion());
                clazz.annotate(Group.class)
                    .param("value", apiVersion.substring(0, apiVersion.indexOf('/')));
                clazz.annotate(Plural.class).param("value", plural);
            });
        }

        clazz.annotate(ToString.class);
        clazz.annotate(EqualsAndHashCode.class);
        JAnnotationUse buildable = clazz.annotate(Buildable.class)
                .param("editableEnabled", false)
                .param("generateBuilderPackage", true)
                .param("builderPackage", BUILDER_PACKAGE);

        buildable.paramArray("refs").annotate(BuildableReference.class)
                .param("value", objectMetaClass);

        final JClass instanceParamsClass = clazz.owner().directClass(INSTANCE_PARAMS_FQN);
        final JClass instanceParamsDeserializer = clazz.owner().directClass(INSTANCE_SPEC_DESERIALIZER_FQN);
        handleMixerResourceSpec(clazz, instanceParamsClass, instanceParamsDeserializer, "InstanceSpec", SUPPORTED_TEMPLATES_FQN, MixerResourceDeserializer.INSTANCE_TYPE_FIELD);

        final JClass handlerParamsClass = clazz.owner().directClass(HANDLER_PARAMS_FQN);
        final JClass handlerParamsDeserializer = clazz.owner().directClass(HANDLER_SPEC_DESERIALIZER_FQN);
        handleMixerResourceSpec(clazz, handlerParamsClass, handlerParamsDeserializer, "HandlerSpec", SUPPORTED_ADAPTERS_FQN,
                MixerResourceDeserializer.HANDLER_TYPE_FIELD);

        final boolean isAdapter = isMixerRelated(clazz, pkgName, "mixer.adapter", "MixerAdapter");
        final boolean isTemplate = isMixerRelated(clazz, pkgName, "mixer.template", "MixerTemplate");
        if (isAdapter || isTemplate) {
            if (isTemplate) {
                // if we're dealing with a template class, we need it to implement the 'InstanceParams' interface
                clazz._implements(instanceParamsClass);
            }
            if (isAdapter) {
                clazz._implements(handlerParamsClass);
            }

        } else if (clazz.name().endsWith("Spec")) {
            JAnnotationArrayMember arrayMember = clazz.annotate(VelocityTransformations.class).paramArray("value");
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-resource.vm");
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-resource-list.vm");
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-manifest.vm")
                    .param("outputPath", "crd.properties").param("gather", true);
            arrayMember.annotate(VelocityTransformation.class).param("value", "/istio-mappings-provider.vm")
                    .param("outputPath", Paths.get("me", "snowdrop", "istio", "api", "model",
                            "IstioResourceMappingsProvider.java").toString())
                    .param("gather", true);
        }
    }

    public void handleMixerResourceSpec(JDefinedClass clazz, JClass paramsClass, JClass specDeserializerClass, String specClassName, String supportedResourcesEnumClass, String polymorphicTypeField) {
        if (clazz.name().contains(specClassName)) {
            // change the 'params' field to be of type 'InstanceParams' instead of Struct to be able to have
            // polymorphic params
            changeFieldType(clazz, MixerResourceDeserializer.POLYMORPHIC_PARAMS_FIELD, paramsClass);

            // annotate class with custom deserializer
            clazz.annotate(JsonDeserialize.class).param("using", specDeserializerClass);

            // use enum for compiledTemplate field
            final JClass supported = clazz.owner().directClass(supportedResourcesEnumClass);
            changeFieldType(clazz, polymorphicTypeField, supported);
        }
    }

    private void changeFieldType(JDefinedClass clazz, String field, JClass newType) {
        // change params to be of type InstanceParams for polymorphic support
        final JFieldVar params = clazz.fields().get(field);
        clazz.removeField(params);
        final JFieldVar newField = clazz.field(params.mods().getValue(), newType, params.name());

        // also change accessors
        final String capitalizedField = field.substring(0, 1).toUpperCase() + field.substring(1);
        final String getter = "get" + capitalizedField;
        final String setter = "set" + capitalizedField;
        clazz.methods().removeIf(m -> m.name().equals(getter) || m.name().equals(setter));
        clazz.method(JMod.PUBLIC, newType, getter).body()._return(JExpr.refthis(field));
        final JMethod setParams = clazz.method(JMod.PUBLIC, clazz.owner().VOID, setter);
        setParams.param(newType, field);
        setParams.body().assign(JExpr.refthis(field), JExpr.direct(field));

    }

    private boolean isMixerRelated(JDefinedClass clazz, String pkgName, String relevantPkgSubPath, String markerAnnotationName) {
        return pkgName.contains(relevantPkgSubPath) && clazz.annotations().stream().anyMatch(a -> a.getAnnotationClass().name().contains(markerAnnotationName));
    }

    @Override
    public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        propertyName = propertyName.equals("deprecatedAllowOrigin") ? "allowOrigin" : propertyName;
        super.propertyField(field, clazz, propertyName, propertyNode);
        if (propertyNode.hasNonNull(IS_INTERFACE_FIELD)) {
            field.annotate(JsonUnwrapped.class);

            // todo: fix me, this won't work if a type has several fields using interfaces
            String interfaceFQN = propertyNode.get(EXISTING_JAVA_TYPE_FIELD).asText();

            // create interface if we haven't done it yet
            try {
                final JDefinedClass fieldInterface = clazz._interface(interfaceFQN.substring(interfaceFQN.lastIndexOf('.') + 1));
                fieldInterface._extends(Serializable.class);
            } catch (JClassAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
            annotateIfNotDone(clazz);
        }
    }

    public void propertyGetter(JMethod getter, JDefinedClass clazz, String propertyName) {
        // overridden to avoid annotating the getter
    }

    public void propertySetter(JMethod setter, JDefinedClass clazz, String propertyName) {
        // overridden to avoid annotating the setter
    }

    private final Set<JDefinedClass> annotated = new HashSet<>();

    private void annotateIfNotDone(JDefinedClass clazz) {
        if (!annotated.contains(clazz)) {
            clazz.annotate(JsonDeserialize.class).param("using", ClassWithInterfaceFieldsDeserializer.class);
            annotated.add(clazz);
        }
    }
}
