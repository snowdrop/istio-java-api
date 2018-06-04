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
	"fmt"
	"log"
	"os"
	"reflect"
	"strings"
	"time"
	mesh "istio.io/api/mesh/v1alpha1"
	mixer "istio.io/api/mixer/v1"
	rbac "istio.io/api/rbac/v1alpha1"
	routing "istio.io/api/routing/v1alpha1"
	networking "istio.io/api/networking/v1alpha3"

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

	"github.com/snowdrop/istio-java-api/pkg/schemagen"
	"bufio"
)

type Schema struct {
	MeshConfig        mesh.MeshConfig
	ProxyConfig       mesh.ProxyConfig
	Attributes           mixer.Attributes
	AttributeValue       mixer.Attributes_AttributeValue
	CheckRequest         mixer.CheckRequest
	QuotaParams          mixer.CheckRequest_QuotaParams
	CheckResponse        mixer.CheckResponse
	QuotaResult          mixer.CheckResponse_QuotaResult
	CompressedAttributes mixer.CompressedAttributes
	ReferencedAttributes mixer.ReferencedAttributes
	ReportRequest        mixer.ReportRequest
	ReportResponse       mixer.ReportResponse
	StringMap            mixer.StringMap
	ServiceRole          rbac.ServiceRole
	ServiceRoleBinding   rbac.ServiceRoleBinding
	CircuitBreaker       routing.CircuitBreaker
	CorsPolicy           routing.CorsPolicy
	DestinationPolicy    routing.DestinationPolicy
	DestinationWeight    routing.DestinationWeight
	EgressRule           routing.EgressRule
	HTTPFaultInjection   routing.HTTPFaultInjection
	HTTPRedirect         routing.HTTPRedirect
	HTTPRetry            routing.HTTPRetry
	HTTPRewrite          routing.HTTPRewrite
	HTTPTimeout          routing.HTTPTimeout
	IngressRule       routing.IngressRule
	IstioService      routing.IstioService
	L4FaultInjection  routing.L4FaultInjection
	L4MatchAttributes routing.L4MatchAttributes
	LoadBalancing     routing.LoadBalancing
	MatchCondition    routing.MatchCondition
	MatchRequest      routing.MatchRequest
	RouteRule         routing.RouteRule
	StringMatch       routing.StringMatch
	Gateway           networking.Gateway
	DestinationRule   networking.DestinationRule
	ServiceEntry      networking.ServiceEntry
	VirtualService    networking.VirtualService
	Circonus          circonus.Params
	Denier            denier.Params
	Dogstatsd         dogstatsd.Params
	DSMetricInfo      dogstatsd.Params_MetricInfo
	Fluentd           fluentd.Params
	KubernetesEnv     kubernetesenv.Params
	ListChecker       list.Params
	//MemQuota             memquota.Params
	OPA        opa.Params
	Prometheus prometheus.Params
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
	ListEntry     listentry.InstanceMsg
	LogEntry      logentry.InstanceMsg
	Metric        metric.InstanceMsg
	Quota         quota.InstanceMsg
	ReportNothing reportnothing.InstanceMsg
	TraceSpan     tracespan.InstanceMsg
}

// code adapted from https://kgrz.io/reading-files-in-go-an-overview.html#scanning-comma-seperated-string
func readDescriptors() []schemagen.PackageDescriptor {
	var descriptors = make([]schemagen.PackageDescriptor, 20)

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

func main() {
	packages := readDescriptors()

	typeMap := map[reflect.Type]reflect.Type{
		reflect.TypeOf(time.Time{}): reflect.TypeOf(""),
		reflect.TypeOf(struct{}{}):  reflect.TypeOf(""),
	}

	enumMap := map[string]string{
		"istio.mesh.v1alpha1.MeshConfig_IngressControllerMode":      "me.snowdrop.istio.api.model.v1.mesh.IngressControllerMode",
		"istio.mesh.v1alpha1.MeshConfig_AuthPolicy":                 "me.snowdrop.istio.api.model.v1.mesh.AuthenticationPolicy",
		"istio.mesh.v1alpha1.AuthenticationPolicy":                  "me.snowdrop.istio.api.model.v1.mesh.AuthenticationPolicy",
		"istio.mesh.v1alpha1.ProxyConfig_InboundInterceptionMode":   "me.snowdrop.istio.api.model.v1.mesh.InboundInterceptionMode",
		"istio.mesh.v1alpha1.MeshConfig_OutboundTrafficPolicy_Mode": "me.snowdrop.istio.api.model.v1.mesh.Mode",
		"istio.mixer.v1.ReferencedAttributes_Condition":             "me.snowdrop.istio.api.model.v1.mixer.Condition",
		"istio.mixer.v1.config.descriptor.ValueType":                "me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType",
		"adapter.circonus.config.Params_MetricInfo_Type":            "me.snowdrop.istio.adapter.circonus.Type",
		"adapter.prometheus.config.Params_MetricInfo_Kind":          "me.snowdrop.istio.adapter.prometheus.Kind",
		"adapter.dogstatsd.config.Params_MetricInfo_Type":           "me.snowdrop.istio.adapter.dogstatsd.Type",
		"adapter.list.config.Params_ListEntryType":                  "me.snowdrop.istio.adapter.list.ListEntryType",
		"istio.networking.v1alpha3.Server_TLSOptions_TLSmode":       "me.snowdrop.istio.api.model.v1.networking.TLSmode",
		"istio.networking.v1alpha3.TLSSettings_TLSmode":             "me.snowdrop.istio.api.model.v1.networking.TLSmode",
		"istio.networking.v1alpha3.ServiceEntry_Location":           "me.snowdrop.istio.api.model.v1.networking.ServiceEntryLocation",
		"istio.networking.v1alpha3.ServiceEntry_Resolution":         "me.snowdrop.istio.api.model.v1.networking.ServiceEntryResolution",
	}

	schema, err := schemagen.GenerateSchema(reflect.TypeOf(Schema{}), packages, typeMap, enumMap)
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
