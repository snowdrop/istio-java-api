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
import java.util.stream.Collectors;

import me.snowdrop.istio.api.model.IstioSpec;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioSpecRegistry {
    private static final String ISTIO_PACKAGE_PREFIX = "me.snowdrop.istio.";

    private static final String ISTIO_API_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + "api.";

    private static final String ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + "mixer.template.";

    private static final String ISTIO_ADAPTER_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + "adapter.";

    private static final Map<String, CRDInfo> crdInfos = new HashMap<>();

    static {
        Properties crdFile = new Properties();

        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("istio-crd.properties")) {
            crdFile.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load Istio CRD information from classpath", e);
        }

        crdInfos.putAll(crdFile.entrySet().stream().collect(
                Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> getCRDInfoFrom(e))
        ));
    }

    private static CRDInfo getCRDInfoFrom(Map.Entry<Object, Object> entry) {
        final String kind = String.valueOf(entry.getKey());

        // compute class name based on CRD kind
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

        // compute package name based on CRD FQN and labels
        final String[] crdDetail = String.valueOf(entry.getValue()).split("\\|");
        final String name = crdDetail[0].trim();
        final String istioLabel = crdDetail[1].trim().substring(crdDetail[1].lastIndexOf('='));

        String packageName;
        switch (istioLabel) {
            case "mixer-adapter":
                packageName = ISTIO_ADAPTER_PACKAGE_PREFIX + kind.toLowerCase() + ".";
                break;
            case "mixer-instance":
                packageName = ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + kind.toLowerCase() + ".";
                break;
            case "core":
                packageName = ISTIO_API_PACKAGE_PREFIX + "policy.";
                break;
            default:
                final String group = CRDInfo.getGroup(name);
                packageName = ISTIO_API_PACKAGE_PREFIX + group + ".";
                /*if(!istioLabel.isEmpty() && !group.equals(istioLabel)) {
                    System.out.println(kind + " => " + istioLabel + " / proposed pkg: " + packageName);
                }*/
        }


        return new CRDInfo(kind, name, packageName + className);
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

        String getGroup() {
            return getGroup(crdName);
        }

        static String getGroup(String crdName) {
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
