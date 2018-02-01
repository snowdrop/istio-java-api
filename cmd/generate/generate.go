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
	"reflect"
	"strings"
	"time"
	mesh "istio.io/api/mesh/v1alpha1"
	mixer "istio.io/api/mixer/v1"
	mixer_config "istio.io/api/mixer/v1/config"
	routing "istio.io/api/routing/v1alpha1"

	"../../pkg/schemagen"
	//"os"
	"os"
)

type Schema struct {
	MeshConfig                               mesh.MeshConfig
	ProxyConfig                              mesh.ProxyConfig
	Attributes                               mixer.Attributes
	AttributeValue                           mixer.Attributes_AttributeValue
	CheckRequest                             mixer.CheckRequest
	QuotaParams                              mixer.CheckRequest_QuotaParams
	CheckResponse                            mixer.CheckResponse
	QuotaResult                              mixer.CheckResponse_QuotaResult
	CompressedAttributes                     mixer.CompressedAttributes
	ReferencedAttributes                     mixer.ReferencedAttributes
	ReportRequest                            mixer.ReportRequest
	ReportResponse                           mixer.ReportResponse
	StringMap                                mixer.StringMap
	Action                                   mixer_config.Action
	AttributeManifest                        mixer_config.AttributeManifest
	AttributeInfo                            mixer_config.AttributeManifest_AttributeInfo
	Handler                                  mixer_config.Handler
	Instance                                 mixer_config.Instance
	Rule                                     mixer_config.Rule
	CircuitBreaker                           routing.CircuitBreaker
	CorsPolicy                               routing.CorsPolicy
	DestinationPolicy                        routing.DestinationPolicy
	DestinationWeight                        routing.DestinationWeight
	EgressRule                               routing.EgressRule
	HTTPFaultInjection                       routing.HTTPFaultInjection
	HTTPRedirect                             routing.HTTPRedirect
	HTTPRetry                                routing.HTTPRetry
	HTTPRewrite                              routing.HTTPRewrite
	HTTPTimeout                              routing.HTTPTimeout
	IngressRule                              routing.IngressRule
	IstioService                             routing.IstioService
	L4FaultInjection                         routing.L4FaultInjection
	L4MatchAttributes                        routing.L4MatchAttributes
	LoadBalancing                            routing.LoadBalancing
	MatchCondition                           routing.MatchCondition
	MatchRequest                             routing.MatchRequest
	RouteRule                                routing.RouteRule
	StringMatch                              routing.StringMatch
}

func main() {
	packages := []schemagen.PackageDescriptor{
		{"istio.io/api/broker/v1/config", "me.snowdrop.istio.api.model.v1.broker", "istio_broker_"},
		{"istio.io/api/mesh/v1alpha1", "me.snowdrop.istio.api.model.v1.mesh", "istio_mesh_"},
		{"istio.io/api/mixer/v1", "me.snowdrop.istio.api.model.v1.mixer", "istio_mixer_"},
		{"istio.io/api/mixer/v1/config", "me.snowdrop.istio.api.model.v1.mixer.config", "istio_mixer_config_"},
		{"istio.io/api/routing/v1alpha1", "me.snowdrop.istio.api.model.v1.routing", "istio_routing_"},
		{"github.com/golang/protobuf/ptypes/duration", "me.snowdrop.istio.api.model", "protobuf_duration_"},
		{"github.com/gogo/protobuf/types", "me.snowdrop.istio.api.model", "protobuf_types_"},
		{"github.com/golang/protobuf/ptypes/any", "me.snowdrop.istio.api.model", "protobuf_any_"},
		{"istio.io/gogo-genproto/googleapis/google/rpc", "me.snowdrop.istio.api.model", "google_rpc_"},
	}

	typeMap := map[reflect.Type]reflect.Type{
		reflect.TypeOf(time.Time{}): reflect.TypeOf(""),
		reflect.TypeOf(struct{}{}):  reflect.TypeOf(""),
	}

	enumMap := map[string]string{
		"istio.mesh.v1alpha1.MeshConfig_IngressControllerMode":               "me.snowdrop.istio.api.model.v1.mesh.IngressControllerMode",
		"istio.mesh.v1alpha1.MeshConfig_AuthPolicy":                          "me.snowdrop.istio.api.model.v1.mesh.AuthenticationPolicy",
		"istio.mesh.v1alpha1.AuthenticationPolicy":                           "me.snowdrop.istio.api.model.v1.mesh.AuthenticationPolicy",
		"istio.mixer.v1.ReferencedAttributes_Condition":                     "me.snowdrop.istio.api.model.v1.mixer.Condition",
		"istio.mixer.v1.config.descriptor.ValueType":                        "me.snowdrop.istio.api.model.v1.mixer.config.descriptor.ValueType",
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
