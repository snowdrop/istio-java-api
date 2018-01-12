/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.mixer.template;

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
