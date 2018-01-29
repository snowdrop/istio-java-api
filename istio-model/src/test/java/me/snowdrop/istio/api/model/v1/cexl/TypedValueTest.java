/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.cexl;

import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
public class TypedValueTest extends BaseIstioTest {
    @Test
    public void definedAttributesShouldHaveValidTypes() {
        // loading AttributeVocabulary validates that specified ValueTypes for attributes do exist
        assertThat(AttributeVocabulary.getKnownAttributes()).isNotEmpty();
    }
}
