package me.snowdrop.istio.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesListBuilder;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.RequestConfig;
import io.fabric8.kubernetes.client.WithRequestCallable;
import io.fabric8.kubernetes.client.dsl.FunctionCallable;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.OperationContext;
import io.fabric8.kubernetes.client.dsl.internal.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl;
import io.fabric8.kubernetes.client.dsl.internal.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl;
import io.fabric8.kubernetes.client.utils.Serialization;
import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilter;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilterList;
import me.snowdrop.istio.api.networking.v1beta1.DestinationRule;
import me.snowdrop.istio.api.networking.v1beta1.DestinationRuleList;
import me.snowdrop.istio.api.networking.v1beta1.Gateway;
import me.snowdrop.istio.api.networking.v1beta1.GatewayList;
import me.snowdrop.istio.api.networking.v1beta1.ServiceEntry;
import me.snowdrop.istio.api.networking.v1beta1.ServiceEntryList;
import me.snowdrop.istio.api.networking.v1beta1.VirtualService;
import me.snowdrop.istio.api.networking.v1beta1.VirtualServiceList;
import me.snowdrop.istio.api.policy.v1beta1.Handler;
import me.snowdrop.istio.api.policy.v1beta1.HandlerList;
import me.snowdrop.istio.api.policy.v1beta1.Instance;
import me.snowdrop.istio.api.policy.v1beta1.InstanceList;
import me.snowdrop.istio.api.security.v1beta1.AuthorizationPolicy;
import me.snowdrop.istio.api.security.v1beta1.AuthorizationPolicyList;
import me.snowdrop.istio.api.security.v1beta1.PeerAuthentication;
import me.snowdrop.istio.api.security.v1beta1.PeerAuthenticationList;
import me.snowdrop.istio.api.security.v1beta1.RequestAuthentication;
import me.snowdrop.istio.api.security.v1beta1.RequestAuthenticationList;
import me.snowdrop.istio.client.internal.operation.networking.v1alpha3.EnvoyFilterOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.DestinationRuleOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.GatewayOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.ServiceEntryOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.VirtualServiceOperationImpl;
import me.snowdrop.istio.client.internal.operation.policy.v1beta1.HandlerOperationImpl;
import me.snowdrop.istio.client.internal.operation.policy.v1beta1.InstanceOperationImpl;
import me.snowdrop.istio.client.internal.operation.security.v1beta1.AuthorizationPolicyOperationImpl;
import me.snowdrop.istio.client.internal.operation.security.v1beta1.PeerAuthenticationOperationImpl;
import me.snowdrop.istio.client.internal.operation.security.v1beta1.RequestAuthenticationOperationImpl;
import okhttp3.OkHttpClient;

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
        return new WithRequestCallable<>(this, requestConfig);
    }

    @Override
    public MixedOperation<DestinationRule, DestinationRuleList, Resource<DestinationRule>> v1beta1DestinationRule() {
        return new DestinationRuleOperationImpl<>(getHttpClient(), getConfiguration(), DestinationRuleList.class);
    }

    @Override
    public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.DestinationRule,
            me.snowdrop.istio.api.networking.v1alpha3.DestinationRuleList,
            Resource<me.snowdrop.istio.api.networking.v1alpha3.DestinationRule>> v1alpha3DestinationRule() {
        return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.DestinationRuleOperationImpl<>(getHttpClient(),
                                                                                                                  getConfiguration(),
                                                                                                                  me.snowdrop.istio.api.networking.v1alpha3.DestinationRuleList.class);
    }

    @Override
    public MixedOperation<EnvoyFilter, EnvoyFilterList, Resource<EnvoyFilter>> v1alpha3EnvoyFilter() {
        return new EnvoyFilterOperationImpl<>(getHttpClient(), getConfiguration(), EnvoyFilterList.class);
    }

    @Override
    public MixedOperation<Gateway, GatewayList, Resource<Gateway>> v1beta1Gateway() {
        return new GatewayOperationImpl<>(getHttpClient(), getConfiguration(), Gateway.class);
    }

    @Override
    public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.Gateway,
            me.snowdrop.istio.api.networking.v1alpha3.GatewayList, Resource<me.snowdrop.istio.api.networking.v1alpha3.Gateway>> v1alpha3Gateway() {
        return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.GatewayOperationImpl<>(getHttpClient(),
                                                                                                          getConfiguration(),
                                                                                                          me.snowdrop.istio.api.networking.v1alpha3.GatewayList.class);
    }

    @Override
    public MixedOperation<ServiceEntry, ServiceEntryList, Resource<ServiceEntry>> v1beta1ServiceEntry() {
        return new ServiceEntryOperationImpl<>(getHttpClient(), getConfiguration(), ServiceEntryList.class);
    }

    @Override
    public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.ServiceEntry,
            me.snowdrop.istio.api.networking.v1alpha3.ServiceEntryList,
            Resource<me.snowdrop.istio.api.networking.v1alpha3.ServiceEntry>> v1alpha3ServiceEntry() {
        return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.ServiceEntryOperationImpl<>(getHttpClient(),
                                                                                                               getConfiguration(),
                                                                                                               me.snowdrop.istio.api.networking.v1alpha3.ServiceEntryList.class);
    }

    @Override
    public MixedOperation<VirtualService, VirtualServiceList, Resource<VirtualService>> v1beta1VirtualService() {
        return new VirtualServiceOperationImpl<>(getHttpClient(), getConfiguration(), VirtualServiceList.class);
    }

    @Override
    public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.VirtualService,
            me.snowdrop.istio.api.networking.v1alpha3.VirtualServiceList,
            Resource<me.snowdrop.istio.api.networking.v1alpha3.VirtualService>> v1alpha3VirtualService() {
        return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.VirtualServiceOperationImpl<>(getHttpClient(),
                                                                                                                 getConfiguration(),
                                                                                                                 me.snowdrop.istio.api.networking.v1alpha3.VirtualServiceList.class);
    }

    @Override
    public MixedOperation<Handler, HandlerList, Resource<Handler>> v1beta1Handler() {
        return new HandlerOperationImpl<>(getHttpClient(), getConfiguration(), HandlerList.class);
    }

    @Override
    public MixedOperation<Instance, InstanceList, Resource<Instance>> v1beta1Instance() {
        return new InstanceOperationImpl<>(getHttpClient(), getConfiguration(), InstanceList.class);
    }

    public MixedOperation<AuthorizationPolicy, AuthorizationPolicyList, Resource<AuthorizationPolicy>> v1beta1AuthorizationPolicy() {
        return new AuthorizationPolicyOperationImpl<>(getHttpClient(), getConfiguration(), AuthorizationPolicyList.class);
    }

    public MixedOperation<RequestAuthentication, RequestAuthenticationList, Resource<RequestAuthentication>> v1beta1RequestAuthentication() {
        return new RequestAuthenticationOperationImpl<>(getHttpClient(), getConfiguration(), RequestAuthenticationList.class);
    }

    public MixedOperation<PeerAuthentication, PeerAuthenticationList, Resource<PeerAuthentication>> v1beta1PeerAuthentication() {
        return new PeerAuthenticationOperationImpl<>(getHttpClient(), getConfiguration(), PeerAuthenticationList.class);
    }

    //Generic methods for handling resources
    @Override
    public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> load(InputStream is) {
        return resourceListFor(is);
    }

    @Override
    public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(KubernetesResourceList<HasMetadata> item) {
        return resourceListFor(item);
    }

    @Override
    public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(HasMetadata... items) {
        return resourceList(new KubernetesListBuilder().withItems(items).build());
    }

    @Override
    public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(Collection<HasMetadata> items) {
        return resourceList(new KubernetesListBuilder().withItems(new ArrayList<>(items)).build());
    }

    @Override
    public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> resourceList(String s) {
        return resourceListFor(s);
    }

    @Override
    public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata> resource(HasMetadata item) {
        return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl(httpClient, getConfiguration(), item);
    }

    @Override
    public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata> resource(String s) {
        return resource((HasMetadata) Serialization.unmarshal(s));
    }


    public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl resourceListFor(Object item) {
        return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl(httpClient, getConfiguration(), item);
    }

    //Compatibility
    public List<IstioResource> registerCustomResources(final String specFileAsString) {
        return resourceList(specFileAsString).createOrReplace()
                .stream()
                .filter(IstioResource.class::isInstance)
                .map(IstioResource.class::cast)
                .collect(Collectors.toList());
    }

    public List<IstioResource> registerCustomResources(final InputStream resource) {
        return load(resource).createOrReplace()
                .stream()
                .filter(IstioResource.class::isInstance)
                .map(IstioResource.class::cast)
                .collect(Collectors.toList());
    }

    public List<IstioResource> getResourcesLike(final IstioResource resource) {
        throw new UnsupportedOperationException();
    }

    public IstioResource registerCustomResource(final IstioResource resource) {
        return (IstioResource) resource(resource).createOrReplace();
    }

    public IstioResource registerOrUpdateCustomResource(final IstioResource resource) {
        return (IstioResource) resource(resource).createOrReplace();
    }

    public Boolean unregisterCustomResource(final IstioResource istioResource) {
        return resource(istioResource).delete();
    }
}
