package me.snowdrop.istio.clientv2;

import io.sundr.codegen.annotations.AnnotationSelector;
import io.sundr.codegen.annotations.ResourceSelector;
import io.sundr.transform.annotations.VelocityTransformation;
import io.sundr.transform.annotations.VelocityTransformations;
import me.snowdrop.istio.api.internal.IstioKind;

@VelocityTransformations(
        value = {
                @VelocityTransformation("/resource-handler.vm")
        },
        resources = {
                @ResourceSelector("crd.properties")
        }

)
public class CodgeGen {
}
