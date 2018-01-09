/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mesh;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public enum IngressControllerMode {
    /**
     * Disables Istio ingress controller.
     */
    OFF,
    /**
     * Istio ingress controller will act on ingress resources that do not
     * contain any annotation or whose annotations match the value
     * specified in the ingress_class parameter described earlier. Use this
     * mode if Istio ingress controller will be the default ingress
     * controller for the entire kubernetes cluster.
     */
    DEFAULT,
    /**
     * Istio ingress controller will only act on ingress resources whose
     * annotations match the value specified in the ingress_class parameter
     * described earlier. Use this mode if Istio ingress controller will be
     * a secondary ingress controller (e.g., in addition to a
     * cloud-provided ingress controller).
     */
    STRICT
}
