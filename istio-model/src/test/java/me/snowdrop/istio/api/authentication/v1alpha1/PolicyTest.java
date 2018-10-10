package me.snowdrop.istio.api.authentication.v1alpha1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;
import me.snowdrop.istio.api.networking.v1alpha3.NamePort;
import me.snowdrop.istio.api.networking.v1alpha3.PortSelector.Port;
import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

public class PolicyTest extends BaseIstioTest {

  /*
   * This test is used to verify the deserialization of a list of
   * objects that contains interfaces (in this case PortSelector.port)
   */
  @Test
  public void loadingFromYAMLShouldWork() throws Exception {
    final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("policy.yaml");

    final Policy policy = mapper.readValue(inputStream, Policy.class);

    final List<TargetSelector> targets = policy.getSpec().getTargets();
    assertEquals(1, targets.size());
    final TargetSelector target = targets.get(0);
    final List<PortSelector> portSelectors = target.getPorts();
    assertEquals(1, portSelectors.size());
    final PortSelector portSelector = portSelectors.get(0);
    final Port port = portSelector.getPort();
    assertTrue(NamePort.class.isAssignableFrom(port.getClass()));
    final NamePort namePort = (NamePort) port;
    assertEquals("https", namePort.getName());
  }

}
