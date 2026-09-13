package us.dot.its.jpo.ode.udp.generic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import us.dot.its.jpo.ode.codec.ffmlib.FfmlibDecodeService;
import us.dot.its.jpo.ode.test.utilities.TestUDPClient;
import us.dot.its.jpo.ode.udp.controller.UDPReceiverProperties;
import us.dot.its.jpo.ode.uper.SupportedMessageType;

@EnableConfigurationProperties
@SpringBootTest(
    classes = {UDPReceiverProperties.class},
    properties = {"ode.receivers.generic.receiver-port=15460"})
@ContextConfiguration(classes = {UDPReceiverProperties.class})
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenericReceiverTest {

  private record MsgFiles(String raw, String withSignature, SupportedMessageType type) {}

  private static final String RES = "src/test/resources/us/dot/its/jpo/ode/udp/";

  private static final List<MsgFiles> MSG_FILES = List.of(
      new MsgFiles(RES + "bsm/BsmReceiverTest_ValidBSM.txt",
          RES + "bsm/BsmReceiverTest_ValidBSM_WithSignature.txt", SupportedMessageType.BSM),
      new MsgFiles(RES + "tim/TimReceiverTest_ValidTIM.txt",
          RES + "tim/TimReceiverTest_ValidTIM_WithSignature.txt", SupportedMessageType.TIM),
      new MsgFiles(RES + "map/MapReceiverTest_ValidMAP.txt",
          RES + "map/MapReceiverTest_ValidMAP_WithSignature.txt", SupportedMessageType.MAP),
      new MsgFiles(RES + "spat/SpatReceiverTest_ValidSPAT.txt",
          RES + "spat/SpatReceiverTest_ValidSPAT_WithSignature.txt", SupportedMessageType.SPAT),
      new MsgFiles(RES + "ssm/SsmReceiverTest_ValidSSM.txt",
          RES + "ssm/SsmReceiverTest_ValidSSM_WithSignature.txt", SupportedMessageType.SSM),
      new MsgFiles(RES + "srm/SrmReceiverTest_ValidData.txt",
          RES + "srm/SrmReceiverTest_ValidData_WithSignature.txt", SupportedMessageType.SRM),
      new MsgFiles(RES + "psm/PsmReceiverTest_ValidPSM.txt",
          RES + "psm/PsmReceiverTest_ValidPSM_WithSignature.txt", SupportedMessageType.PSM),
      new MsgFiles(RES + "sdsm/SdsmReceiverTest_ValidSDSM.txt",
          RES + "sdsm/SdsmReceiverTest_ValidSDSM_WithSignature.txt", SupportedMessageType.SDSM),
      new MsgFiles(RES + "rtcm/RtcmReceiverTest_ValidRTC.txt",
          RES + "rtcm/RtcmReceiverTest_ValidRTC_WithSignature.txt", SupportedMessageType.RTCM),
      new MsgFiles(RES + "rsm/RsmReceiverTest_ValidRSM.txt",
          RES + "rsm/RsmReceiverTest_ValidRSM_WithSignature.txt", SupportedMessageType.RSM)
  );

  @Autowired
  UDPReceiverProperties udpReceiverProperties;

  private FfmlibDecodeService decodeService;
  private GenericReceiver genericReceiver;
  private ExecutorService executorService;

  @BeforeAll
  void startReceiver() {
    decodeService = mock(FfmlibDecodeService.class);
    genericReceiver = new GenericReceiver(udpReceiverProperties.getGeneric(), decodeService);
    executorService = Executors.newCachedThreadPool();
    executorService.submit(genericReceiver);
  }

  @AfterAll
  void cleanup() {
    genericReceiver.setStopped(true);
    executorService.shutdown();
  }

  @Test
  void testRawJ2735() throws Exception {
    TestUDPClient udpClient = new TestUDPClient(udpReceiverProperties.getGeneric().getReceiverPort());
    for (MsgFiles mf : MSG_FILES) {
      clearInvocations(decodeService);
      udpClient.send(Files.readString(Paths.get(mf.raw())));
      verify(decodeService, timeout(5000)).decode(any(DatagramPacket.class), eq(mf.type()));
    }
  }

  @Test
  void testWithSignature() throws Exception {
    TestUDPClient udpClient = new TestUDPClient(udpReceiverProperties.getGeneric().getReceiverPort());
    for (MsgFiles mf : MSG_FILES) {
      clearInvocations(decodeService);
      udpClient.send(Files.readString(Paths.get(mf.withSignature())));
      verify(decodeService, timeout(5000)).decode(any(DatagramPacket.class), eq(mf.type()));
    }
  }
}
