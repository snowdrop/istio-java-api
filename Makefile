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

all: build

clean:
	rm -rf istio-model/schema/
	mvn clean

build:
	CGO_ENABLED=0 GO15VENDOREXPERIMENT=1 go build -a ./cmd/generate/generate.go
	mkdir -p istio-model/schema
	./generate > istio-model/schema/istio-schema.json
	mvn clean install