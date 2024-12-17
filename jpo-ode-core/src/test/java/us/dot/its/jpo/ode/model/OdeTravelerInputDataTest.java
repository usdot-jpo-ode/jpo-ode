package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.plugin.RoadSideUnit;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.SnmpProtocol;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.FrameType;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.MutcdCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class OdeTravelerInputDataTest {

  /**
   * Test method for converting pre-J2735-2016 ASN.1 to J2735-2024 ASN.1
   * Prior to J2735 2016, the following fields had different names:
   * - 'doNotUse1' was 'sspTimRights'
   * - 'doNotUse2' was 'sspLocationRights'
   * - 'doNotUse3' was 'sspMsgContent'
   * - 'doNotUse4' was 'sspMsgTypes'
   * - 'durationTime' was 'duratonTime'
   */
  @Test
  void testConvertPreJ2735_2016ToJ2735_2024() throws IOException {
    // prepare
    String timRequestPreJ2735_2016 = new String(Files.readAllBytes(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/model/timRequest_pre-J2735-2016.json")));
    ObjectMapper mapper = new ObjectMapper();
    OdeTravelerInputData expectedTID = getExpectedDeserializedObject();

    // execute
    val deserializedTID = mapper.readValue(timRequestPreJ2735_2016, OdeTravelerInputData.class);

    // verify (compare inputTID to an expected OdeTravelerInputData object)
    Assertions.assertEquals(expectedTID, deserializedTID);
  }

  /**
   * Test method for converting J2735-2016 ASN.1 to J2735-2024 ASN.1
   * In J2735 2016, the following fields had different names:
   * - 'doNotUse1' was 'sspTimRights'
   * - 'doNotUse2' was 'sspLocationRights'
   * - 'doNotUse3' was 'sspMsgRights1'
   * - 'doNotUse4' was 'sspMsgRights2'
   * - 'durationTime' was 'duratonTime'
   */
  @Test
  void testConvertJ2735_2016ToJ2735_2024() throws IOException {
    // prepare
    String timRequestJ2735_2016 = new String(Files.readAllBytes(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/model/timRequest_J2735-2016.json")));
    ObjectMapper mapper = new ObjectMapper();
    OdeTravelerInputData expectedTID = getExpectedDeserializedObject();

    // execute
    val deserializedTID = mapper.readValue(timRequestJ2735_2016, OdeTravelerInputData.class);

    // verify (compare inputTID to an expected OdeTravelerInputData object)
    Assertions.assertEquals(expectedTID, deserializedTID);
  }

  /**
   * Test method for converting J2735-2020 ASN.1 to J2735-2024 ASN.1
   * In J2735 2020, the following fields had different names:
   * - 'doNotUse1' was 'notUsed'
   * - 'doNotUse2' was 'notUsed1'
   * - 'doNotUse3' was 'notUsed2'
   * - 'doNotUse4' was 'notUsed3'
   */
  @Test
  void testConvertJ2735_2020ToJ2735_2024() throws IOException {
    // prepare
    String timRequestJ2735_2020 = new String(Files.readAllBytes(
        Paths.get("src/test/resources/us/dot/its/jpo/ode/model/timRequest_J2735-2020.json")));
    ObjectMapper mapper = new ObjectMapper();
    OdeTravelerInputData expectedTID = getExpectedDeserializedObject();

    // execute
    val deserializedTID = mapper.readValue(timRequestJ2735_2020, OdeTravelerInputData.class);

    // verify (compare inputTID to an expected OdeTravelerInputData object)
    Assertions.assertEquals(expectedTID, deserializedTID);
  }

  private static OdeTravelerInputData getExpectedDeserializedObject() {
    OdeTravelerInputData expected = new OdeTravelerInputData();
    var req = new ServiceRequest();
    var rsu = new RoadSideUnit.RSU();
    rsu.setRsuTarget("127.0.0.2");
    rsu.setRsuUsername("v3user");
    rsu.setRsuPassword("password");
    rsu.setRsuRetries(1);
    rsu.setRsuTimeout(1000);
    rsu.setRsuIndex(10);
    rsu.setSnmpProtocol(SnmpProtocol.valueOf("NTCIP1218"));
    req.setRsus(new RoadSideUnit.RSU[] {rsu});
    var snmp = new SNMP();
    snmp.setRsuid("00000083");
    snmp.setMsgid(31);
    snmp.setMode(1);
    snmp.setChannel(178);
    snmp.setInterval(2);
    snmp.setDeliverystart("2017-06-01T17:47:11-05:00");
    snmp.setDeliverystop("2018-01-01T17:47:11-05:15");
    snmp.setEnable(1);
    snmp.setStatus(4);
    req.setSnmp(snmp);
    expected.setRequest(req);
    var tim = new OdeTravelerInformationMessage();
    tim.setMsgCnt(1);
    tim.setTimeStamp("2017-08-03T22:25:36.297Z");
    tim.setPacketID("EC9C236B0000000000");
    tim.setUrlB("null");
    var dataframes = new OdeTravelerInformationMessage.DataFrame[1];
    var df = new OdeTravelerInformationMessage.DataFrame();
    df.setDoNotUse1((short) 0);
    df.setFrameType(FrameType.TravelerInfoType.valueOf("advisory"));
    var msgId = new OdeTravelerInformationMessage.DataFrame.MsgId();
    var roadSignID = new OdeTravelerInformationMessage.DataFrame.RoadSignID();
    var position = new OdePosition3D();
    position.setLatitude(new BigDecimal("41.678473"));
    position.setLongitude(new BigDecimal("-108.782775"));
    position.setElevation(new BigDecimal("917.1432"));
    roadSignID.setPosition(position);
    roadSignID.setViewAngle("1010101010101010");
    roadSignID.setMutcdCode(MutcdCode.MutcdCodeEnum.valueOf("warning"));
    roadSignID.setCrc("0000");
    msgId.setRoadSignID(roadSignID);
    df.setMsgId(msgId);
    df.setStartDateTime("2017-08-02T22:25:00.000Z");
    df.setDurationTime(1);
    df.setPriority(0);
    df.setDoNotUse2((short) 0);
    var region = new OdeTravelerInformationMessage.DataFrame.Region();
    region.setName("Testing TIM");
    region.setRegulatorID(0);
    region.setSegmentID(33);
    var anchorPosition = new OdePosition3D();
    anchorPosition.setLatitude(new BigDecimal("41.2500807"));
    anchorPosition.setLongitude(new BigDecimal("-111.0093847"));
    anchorPosition.setElevation(new BigDecimal("2020.6969900289998"));
    region.setAnchorPosition(anchorPosition);
    region.setLaneWidth(BigDecimal.valueOf(7));
    region.setDirectionality("3");
    region.setClosedPath(false);
    region.setDirection("0000000000001010");
    region.setDescription("path");
    var path = new OdeTravelerInformationMessage.DataFrame.Region.Path();
    path.setScale(0);
    path.setType("ll");
    var nodes = new OdeTravelerInformationMessage.NodeXY[13];
    nodes[0] = new OdeTravelerInformationMessage.NodeXY();
    nodes[0].setDelta("node-LL");
    nodes[0].setNodeLat(BigDecimal.valueOf(-0.0002048));
    nodes[0].setNodeLong(BigDecimal.valueOf(0.0002047));
    nodes[1] = new OdeTravelerInformationMessage.NodeXY();
    nodes[1].setDelta("node-LL");
    nodes[1].setNodeLat(BigDecimal.valueOf(-0.0008192));
    nodes[1].setNodeLong(BigDecimal.valueOf(0.0008191));
    nodes[2] = new OdeTravelerInformationMessage.NodeXY();
    nodes[2].setDelta("node-LL");
    nodes[2].setNodeLat(BigDecimal.valueOf(-0.0032768));
    nodes[2].setNodeLong(BigDecimal.valueOf(0.0032767));
    nodes[3] = new OdeTravelerInformationMessage.NodeXY();
    nodes[3].setDelta("node-LL");
    nodes[3].setNodeLat(BigDecimal.valueOf(-0.0131072));
    nodes[3].setNodeLong(BigDecimal.valueOf(0.0131071));
    nodes[4] = new OdeTravelerInformationMessage.NodeXY();
    nodes[4].setDelta("node-LL");
    nodes[4].setNodeLat(BigDecimal.valueOf(-0.2097152));
    nodes[4].setNodeLong(BigDecimal.valueOf(0.2097151));
    nodes[5] = new OdeTravelerInformationMessage.NodeXY();
    nodes[5].setDelta("node-LL");
    nodes[5].setNodeLat(BigDecimal.valueOf(-0.8388608));
    nodes[5].setNodeLong(BigDecimal.valueOf(0.8388607));
    nodes[6] = new OdeTravelerInformationMessage.NodeXY();
    nodes[6].setDelta("node-LL1");
    nodes[6].setNodeLat(BigDecimal.valueOf(-0.0002048));
    nodes[6].setNodeLong(BigDecimal.valueOf(0.0002047));
    nodes[7] = new OdeTravelerInformationMessage.NodeXY();
    nodes[7].setDelta("node-LL2");
    nodes[7].setNodeLat(BigDecimal.valueOf(-0.0008192));
    nodes[7].setNodeLong(BigDecimal.valueOf(0.0008191));
    nodes[8] = new OdeTravelerInformationMessage.NodeXY();
    nodes[8].setDelta("node-LL3");
    nodes[8].setNodeLat(BigDecimal.valueOf(-0.0032768));
    nodes[8].setNodeLong(BigDecimal.valueOf(0.0032767));
    nodes[9] = new OdeTravelerInformationMessage.NodeXY();
    nodes[9].setDelta("node-LL4");
    nodes[9].setNodeLat(BigDecimal.valueOf(-0.0131072));
    nodes[9].setNodeLong(BigDecimal.valueOf(0.0131071));
    nodes[10] = new OdeTravelerInformationMessage.NodeXY();
    nodes[10].setDelta("node-LL5");
    nodes[10].setNodeLat(BigDecimal.valueOf(-0.2097152));
    nodes[10].setNodeLong(BigDecimal.valueOf(0.2097151));
    nodes[11] = new OdeTravelerInformationMessage.NodeXY();
    nodes[11].setDelta("node-LL6");
    nodes[11].setNodeLat(BigDecimal.valueOf(-0.8388608));
    nodes[11].setNodeLong(BigDecimal.valueOf(0.8388607));
    nodes[12] = new OdeTravelerInformationMessage.NodeXY();
    nodes[12].setDelta("node-LatLon");
    nodes[12].setNodeLat(BigDecimal.valueOf(41.2500807));
    nodes[12].setNodeLong(BigDecimal.valueOf(-111.0093847));
    path.setNodes(nodes);
    region.setPath(path);
    df.setRegions(new OdeTravelerInformationMessage.DataFrame.Region[] {region});
    df.setDoNotUse3((short) 3);
    df.setDoNotUse4((short) 2);
    df.setContent("Advisory");
    df.setItems(new String[] {"125", "some text", "250", "'98765"});
    df.setUrl("null");
    dataframes[0] = df;
    tim.setDataframes(dataframes);
    expected.setTim(tim);
    return expected;
  }

}