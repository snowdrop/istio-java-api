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
package me.snowdrop.istio.client.internal.handler.adapter;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ResourceHandler;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;

import okhttp3.OkHttpClient;

import me.snowdrop.istio.adapter.stackdriver.Stackdriver;
import me.snowdrop.istio.adapter.stackdriver.StackdriverBuilder;

import me.snowdrop.istio.client.internal.operation.adapter.StackdriverOperationImpl;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class StackdriverHandler implements ResourceHandler<Stackdriver, StackdriverBuilder> {
  @Override
  public String getKind() {
    return Stackdriver.class.getSimpleName();
  }

  @Override
  public Stackdriver create(OkHttpClient client, Config config, String namespace, Stackdriver item) {
    return new StackdriverOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).create();
  }

  @Override
  public Stackdriver replace(OkHttpClient client, Config config, String namespace, Stackdriver item) {
    return new StackdriverOperationImpl(client, config, null, namespace, null, true, item, null, true, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).replace(item);
  }

  @Override
  public Stackdriver reload(OkHttpClient client, Config config, String namespace, Stackdriver item) {
    return new StackdriverOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).fromServer().get();
  }

  @Override
  public StackdriverBuilder edit(Stackdriver item) {
    return new StackdriverBuilder(item);
  }

  @Override
  public Boolean delete(OkHttpClient client, Config config, String namespace, Stackdriver item) {
    return new StackdriverOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).delete(item);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, Stackdriver item, Watcher<Stackdriver> watcher) {
    return new StackdriverOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(watcher);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, Stackdriver item, String resourceVersion, Watcher<Stackdriver> watcher) {
    return new StackdriverOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(resourceVersion, watcher);
  }

  @Override
  public Stackdriver waitUntilReady(OkHttpClient client, Config config, String namespace, Stackdriver item, long amount, TimeUnit timeUnit) throws InterruptedException {
    return new StackdriverOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).waitUntilReady(amount, timeUnit);
  }
}
