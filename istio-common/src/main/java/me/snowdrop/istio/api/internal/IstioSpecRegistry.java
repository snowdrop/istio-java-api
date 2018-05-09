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
    private static final String CHECK_NOTHING_CRD_NAME = "checknothings.config.istio.io";
    private static final String LIST_ENTRY_CRD_NAME = "listentries.config.istio.io";
    private static final String LOG_ENTRY_CRD_NAME = "logentries.config.istio.io";
    private static final String METRIC_CRD_NAME = "metrics.config.istio.io";
    private static final String QUOTA_CRD_NAME = "quotas.config.istio.io";
    private static final String REPORT_NOTHING_CRD_NAME = "reportnothings.config.istio.io";
    // todo: add support for all CRDs reported by oc get customresourcedefinitions | grep istio.io
    /*
    apikeys.config.istio.io                                   26d
attributemanifests.config.istio.io                        26d
authorizations.config.istio.io                            26d
checknothings.config.istio.io                             26d
circonuses.config.istio.io                                26d
deniers.config.istio.io                                   26d
destinationpolicies.config.istio.io                       26d
destinationrules.config.istio.io                          26d
destinationrules.networking.istio.io                      26d
egressrules.config.istio.io                               26d
enduserauthenticationpolicyspecbindings.config.istio.io   26d
enduserauthenticationpolicyspecs.config.istio.io          26d
externalservices.config.istio.io                          26d
externalservices.networking.istio.io                      26d
fluentds.config.istio.io                                  26d
gateways.networking.istio.io                              26d
httpapispecbindings.config.istio.io                       26d
httpapispecs.config.istio.io                              26d
kubernetesenvs.config.istio.io                            26d
kuberneteses.config.istio.io                              26d
listcheckers.config.istio.io                              26d
listentries.config.istio.io                               26d
logentries.config.istio.io                                26d
memquotas.config.istio.io                                 26d
metrics.config.istio.io                                   26d
noops.config.istio.io                                     26d
opas.config.istio.io                                      26d
policies.authentication.istio.io                          26d
prometheuses.config.istio.io                              26d
quotas.config.istio.io                                    26d
quotaspecbindings.config.istio.io                         26d
quotaspecs.config.istio.io                                26d
rbacs.config.istio.io                                     26d
reportnothings.config.istio.io                            26d
routerules.config.istio.io                                26d
rules.config.istio.io                                     26d
servicecontrolreports.config.istio.io                     26d
servicecontrols.config.istio.io                           26d
servicerolebindings.config.istio.io                       26d
serviceroles.config.istio.io                              26d
solarwindses.config.istio.io                              26d
stackdrivers.config.istio.io                              26d
statsds.config.istio.io                                   26d
stdios.config.istio.io                                    26d
tracespans.config.istio.io                                26d
v1alpha2routerules.config.istio.io                        26d
virtualservices.networking.istio.io                       26d
     */
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
        KIND_TO_CRD.put("checknothing", CHECK_NOTHING_CRD_NAME);
        KIND_TO_CRD.put("listentry", LIST_ENTRY_CRD_NAME);
        KIND_TO_CRD.put("logentry", LOG_ENTRY_CRD_NAME);
        KIND_TO_CRD.put("metric", METRIC_CRD_NAME);
        KIND_TO_CRD.put("quota", QUOTA_CRD_NAME);
        KIND_TO_CRD.put("reportnothing", REPORT_NOTHING_CRD_NAME);
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

    public static String getKindFor(Class<? extends IstioSpec> spec) {
        try {
            return spec.newInstance().getKind();
        } catch (Exception e) {
            return null;
        }
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
