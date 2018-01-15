package me.snowdrop.istio.api.internal;

/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.snowdrop.istio.api.model.IstioSpec;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class IstioSpecRegistry {
    private static final String ISTIO_PACKAGE_PREFIX = "me.snowdrop.istio.api.model.";
    private static final String ISTIO_VERSION = "v1.";
    private static final String ISTIO_MIXER_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + "mixer.";
    private static final String ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX = ISTIO_MIXER_PACKAGE_PREFIX + "template.";
    private static final String ISTIO_ROUTING_PACKAGE_PREFIX = ISTIO_PACKAGE_PREFIX + ISTIO_VERSION + "routing.";

    private static final Map<String, Class<? extends IstioSpec>> KIND_TO_TYPE = new HashMap<>();
    private static final Map<String, String> KIND_TO_CLASSNAME = new HashMap<>();

    private static final String DESTINATION_POLICY_CRD_NAME = "destinationpolicies.config.istio.io";
    private static final String EGRESS_RULE_CRD_NAME = "egressrules.config.istio.io";
    private static final String ROUTE_RULE_CRD_NAME = "routerules.config.istio.io";
    private static final Map<String, String> KIND_TO_CRD = new ConcurrentHashMap<>();
    
    static {
        KIND_TO_CLASSNAME.put("RouteRule", ISTIO_ROUTING_PACKAGE_PREFIX + "RouteRule");
        KIND_TO_CLASSNAME.put("DestinationPolicy", ISTIO_ROUTING_PACKAGE_PREFIX + "DestinationPolicy");
        KIND_TO_CLASSNAME.put("EgressRule", ISTIO_ROUTING_PACKAGE_PREFIX + "EgressRule");
        KIND_TO_CLASSNAME.put("checknothing", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "CheckNothing");
        KIND_TO_CLASSNAME.put("listentry", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "ListEntry");
        KIND_TO_CLASSNAME.put("logentry", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "LogEntry");
        KIND_TO_CLASSNAME.put("metric", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "Metric");
        KIND_TO_CLASSNAME.put("quota", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "Quota");
        KIND_TO_CLASSNAME.put("reportnothing", ISTIO_MIXER_TEMPLATE_PACKAGE_PREFIX + "ReportNothing");

        KIND_TO_CRD.put("DestinationPolicy", DESTINATION_POLICY_CRD_NAME);
        KIND_TO_CRD.put("EgressRule", EGRESS_RULE_CRD_NAME);
        KIND_TO_CRD.put("RouteRule", ROUTE_RULE_CRD_NAME);
    }

    public static Class<? extends IstioSpec> resolveIstioSpecForKind(String name) {
        Class<? extends IstioSpec> result = KIND_TO_TYPE.get(name);
        if (result == null) {
            final String className = KIND_TO_CLASSNAME.get(name);
            if (className != null) {
                result = loadClassIfExists(className);
                KIND_TO_TYPE.put(name, result);
            }
        }

        return result;
    }

    public static boolean isIstioSpec(String simpleClassName) {
        // this relies on the fact that currently generated IstioSpec classes use their simple names as kind… This assumption
        // might turn out wrong at some point in the future
        return KIND_TO_CLASSNAME.containsKey(simpleClassName);
    }

    public static String getCRDNameFor(String kind) {
        return KIND_TO_CRD.get(kind);
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

}
