/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.cexl;

import me.snowdrop.istio.api.mixer.config.descriptor.ValueType;
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

    @Test
    public void expressionsShouldReturnProperType() {
        assertThat(TypedValue.from("request.size| 200").getType()).isEqualTo(ValueType.INT64);
        assertThat(TypedValue.from("request.size | 0").getType()).isEqualTo(ValueType.INT64);
        assertThat(TypedValue.from("request.headers[\"X-FORWARDED-HOST\"]==\"myhost\"").getType()).isEqualTo(ValueType.BOOL);
        assertThat(TypedValue.from("request.headers[\"X-FORWARDED-HOST\"] == \"myhost\"").getType()).isEqualTo(ValueType.BOOL);
        assertThat(TypedValue.from("(request.headers[\"x-user-group\"] == \"admin\") || (request.auth.principal == \"admin\")").getType()).isEqualTo(ValueType.BOOL);
        assertThat(TypedValue.from("(request.auth.principal | \"nobody\" ) == \"user1\" ").getType()).isEqualTo(ValueType.BOOL);
        assertThat(TypedValue.from("(source.labels[\"app\"]==\"reviews\") && (source.labels[\"version\"]==\"v3\")").getType()).isEqualTo(ValueType.BOOL);
    }
}
