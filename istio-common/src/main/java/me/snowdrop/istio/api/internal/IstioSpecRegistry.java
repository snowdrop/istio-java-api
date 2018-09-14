package me.snowdrop.istio.api.internal;

/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import me.snowdrop.istio.api.model.IstioSpec;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioSpecRegistry {
    private static final String ISTIO_PACKAGE_PREFIX = "me.snowdrop.istio.";
    private static final String ISTIO_API_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + "api.model.";
    private static final String ISTIO_VERSION = "v1.";
    private static final String ISTIO_MIXER_PACKAGE_PREFIX = ISTIO_API_PACKAGE_PREFIX + ISTIO_VERSION + "mixer.";
    private static final String ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX = ISTIO_MIXER_PACKAGE_PREFIX + "template.";
    private static final String ISTIO_ROUTING_PACKAGE_PREFIX = ISTIO_API_PACKAGE_PREFIX + ISTIO_VERSION + "routing.";
    private static final String ISTIO_POLICY_PACKAGE_PREFIX = ISTIO_API_PACKAGE_PREFIX + ISTIO_VERSION + "policy.";
    private static final String ISTIO_NETWORKING_PACKAGE_PREFIX = ISTIO_API_PACKAGE_PREFIX + ISTIO_VERSION + "networking.";
    private static final String ISTIO_ADAPTER_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + "adapter.";

    private static final Map<String, CRDInfo> crdInfos = new HashMap<>();

    static {
        loadCRDInfosFromProperties("adapter_crds.properties", key -> ISTIO_ADAPTER_PACKAGE_PREFIX + key.toLowerCase() + ".");
        loadCRDInfosFromProperties("template_crds.properties", key -> ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX);
        loadCRDInfosFromProperties("other_crds.properties", key -> ISTIO_ROUTING_PACKAGE_PREFIX);
        loadCRDInfosFromProperties("policy_crds.properties", key -> ISTIO_POLICY_PACKAGE_PREFIX);
        loadCRDInfosFromProperties("networking_crds.properties", key -> ISTIO_NETWORKING_PACKAGE_PREFIX);
        loadCRDInfosFromProperties("additional_resources.properties", key -> ISTIO_PACKAGE_PREFIX);
    }

    private static void loadCRDInfosFromProperties(String propertyFileName, Function<String, String> packageNameFromPropertyKey) {
        Properties crdFile = new Properties();

        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFileName)) {
            crdFile.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load '" + propertyFileName + "' from classpath", e);
        }

        crdInfos.putAll(crdFile.entrySet().stream().collect(
                Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> getCRDInfoFrom(e, packageNameFromPropertyKey.apply(e.getKey().toString())))
        ));
    }

    private static CRDInfo getCRDInfoFrom(Map.Entry<Object, Object> entry, String packageName) {
        final String kind = String.valueOf(entry.getKey());

        String className;
        if (kind.contains("entry")) {
            className = kind.replace("entry", "Entry");
        } else if (kind.contains("Msg")) {
            className = kind.replace("Msg", "");
        } else if (kind.contains("nothing")) {
            className = kind.replace("nothing", "Nothing");
        } else if (kind.contains("key")) {
            className = kind.replace("key", "Key");
        } else if (kind.contains("span")) {
            className = kind.replace("span", "Span");
        } else {
            className = kind;
        }
        final char c = className.charAt(0);
        className = className.replaceFirst("" + c, "" + Character.toTitleCase(c));

        return new CRDInfo(kind, String.valueOf(entry.getValue()), packageName + className);
    }

    static class CRDInfo {
        private final String kind;

        private final String crdName;

        private final String className;

        private Optional<Class<? extends IstioSpec>> clazz = Optional.empty();

        private boolean visited;

        public CRDInfo(String kind, String crdName, String className) {
            this.kind = kind;
            this.crdName = crdName;
            this.className = className;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CRDInfo crdInfo = (CRDInfo) o;

            return kind.equals(crdInfo.kind);
        }

        @Override
        public int hashCode() {
            return kind.hashCode();
        }

        @Override
        public String toString() {
            return kind + ":\t" + crdName + "\t=>\t" + className;
        }

        public String getGroup() {
            final int beginIndex = crdName.indexOf('.');
            return crdName.substring(beginIndex + 1, crdName.indexOf('.', beginIndex + 1));
        }

        boolean isUnvisited() {
            return !visited;
        }
    }

    public static String unvisitedCRDNames() {
        return crdInfos.values().stream().filter(CRDInfo::isUnvisited).map(CRDInfo::toString).collect(Collectors.joining("\n"));
    }

    public static Class<? extends IstioSpec> resolveIstioSpecForKind(String name) {
        final CRDInfo crdInfo = crdInfos.get(name);
        if (crdInfo != null) {
            crdInfo.visited = true;
            Optional<Class<? extends IstioSpec>> result = crdInfo.clazz;
            if (!result.isPresent()) {
                final Class<? extends IstioSpec> clazz = loadClassIfExists(crdInfo.className);
                crdInfo.clazz = Optional.of(clazz);
                return clazz;
            } else {
                return result.get();
            }
        } else {
            return null;
        }
    }

    public static String getKindFor(Class<? extends IstioSpec> spec) {
        try {
            return spec.newInstance().getKind();
        } catch (Exception e) {
            return null;
        }
    }

    public static Optional<String> getIstioKind(String simpleClassName) {
        CRDInfo crd = crdInfos.get(simpleClassName);
        if (crd != null) {
            crd.visited = true;
            return Optional.of(simpleClassName);
        } else {
            final String lowerSimpleClassName = simpleClassName.toLowerCase();
            crd = crdInfos.get(lowerSimpleClassName);
            if (crd != null) {
                crd.visited = true;
                return Optional.of(lowerSimpleClassName);
            } else {
                return Optional.empty();
            }
        }
    }

    public static Optional<String> getCRDNameFor(String kind) {
        final CRDInfo crdInfo = crdInfos.get(kind);
        return crdInfo != null ? Optional.of(crdInfo.crdName) : Optional.empty();
    }

    public static Set<String> getKnownKinds() {
        return crdInfos.keySet();
    }

    private static Class<? extends IstioSpec> loadClassIfExists(String className) {
        try {
            final Class<?> loaded = IstioSpecRegistry.class.getClassLoader().loadClass(className);
            if (IstioSpec.class.isAssignableFrom(loaded)) {
                return loaded.asSubclass(IstioSpec.class);
            } else {
                throw new IllegalArgumentException(String.format("%s is not an implementation of %s", className, IstioSpec.class.getSimpleName()));
            }
        } catch (Throwable t) {
            throw new IllegalArgumentException(String.format("Cannot load class: %s", className), t);
        }
    }

    public static void main(String[] args) {
        System.out.println(crdInfos.values().stream()
                .collect(Collectors.groupingBy(CRDInfo::getGroup))
                .entrySet().stream()
                .map(entry ->
                        entry.getKey() + ":\n\t"
                                + entry.getValue().stream()
                                .map(Object::toString)
                                .sorted(String::compareToIgnoreCase)
                                .collect(Collectors.joining("\n\t")))
                .collect(Collectors.joining("\n"))
        );
    }

}
