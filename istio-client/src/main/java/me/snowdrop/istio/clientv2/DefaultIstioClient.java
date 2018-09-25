package me.snowdrop.istio.clientv2;

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
import me.snowdrop.istio.client.internal.operation.api.DestinationRuleOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.EnvoyFilterOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.GatewayOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.PolicyOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.RuleOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.ServiceEntryOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.ServiceRoleBindingOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.ServiceRoleOperationImpl;
import me.snowdrop.istio.client.internal.operation.api.VirtualServiceOperationImpl;
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
        return null;
    }

    @Override
    public NamespacedIstioClient inNamespace(String name) {
        return null;
    }

    @Override
    public FunctionCallable<NamespacedIstioClient> withRequestConfig(RequestConfig requestConfig) {
        return null;
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
        return new PolicyOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<DestinationRule, DestinationRuleList, DoneableDestinationRule, Resource<DestinationRule, DoneableDestinationRule>> destinationRule() {
        return new DestinationRuleOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<EnvoyFilter, EnvoyFilterList, DoneableEnvoyFilter, Resource<EnvoyFilter, DoneableEnvoyFilter>> envoyFilter() {
        return new EnvoyFilterOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Gateway, GatewayList, DoneableGateway, Resource<Gateway, DoneableGateway>> gateway() {
        return new GatewayOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<ServiceEntry, ServiceEntryList, DoneableServiceEntry, Resource<ServiceEntry, DoneableServiceEntry>> serviceEntry() {
        return new ServiceEntryOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<VirtualService, VirtualServiceList, DoneableVirtualService, Resource<VirtualService, DoneableVirtualService>> virtualService() {
        return new VirtualServiceOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<Rule, RuleList, DoneableRule, Resource<Rule, DoneableRule>> rule() {
        return new RuleOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<ServiceRoleBinding, ServiceRoleBindingList, DoneableServiceRoleBinding, Resource<ServiceRoleBinding, DoneableServiceRoleBinding>> serviceRoleBinding() {
        return new ServiceRoleBindingOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }

    @Override
    public MixedOperation<ServiceRole, ServiceRoleList, DoneableServiceRole, Resource<ServiceRole, DoneableServiceRole>> serviceRole() {
        return new ServiceRoleOperationImpl(getHttpClient(), getConfiguration(), getNamespace());
    }
}
