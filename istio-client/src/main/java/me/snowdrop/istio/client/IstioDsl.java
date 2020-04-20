package me.snowdrop.istio.client;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import me.snowdrop.istio.api.authentication.v1alpha1.Policy;
import me.snowdrop.istio.api.authentication.v1alpha1.PolicyList;
import me.snowdrop.istio.api.authentication.v1alpha1.DoneablePolicy;
import me.snowdrop.istio.api.networking.v1beta1.DestinationRule;
import me.snowdrop.istio.api.networking.v1beta1.DestinationRuleList;
import me.snowdrop.istio.api.networking.v1beta1.DoneableDestinationRule;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilter;
import me.snowdrop.istio.api.networking.v1alpha3.EnvoyFilterList;
import me.snowdrop.istio.api.networking.v1alpha3.DoneableEnvoyFilter;
import me.snowdrop.istio.api.networking.v1beta1.Gateway;
import me.snowdrop.istio.api.networking.v1beta1.GatewayList;
import me.snowdrop.istio.api.networking.v1beta1.DoneableGateway;
import me.snowdrop.istio.api.networking.v1beta1.ServiceEntry;
import me.snowdrop.istio.api.networking.v1beta1.ServiceEntryList;
import me.snowdrop.istio.api.networking.v1beta1.DoneableServiceEntry;
import me.snowdrop.istio.api.networking.v1beta1.VirtualService;
import me.snowdrop.istio.api.networking.v1beta1.VirtualServiceList;
import me.snowdrop.istio.api.networking.v1beta1.DoneableVirtualService;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRoleBinding;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRoleBindingList;
import me.snowdrop.istio.api.rbac.v1alpha1.DoneableServiceRoleBinding;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRole;
import me.snowdrop.istio.api.rbac.v1alpha1.ServiceRoleList;
import me.snowdrop.istio.api.rbac.v1alpha1.DoneableServiceRole;

public interface IstioDsl {
  MixedOperation<Policy,PolicyList, DoneablePolicy,Resource<Policy,DoneablePolicy>> policy();
  MixedOperation<DestinationRule,DestinationRuleList, DoneableDestinationRule,Resource<DestinationRule,DoneableDestinationRule>> destinationRule();
  MixedOperation<EnvoyFilter,EnvoyFilterList, DoneableEnvoyFilter,Resource<EnvoyFilter,DoneableEnvoyFilter>> envoyFilter();
  MixedOperation<Gateway,GatewayList, DoneableGateway,Resource<Gateway,DoneableGateway>> gateway();
  MixedOperation<ServiceEntry,ServiceEntryList, DoneableServiceEntry,Resource<ServiceEntry,DoneableServiceEntry>> serviceEntry();
  MixedOperation<VirtualService,VirtualServiceList, DoneableVirtualService,Resource<VirtualService,DoneableVirtualService>> virtualService();
  MixedOperation<ServiceRoleBinding,ServiceRoleBindingList, DoneableServiceRoleBinding,Resource<ServiceRoleBinding,DoneableServiceRoleBinding>> serviceRoleBinding();
  MixedOperation<ServiceRole,ServiceRoleList, DoneableServiceRole,Resource<ServiceRole,DoneableServiceRole>> serviceRole();
}
