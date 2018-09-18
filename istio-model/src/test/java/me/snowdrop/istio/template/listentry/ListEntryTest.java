/*
 * *
 *  * Copyright (C) 2018 Red Hat, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */
package me.snowdrop.istio.template.listentry;

import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class ListEntryTest extends BaseIstioTest {

    @Test
    public void listEntryRoundtripShouldWork() throws Exception {
        /*
        apiVersion: "config.istio.io/v1alpha2"
kind: listentry
metadata:
  name: appversion
  namespace: istio-config-default
spec:
  value: source.labels["version"]
         */
    }
}
