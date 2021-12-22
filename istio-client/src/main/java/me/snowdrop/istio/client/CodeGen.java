package me.snowdrop.istio.client;

import io.sundr.transform.annotations.ResourceSelector;
import io.sundr.transform.annotations.TemplateTransformation;
import io.sundr.transform.annotations.TemplateTransformations;

@TemplateTransformations(
		value = {
				@TemplateTransformation("/resource-operation.vm"),
//				@TemplateTransformation("/resource-handler.vm"),
				/*@TemplateTransformation(value = "/resource-handler-services.vm", gather = true, outputPath = "META-INF/services/io.fabric8.kubernetes.client.ResourceHandler"),*/
				@TemplateTransformation(value = "/handler-registration.vm", gather = true, outputPath = "me/snowdrop/istio/client/internal/HandlersRegistration.java")
		},
		resources = {
				@ResourceSelector("crd.properties")
		}

)
public class CodeGen {
}
