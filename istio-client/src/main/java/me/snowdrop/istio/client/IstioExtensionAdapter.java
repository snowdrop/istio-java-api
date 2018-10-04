/*
 * Copyright (C) 2018 Red Hat inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.snowdrop.istio.client;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.fabric8.kubernetes.api.model.RootPaths;
import io.fabric8.kubernetes.client.Client;
import io.fabric8.kubernetes.client.ExtensionAdapter;
import okhttp3.OkHttpClient;

public class IstioExtensionAdapter implements ExtensionAdapter<IstioClient> {

    private static final ConcurrentMap<URL, Boolean> IS_ISTIO = new ConcurrentHashMap<>();

    private static final ConcurrentMap<URL, Boolean> USES_ISTIO_APIGROUPS = new ConcurrentHashMap<>();

    @Override
    public Class<IstioClient> getExtensionType() {
        return IstioClient.class;
    }

    @Override
    public Boolean isAdaptable(Client client) {
        return isIstioAvailable(client);
    }

    @Override
    public IstioClient adapt(Client client) {
        return new DefaultIstioClient(client.adapt(OkHttpClient.class), client.getConfiguration());
    }

    private boolean isIstioAvailable(Client client) {
        URL masterUrl = client.getMasterUrl();
        if (IS_ISTIO.containsKey(masterUrl)) {
            return IS_ISTIO.get(masterUrl);
        } else {
            RootPaths rootPaths = client.rootPaths();
            if (rootPaths != null) {
                List<String> paths = rootPaths.getPaths();
                if (paths != null) {
                    for (String path : paths) {
                        // lets detect the new API Groups APIs for Istio
                        if (path.endsWith("istio.io") || path.contains("istio.io/")) {
                            USES_ISTIO_APIGROUPS.putIfAbsent(masterUrl, true);
                            IS_ISTIO.putIfAbsent(masterUrl, true);
                            return true;
                        }
                    }
                }
            }
        }
        IS_ISTIO.putIfAbsent(masterUrl, false);
        return false;
    }
}
