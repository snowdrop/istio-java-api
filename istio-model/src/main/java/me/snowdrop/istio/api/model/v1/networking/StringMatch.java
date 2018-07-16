/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package me.snowdrop.istio.api.model.v1.networking;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.snowdrop.istio.api.internal.StringMatchDeserializer;

/**
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@JsonDeserialize(using = StringMatchDeserializer.class)
public interface StringMatch extends Serializable {
}
