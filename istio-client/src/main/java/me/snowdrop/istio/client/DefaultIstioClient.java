package me.snowdrop.istio.client;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesListBuilder;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.*;
import io.fabric8.kubernetes.client.dsl.internal.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl;
import io.fabric8.kubernetes.client.dsl.internal.NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl;
import me.snowdrop.istio.api.IstioResource;
import me.snowdrop.istio.api.networking.v1alpha3.DoneableEnvoyFilter;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilter;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilterList;
import me.snowdrop.istio.api.networking.v1beta1.*;
import me.snowdrop.istio.api.rbac.v1alpha1.*;
import me.snowdrop.istio.client.internal.operation.networking.v1alpha3.EnvoyFilterOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.DestinationRuleOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.GatewayOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.ServiceEntryOperationImpl;
import me.snowdrop.istio.client.internal.operation.networking.v1beta1.VirtualServiceOperationImpl;
import me.snowdrop.istio.client.internal.operation.rbac.v1alpha1.ServiceRoleBindingOperationImpl;
import me.snowdrop.istio.client.internal.operation.rbac.v1alpha1.ServiceRoleOperationImpl;
import okhttp3.OkHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
	public MixedOperation<DestinationRule, DestinationRuleList, DoneableDestinationRule, Resource<DestinationRule, DoneableDestinationRule>> v1beta1DestinationRule() {
		return new DestinationRuleOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.DestinationRule, me.snowdrop.istio.api.networking.v1alpha3.DestinationRuleList, me.snowdrop.istio.api.networking.v1alpha3.DoneableDestinationRule, Resource<me.snowdrop.istio.api.networking.v1alpha3.DestinationRule, me.snowdrop.istio.api.networking.v1alpha3.DoneableDestinationRule>> v1alpha3DestinationRule() {
		return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.DestinationRuleOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<EnvoyFilter, EnvoyFilterList, DoneableEnvoyFilter, Resource<EnvoyFilter, DoneableEnvoyFilter>> v1alpha3EnvoyFilter() {
		return new EnvoyFilterOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<Gateway, GatewayList, DoneableGateway, Resource<Gateway, DoneableGateway>> v1beta1Gateway() {
		return new GatewayOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.Gateway, me.snowdrop.istio.api.networking.v1alpha3.GatewayList, me.snowdrop.istio.api.networking.v1alpha3.DoneableGateway, Resource<me.snowdrop.istio.api.networking.v1alpha3.Gateway, me.snowdrop.istio.api.networking.v1alpha3.DoneableGateway>> v1alpha3Gateway() {
		return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.GatewayOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<ServiceEntry, ServiceEntryList, DoneableServiceEntry, Resource<ServiceEntry, DoneableServiceEntry>> v1beta1ServiceEntry() {
		return new ServiceEntryOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.ServiceEntry, me.snowdrop.istio.api.networking.v1alpha3.ServiceEntryList, me.snowdrop.istio.api.networking.v1alpha3.DoneableServiceEntry, Resource<me.snowdrop.istio.api.networking.v1alpha3.ServiceEntry, me.snowdrop.istio.api.networking.v1alpha3.DoneableServiceEntry>> v1alpha3ServiceEntry() {
		return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.ServiceEntryOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<VirtualService, VirtualServiceList, DoneableVirtualService, Resource<VirtualService, DoneableVirtualService>> v1beta1VirtualService() {
		return new VirtualServiceOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.VirtualService, me.snowdrop.istio.api.networking.v1alpha3.VirtualServiceList, me.snowdrop.istio.api.networking.v1alpha3.DoneableVirtualService, Resource<me.snowdrop.istio.api.networking.v1alpha3.VirtualService, me.snowdrop.istio.api.networking.v1alpha3.DoneableVirtualService>> v1alpha3VirtualService() {
		return new me.snowdrop.istio.client.internal.operation.networking.v1alpha3.VirtualServiceOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<ServiceRoleBinding, ServiceRoleBindingList, DoneableServiceRoleBinding, Resource<ServiceRoleBinding, DoneableServiceRoleBinding>> v1alpha1ServiceRoleBinding() {
		return new ServiceRoleBindingOperationImpl(getHttpClient(), getConfiguration());
	}

	@Override
	public MixedOperation<ServiceRole, ServiceRoleList, DoneableServiceRole, Resource<ServiceRole, DoneableServiceRole>> v1alpha1ServiceRole() {
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
		return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), item, null, null, -1, null, false) {
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
		return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableListImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), s, null, null, -1, null, false) {
		};
	}


	@Override
	public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(HasMetadata item) {
		return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), item, -1, null, false) {
		};
	}

	@Override
	public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(String s) {
		return new NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicableImpl(httpClient, getConfiguration(), getNamespace(), null, false, false, new ArrayList<>(), s, -1, null, false) {
		};
	}


	//Compatibility
	public List<IstioResource> registerCustomResources(final String specFileAsString) {
		return resourceList(specFileAsString).createOrReplace()
				.stream()
				.filter(r -> r instanceof IstioResource)
				.map(r -> (IstioResource) r)
				.collect(Collectors.toList());
	}

	public List<IstioResource> registerCustomResources(final InputStream resource) {
		return load(resource).createOrReplace()
				.stream()
				.filter(r -> r instanceof IstioResource)
				.map(r -> (IstioResource) r)
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
