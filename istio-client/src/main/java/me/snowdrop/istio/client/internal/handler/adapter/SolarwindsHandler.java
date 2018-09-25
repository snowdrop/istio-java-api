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

import me.snowdrop.istio.adapter.solarwinds.Solarwinds;
import me.snowdrop.istio.adapter.solarwinds.SolarwindsBuilder;

import me.snowdrop.istio.client.internal.operation.adapter.SolarwindsOperationImpl;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class SolarwindsHandler implements ResourceHandler<Solarwinds, SolarwindsBuilder> {
  @Override
  public String getKind() {
    return Solarwinds.class.getSimpleName();
  }

  @Override
  public Solarwinds create(OkHttpClient client, Config config, String namespace, Solarwinds item) {
    return new SolarwindsOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).create();
  }

  @Override
  public Solarwinds replace(OkHttpClient client, Config config, String namespace, Solarwinds item) {
    return new SolarwindsOperationImpl(client, config, null, namespace, null, true, item, null, true, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).replace(item);
  }

  @Override
  public Solarwinds reload(OkHttpClient client, Config config, String namespace, Solarwinds item) {
    return new SolarwindsOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).fromServer().get();
  }

  @Override
  public SolarwindsBuilder edit(Solarwinds item) {
    return new SolarwindsBuilder(item);
  }

  @Override
  public Boolean delete(OkHttpClient client, Config config, String namespace, Solarwinds item) {
    return new SolarwindsOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).delete(item);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, Solarwinds item, Watcher<Solarwinds> watcher) {
    return new SolarwindsOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(watcher);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, Solarwinds item, String resourceVersion, Watcher<Solarwinds> watcher) {
    return new SolarwindsOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(resourceVersion, watcher);
  }

  @Override
  public Solarwinds waitUntilReady(OkHttpClient client, Config config, String namespace, Solarwinds item, long amount, TimeUnit timeUnit) throws InterruptedException {
    return new SolarwindsOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).waitUntilReady(amount, timeUnit);
  }
}
