package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import org.junit.Test;
import mockit.*;
import us.dot.its.jpo.ode.dds.DotWarehouseData.OdeTimeToLive;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.TimeToLive;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.util.CodecUtils;

public class DotWarehouseDataTest {
	@Mocked
	CodecUtils mockCodecUtils;
	
	@Test
	public void TestDotWarehouseData(@Mocked String serialID, @Mocked GroupID groupID,
					@Mocked TimeToLive timeToLive, @Mocked J2735GeoRegion mockServiceRegion) {
		new Expectations(){{
			timeToLive.name();
			result="minute";
		}};
		
		DotWarehouseData dotWarehouseData = new DotWarehouseData(serialID, groupID, timeToLive, mockServiceRegion);
		
		assertEquals(mockServiceRegion, dotWarehouseData.getServiceRegion());
		assertEquals(OdeTimeToLive.valueOf("minute"), dotWarehouseData.getTimeToLive());
		
		new Verifications(){{
			mockCodecUtils.toHex(groupID.byteArrayValue()); times = 1;
			assertEquals(mockCodecUtils.toHex(groupID.byteArrayValue()), dotWarehouseData.getGroupId());
		}};
	}
	
	@Test
	public void TestIsWithinBounds(@Mocked J2735GeoRegion mockServiceRegion1, @Mocked J2735GeoRegion mockServiceRegion2) {
		new Expectations(){{ 
			mockServiceRegion1.contains((J2735Position3D)any); result = true;
			mockServiceRegion2.contains((J2735Position3D)any); result = false;
		}};
		
		DotWarehouseData dotWarehouseData = new DotWarehouseData();
		boolean result1 = dotWarehouseData.isWithinBounds(mockServiceRegion1);
		assertEquals(true, result1);
		
		boolean result2 = dotWarehouseData.isWithinBounds(mockServiceRegion2);
		assertEquals(false, result2);
	}
	
	@Test
	public void TestConstructor1(@Injectable final OdeData mockOdeData) {	
		DotWarehouseData dotWarehouseData = new DotWarehouseData();
		
		new Verifications(){{
			new OdeData();
		}};
	}
	
	@Test
	public void TestConstructor2(@Injectable final OdeData mockOdeData) {
		String serialId="serialId";
		DotWarehouseData dotWarehouseData = new DotWarehouseData(serialId);
		
		new Verifications(){{
			new OdeData(serialId);
		}};
	}
	
	@Test
	public void TestConstructor3(@Injectable final OdeData mockOdeData) {
		String streamId="streamId";
		long bundleId = 1;
		long recordId = 1; 
		DotWarehouseData dotWarehouseData = new DotWarehouseData(streamId, bundleId, recordId);
		
		new Verifications(){{
			new OdeData(streamId, bundleId, recordId);
		}};
	}
	
	@Test
	public void TestGetPosition(@Mocked J2735Position3D centerPos) {
		DotWarehouseData dotWarehouseData = new DotWarehouseData();
		dotWarehouseData.setCenterPosition(centerPos);
		assertEquals(centerPos, dotWarehouseData.getPosition());
		assertEquals(centerPos, dotWarehouseData.getPosition());
	}
	
	@Test
	public void TestHashCode(@Mocked J2735GeoRegion serviceRegion) {
		DotWarehouseData dotWarehouseData1 = new DotWarehouseData();
		
		DotWarehouseData dotWarehouseData2 = new DotWarehouseData();
		dotWarehouseData2.setServiceRegion(serviceRegion);
		
		DotWarehouseData dotWarehouseData3 = new DotWarehouseData();
		dotWarehouseData3.setServiceRegion(serviceRegion);
		
		int hashcode1 = dotWarehouseData1.hashCode();
		int hashcode2 = dotWarehouseData2.hashCode();
		int hashcode3 = dotWarehouseData3.hashCode();
		
		assertNotEquals(hashcode1, hashcode2);
		assertEquals(hashcode2, hashcode3);
		assertNotEquals(hashcode1, hashcode3);
	}
	
	@Test
	public void TestEquals1(@Mocked J2735GeoRegion serviceRegion) {
		DotWarehouseData dotWarehouseData1 = new DotWarehouseData();
		
		DotWarehouseData dotWarehouseData2 = new DotWarehouseData();
		dotWarehouseData2.setServiceRegion(serviceRegion);
		
		DotWarehouseData dotWarehouseData3 = new DotWarehouseData();
		dotWarehouseData3.setServiceRegion(serviceRegion);
		
		assertNotEquals(dotWarehouseData1, dotWarehouseData2);
		assertEquals(dotWarehouseData2, dotWarehouseData3);
		assertNotEquals(dotWarehouseData1, dotWarehouseData3);
	}
	
	@Test
	public void TestEquals2(@Mocked String serialID, @Mocked GroupID groupID,
					@Mocked TimeToLive timeToLive, @Mocked J2735GeoRegion mockServiceRegion) {
		new Expectations(){{
			timeToLive.name();
			result="minute";
		}};
		
		DotWarehouseData dotWarehouseData1 = new DotWarehouseData();
		
		DotWarehouseData dotWarehouseData2 = new DotWarehouseData("serial", groupID, timeToLive, mockServiceRegion);
		
		assertNotEquals(dotWarehouseData1, dotWarehouseData2);
	}

}
