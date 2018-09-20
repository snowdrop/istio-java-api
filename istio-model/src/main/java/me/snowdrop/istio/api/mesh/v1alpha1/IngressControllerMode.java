/*
 * *
 *  * Copyright (C) 2018 Red Hat, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */
package me.snowdrop.istio.api.mesh.v1alpha1;

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
