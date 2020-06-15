/**
 * Copyright (C) 2011 Red Hat, Inc.
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
package main

import (
	"bytes"
	"encoding/json"
	"flag"
	"fmt"
	"github.com/ghodss/yaml"
	"github.com/gogo/protobuf/types"
	"io/ioutil"
	authentication "istio.io/api/authentication/v1alpha1"
	mesh "istio.io/api/mesh/v1alpha1"
	mixer "istio.io/api/mixer/v1"
	networkingv1alpha3 "istio.io/api/networking/v1alpha3"
	networking "istio.io/api/networking/v1beta1"
	rbac "istio.io/api/rbac/v1alpha1"
	security "istio.io/api/security/v1beta1"
	bypass "istio.io/istio/mixer/adapter/bypass/config"
	circonus "istio.io/istio/mixer/adapter/circonus/config"
	cloudwatch "istio.io/istio/mixer/adapter/cloudwatch/config"
	denier "istio.io/istio/mixer/adapter/denier/config"
	dogstatsd "istio.io/istio/mixer/adapter/dogstatsd/config"
	fluentd "istio.io/istio/mixer/adapter/fluentd/config"
	kubernetesenv "istio.io/istio/mixer/adapter/kubernetesenv/config"
	list "istio.io/istio/mixer/adapter/list/config"
	memquota "istio.io/istio/mixer/adapter/memquota/config"
	opa "istio.io/istio/mixer/adapter/opa/config"
	prometheus "istio.io/istio/mixer/adapter/prometheus/config"
	redisquota "istio.io/istio/mixer/adapter/redisquota/config"
	solarwinds "istio.io/istio/mixer/adapter/solarwinds/config"
	stackdriver "istio.io/istio/mixer/adapter/stackdriver/config"
	statsd "istio.io/istio/mixer/adapter/statsd/config"
	stdio "istio.io/istio/mixer/adapter/stdio/config"
	zipkin "istio.io/istio/mixer/adapter/zipkin/config"
	"istio.io/istio/mixer/template/apikey"
	"istio.io/istio/mixer/template/authorization"
	"istio.io/istio/mixer/template/checknothing"
	"istio.io/istio/mixer/template/edge"
	"istio.io/istio/mixer/template/listentry"
	"istio.io/istio/mixer/template/logentry"
	"istio.io/istio/mixer/template/metric"
	"istio.io/istio/mixer/template/quota"
	"istio.io/istio/mixer/template/reportnothing"
	"istio.io/istio/mixer/template/tracespan"
	"log"
	"os"
	"reflect"
	"strings"

	"bufio"
	"github.com/snowdrop/istio-java-api/pkg/schemagen"
)

type Schema struct {
	Struct                                     types.Struct
	Value                                      types.Value
	BoolValue                                  types.Value_BoolValue
	ListValue                                  types.Value_ListValue
	NumberValue                                types.Value_NumberValue
	NullValue                                  types.Value_NullValue
	StringValue                                types.Value_StringValue
	StructValue                                types.Value_StructValue
	MeshConfig                                 mesh.MeshConfig
	ProxyConfig                                mesh.ProxyConfig
	Attributes                                 mixer.Attributes
	AttributeValue                             mixer.Attributes_AttributeValue
	CheckRequest                               mixer.CheckRequest
	QuotaParams                                mixer.CheckRequest_QuotaParams
	CheckResponse                              mixer.CheckResponse
	QuotaResult                                mixer.CheckResponse_QuotaResult
	CompressedAttributes                       mixer.CompressedAttributes
	ReferencedAttributes                       mixer.ReferencedAttributes
	ReportRequest                              mixer.ReportRequest
	ReportResponse                             mixer.ReportResponse
	StringMap                                  mixer.StringMap
	RbacConfig                                 rbac.RbacConfig
	ServiceRole                                rbac.ServiceRole
	ServiceRoleBinding                         rbac.ServiceRoleBinding
	EnvoyFilter                                networkingv1alpha3.EnvoyFilter
	ClusterObjectTypes                         networkingv1alpha3.EnvoyFilter_EnvoyConfigObjectMatch_Cluster
	ListenerObjectTypes                        networkingv1alpha3.EnvoyFilter_EnvoyConfigObjectMatch_Listener
	RouteConfigurationObjectTypes              networkingv1alpha3.EnvoyFilter_EnvoyConfigObjectMatch_RouteConfiguration
	V1Alpha3Gateway                            networkingv1alpha3.Gateway
	V1Alpha3DestinationRule                    networkingv1alpha3.DestinationRule
	V1Alpha3SimpleLoadBalancerSettings         networkingv1alpha3.LoadBalancerSettings_Simple
	V1Alpha3ConsistentHashLoadBalancerSettings networkingv1alpha3.LoadBalancerSettings_ConsistentHash
	V1Alpha3HttpCookieHashKey                  networkingv1alpha3.LoadBalancerSettings_ConsistentHashLB_HttpCookie
	V1Alpha3HttpHeaderNameHashKey              networkingv1alpha3.LoadBalancerSettings_ConsistentHashLB_HttpHeaderName
	V1Alpha3UseSourceIpHashKey                 networkingv1alpha3.LoadBalancerSettings_ConsistentHashLB_UseSourceIp
	V1Alpha3ExactStringMatch                   networkingv1alpha3.StringMatch_Exact
	V1Alpha3PrefixStringMatch                  networkingv1alpha3.StringMatch_Prefix
	V1Alpha3RegexStringMatch                   networkingv1alpha3.StringMatch_Regex
	V1Alpha3VPortSelector                      networkingv1alpha3.PortSelector
	V1Alpha3ExponentialDelay                   networkingv1alpha3.HTTPFaultInjection_Delay_ExponentialDelay
	V1Alpha3FixedDelay                         networkingv1alpha3.HTTPFaultInjection_Delay_FixedDelay
	V1Alpha3GrpcStatusAbort                    networkingv1alpha3.HTTPFaultInjection_Abort_GrpcStatus
	V1Alpha3Http2ErrorAbort                    networkingv1alpha3.HTTPFaultInjection_Abort_Http2Error
	V1Alpha3HttpStatusAbort                    networkingv1alpha3.HTTPFaultInjection_Abort_HttpStatus
	V1Alpha3ServiceEntry                       networkingv1alpha3.ServiceEntry
	V1Alpha3VirtualService                     networkingv1alpha3.VirtualService
	V1Alpha3Sidecar                            networkingv1alpha3.Sidecar
	Policy                                     authentication.Policy
	AuthNamePortSelector                       authentication.PortSelector_Name
	AuthNumberPortSelector                     authentication.PortSelector_Number
	JwtPeerAuthenticationMethod                authentication.PeerAuthenticationMethod_Jwt
	MtlsPeerAuthenticationMethod               authentication.PeerAuthenticationMethod_Mtls
	Gateway                                    networking.Gateway
	DestinationRule                            networking.DestinationRule
	SimpleLoadBalancerSettings                 networking.LoadBalancerSettings_Simple
	ConsistentHashLoadBalancerSettings         networking.LoadBalancerSettings_ConsistentHash
	HttpCookieHashKey                          networking.LoadBalancerSettings_ConsistentHashLB_HttpCookie
	HttpHeaderNameHashKey                      networking.LoadBalancerSettings_ConsistentHashLB_HttpHeaderName
	UseSourceIpHashKey                         networking.LoadBalancerSettings_ConsistentHashLB_UseSourceIp
	ExactStringMatch                           networking.StringMatch_Exact
	PrefixStringMatch                          networking.StringMatch_Prefix
	RegexStringMatch                           networking.StringMatch_Regex
	VPortSelector                              networking.PortSelector
	ExponentialDelay                           networking.HTTPFaultInjection_Delay_ExponentialDelay
	FixedDelay                                 networking.HTTPFaultInjection_Delay_FixedDelay
	GrpcStatusAbort                            networking.HTTPFaultInjection_Abort_GrpcStatus
	Http2ErrorAbort                            networking.HTTPFaultInjection_Abort_Http2Error
	HttpStatusAbort                            networking.HTTPFaultInjection_Abort_HttpStatus
	ServiceEntry                               networking.ServiceEntry
	VirtualService                             networking.VirtualService
	Sidecar                                    networking.Sidecar
	Bypass                                     bypass.Params
	Circonus                                   circonus.Params
	CloudWatch                                 cloudwatch.Params
	CWMetricDatum                              cloudwatch.Params_MetricDatum
	CWLogInfo                                  cloudwatch.Params_LogInfo
	Denier                                     denier.Params
	Dogstatsd                                  dogstatsd.Params
	DSMetricInfo                               dogstatsd.Params_MetricInfo
	Fluentd                                    fluentd.Params
	KubernetesEnv                              kubernetesenv.Params
	ListChecker                                list.Params
	MemQuota                                   memquota.Params
	OPA                                        opa.Params
	Prometheus                                 prometheus.Params
	ExplicitBucketsDefinition                  prometheus.Params_MetricInfo_BucketsDefinition_ExplicitBuckets
	LinearBucketsDefinition                    prometheus.Params_MetricInfo_BucketsDefinition_LinearBuckets
	ExponentialBucketsDefinition               prometheus.Params_MetricInfo_BucketsDefinition_ExponentialBuckets
	RedisQuota                                 redisquota.Params
	SolarWinds                                 solarwinds.Params
	SWLogInfo                                  solarwinds.Params_LogInfo
	SWMetricInfo                               solarwinds.Params_MetricInfo
	StackDriver                                stackdriver.Params
	SDLogInfo                                  stackdriver.Params_LogInfo
	SDMetricInfo                               stackdriver.Params_MetricInfo
	SDApiKey                                   stackdriver.Params_ApiKey
	SDAppCredentials                           stackdriver.Params_AppCredentials
	SDServiceAccountPath                       stackdriver.Params_ServiceAccountPath
	Statsd                                     statsd.Params
	StatsdMetricInfo                           statsd.Params_MetricInfo
	Stdio                                      stdio.Params
	Zipkin                                     zipkin.Params
	APIKey                                     apikey.InstanceMsg
	Authorization                              authorization.InstanceMsg
	CheckNothing                               checknothing.InstanceMsg
	Edge                                       edge.InstanceMsg
	ListEntry                                  listentry.InstanceMsg
	LogEntry                                   logentry.InstanceMsg
	Metric                                     metric.InstanceMsg
	Quota                                      quota.InstanceMsg
	ReportNothing                              reportnothing.InstanceMsg
	TraceSpan                                  tracespan.InstanceMsg
	AuthorizationPolicy                        security.AuthorizationPolicy
}

// code adapted from https://kgrz.io/reading-files-in-go-an-overview.html#scanning-comma-seperated-string
func readDescriptors() []schemagen.PackageDescriptor {
	var descriptors = make([]schemagen.PackageDescriptor, 0, 20)

	file, err := os.Open("istio-common/src/main/resources/packages.csv")
	if err != nil {
		fmt.Println(err)
		return descriptors
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanLines)

	for scanner.Scan() {
		line := scanner.Text()

		// ignore commented out lines
		if strings.HasPrefix(line, "#") {
			continue
		}

		lineScanner := bufio.NewScanner(strings.NewReader(line))
		lineScanner.Split(func(data []byte, atEOF bool) (advance int, token []byte, err error) {
			commaidx := bytes.IndexByte(data, ',')
			if commaidx > 0 {
				// we need to return the next position
				buffer := data[:commaidx]
				return commaidx + 1, bytes.TrimSpace(buffer), nil
			}

			// if we are at the end of the string, just return the entire buffer
			if atEOF {
				// but only do that when there is some data. If not, this might mean
				// that we've reached the end of our input CSV string
				if len(data) > 0 {
					return len(data), bytes.TrimSpace(data), nil
				}
			}

			// when 0, nil, nil is returned, this is a signal to the interface to read
			// more data in from the input reader. In this case, this input is our
			// string reader and this pretty much will never occur.
			return 0, nil, nil
		})

		var packageInfo = make([]string, 3)
		var i = 0
		for lineScanner.Scan() {
			packageInfo[i] = lineScanner.Text()
			i = i + 1
		}
		descriptors = append(descriptors, schemagen.PackageDescriptor{GoPackage: packageInfo[0], JavaPackage: packageInfo[1], Prefix: packageInfo[2]})
	}

	return descriptors
}

type class struct {
	Class  string            `json:"class"`
	Fields map[string]string `json:"fields"`
}

type classData struct {
	Classes []class `json:"classes"`
}

func loadInterfacesData(crds map[string]schemagen.CrdDescriptor) (map[string]string, map[string]string) {
	impls := make(map[string]string)
	interfaces := make(map[string]string)

	path := "istio-common/src/main/resources/classes-with-interface-fields.yml"
	source, err := ioutil.ReadFile(path)
	if err != nil {
		panic(err)
	}

	var classes classData
	err = yaml.Unmarshal(source, &classes)
	if err != nil {
		log.Fatal(err)
	}

	for _, class := range classes.Classes {
		className := class.Class
		_, ok := crds[strings.ToLower(className[strings.LastIndex(className, ".")+1:])]
		if ok {
			className += "Spec"
		}

		// we need to qualify the interface name to avoid collisions: we use the Java package name minus the "me.snowdrop.istio" prefix
		// note that this qualifying must match was is done in schemagen/generate#getQualifiedInterfaceName
		pkgName := className[len("me.snowdrop.istio.") : strings.LastIndex(className, ".")+1]
		interfaceName := className + "." // to define inner classes for interface fields
		interfaceSet := false
		for key, field := range class.Fields {
			if strings.HasPrefix(field, "is") {
				lastUnderscore := strings.LastIndex(field, "_")

				if !interfaceSet {
					interfaceName += field[lastUnderscore+1:]
					interfaceSet = true
				}

				impl := field[2:lastUnderscore+1] + strings.Title(key)
				impls[pkgName+impl] = interfaceName
				interfaces[pkgName+field] = interfaceName
			}
		}
	}

	return impls, interfaces
}

func loadCRDs() map[string]schemagen.CrdDescriptor {
	crds := make(map[string]schemagen.CrdDescriptor)

	path := "istio-common/src/main/resources/istio-crd.properties"

	file, err := os.Open(path)
	if err != nil {
		fmt.Println(err)
		return crds
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanLines)

	for scanner.Scan() {
		line := scanner.Text()

		split := strings.Split(line, "|")
		crd := strings.ToLower(split[0][:strings.IndexRune(split[0], '=')])
		crdType := strings.TrimSpace(split[1][strings.IndexRune(split[1], '=')+1:])
		crds[crd] = schemagen.CrdDescriptor{
			Name:    crd,
			Visited: false,
			CrdType: crdType,
		}
	}

	return crds
}

func main() {
	strict := flag.Bool("strict", false, "Toggle strict mode to check for missing types")
	flag.Parse()

	crds := loadCRDs()
	interfacesImpl, interfacesMap := loadInterfacesData(crds)

	packages := readDescriptors()

	enumMap := map[string]string{
		"istio.authentication.v1alpha1.PrincipalBinding":                                  "me.snowdrop.istio.api.authentication.v1alpha1.PrincipalBinding",
		"istio.authentication.v1alpha1.MutualTls_Mode":                                    "me.snowdrop.istio.api.authentication.v1alpha1.Mode",
		"istio.mesh.v1alpha1.MeshConfig_AccessLogEncoding":                                "me.snowdrop.istio.api.mesh.v1alpha1.AccessLogEncoding",
		"istio.mesh.v1alpha1.MeshConfig_IngressControllerMode":                            "me.snowdrop.istio.api.mesh.v1alpha1.IngressControllerMode",
		"istio.mesh.v1alpha1.MeshConfig_AuthPolicy":                                       "me.snowdrop.istio.api.mesh.v1alpha1.AuthenticationPolicy",
		"istio.mesh.v1alpha1.MeshConfig_H2UpgradePolicy":                                  "me.snowdrop.istio.api.mesh.v1alpha1.H2UpgradePolicy",
		"istio.mesh.v1alpha1.AuthenticationPolicy":                                        "me.snowdrop.istio.api.mesh.v1alpha1.AuthenticationPolicy",
		"istio.mesh.v1alpha1.ProxyConfig_InboundInterceptionMode":                         "me.snowdrop.istio.api.mesh.v1alpha1.InboundInterceptionMode",
		"istio.mesh.v1alpha1.MeshConfig_OutboundTrafficPolicy_Mode":                       "me.snowdrop.istio.api.mesh.v1alpha1.Mode",
		"istio.mesh.v1alpha1.Resource":                                                    "me.snowdrop.istio.api.mesh.v1alpha1.Resource",
		"istio.mixer.v1.HeaderOperation_Operation":                                        "me.snowdrop.istio.api.mixer.v1.Operation",
		"istio.mixer.v1.ReferencedAttributes_Condition":                                   "me.snowdrop.istio.api.mixer.v1.Condition",
		"istio.mixer.v1.ReportRequest_RepeatedAttributesSemantics":                        "me.snowdrop.istio.api.mixer.v1.RepeatedAttributesSemantics",
		"istio.mixer.v1.config.descriptor.ValueType":                                      "me.snowdrop.istio.api.mixer.v1.config.descriptor.ValueType",
		"istio.networking.v1alpha3.EnvoyFilter_RouteConfigurationMatch_RouteMatch_Action": "me.snowdrop.istio.api.networking.v1alpha3.Action",
		"istio.networking.v1alpha3.ClientTLSSettings_TLSmode":                             "me.snowdrop.istio.api.networking.v1alpha3.ClientTLSSettingsMode",
		"istio.networking.v1alpha3.EnvoyFilter_DeprecatedListenerMatch_ListenerType":      "me.snowdrop.istio.api.networking.v1alpha3.ListenerType",
		"istio.networking.v1alpha3.EnvoyFilter_DeprecatedListenerMatch_ListenerProtocol":  "me.snowdrop.istio.api.networking.v1alpha3.ListenerProtocol",
		"istio.networking.v1alpha3.EnvoyFilter_InsertPosition_Index":                      "me.snowdrop.istio.api.networking.v1alpha3.Index",
		"istio.networking.v1alpha3.EnvoyFilter_Filter_FilterType":                         "me.snowdrop.istio.api.networking.v1alpha3.FilterType",
		"istio.networking.v1alpha3.EnvoyFilter_ApplyTo":                                   "me.snowdrop.istio.api.networking.v1alpha3.ApplyTo",
		"istio.networking.v1alpha3.EnvoyFilter_PatchContext":                              "me.snowdrop.istio.api.networking.v1alpha3.PatchContext",
		"istio.networking.v1alpha3.EnvoyFilter_Patch_Operation":                           "me.snowdrop.istio.api.networking.v1alpha3.Operation",
		"istio.networking.v1alpha3.CaptureMode":                                           "me.snowdrop.istio.api.networking.v1alpha3.CaptureMode",
		"istio.networking.v1alpha3.ServerTLSSettings_TLSmode":                             "me.snowdrop.istio.api.networking.v1alpha3.ServerTLSSettingsMode",
		"istio.networking.v1alpha3.ServerTLSSettings_TLSProtocol":                         "me.snowdrop.istio.api.networking.v1alpha3.ServerTLSSettingsProtocol",
		"istio.networking.v1alpha3.ConnectionPoolSettings_HTTPSettings_H2UpgradePolicy":   "me.snowdrop.istio.api.networking.v1alpha3.H2UpgradePolicy",
		"istio.networking.v1alpha3.LoadBalancerSettings_SimpleLB":                         "me.snowdrop.istio.api.networking.v1alpha3.SimpleLB",
		"istio.networking.v1alpha3.ServiceEntry_Location":                                 "me.snowdrop.istio.api.networking.v1alpha3.ServiceEntryLocation",
		"istio.networking.v1alpha3.ServiceEntry_Resolution":                               "me.snowdrop.istio.api.networking.v1alpha3.ServiceEntryResolution",
		"istio.networking.v1alpha3.OutboundTrafficPolicy_Mode":                            "me.snowdrop.istio.api.networking.v1alpha3.OutboundTrafficPolicyMode",
		"istio.networking.v1beta1.CaptureMode":                                            "me.snowdrop.istio.api.networking.v1beta1.CaptureMode",
		"istio.networking.v1beta1.ServerTLSSettings_TLSmode":                              "me.snowdrop.istio.api.networking.v1beta1.ServerTLSSettingsMode",
		"istio.networking.v1beta1.ServerTLSSettings_TLSProtocol":                          "me.snowdrop.istio.api.networking.v1beta1.ServerTLSSettingsProtocol",
		"istio.networking.v1beta1.ClientTLSSettings_TLSmode":                              "me.snowdrop.istio.api.networking.v1beta1.ClientTLSSettingsMode",
		"istio.networking.v1beta1.ConnectionPoolSettings_HTTPSettings_H2UpgradePolicy":    "me.snowdrop.istio.api.networking.v1beta1.H2UpgradePolicy",
		"istio.networking.v1beta1.LoadBalancerSettings_SimpleLB":                          "me.snowdrop.istio.api.networking.v1beta1.SimpleLB",
		"istio.networking.v1beta1.ServiceEntry_Location":                                  "me.snowdrop.istio.api.networking.v1beta1.ServiceEntryLocation",
		"istio.networking.v1beta1.ServiceEntry_Resolution":                                "me.snowdrop.istio.api.networking.v1beta1.ServiceEntryResolution",
		"istio.networking.v1beta1.OutboundTrafficPolicy_Mode":                             "me.snowdrop.istio.api.networking.v1beta1.OutboundTrafficPolicyMode",
		"istio.policy.v1beta1.Rule_HeaderOperationTemplate_Operation":                     "me.snowdrop.istio.api.policy.v1beta1.Operation",
		"istio.policy.v1beta1.FractionalPercent_DenominatorType":                          "me.snowdrop.istio.api.policy.v1beta1.DenominatorType",
		"istio.rbac.v1alpha1.EnforcementMode":                                             "me.snowdrop.istio.api.rbac.v1alpha1.EnforcementMode",
		"istio.rbac.v1alpha1.RbacConfig_Mode":                                             "me.snowdrop.istio.api.rbac.v1alpha1.Mode",
		"istio.security.v1beta1.AuthorizationPolicy_Action":                               "me.snowdrop.istio.api.security.v1beta1.Action",
		"adapter.circonus.config.Params_MetricInfo_Type":                                  "me.snowdrop.istio.mixer.adapter.circonus.Type",
		"adapter.cloudwatch.config.Params_MetricDatum_Unit":                               "me.snowdrop.istio.mixer.adapter.cloudwatch.Unit",
		"adapter.prometheus.config.Params_MetricInfo_Kind":                                "me.snowdrop.istio.mixer.adapter.prometheus.Kind",
		"adapter.dogstatsd.config.Params_MetricInfo_Type":                                 "me.snowdrop.istio.mixer.adapter.dogstatsd.Type",
		"adapter.list.config.Params_ListEntryType":                                        "me.snowdrop.istio.mixer.adapter.list.ListEntryType",
		"adapter.redisquota.config.Params_QuotaAlgorithm":                                 "me.snowdrop.istio.mixer.adapter.redisquota.QuotaAlgorithm",
		"adapter.signalfx.config.Params_MetricConfig_Type":                                "me.snowdrop.istio.mixer.adapter.signalfx.Type",
		"adapter.statsd.config.Params_MetricInfo_Type":                                    "me.snowdrop.istio.mixer.adapter.statsd.Type",
		"adapter.stdio.config.Params_Stream":                                              "me.snowdrop.istio.mixer.adapter.stdio.Stream",
		"adapter.stdio.config.Params_Level":                                               "me.snowdrop.istio.mixer.adapter.stdio.Level",
		"google.api.MetricDescriptor_MetricKind":                                          "me.snowdrop.istio.mixer.adapter.stackdriver.MetricKind",
		"google.api.MetricDescriptor_ValueType":                                           "me.snowdrop.istio.mixer.adapter.stackdriver.ValueType",
		"google.protobuf.NullValue":                                                       "me.snowdrop.istio.api.NullValue",
	}

	schema, err := schemagen.GenerateSchema(reflect.TypeOf(Schema{}), packages, enumMap, interfacesMap, interfacesImpl, crds, *strict)
	if err != nil {
		fmt.Fprint(os.Stderr, err)
		fmt.Fprintln(os.Stderr, "\n")
		os.Exit(1)
	}

	b, err := json.Marshal(&schema)
	if err != nil {
		log.Fatal(err)
	}
	result := string(b)
	result = strings.Replace(result, "\"additionalProperty\":", "\"additionalProperties\":", -1)
	var out bytes.Buffer
	err = json.Indent(&out, []byte(result), "", "  ")
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(out.String())
}
