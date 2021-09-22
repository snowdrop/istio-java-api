package me.snowdrop.istio.client;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.Client;
import io.fabric8.kubernetes.client.dsl.NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import me.snowdrop.istio.api.IstioResource;

public interface IstioClient extends Client, IstioDsl {

    ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> load(InputStream is);

    NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(KubernetesResourceList<HasMetadata> item);

    NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(HasMetadata... items);

    NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(Collection<HasMetadata> items);

    ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(String s);

    NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata> resource(HasMetadata item);

    NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata> resource(String s);

    List<IstioResource> registerCustomResources(final String specFileAsString);

    List<IstioResource> registerCustomResources(final InputStream resource);

    List<IstioResource> getResourcesLike(final IstioResource resource);

    IstioResource registerCustomResource(final IstioResource resource);

    IstioResource registerOrUpdateCustomResource(final IstioResource resource);

    Boolean unregisterCustomResource(final IstioResource istioResource);

}
