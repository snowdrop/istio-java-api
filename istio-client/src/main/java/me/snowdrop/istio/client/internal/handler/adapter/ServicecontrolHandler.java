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

import me.snowdrop.istio.adapter.servicecontrol.Servicecontrol;
import me.snowdrop.istio.adapter.servicecontrol.ServicecontrolBuilder;

import me.snowdrop.istio.client.internal.operation.adapter.ServicecontrolOperationImpl;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ServicecontrolHandler implements ResourceHandler<Servicecontrol, ServicecontrolBuilder> {
  @Override
  public String getKind() {
    return Servicecontrol.class.getSimpleName();
  }

  @Override
  public Servicecontrol create(OkHttpClient client, Config config, String namespace, Servicecontrol item) {
    return new ServicecontrolOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).create();
  }

  @Override
  public Servicecontrol replace(OkHttpClient client, Config config, String namespace, Servicecontrol item) {
    return new ServicecontrolOperationImpl(client, config, null, namespace, null, true, item, null, true, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).replace(item);
  }

  @Override
  public Servicecontrol reload(OkHttpClient client, Config config, String namespace, Servicecontrol item) {
    return new ServicecontrolOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).fromServer().get();
  }

  @Override
  public ServicecontrolBuilder edit(Servicecontrol item) {
    return new ServicecontrolBuilder(item);
  }

  @Override
  public Boolean delete(OkHttpClient client, Config config, String namespace, Servicecontrol item) {
    return new ServicecontrolOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).delete(item);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, Servicecontrol item, Watcher<Servicecontrol> watcher) {
    return new ServicecontrolOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(watcher);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, Servicecontrol item, String resourceVersion, Watcher<Servicecontrol> watcher) {
    return new ServicecontrolOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(resourceVersion, watcher);
  }

  @Override
  public Servicecontrol waitUntilReady(OkHttpClient client, Config config, String namespace, Servicecontrol item, long amount, TimeUnit timeUnit) throws InterruptedException {
    return new ServicecontrolOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).waitUntilReady(amount, timeUnit);
  }
}
