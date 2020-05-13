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

PWD=$(shell pwd)
SCHEMA_DIR=$(PWD)/istio-model/src/main/resources/schema
DECL_DIR=$(PWD)/istio-common/src/main/resources
CRD_FILE=$(DECL_DIR)/istio-crd.properties

all: build

strict: metadata
	go run ./cmd/generate/generate.go -strict > $(SCHEMA_DIR)/istio-schema.json
	ISTIO_STRICT=true ./mvnw clean install -e

clean:
	./mvnw clean

metadata:
	./scripts/generate_metadata.sh "${version}"

schema:
	go run ./cmd/generate/generate.go > $(SCHEMA_DIR)/istio-schema.json

build: schema
	./mvnw clean install -e

istio_version:
	./scripts/generate_metadata.sh version