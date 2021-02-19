package me.snowdrop.istio.client;

import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
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

public interface IstioDsl {
    MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.DestinationRule, me.snowdrop.istio.api.networking.v1alpha3.DestinationRuleList, Resource<me.snowdrop.istio.api.networking.v1alpha3.DestinationRule>> v1alpha3DestinationRule();

    MixedOperation<DestinationRule, DestinationRuleList, Resource<DestinationRule>> v1beta1DestinationRule();

    MixedOperation<EnvoyFilter, EnvoyFilterList, Resource<EnvoyFilter>> v1alpha3EnvoyFilter();

    MixedOperation<Gateway, GatewayList, Resource<Gateway>> v1beta1Gateway();

    MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.Gateway, me.snowdrop.istio.api.networking.v1alpha3.GatewayList, Resource<me.snowdrop.istio.api.networking.v1alpha3.Gateway>> v1alpha3Gateway();

    MixedOperation<ServiceEntry, ServiceEntryList, Resource<ServiceEntry>> v1beta1ServiceEntry();

    MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.ServiceEntry, me.snowdrop.istio.api.networking.v1alpha3.ServiceEntryList, Resource<me.snowdrop.istio.api.networking.v1alpha3.ServiceEntry>> v1alpha3ServiceEntry();

    MixedOperation<VirtualService, VirtualServiceList, Resource<VirtualService>> v1beta1VirtualService();

    MixedOperation<me.snowdrop.istio.api.networking.v1alpha3.VirtualService, me.snowdrop.istio.api.networking.v1alpha3.VirtualServiceList, Resource<me.snowdrop.istio.api.networking.v1alpha3.VirtualService>> v1alpha3VirtualService();

    MixedOperation<Handler, HandlerList, Resource<Handler>> v1beta1Handler();

    MixedOperation<Instance, InstanceList, Resource<Instance>> v1beta1Instance();

    MixedOperation<AuthorizationPolicy, AuthorizationPolicyList, Resource<AuthorizationPolicy>> v1beta1AuthorizationPolicy();

    MixedOperation<RequestAuthentication, RequestAuthenticationList, Resource<RequestAuthentication>> v1beta1RequestAuthentication();

    MixedOperation<PeerAuthentication, PeerAuthenticationList, Resource<PeerAuthentication>> v1beta1PeerAuthentication();
}
