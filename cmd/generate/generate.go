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
	"io/ioutil"
	authentication "istio.io/api/authentication/v1alpha1"
	mesh "istio.io/api/mesh/v1alpha1"
	mixer "istio.io/api/mixer/v1"
	networking "istio.io/api/networking/v1alpha3"
	policy "istio.io/api/policy/v1beta1"
	rbac "istio.io/api/rbac/v1alpha1"
	"istio.io/istio/mixer/template/edge"
	"log"
	"os"
	"reflect"
	"strings"
	"time"

	circonus "istio.io/istio/mixer/adapter/circonus/config"
	denier "istio.io/istio/mixer/adapter/denier/config"
	dogstatsd "istio.io/istio/mixer/adapter/dogstatsd/config"
	fluentd "istio.io/istio/mixer/adapter/fluentd/config"
	kubernetesenv "istio.io/istio/mixer/adapter/kubernetesenv/config"
	list "istio.io/istio/mixer/adapter/list/config"
	opa "istio.io/istio/mixer/adapter/opa/config"
	prometheus "istio.io/istio/mixer/adapter/prometheus/config"
	solarwinds "istio.io/istio/mixer/adapter/solarwinds/config"
	"istio.io/istio/mixer/template/apikey"
	"istio.io/istio/mixer/template/authorization"
	"istio.io/istio/mixer/template/checknothing"
	"istio.io/istio/mixer/template/listentry"
	"istio.io/istio/mixer/template/logentry"
	"istio.io/istio/mixer/template/metric"
	"istio.io/istio/mixer/template/quota"
	"istio.io/istio/mixer/template/reportnothing"
	"istio.io/istio/mixer/template/tracespan"

	"bufio"
	"github.com/snowdrop/istio-java-api/pkg/schemagen"
)

type Schema struct {
	MeshConfig                         mesh.MeshConfig
	ProxyConfig                        mesh.ProxyConfig
	Attributes                         mixer.Attributes
	AttributeValue                     mixer.Attributes_AttributeValue
	CheckRequest                       mixer.CheckRequest
	QuotaParams                        mixer.CheckRequest_QuotaParams
	CheckResponse                      mixer.CheckResponse
	QuotaResult                        mixer.CheckResponse_QuotaResult
	CompressedAttributes               mixer.CompressedAttributes
	ReferencedAttributes               mixer.ReferencedAttributes
	ReportRequest                      mixer.ReportRequest
	ReportResponse                     mixer.ReportResponse
	StringMap                          mixer.StringMap
	ServiceRole                        rbac.ServiceRole
	ServiceRoleBinding                 rbac.ServiceRoleBinding
	EnvoyFilter                        networking.EnvoyFilter
	Rule                               policy.Rule
	Policy                             authentication.Policy
	JwtPeerAuthenticationMethod        authentication.PeerAuthenticationMethod_Jwt
	MtlsPeerAuthenticationMethod       authentication.PeerAuthenticationMethod_Mtls
	Gateway                            networking.Gateway
	DestinationRule                    networking.DestinationRule
	SimpleLoadBalancerSettings         networking.LoadBalancerSettings_Simple
	ConsistentHashLoadBalancerSettings networking.LoadBalancerSettings_ConsistentHash
	HttpCookieHashKey                  networking.LoadBalancerSettings_ConsistentHashLB_HttpCookie
	HttpHeaderNameHashKey              networking.LoadBalancerSettings_ConsistentHashLB_HttpHeaderName
	UseSourceIpHashKey                 networking.LoadBalancerSettings_ConsistentHashLB_UseSourceIp
	ExactStringMatch                   networking.StringMatch_Exact
	PrefixStringMatch                  networking.StringMatch_Prefix
	RegexStringMatch                   networking.StringMatch_Regex
	NamePortSelector                   networking.PortSelector_Name
	NumberPortSelector                 networking.PortSelector_Number
	ExponentialDelay                   networking.HTTPFaultInjection_Delay_ExponentialDelay
	FixedDelay                         networking.HTTPFaultInjection_Delay_FixedDelay
	GrpcStatusAbort                    networking.HTTPFaultInjection_Abort_GrpcStatus
	Http2ErrorAbort                    networking.HTTPFaultInjection_Abort_Http2Error
	HttpStatusAbort                    networking.HTTPFaultInjection_Abort_HttpStatus
	ServiceEntry                       networking.ServiceEntry
	VirtualService                     networking.VirtualService
	Circonus                           circonus.Params
	Denier                             denier.Params
	Dogstatsd                          dogstatsd.Params
	DSMetricInfo                       dogstatsd.Params_MetricInfo
	Fluentd                            fluentd.Params
	KubernetesEnv                      kubernetesenv.Params
	ListChecker                        list.Params
	//MemQuota             memquota.Params
	OPA                          opa.Params
	Prometheus                   prometheus.Params
	ExplicitBucketsDefinition    prometheus.Params_MetricInfo_BucketsDefinition_ExplicitBuckets
	LinearBucketsDefinition      prometheus.Params_MetricInfo_BucketsDefinition_LinearBuckets
	ExponentialBucketsDefinition prometheus.Params_MetricInfo_BucketsDefinition_ExponentialBuckets
	//ServiceControl servicecontrol.Params
	SolarWinds   solarwinds.Params
	SWLogInfo    solarwinds.Params_LogInfo
	SWMetricInfo solarwinds.Params_MetricInfo
	//StackDriver	stackdriver.Params
	//Statsd        statsd.Params
	//Stdio         stdio.Params
	APIKey        apikey.InstanceMsg
	Authorization authorization.InstanceMsg
	CheckNothing  checknothing.InstanceMsg
	Edge          edge.InstanceMsg
	ListEntry     listentry.InstanceMsg
	LogEntry      logentry.InstanceMsg
	Metric        metric.InstanceMsg
	Quota         quota.InstanceMsg
	ReportNothing reportnothing.InstanceMsg
	TraceSpan     tracespan.InstanceMsg
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

func loadInterfacesData() map[string]string {
	result := make(map[string]string)

	path := "istio-common/src/main/resources/interfaces-data.yml"
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
		//var interfaceName = className[:strings.LastIndex(className, ".")+1]
		var interfaceName = className + "." // to define inner classes for interface fields
		var interfaceSet = false
		for key, field := range class.Fields {
			if strings.HasPrefix(field, "is") {
				lastUnderscore := strings.LastIndex(field, "_")

				if !interfaceSet {
					interfaceName += field[lastUnderscore+1:]
					interfaceSet = true
				}

				impl := field[2:lastUnderscore+1] + strings.Title(key)
				result[impl] = interfaceName
			}
		}
	}

	return result
}

