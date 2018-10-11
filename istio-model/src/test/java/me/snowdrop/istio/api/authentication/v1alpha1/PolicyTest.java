package me.snowdrop.istio.api.authentication.v1alpha1;

import java.io.InputStream;
import java.util.List;

import me.snowdrop.istio.tests.BaseIstioTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    assertEquals(2, portSelectors.size());

    PortSelector portSelector = portSelectors.get(0);
    PortSelector.Port port = portSelector.getPort();
    assertTrue(NamePort.class.isAssignableFrom(port.getClass()));
    final NamePort namePort = (NamePort) port;
    assertEquals("https", namePort.getName());

    portSelector = portSelectors.get(1);
    port = portSelector.getPort();
    assertTrue(NumberPort.class.isAssignableFrom(port.getClass()));
    final NumberPort numberPort = (NumberPort) port;
    assertEquals(443, (int) numberPort.getNumber());
  }

}
