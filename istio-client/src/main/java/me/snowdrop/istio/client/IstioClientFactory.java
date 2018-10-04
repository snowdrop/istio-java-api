/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@Deprecated
public class IstioClientFactory {
    public static IstioClient defaultClient(Config config) {
        KubernetesClient client = new DefaultKubernetesClient(config);

        KubernetesAdapter adapter = new KubernetesAdapter(client);
        return new IstioClient(adapter);
    }
}
