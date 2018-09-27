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
package me.snowdrop.istio.client.internal.handler.mixer.template;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ResourceHandler;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;

import okhttp3.OkHttpClient;

import me.snowdrop.istio.mixer.template.checknothing.CheckNothing;
import me.snowdrop.istio.mixer.template.checknothing.CheckNothingBuilder;

import me.snowdrop.istio.client.internal.operation.mixer.template.CheckNothingOperationImpl;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class CheckNothingHandler implements ResourceHandler<CheckNothing, CheckNothingBuilder> {
  @Override
  public String getKind() {
    return CheckNothing.class.getSimpleName();
  }

  @Override
  public CheckNothing create(OkHttpClient client, Config config, String namespace, CheckNothing item) {
    return new CheckNothingOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).create();
  }

  @Override
  public CheckNothing replace(OkHttpClient client, Config config, String namespace, CheckNothing item) {
    return new CheckNothingOperationImpl(client, config, null, namespace, null, true, item, null, true, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).replace(item);
  }

  @Override
  public CheckNothing reload(OkHttpClient client, Config config, String namespace, CheckNothing item) {
    return new CheckNothingOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).fromServer().get();
  }

  @Override
  public CheckNothingBuilder edit(CheckNothing item) {
    return new CheckNothingBuilder(item);
  }

  @Override
  public Boolean delete(OkHttpClient client, Config config, String namespace, CheckNothing item) {
    return new CheckNothingOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).delete(item);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, CheckNothing item, Watcher<CheckNothing> watcher) {
    return new CheckNothingOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(watcher);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, CheckNothing item, String resourceVersion, Watcher<CheckNothing> watcher) {
    return new CheckNothingOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(resourceVersion, watcher);
  }

  @Override
  public CheckNothing waitUntilReady(OkHttpClient client, Config config, String namespace, CheckNothing item, long amount, TimeUnit timeUnit) throws InterruptedException {
    return new CheckNothingOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).waitUntilReady(amount, timeUnit);
  }
}
