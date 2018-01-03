/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.tests;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class BaseIstioTest {
    protected final YAMLMapper mapper = new YAMLMapper();
}
