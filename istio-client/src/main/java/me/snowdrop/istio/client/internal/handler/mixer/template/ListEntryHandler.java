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

import me.snowdrop.istio.mixer.template.listentry.ListEntry;
import me.snowdrop.istio.mixer.template.listentry.ListEntryBuilder;

import me.snowdrop.istio.client.internal.operation.mixer.template.ListEntryOperationImpl;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ListEntryHandler implements ResourceHandler<ListEntry, ListEntryBuilder> {
  @Override
  public String getKind() {
    return ListEntry.class.getSimpleName();
  }

  @Override
  public ListEntry create(OkHttpClient client, Config config, String namespace, ListEntry item) {
    return new ListEntryOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).create();
  }

  @Override
  public ListEntry replace(OkHttpClient client, Config config, String namespace, ListEntry item) {
    return new ListEntryOperationImpl(client, config, null, namespace, null, true, item, null, true, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).replace(item);
  }

  @Override
  public ListEntry reload(OkHttpClient client, Config config, String namespace, ListEntry item) {
    return new ListEntryOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).fromServer().get();
  }

  @Override
  public ListEntryBuilder edit(ListEntry item) {
    return new ListEntryBuilder(item);
  }

  @Override
  public Boolean delete(OkHttpClient client, Config config, String namespace, ListEntry item) {
    return new ListEntryOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).delete(item);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, ListEntry item, Watcher<ListEntry> watcher) {
    return new ListEntryOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(watcher);
  }

  @Override
  public Watch watch(OkHttpClient client, Config config, String namespace, ListEntry item, String resourceVersion, Watcher<ListEntry> watcher) {
    return new ListEntryOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).watch(resourceVersion, watcher);
  }

  @Override
  public ListEntry waitUntilReady(OkHttpClient client, Config config, String namespace, ListEntry item, long amount, TimeUnit timeUnit) throws InterruptedException {
    return new ListEntryOperationImpl(client, config, null, namespace, null, true, item, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>()).waitUntilReady(amount, timeUnit);
  }
}
