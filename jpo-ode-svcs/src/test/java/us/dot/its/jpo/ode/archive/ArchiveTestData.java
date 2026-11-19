package us.dot.its.jpo.ode.archive;

import java.io.IOException;
import java.util.List;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.test.utilities.ApprovalTestCase;

/**
 * Packets and topic configuration shared by the archive importer tests.
 */
final class ArchiveTestData {

  /** A BSM packet without headers, from UdpHexDecoderTest. */
  static final String BSM_HEX =
      "001480ADDA7CDE5517E962C66947240CB711E804C8B106B7DB7B12B3056B8AA1AA4E838D00400F86822A3CD398D89E1BB8405B72C3C7A398C3CAFF63338526C646F4FFF524AD9E404039D5DA2FA62FEB57E305B552C7BE088B61E52A6BFC8CAF5AF64414F3E4513FEC189F8B5E1138B824A48B29BA1F43CB12CE296BCA3DFA8F651AB44AB1B81B633B797D5645DAA4EDADAB4AC22A0BC38AB361443395BAA2C81CC4538E7413E9C8C3F696BB2C9B6B0000";

  private ArchiveTestData() {
  }

  /**
   * Returns the input packet of a MAP UDP approval test case.
   *
   * @param descriptionPart part of the test case description
   * @return the packet hex
   */
  static String mapPacketHex(String descriptionPart) throws IOException {
    List<ApprovalTestCase> cases = ApprovalTestCase.deserializeTestCases(
        "src/test/resources/us.dot.its.jpo.ode.udp.map/UDPMAP_To_EncodedJSON_Validation.json");
    return cases.stream()
        .filter(c -> c.getDescription().contains(descriptionPart))
        .findFirst()
        .orElseThrow()
        .getInput();
  }

  static JsonTopics jsonTopics() {
    JsonTopics topics = new JsonTopics();
    topics.setBsm("topic.OdeBsmJson");
    topics.setMap("topic.OdeMapJson");
    topics.setPsm("topic.OdePsmJson");
    topics.setSpat("topic.OdeSpatJson");
    topics.setSrm("topic.OdeSrmJson");
    topics.setSsm("topic.OdeSsmJson");
    topics.setTim("topic.OdeTimJson");
    topics.setSdsm("topic.OdeSdsmJson");
    topics.setRtcm("topic.OdeRtcmJson");
    topics.setRsm("topic.OdeRsmJson");
    topics.setDriverAlert("topic.OdeDriverAlertJson");
    topics.setBsmFiltered("topic.FilteredOdeBsmJson");
    topics.setSpatFiltered("topic.FilteredOdeSpatJson");
    topics.setTimFiltered("topic.FilteredOdeTimJson");
    topics.setTimTmcFiltered("topic.OdeTimJsonTMCFiltered");
    topics.setTimCertExpiration("topic.OdeTIMCertExpirationTimeJson");
    return topics;
  }

  static RawEncodedJsonTopics rawEncodedJsonTopics() {
    RawEncodedJsonTopics topics = new RawEncodedJsonTopics();
    topics.setBsm("topic.OdeRawEncodedBSMJson");
    topics.setMap("topic.OdeRawEncodedMAPJson");
    topics.setPsm("topic.OdeRawEncodedPSMJson");
    topics.setSpat("topic.OdeRawEncodedSPATJson");
    topics.setSrm("topic.OdeRawEncodedSRMJson");
    topics.setSsm("topic.OdeRawEncodedSSMJson");
    topics.setTim("topic.OdeRawEncodedTIMJson");
    topics.setSdsm("topic.OdeRawEncodedSDSMJson");
    topics.setRtcm("topic.OdeRawEncodedRTCMJson");
    topics.setRsm("topic.OdeRawEncodedRSMJson");
    return topics;
  }

  static ArchiveTopicResolver topicResolver() {
    return new ArchiveTopicResolver(jsonTopics(), rawEncodedJsonTopics());
  }

  static String compactLine(String odeReceivedAt, String originIp, String asn1) {
    return "{\"odeReceivedAt\":\"" + odeReceivedAt + "\",\"originIp\":\"" + originIp
        + "\",\"asn1\":\"" + asn1 + "\"}";
  }
}
