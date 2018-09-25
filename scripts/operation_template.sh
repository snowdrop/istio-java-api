#!/bin/sh

#define parameters which are passed in.

PKG=$1
CAT=$2
RESOURCE=$3

#define the template.
cat  << EOF

/**
 * Copyright (C) 2015 Red Hat, Inc.
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
package me.snowdrop.istio.clientv2.operation.$CAT;


import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.HasMetadataOperation;

import okhttp3.OkHttpClient;

import ${PKG}.Doneable${RESOURCE};
import ${PKG}.${RESOURCE};
import ${PKG}.${RESOURCE}List;

import java.util.Map;
import java.util.TreeMap;

public class ${RESOURCE}OperationImpl extends HasMetadataOperation<${RESOURCE}, ${RESOURCE}List, Doneable${RESOURCE}, Resource<${RESOURCE}, Doneable${RESOURCE}>> {

  public ${RESOURCE}OperationImpl(OkHttpClient client, Config config, String namespace) {
    this(client, config, null, namespace, null, true, null, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>());
  }

  public ${RESOURCE}OperationImpl(OkHttpClient client, Config config, String apiVersion, String namespace, String name, Boolean cascading, ${RESOURCE} item, String resourceVersion, Boolean reloadingFromServer, long gracePeriodSeconds, Map<String, String> labels, Map<String, String> labelsNot, Map<String, String[]> labelsIn, Map<String, String[]> labelsNotIn, Map<String, String> fields) {
    super(client, config, null, apiVersion, "${RESOURCE}s", namespace, name, cascading, item, resourceVersion, reloadingFromServer, gracePeriodSeconds, labels, labelsNot, labelsIn, labelsNotIn, fields);
  }
}
EOF
