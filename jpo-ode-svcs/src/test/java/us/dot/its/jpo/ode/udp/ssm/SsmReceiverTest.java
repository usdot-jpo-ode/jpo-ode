package us.dot.its.jpo.ode.udp.ssm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.TestMetricsConfig;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;
import us.dot.its.jpo.ode.test.utilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@EnableConfigurationProperties
@SpringBootTest(
    classes = {OdeKafkaProperties.class, UDPReceiverProperties.class, TestMetricsConfig.class},
    properties = {"ode.receivers.ssm.receiver-port=15358"})
@ContextConfiguration(classes = {UDPReceiverProperties.class, KafkaProperties.class})
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SsmReceiverTest {

  private static final String BASE = "src/test/resources/us/dot/its/jpo/ode/udp/ssm/";

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @MockBean
  FfmlibDecodeService decodeService;

  EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  private SsmReceiver ssmReceiver;
  private ExecutorService executorService;

  @BeforeAll
  void startReceiver() {
    ssmReceiver = new SsmReceiver(udpReceiverProperties.getSsm(), decodeService);
    executorService = Executors.newCachedThreadPool();
    executorService.submit(ssmReceiver);
  }

  @AfterAll
  void cleanup() {
    ssmReceiver.setStopped(true);
    executorService.shutdown();
  }

  @Test
  void testRawJ2735() throws Exception {
    String content = Files.readString(Paths.get(BASE + "SsmReceiverTest_ValidSSM.txt"));
    new TestUDPClient(udpReceiverProperties.getSsm().getReceiverPort()).send(content);
    verify(decodeService, timeout(5000)).decode(any(DatagramPacket.class), eq(SupportedMessageType.SSM));
  }

  @Test
  void testWithSignature() throws Exception {
    String content = Files.readString(Paths.get(BASE + "SsmReceiverTest_ValidSSM_WithSignature.txt"));
    new TestUDPClient(udpReceiverProperties.getSsm().getReceiverPort()).send(content);
    verify(decodeService, timeout(5000)).decode(any(DatagramPacket.class), eq(SupportedMessageType.SSM));
  }
}
