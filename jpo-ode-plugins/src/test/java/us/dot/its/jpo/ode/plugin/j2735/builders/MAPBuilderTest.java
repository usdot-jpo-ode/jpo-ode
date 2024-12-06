package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * Testing the Map POJO builder classes.
 */
public class MAPBuilderTest {
  @Test
  public void shouldTranslateMap() {
    String mapXml = """
        <OdeAsn1Data><metadata><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType>
        <serialId><streamId>d07badec-84f0-48d8-8d4c-898fceaf4ecc</streamId><bundleSize>1
        </bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber>
        </serialId><odeReceivedAt>2020-11-30T23:45:24.913657Z</odeReceivedAt><schemaVersion>6
        </schemaVersion><maxDurationTime>0</maxDurationTime><odePacketID/><odeTimStartDateTime/>
        <recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><logFileName/>
        <recordType>mapTx</recordType><securityResultCode>success</securityResultCode>
        <receivedMessageDetails/><encodings><encodings><elementName>unsecuredData</elementName>
        <elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings>
        </encodings><mapSource>V2X</mapSource></metadata><payload><dataType>MessageFrame
        </dataType><data><MessageFrame><messageId>18</messageId><value><MapData><msgIssueRevision>
        4</msgIssueRevision><layerType><mixedContent/></layerType><layerID>12</layerID>
        <intersections><IntersectionGeometry><id><id>156</id></id><revision>1</revision>
        <refPoint><lat>389284111</lat><long>-772410713</long></refPoint><laneSet><GenericLane>
        <laneID>1</laneID><laneAttributes><directionalUse>00</directionalUse><sharedWith>
        0000000000</sharedWith><laneType><vehicle>00000000</vehicle></laneType></laneAttributes>
        <nodeList><nodes><NodeXY><delta><node-XY2><x>43</x><y>24</y></node-XY2></delta></NodeXY>
        <NodeXY><delta><node-XY2><x>43</x><y>24</y></node-XY2></delta></NodeXY></nodes></nodeList>
        </GenericLane></laneSet></IntersectionGeometry></intersections></MapData></value>
        </MessageFrame></data></payload></OdeAsn1Data>
        """;

    JsonNode jsonMap = null;
    try {
      jsonMap = XmlUtils.toObjectNode(mapXml);
    } catch (XmlUtilsException e) {
      fail("XML parsing error:" + e);
    }
    J2735MAP actualMap = MAPBuilder.genericMAP(jsonMap.findValue("MapData"));
    String expected = """
    {\"msgIssueRevision\":4,\"layerType\":\"mixedContent\",\"layerID\":12,\"intersections\":{\"intersectionGeometry\":[{\"id\":{\"id\":156},\"revision\":1,\"refPoint\":{\"latitude\":38.9284111,\"longitude\":-77.2410713},\"laneSet\":{\"GenericLane\":[{\"laneID\":1,\"laneAttributes\":{\"directionalUse\":{\"ingressPath\":false,\"egressPath\":false},\"shareWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"laneType\":{\"vehicle\":{\"isVehicleRevocableLane\":false,\"isVehicleFlyOverLane\":false,\"permissionOnRequest\":false,\"hasIRbeaconCoverage\":false,\"restrictedToBusUse\":false,\"restrictedToTaxiUse\":false,\"restrictedFromPublicUse\":false,\"hovLaneUseOnly\":false}}},\"nodeList\":{\"nodes\":[{\"delta\":{\"nodeXY2\":{\"x\":43,\"y\":24}}},{\"delta\":{\"nodeXY2\":{\"x\":43,\"y\":24}}}]}}]}}]}}""";
    assertEquals(expected, actualMap.toJson());
  }
}
