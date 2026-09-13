package us.dot.its.jpo.ode.udp.spat;

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
    properties = {"ode.receivers.spat.receiver-port=15356"})
@ContextConfiguration(classes = {UDPReceiverProperties.class, KafkaProperties.class})
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpatReceiverTest {

  private static final String BASE = "src/test/resources/us/dot/its/jpo/ode/udp/spat/";

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  @MockBean
  FfmlibDecodeService decodeService;

  EmbeddedKafkaBroker embeddedKafka = EmbeddedKafkaHolder.getEmbeddedKafka();

  private SpatReceiver spatReceiver;
  private ExecutorService executorService;

  @BeforeAll
  void startReceiver() {
    spatReceiver = new SpatReceiver(udpReceiverProperties.getSpat(), decodeService);
    executorService = Executors.newCachedThreadPool();
    executorService.submit(spatReceiver);
  }

  @AfterAll
  void cleanup() {
    spatReceiver.setStopped(true);
    executorService.shutdown();
  }

  @Test
  void testRawJ2735() throws Exception {
    String content = Files.readString(Paths.get(BASE + "SpatReceiverTest_ValidSPAT.txt"));
    new TestUDPClient(udpReceiverProperties.getSpat().getReceiverPort()).send(content);
    verify(decodeService, timeout(5000)).decode(any(DatagramPacket.class), eq(SupportedMessageType.SPAT));
  }

  @Test
  void testWithSignature() throws Exception {
    String content = Files.readString(Paths.get(BASE + "SpatReceiverTest_ValidSPAT_WithSignature.txt"));
    new TestUDPClient(udpReceiverProperties.getSpat().getReceiverPort()).send(content);
    verify(decodeService, timeout(5000)).decode(any(DatagramPacket.class), eq(SupportedMessageType.SPAT));
  }
}
