#
# Copyright (C) 2011 Red Hat, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

SHELL := /bin/bash

SCHEMA_DIR=istio-model/src/main/resources/schema
DECL_DIR=istio-common/src/main/resources
ADAPTER_CRDS=$(DECL_DIR)/adapter_crds.properties
TEMPLATE_CRDS=$(DECL_DIR)/template_crds.properties
OTHER_CRDS=$(DECL_DIR)/other_crds.properties
CRD_FILE=$(DECL_DIR)/crd_list.properties

all: build

clean:
	mvn clean

crd:
	oc get crd -o=jsonpath="{range .items[*]}{.spec.names.kind}={.metadata.name}| istio={.metadata.labels.istio}{'\n'}{end}" > $(CRD_FILE)
	grep mixer-adapter $(CRD_FILE) | cut -d'|' -f1 > $(ADAPTER_CRDS)
	grep mixer-instance $(CRD_FILE) | cut -d'|' -f1 > $(TEMPLATE_CRDS)
	grep -v mixer-instance $(CRD_FILE) | grep -v mixer-adapter | cut -d'|' -f1 > $(OTHER_CRDS)
	rm $(CRD_FILE)

schema:
	CGO_ENABLED=0 go build -a ./cmd/generate/generate.go
	./generate > $(SCHEMA_DIR)/istio-schema.json

build: schema
	mvn clean install