func main() {
	strict := flag.Bool("strict", false, "Toggle strict mode to check for missing types")
	flag.Parse()

	interfacesImpl := loadInterfacesData()

	packages := readDescriptors()

	typeMap := map[reflect.Type]reflect.Type{
		reflect.TypeOf(time.Time{}): reflect.TypeOf(""),
		reflect.TypeOf(struct{}{}):  reflect.TypeOf(""),
	}

	enumMap := map[string]string{
		"istio.mesh.v1alpha1.MeshConfig_IngressControllerMode":                 "me.snowdrop.istio.api.model.v1.mesh.IngressControllerMode",
		"istio.mesh.v1alpha1.MeshConfig_AuthPolicy":                            "me.snowdrop.istio.api.model.v1.mesh.AuthenticationPolicy",
		"istio.mesh.v1alpha1.AuthenticationPolicy":                             "me.snowdrop.istio.api.model.v1.mesh.AuthenticationPolicy",
		"istio.mesh.v1alpha1.ProxyConfig_InboundInterceptionMode":              "me.snowdrop.istio.api.model.v1.mesh.InboundInterceptionMode",
		"istio.mesh.v1alpha1.MeshConfig_OutboundTrafficPolicy_Mode":            "me.snowdrop.istio.api.model.v1.mesh.Mode",
		"istio.mixer.v1.HeaderOperation_Operation":                             "me.snowdrop.istio.api.model.v1.mixer.Operation",
		"istio.mixer.v1.ReferencedAttributes_Condition":                        "me.snowdrop.istio.api.model.v1.mixer.Condition",
		"istio.mixer.v1.config.descriptor.ValueType":                           "me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType",
		"adapter.circonus.config.Params_MetricInfo_Type":                       "me.snowdrop.istio.adapter.circonus.Type",
		"adapter.prometheus.config.Params_MetricInfo_Kind":                     "me.snowdrop.istio.adapter.prometheus.Kind",
		"adapter.dogstatsd.config.Params_MetricInfo_Type":                      "me.snowdrop.istio.adapter.dogstatsd.Type",
		"adapter.list.config.Params_ListEntryType":                             "me.snowdrop.istio.adapter.list.ListEntryType",
		"istio.networking.v1alpha3.Server_TLSOptions_TLSmode":                  "me.snowdrop.istio.api.model.v1.networking.TLSmode",
		"istio.networking.v1alpha3.TLSSettings_TLSmode":                        "me.snowdrop.istio.api.model.v1.networking.TLSmode",
		"istio.networking.v1alpha3.ServiceEntry_Location":                      "me.snowdrop.istio.api.model.v1.networking.ServiceEntryLocation",
		"istio.networking.v1alpha3.ServiceEntry_Resolution":                    "me.snowdrop.istio.api.model.v1.networking.ServiceEntryResolution",
		"istio.networking.v1alpha3.LoadBalancerSettings_SimpleLB":              "me.snowdrop.istio.api.model.v1.networking.SimpleLB",
		"istio.networking.v1alpha3.EnvoyFilter_ListenerMatch_ListenerType":     "me.snowdrop.istio.api.model.v1.networking.ListenerType",
		"istio.networking.v1alpha3.EnvoyFilter_ListenerMatch_ListenerProtocol": "me.snowdrop.istio.api.model.v1.networking.ListenerProtocol",
		"istio.networking.v1alpha3.EnvoyFilter_InsertPosition_Index":           "me.snowdrop.istio.api.model.v1.networking.Index",
		"istio.networking.v1alpha3.EnvoyFilter_Filter_FilterType":              "me.snowdrop.istio.api.model.v1.networking.FilterType",
		"istio.rbac.v1alpha1.EnforcementMode":                                  "me.snowdrop.istio.api.model.v1.rbac.EnforcementMode",
		"istio.authentication.v1alpha1.PrincipalBinding":                       "me.snowdrop.istio.api.model.v1.authentication.PrincipalBinding",
		"istio.authentication.v1alpha1.MutualTls_Mode":                         "me.snowdrop.istio.api.model.v1.authentication.Mode",
	}

	interfacesMap := map[string]string{
		"isParams_MetricInfo_BucketsDefinition_Definition": "me.snowdrop.istio.adapter.prometheus.BucketsDefinition",
		"isLoadBalancerSettings_LbPolicy":                  "me.snowdrop.istio.api.model.v1.networking.LoadBalancerSettings",
		"isStringMatch_MatchType":                          "me.snowdrop.istio.api.model.v1.networking.StringMatch",
		"isPortSelector_Port":                              "me.snowdrop.istio.api.model.v1.networking.PortSelector",
		"isHTTPFaultInjection_Delay_HttpDelayType":         "me.snowdrop.istio.api.model.v1.networking.DelayType",
		"isHTTPFaultInjection_Abort_ErrorType":             "me.snowdrop.istio.api.model.v1.networking.ErrorType",
		"isLoadBalancerSettings_ConsistentHashLB_HashKey":  "me.snowdrop.istio.api.model.v1.networking.HashKey",
		"isPeerAuthenticationMethod_Params":                "me.snowdrop.istio.api.model.v1.authentication.PeerAuthenticationMethod",
	}

	/*interfacesImpl := map[string]string{
		"Params_MetricInfo_BucketsDefinition_LinearBuckets":      "me.snowdrop.istio.adapter.prometheus.BucketsDefinition",
		"Params_MetricInfo_BucketsDefinition_ExponentialBuckets": "me.snowdrop.istio.adapter.prometheus.BucketsDefinition",
		"Params_MetricInfo_BucketsDefinition_ExplicitBuckets":    "me.snowdrop.istio.adapter.prometheus.BucketsDefinition",
		"LoadBalancerSettings_Simple":                            "me.snowdrop.istio.api.model.v1.networking.Lb",
		"LoadBalancerSettings_ConsistentHash":                    "me.snowdrop.istio.api.model.v1.networking.LoadBalancerSettings",
		"StringMatch_Exact":                                      "me.snowdrop.istio.api.model.v1.networking.StringMatch",
		"StringMatch_Prefix":                                     "me.snowdrop.istio.api.model.v1.networking.StringMatch",
		"StringMatch_Regex":                                      "me.snowdrop.istio.api.model.v1.networking.StringMatch",
		"PortSelector_Name":                                      "me.snowdrop.istio.api.model.v1.networking.PortSelector",
		"PortSelector_Number":                                    "me.snowdrop.istio.api.model.v1.networking.PortSelector",
		"HTTPFaultInjection_Delay_FixedDelay":                    "me.snowdrop.istio.api.model.v1.networking.DelayType",
		"HTTPFaultInjection_Delay_ExponentialDelay":              "me.snowdrop.istio.api.model.v1.networking.DelayType",
		"HTTPFaultInjection_Abort_HttpStatus":                    "me.snowdrop.istio.api.model.v1.networking.ErrorType",
		"HTTPFaultInjection_Abort_GrpcStatus":                    "me.snowdrop.istio.api.model.v1.networking.ErrorType",
		"HTTPFaultInjection_Abort_Http2Error":                    "me.snowdrop.istio.api.model.v1.networking.ErrorType",
		"LoadBalancerSettings_ConsistentHashLB_HttpHeaderName":   "me.snowdrop.istio.api.model.v1.networking.HashKey",
		"LoadBalancerSettings_ConsistentHashLB_HttpCookie":       "me.snowdrop.istio.api.model.v1.networking.HashKey",
		"LoadBalancerSettings_ConsistentHashLB_UseSourceIp":      "me.snowdrop.istio.api.model.v1.networking.HashKey",
		"PeerAuthenticationMethod_Mtls":                          "me.snowdrop.istio.api.model.v1.authentication.PeerAuthenticationMethod",
		"PeerAuthenticationMethod_Jwt":                           "me.snowdrop.istio.api.model.v1.authentication.PeerAuthenticationMethod",
	}*/

	schema, err := schemagen.GenerateSchema(reflect.TypeOf(Schema{}), packages, typeMap, enumMap, interfacesMap, interfacesImpl, *strict)
	if err != nil {
		log.Fatal(err)
		os.Exit(-1)
	}

	args := os.Args[1:]
	if len(args) < 1 || args[0] != "validation" {
		schema.Resources = nil
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
