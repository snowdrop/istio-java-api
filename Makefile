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

all: build

clean:
	rm -rf $(SCHEMA_DIR)
	mvn clean

schema:
	CGO_ENABLED=0 GO15VENDOREXPERIMENT=1 go build -a ./cmd/generate/generate.go
	mkdir -p $(SCHEMA_DIR)
	./generate > $(SCHEMA_DIR)/istio-schema.json

build: schema
	mvn clean install