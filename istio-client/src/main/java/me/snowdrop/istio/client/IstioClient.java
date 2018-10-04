package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.Client;
import me.snowdrop.istio.api.IstioResource;
import java.util.List;
import java.io.InputStream;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.dsl.ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import java.util.Collection;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.dsl.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;

public interface IstioClient extends Client, IstioDsl {

    AdapterDsl adapter();
    MixerDsl mixer();

    public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> load(InputStream is);

    public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(KubernetesResourceList item);

    public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(HasMetadata... items);

    public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(Collection<HasMetadata> items);

    public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(String s);

    public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(HasMetadata item);

    public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(String s);


    public List<IstioResource> registerCustomResources(final String specFileAsString);
    public List<IstioResource> registerCustomResources(final InputStream resource);
    
    public List<IstioResource> getResourcesLike(final IstioResource resource);
    
    public IstioResource registerCustomResource(final IstioResource resource);

    public IstioResource registerOrUpdateCustomResource(final IstioResource resource);

    public Boolean unregisterCustomResource(final IstioResource istioResource);

}
