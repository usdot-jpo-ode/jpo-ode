package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2ExtensionBase;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeBsmDataCreatorHelperTest {
	@Test
	public void testCreateOdeBsmData() {
		String consumedData = "<OdeAsn1Data><metadata><bsmSource>RV</bsmSource><logFileName/><recordType>bsmTx</recordType><securityResultCode>success</securityResultCode><receivedMessageDetails><locationData><latitude/><longitude/><elevation/><speed/><heading/></locationData><rxSource>RV</rxSource></receivedMessageDetails><encodings><encodings><elementName>unsecuredData</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings><payloadType>us.dot.its.jpo.ode.model.OdeAsn1Payload</payloadType><serialId><streamId>746fdbbb-188b-471b-a4ed-8f9c03108512</streamId><bundleSize>1</bundleSize><bundleId>0</bundleId><recordId>0</recordId><serialNumber>0</serialNumber></serialId><odeReceivedAt>2022-10-31T06:50:07.892312Z</odeReceivedAt><schemaVersion>6</schemaVersion><maxDurationTime>0</maxDurationTime><recordGeneratedAt/><recordGeneratedBy/><sanitized>false</sanitized><odePacketID/><odeTimStartDateTime/><originIp>172.19.0.1</originIp></metadata><payload><dataType>MessageFrame</dataType><data><MessageFrame><messageId>20</messageId><value><BasicSafetyMessage><coreData><msgCnt>35</msgCnt><id>0D337EAF</id><secMark>8800</secMark><lat>396792924</lat><long>-1059352817</long><elev>33959</elev><accuracy><semiMajor>35</semiMajor><semiMinor>27</semiMinor><orientation>0</orientation></accuracy><transmission><park/></transmission><speed>0</speed><heading>19696</heading><angle>0</angle><accelSet><long>0</long><lat>0</lat><vert>50</vert><yaw>1</yaw></accelSet><brakes><wheelBrakes>00000</wheelBrakes><traction><on/></traction><abs><on/></abs><scs><on/></scs><brakeBoost><unavailable/></brakeBoost><auxBrakes><unavailable/></auxBrakes></brakes><size><width>0</width><length>0</length></size></coreData><partII><PartIIcontent><partII-Id>0</partII-Id><partII-Value><VehicleSafetyExtensions><events>1000000000000</events><pathHistory><crumbData><PathHistoryPoint><latOffset>-1347</latOffset><lonOffset>-3753</lonOffset><elevationOffset>22</elevationOffset><timeOffset>61530</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>-1998</latOffset><lonOffset>-6328</lonOffset><elevationOffset>-12</elevationOffset><timeOffset>62370</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>-1887</latOffset><lonOffset>-8337</lonOffset><elevationOffset>-39</elevationOffset><timeOffset>62710</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>-1166</latOffset><lonOffset>-10100</lonOffset><elevationOffset>-57</elevationOffset><timeOffset>63020</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>164</latOffset><lonOffset>-11501</lonOffset><elevationOffset>-77</elevationOffset><timeOffset>63380</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>2130</latOffset><lonOffset>-12445</lonOffset><elevationOffset>-95</elevationOffset><timeOffset>63780</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>4586</latOffset><lonOffset>-12737</lonOffset><elevationOffset>-102</elevationOffset><timeOffset>64220</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>6499</latOffset><lonOffset>-12139</lonOffset><elevationOffset>-111</elevationOffset><timeOffset>64670</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>8041</latOffset><lonOffset>-10872</lonOffset><elevationOffset>-90</elevationOffset><timeOffset>65030</timeOffset></PathHistoryPoint><PathHistoryPoint><latOffset>10185</latOffset><lonOffset>-7426</lonOffset><elevationOffset>-53</elevationOffset><timeOffset>65535</timeOffset></PathHistoryPoint></crumbData></pathHistory><pathPrediction><radiusOfCurve>32767</radiusOfCurve><confidence>200</confidence></pathPrediction><lights>000010000</lights></VehicleSafetyExtensions></partII-Value></PartIIcontent><PartIIcontent><partII-Id>2</partII-Id><partII-Value><SupplementalVehicleExtensions><classification>0</classification><classDetails><keyType>0</keyType><hpmsType><none/></hpmsType></classDetails><vehicleData/><weatherReport><isRaining><error/></isRaining><rainRate>65535</rainRate><precipSituation><unknown/></precipSituation><solarRadiation>65535</solarRadiation><friction>101</friction><roadFriction>0</roadFriction></weatherReport><weatherProbe><airTemp>73</airTemp><airPressure>41</airPressure><rainRates><statusFront><washerInUse/></statusFront><rateFront>0</rateFront></rainRates></weatherProbe></SupplementalVehicleExtensions></partII-Value></PartIIcontent><PartIIcontent><partII-Id>1</partII-Id><partII-Value><SpecialVehicleExtensions><vehicleAlerts><sspRights>0</sspRights><sirenUse><notInUse/></sirenUse><lightsUse><notInUse/></lightsUse><multi><unavailable/></multi><events><sspRights>0</sspRights><event>1000000000000000</event></events></vehicleAlerts></SpecialVehicleExtensions></partII-Value></PartIIcontent></partII></BasicSafetyMessage></value></MessageFrame></data></payload></OdeAsn1Data>";
		try {
			XmlUtils.toObjectNode(consumedData);
		} catch (XmlUtilsException e) {
			fail("XML parsing error:" + e);
		}
		OdeBsmData bsmData;
		try {
			bsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
			assertNotNull(bsmData);
			J2735BsmPart2ExtensionBase part2Ext = ((OdeBsmPayload)bsmData.getPayload()).getBsm().getPartII().get(0).getValue();
			BigDecimal timeOffset = ((J2735VehicleSafetyExtensions)part2Ext).getPathHistory().getCrumbData().get(9).getTimeOffset();
			assertEquals(Integer.toString(65535), timeOffset.toString());
		} catch (XmlUtilsException e) {
			e.printStackTrace();
		}

	}
}
