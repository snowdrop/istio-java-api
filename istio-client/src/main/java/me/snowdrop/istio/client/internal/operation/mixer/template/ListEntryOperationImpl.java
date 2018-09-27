
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
package me.snowdrop.istio.client.internal.operation.mixer.template;


import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.HasMetadataOperation;

import okhttp3.OkHttpClient;

import me.snowdrop.istio.mixer.template.listentry.DoneableListEntry;
import me.snowdrop.istio.mixer.template.listentry.ListEntry;
import me.snowdrop.istio.mixer.template.listentry.ListEntryList;

import java.util.Map;
import java.util.TreeMap;

public class ListEntryOperationImpl extends HasMetadataOperation<ListEntry, ListEntryList, DoneableListEntry, Resource<ListEntry, DoneableListEntry>> {

  public ListEntryOperationImpl(OkHttpClient client, Config config, String namespace) {
    this(client, config, null, namespace, null, true, null, null, false, -1, new TreeMap<String, String>(), new TreeMap<String, String>(), new TreeMap<String, String[]>(), new TreeMap<String, String[]>(), new TreeMap<String, String>());
  }

  public ListEntryOperationImpl(OkHttpClient client, Config config, String apiVersion, String namespace, String name, Boolean cascading, ListEntry item, String resourceVersion, Boolean reloadingFromServer, long gracePeriodSeconds, Map<String, String> labels, Map<String, String> labelsNot, Map<String, String[]> labelsIn, Map<String, String[]> labelsNotIn, Map<String, String> fields) {
    super(client, config, null, apiVersion, "ListEntrys", namespace, name, cascading, item, resourceVersion, reloadingFromServer, gracePeriodSeconds, labels, labelsNot, labelsIn, labelsNotIn, fields);
  }
}
