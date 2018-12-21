/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.asn1.j2735;

public class J2735UtilTest {
	
   //TODO open-ode
//	private static Coder coder;
//	private static final String MESSAGE_DIR = "CVMessages/";
//	
//	static final private boolean isDebugOutput = false;
//	
//	private static final Logger log = LoggerFactory.getLogger(J2735UtilTest.class);
//
//	@BeforeClass
//	public static void init() throws ControlTableNotFoundException, InitializationException {
//		UnitTestHelper.initLog4j(isDebugOutput);
//		
//		J2735.initialize();
//		coder = J2735.getPERUnalignedCoder();
//		if ( log.isDebugEnabled() ) {
//			coder.enableEncoderDebugging();
//			coder.enableDecoderDebugging();
//		}
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		coder = null;
//		J2735.deinitialize();
//	}	
//
//	@Test
//	public void testIntGeoCoordinateToDouble() {
//		int lat = 433274856;
//		double expected = 43.3274856;
//		double actual = J2735Util.convertGeoCoordinateToDouble(lat);
//		assertEquals(expected, actual, .000000001);
//		
//		lat = -433274856;
//		expected = -43.3274856;
//		actual = J2735Util.convertGeoCoordinateToDouble(lat);
//		assertEquals(expected, actual, .000000001);
//		
//		lat = -430000000;
//		expected = -43.0000000;
//		actual = J2735Util.convertGeoCoordinateToDouble(lat);
//		assertEquals(expected, actual, .000000001);
//		
//		int longitude = 1697482546;
//		expected = 169.7482546;
//		actual = J2735Util.convertGeoCoordinateToDouble(longitude);
//		assertEquals(expected, actual, .000000001);
//		
//		longitude = -1697482546;
//		expected = -169.7482546;
//		actual = J2735Util.convertGeoCoordinateToDouble(longitude);
//		assertEquals(expected, actual, .000000001);
//		
//		longitude = -1800000000;
//		expected = -180.0000000;
//		actual = J2735Util.convertGeoCoordinateToDouble(longitude);
//		assertEquals(expected, actual, .000000001);
//	}
//
//	@Test
//	public void testDoubleGeoCoordinateToInt() {
//		// no round up
//		double lat = 43.32748561121;
//		int expected = 433274856;
//		int actual = J2735Util.convertGeoCoordinateToInt(lat);
//		assertEquals(expected, actual);
//		
//		// round up
//		lat = -43.3274856999;
//		expected = -433274857;
//		actual = J2735Util.convertGeoCoordinateToInt(lat);
//		assertEquals(expected, actual);
//		
//		lat = -43.00;
//		expected = -430000000;
//		actual = J2735Util.convertGeoCoordinateToInt(lat);
//		assertEquals(expected, actual);
//		
//		// no round up
//		double longitude = 169.748254647483;
//		expected = 1697482546;
//		actual = J2735Util.convertGeoCoordinateToInt(longitude);
//		assertEquals(expected, actual);
//		
//		// round up
//		longitude = -169.74825466678948;
//		expected = -1697482547;
//		actual = J2735Util.convertGeoCoordinateToInt(longitude);
//		assertEquals(expected, actual);
//		
//		longitude = -180.00;
//		expected = -1800000000;
//		actual = J2735Util.convertGeoCoordinateToInt(longitude);
//		assertEquals(expected, actual);
//	}
//	
//	@Test
//	public void testExpireInMinAndFriends() throws InterruptedException {
//		Calendar now = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
//		String nowStr = J2735Util.formatCalendar(now);
//		assertTrue(nowStr.endsWith("UTC"));
//		System.out.println(nowStr);
//		DDateTime expiration = J2735Util.expireInMin(1);
//		Calendar expire = J2735Util.DDateTimeToCalendar(expiration);
//		String expireStr = J2735Util.formatCalendar(expire);
//		System.out.println(expireStr);
//		assertTrue(expireStr.endsWith("UTC"));
//		assertTrue(expire.after(now));
//		long deltaSeconds = (expire.getTimeInMillis() - now.getTimeInMillis())/1000;
//		//System.out.println(deltaSeconds);
//		assertTrue( deltaSeconds == 60 || deltaSeconds == 59);
//		assertFalse(J2735Util.isExpired(expiration));
//		//Thread.sleep(60*1000);
//		//assertTrue(J2735Util.isExpired(expiration));
//	}
//	
//	@Test
//	public void testDecodeByName() {
//		String tests[][] = {
//				{"VehicleSituationDataServiceRequest.uper", "ServiceRequest" },
//				{"VehSitDataMessage.uper", "VehSitDataMessage" },
//		};
//		Arrays.stream(tests).forEach( t -> 
//		{
//			try {
//				testDecodeByName(t[0], t[1]);
//				log.info("Decoded file " + t[0] + " into object " + t[1]);
//			} catch (Exception e) {
//				log.error("", e);
//				assertTrue(false);
//			}
//		});
//	}
//	
//	private void testDecodeByName(String fileName, String objectName) throws DecodeFailedException, DecodeNotSupportedException, EncodeFailedException, EncodeNotSupportedException {
//		// decode file/stream by objectName
//		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(MESSAGE_DIR + fileName);
//		AbstractData object = J2735Util.decode(coder, inputStream, objectName);
//		assertNotNull(object);
//		assertEquals(object.getClass().getSimpleName(), objectName);
//		// decode bytes by objectName
//		ByteArrayOutputStream sink = new ByteArrayOutputStream();
//		coder.encode(object,sink);
//		byte[] encodedObject = sink.toByteArray();
//		assertNotNull(encodedObject);
//		AbstractData object2 = J2735Util.decode(coder, encodedObject, objectName);
//		assertNotNull(object2);
//		assertEquals(object2.getClass().getSimpleName(), objectName);
//		assertEquals(object.toString(), object2.toString());
//	}
	
}
