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
package me.snowdrop.istio.client.internal.handler.$CAT;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ResourceHandler;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;

import okhttp3.OkHttpClient;

import ${PKG}.${RESOURCE};
import ${PKG}.${RESOURCE}Builder;

import me.snowdrop.istio.client.internal.operation.${CAT}.${RESOURCE}OperationImpl;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ${RESOURCE}Handler implements ResourceHandler<${RESOURCE}, ${RESOURCE}Builder> {
  @Override
  public String getKind() {
    return ${RESOURCE}.class.getSimpleName();
  }

  @Override
  public ${RESOURCE} create(OkHttpClient client, Config config, String namespace, ${RESOURCE} item) {
    return new ${RESOURCE}OperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).create();
  }

  @Override
  public ${RESOURCE} replace(OkHttpClient client, Config config, String namespace, ${RESOURCE} item) {
    return new ${RESOURCE}OperationImpl(client, config, null, namespace, null, true, item, null, true, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).replace(item);
  }

  @Override
  public ${RESOURCE} reload(OkHttpClient client, Config config, String namespace, ${RESOURCE} item) {
    return new ${RESOURCE}OperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).fromServer().get();
  }

  @Override
  public ${RESOURCE}Builder edit(${RESOURCE} item) {
    return new ${RESOURCE}Builder(item);
  }

  @Override
  public Boolean delete(OkHttpClient client, Config config, String namespace, ${RESOURCE} item) {
    return new ${RESOURCE}OperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).delete(item);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, ${RESOURCE} item, Watcher<${RESOURCE}> watcher) {
    return new ${RESOURCE}OperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(watcher);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, ${RESOURCE} item, String resourceVersion, Watcher<${RESOURCE}> watcher) {
    return new ${RESOURCE}OperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(resourceVersion, watcher);
  }

  @Override
  public ${RESOURCE} waitUntilReady(OkHttpClient client, Config config, String namespace, ${RESOURCE} item, long amount, TimeUnit timeUnit) throws InterruptedException {
    return new ${RESOURCE}OperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).waitUntilReady(amount, timeUnit);
  }
}
EOF