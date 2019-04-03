package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.RequestConfig;
import io.fabric8.kubernetes.client.dsl.FunctionCallable;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import me.snowdrop.istio.api.authentication.v1alpha1.DoneablePolicy;
import me.snowdrop.istio.api.authentication.v1alpha1.Policy;
import me.snowdrop.istio.api.authentication.v1alpha1.PolicyList;
import me.snowdrop.istio.api.networking.v1alpha3.DestinationRule;
import me.snowdrop.istio.api.networking.v1alpha3.DestinationRuleList;
import me.snowdrop.istio.api.networking.v1alpha3.DoneableDestinationRule;
import me.snowdrop.istio.api.networking.v1alpha3.DoneableEnvoyFilter;
import me.snowdrop.istio.api.networking.v1alpha3.DoneableGateway;
import me.snowdrop.istio.api.networking.v1alpha3.DoneableServiceEntry;
import me.snowdrop.istio.api.networking.v1alpha3.DoneableVirtualService;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilter;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilterList;
import me.snowdrop.istio.api.networking.v1alpha3.Gateway;
import me.snowdrop.istio.api.networking.v1alpha3.GatewayList;
import me.snowdrop.istio.api.networking.v1alpha3.ServiceEntry;
import me.snowdrop.istio.api.networking.v1alpha3.ServiceEntryList;
import me.snowdrop.istio.api.networking.v1alpha3.VirtualService;
import me.snowdrop.istio.api.networking.v1alpha3.VirtualServiceList;
import me.snowdrop.istio.api.policy.v1beta1.DoneableRule;
import me.snowdrop.istio.api.policy.v1beta1.Rule;
import me.snowdrop.istio.api.policy.v1beta1.RuleList;
import me.snowdrop.istio.api.rbac.v1alpha1.DoneableServiceRole;
import me.snowdrop.istio.api.rbac.v1alpha1.DoneableServiceRoleBinding;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRole;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRoleBinding;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRoleBindingList;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRoleList;
import me.snowdrop.istio.client.internal.operation.DestinationRuleOperationImpl;
import me.snowdrop.istio.client.internal.operation.EnvoyFilterOperationImpl;
import me.snowdrop.istio.client.internal.operation.GatewayOperationImpl;
import me.snowdrop.istio.client.internal.operation.PolicyOperationImpl;
import me.snowdrop.istio.client.internal.operation.RuleOperationImpl;
import me.snowdrop.istio.client.internal.operation.ServiceEntryOperationImpl;
import me.snowdrop.istio.client.internal.operation.ServiceRoleBindingOperationImpl;
import me.snowdrop.istio.client.internal.operation.ServiceRoleOperationImpl;
import me.snowdrop.istio.client.internal.operation.VirtualServiceOperationImpl;
import okhttp3.OkHttpClient;
import io.fabric8.kubernetes.api.model.KubernetesListBuilder;
import io.fabric8.kubernetes.client.dsl.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.api.model.HasMetadata;
import me.snowdrop.istio.api.IstioResource;
import io.fabric8.kubernetes.client.dsl.internal.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl;
import java.io.InputStream;
import java.util.ArrayList;
import io.fabric8.kubernetes.client.dsl.NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.dsl.internal.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl;
import io.fabric8.kubernetes.client.dsl.ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import io.fabric8.kubernetes.client.WithRequestCallable;
import io.fabric8.kubernetes.client.ConfigBuilder;

public class DefaultIstioClient extends BaseClient implements NamespacedIstioClient {

    public DefaultIstioClient() {
        super();
    }

    public DefaultIstioClient(Config configuration) {
        super(configuration);
    }

    public DefaultIstioClient(OkHttpClient httpClient, Config configuration) {
        super(httpClient, configuration);
    }

    @Override
    public NamespacedIstioClient inAnyNamespace() {
        return inNamespace(null);
    }

    @Override
    public NamespacedIstioClient inNamespace(String namespace) {
        Config updated = new ConfigBuilder(getConfiguration())
                .withNamespace(namespace)
                .build();

        return new DefaultIstioClient(getHttpClient(), updated);
    }

