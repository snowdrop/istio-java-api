package me.snowdrop.istio.client;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.api.model.Doneable;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.HasMetadataOperation;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import io.fabric8.kubernetes.client.utils.Utils;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import okhttp3.OkHttpClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IstioCustomResourceOperationsImpl<T extends HasMetadata, L extends KubernetesResourceList, D extends Doneable<T>> extends CustomResourceOperationsImpl<T, L, D> {

    public IstioCustomResourceOperationsImpl(OkHttpClient httpClient, Config configuration, CustomResourceDefinition crd, Class<T> resourceType, Class<L> resourceListType, Class<D> doneType) {
        super(httpClient, configuration, apiGroup(crd), apiVersion(crd), resourceT(crd), (String)null, (String)null, false, (T)null, (String)null, false, resourceType, resourceListType, doneType);
    }

    public IstioCustomResourceOperationsImpl(OkHttpClient client, Config config, String apiGroup, String apiVersion, String resourceT, String namespace, String name, Boolean cascading, T item, String resourceVersion, Boolean reloadingFromServer, Class<T> type, Class<L> listType, Class<D> doneableType) {
        super(client, config, apiGroup, apiVersion, resourceT, namespace, name, cascading, item, resourceVersion, reloadingFromServer, type, listType, doneableType);

    }

    @Override
    public IstioCustomResourceOperationsImpl inNamespace(String namespace) {

        return new IstioCustomResourceOperationsImpl(client, getConfig(), getAPIGroup(), getAPIVersion(), getResourceT(), namespace, getName(), isCascading(), getItem(), getResourceVersion(), isReloadingFromServer(), getType(), getListType(), getDoneableType());

    }

    @Override
    public IstioCustomResourceOperationsImpl withName(String name) {
        return new IstioCustomResourceOperationsImpl(client, getConfig(), getAPIGroup(), getAPIVersion(), getResourceT(), getNamespace(), name, isCascading(), getItem(), getResourceVersion(), isReloadingFromServer(), getType(), getListType(), getDoneableType());
    }

    @Override
    public T replace(T item) {
            String fixedResourceVersion = this.getResourceVersion();
            Exception caught = null;
            int maxTries = 10;

            for(int i = 0; i < maxTries; ++i) {
                try {
                    if (this.isCascading() && this.reaper != null && !this.isReaping()) {
                        this.setReaping(true);
                        this.reaper.reap();
                    }

                    final String resourceVersion;
                    if (fixedResourceVersion != null) {
                        resourceVersion = fixedResourceVersion;
                    } else {
                        T got = (T)this.get();
                        if (got == null) {
                            return null;
                        }

                        if (got.getMetadata() != null) {
                            resourceVersion = got.getMetadata().getResourceVersion();
                        } else {
                            resourceVersion = null;
                        }
                    }

                    me.snowdrop.istio.api.builder.Function<T, T> visitor = new me.snowdrop.istio.api.builder.Function<T, T>() {
                        public T apply(T resource) {
                            try {
                                resource.getMetadata().setResourceVersion(resourceVersion);
                                return (T)IstioCustomResourceOperationsImpl.this.handleReplace(resource);
                            } catch (Exception var3) {
                                throw KubernetesClientException.launderThrowable(IstioCustomResourceOperationsImpl.this.forOperationType("replace"), var3);
                            }
                        }
                    };
                    D doneable = this.getDoneableType().getDeclaredConstructor(this.getType(), me.snowdrop.istio.api.builder.Function.class).newInstance(item, visitor);
                    return (T)doneable.done();
                } catch (KubernetesClientException var10) {
                    caught = var10;
                    if (var10.getCode() != 409 || fixedResourceVersion != null) {
                        throw KubernetesClientException.launderThrowable(this.forOperationType("replace"), (Throwable)caught);
                    }

                    if (i < maxTries - 1) {
                        try {
                            TimeUnit.SECONDS.sleep(1L);
                        } catch (InterruptedException var9) {
                            ;
                        }
                    }
                } catch (Exception var11) {
                    caught = var11;
                }
            }

            throw KubernetesClientException.launderThrowable(this.forOperationType("replace"), (Throwable)caught);
    }
}