    @Override
    public FunctionCallable<NamespacedIstioClient> withRequestConfig(RequestConfig requestConfig) {
        return new WithRequestCallable<NamespacedIstioClient>(this, requestConfig);
    }    

    @Override
    public AdapterDsl adapter() {
        return new AdapterClient(getHttpClient(), getConfiguration());
    }

    @Override
    public MixerDsl mixer() {
        return new MixerClient(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<Policy, PolicyList, DoneablePolicy, Resource<Policy, DoneablePolicy>> policy() {
        return new PolicyOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<DestinationRule, DestinationRuleList, DoneableDestinationRule, Resource<DestinationRule, DoneableDestinationRule>> destinationRule() {
        return new DestinationRuleOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<EnvoyFilter, EnvoyFilterList, DoneableEnvoyFilter, Resource<EnvoyFilter, DoneableEnvoyFilter>> envoyFilter() {
        return new EnvoyFilterOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<Gateway, GatewayList, DoneableGateway, Resource<Gateway, DoneableGateway>> gateway() {
        return new GatewayOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<ServiceEntry, ServiceEntryList, DoneableServiceEntry, Resource<ServiceEntry, DoneableServiceEntry>> serviceEntry() {
        return new ServiceEntryOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<VirtualService, VirtualServiceList, DoneableVirtualService, Resource<VirtualService, DoneableVirtualService>> virtualService() {
        return new VirtualServiceOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<Rule, RuleList, DoneableRule, Resource<Rule, DoneableRule>> rule() {
        return new RuleOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<ServiceRoleBinding, ServiceRoleBindingList, DoneableServiceRoleBinding, Resource<ServiceRoleBinding, DoneableServiceRoleBinding>> serviceRoleBinding() {
        return new ServiceRoleBindingOperationImpl(getHttpClient(), getConfiguration());
    }

    @Override
    public MixedOperation<ServiceRole, ServiceRoleList, DoneableServiceRole, Resource<ServiceRole, DoneableServiceRole>> serviceRole() {
        return new ServiceRoleOperationImpl(getHttpClient(), getConfiguration());
    }

    //Generic methods for handling resources
  @Override
  public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> load(InputStream is) {
    return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), is, null, false) {
    };
  }

  @Override
  public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(KubernetesResourceList item) {
    return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), item, null, null, -1, false) {
    };
  }

  @Override
  public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(HasMetadata... items) {
    return resourceList(new KubernetesListBuilder().withItems(items).build());
  }

  @Override
  public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(Collection<HasMetadata> items) {
    return resourceList(new KubernetesListBuilder().withItems(new ArrayList<HasMetadata>(items)).build());
  }

  @Override
  public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(String s) {
    return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), s, null, null, -1, false) {
    };
  }


  @Override
  public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(HasMetadata item) {
    return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), item, -1, false) {
    };
  }

  @Override
  public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(String s) {
    return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), s, -1, false) {
    };
  }


    //Compatibility
    public List<IstioResource> registerCustomResources(final String specFileAsString) {
        return resourceList(specFileAsString).createOrReplace()
            .stream()
            .filter(r -> r instanceof IstioResource)
            .map(r -> (IstioResource)r)
            .collect(Collectors.toList());
    }

    public List<IstioResource> registerCustomResources(final InputStream resource) {
        return load(resource).createOrReplace()
            .stream()
            .filter(r -> r instanceof IstioResource)
            .map(r -> (IstioResource)r)
            .collect(Collectors.toList());
    }
    
    public List<IstioResource> getResourcesLike(final IstioResource resource) {
        throw new UnsupportedOperationException();
    }
    
    public IstioResource registerCustomResource(final IstioResource resource) {
        return (IstioResource)resource(resource).createOrReplace();
    }

    public IstioResource registerOrUpdateCustomResource(final IstioResource resource) {
        return (IstioResource)resource(resource).createOrReplace();
    }

    public Boolean unregisterCustomResource(final IstioResource istioResource) {
        return resource(istioResource).delete();
    }
}
