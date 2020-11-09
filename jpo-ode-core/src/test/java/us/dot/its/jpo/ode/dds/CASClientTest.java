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
package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
//import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.dds.CASClient.CASException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpClient;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory.HttpResponse;

//@RunWith(JMockit.class)
public class CASClientTest {
   @Mocked HttpClientFactory mockHttpClientFactory;
   @Mocked SSLContext sslContext;
   @Mocked private HttpResponse mockResponse;
   
   /* 
    * For some very odd reason, just having mocked objects of Pattern and Matcher causes
    * surefile plug-in to report the following very odd and strange errors on all
    * subsequest test cases. Hence, we have to use real values so we don't have to mock
    * which is a better approach anyway.
    * 
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running us.dot.its.jpo.ode.asn1.j2735.CVMessageTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.673 sec - in us.dot.its.jpo.ode.asn1.j2735.CVMessageTest
Running us.dot.its.jpo.ode.asn1.j2735.J2735UtilTest
01:20:03.199 [main] INFO us.dot.its.jpo.ode.asn1.j2735.J2735UtilTest - Decoded file VehicleSituationDataServiceRequest.uper into object ServiceRequest
01:20:03.227 [main] INFO us.dot.its.jpo.ode.asn1.j2735.J2735UtilTest - Decoded file VehSitDataMessage.uper into object VehSitDataMessage
2017-05-29 05:20:03.239 UTC
2017-05-29 05:21:03.000 UTC
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.25 sec - in us.dot.its.jpo.ode.asn1.j2735.J2735UtilTest
Running us.dot.its.jpo.ode.asn1.j2735.msg.ids.ConnectedVehicleMessageLookupTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.008 sec - in us.dot.its.jpo.ode.asn1.j2735.msg.ids.ConnectedVehicleMessageLookupTest
Running us.dot.its.jpo.ode.dds.CASClientTest
Tests run: 0, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.521 sec - in us.dot.its.jpo.ode.dds.CASClientTest
Running us.dot.its.jpo.ode.dds.DdsClientTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.01 sec <<< FAILURE! - in us.dot.its.jpo.ode.dds.DdsClientTest
us.dot.its.jpo.ode.dds.DdsClientTest  Time elapsed: 0.01 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.dds.DdsDepRequestTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.013 sec <<< FAILURE! - in us.dot.its.jpo.ode.dds.DdsDepRequestTest
us.dot.its.jpo.ode.dds.DdsDepRequestTest  Time elapsed: 0.013 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.dds.DdsRequestTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.002 sec <<< FAILURE! - in us.dot.its.jpo.ode.dds.DdsRequestTest
us.dot.its.jpo.ode.dds.DdsRequestTest  Time elapsed: 0.002 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.dds.DdsStatusMessageDecoderTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.016 sec <<< FAILURE! - in us.dot.its.jpo.ode.dds.DdsStatusMessageDecoderTest
us.dot.its.jpo.ode.dds.DdsStatusMessageDecoderTest  Time elapsed: 0.016 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.dds.DdsStatusMessageTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.004 sec <<< FAILURE! - in us.dot.its.jpo.ode.dds.DdsStatusMessageTest
us.dot.its.jpo.ode.dds.DdsStatusMessageTest  Time elapsed: 0.003 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.dds.DepositResponseDecoderTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.013 sec <<< FAILURE! - in us.dot.its.jpo.ode.dds.DepositResponseDecoderTest
us.dot.its.jpo.ode.dds.DepositResponseDecoderTest  Time elapsed: 0.013 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.dds.StatusMessageHandlerTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.011 sec <<< FAILURE! - in us.dot.its.jpo.ode.dds.StatusMessageHandlerTest
us.dot.its.jpo.ode.dds.StatusMessageHandlerTest  Time elapsed: 0.011 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.model.J2735GeoRegionTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.001 sec <<< FAILURE! - in us.dot.its.jpo.ode.model.J2735GeoRegionTest
us.dot.its.jpo.ode.model.J2735GeoRegionTest  Time elapsed: 0.001 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.snmp.SNMPTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.01 sec <<< FAILURE! - in us.dot.its.jpo.ode.snmp.SNMPTest
us.dot.its.jpo.ode.snmp.SNMPTest  Time elapsed: 0.01 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.util.DateTimeUtilsTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.013 sec <<< FAILURE! - in us.dot.its.jpo.ode.util.DateTimeUtilsTest
us.dot.its.jpo.ode.util.DateTimeUtilsTest  Time elapsed: 0.013 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.util.GeoUtilsTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.004 sec <<< FAILURE! - in us.dot.its.jpo.ode.util.GeoUtilsTest
us.dot.its.jpo.ode.util.GeoUtilsTest  Time elapsed: 0.004 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.util.JsonUtilsTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.003 sec <<< FAILURE! - in us.dot.its.jpo.ode.util.JsonUtilsTest
us.dot.its.jpo.ode.util.JsonUtilsTest  Time elapsed: 0.003 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.util.OdeGeoUtilsTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.005 sec <<< FAILURE! - in us.dot.its.jpo.ode.util.OdeGeoUtilsTest
us.dot.its.jpo.ode.util.OdeGeoUtilsTest  Time elapsed: 0.004 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.wrapper.HttpClientFactoryTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.005 sec <<< FAILURE! - in us.dot.its.jpo.ode.wrapper.HttpClientFactoryTest
us.dot.its.jpo.ode.wrapper.HttpClientFactoryTest  Time elapsed: 0.005 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.wrapper.HttpClientTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.013 sec <<< FAILURE! - in us.dot.its.jpo.ode.wrapper.HttpClientTest
us.dot.its.jpo.ode.wrapper.HttpClientTest  Time elapsed: 0.013 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.wrapper.SSLBuilderTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.015 sec <<< FAILURE! - in us.dot.its.jpo.ode.wrapper.SSLBuilderTest
us.dot.its.jpo.ode.wrapper.SSLBuilderTest  Time elapsed: 0.015 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Running us.dot.its.jpo.ode.wrapper.WebSocketEndpointTest
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.013 sec <<< FAILURE! - in us.dot.its.jpo.ode.wrapper.WebSocketEndpointTest
us.dot.its.jpo.ode.wrapper.WebSocketEndpointTest  Time elapsed: 0.013 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:365)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)

Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=256m; support was removed in 8.0

Results :

Tests in error:
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer
  JUnit4Provider.invoke:161->executeTestSet:238->executeWithRerun:274->execute:365 ╗ NullPointer

Tests run: 26, Failures: 0, Errors: 17, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 7.387 s
[INFO] Finished at: 2017-05-29T01:20:05-04:00
[INFO] Final Memory: 19M/227M
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.19.1:test (default-test) on project jpo-ode-core: Execution default-test of goal org.apache.maven.plugins:maven-surefire-plugin:2.19.1:test failed: There was an error in the forked process
[ERROR] org.apache.maven.surefire.testset.TestSetFailedException: java.lang.NullPointerException
[ERROR]         at org.apache.maven.surefire.common.junit4.JUnit4RunListener.rethrowAnyTestMechanismFailures(JUnit4RunListener.java:209)
[ERROR]         at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:169)
[ERROR]         at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:290)
[ERROR]         at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:242)
[ERROR]         at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:121)
[ERROR] Caused by: java.lang.NullPointerException
[ERROR]         at org.apache.maven.surefire.common.junit4.JUnit4RunListener.extractClassName(JUnit4RunListener.java:188)
[ERROR]         at org.apache.maven.surefire.common.junit4.JUnit4RunListener.getClassName(JUnit4RunListener.java:157)
[ERROR]         at org.apache.maven.surefire.common.junit4.JUnit4RunListener.createReportEntry(JUnit4RunListener.java:181)
[ERROR]         at org.apache.maven.surefire.common.junit4.JUnit4RunListener.testFinished(JUnit4RunListener.java:143)
[ERROR]         at org.junit.runner.notification.SynchronizedRunListener.testFinished(SynchronizedRunListener.java:56)
[ERROR]         at org.junit.runner.notification.RunNotifier$7.notifyListener(RunNotifier.java:190)
[ERROR]         at org.junit.runner.notification.RunNotifier$SafeNotifier.run(RunNotifier.java:72)
[ERROR]         at org.junit.runner.notification.RunNotifier.fireTestFinished(RunNotifier.java:187)
[ERROR]         at org.junit.internal.runners.model.EachTestNotifier.fireTestFinished(EachTestNotifier.java:38)
[ERROR]         at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:331)
[ERROR]         at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
[ERROR]         at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
[ERROR]         at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
[ERROR]         at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
[ERROR]         at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
[ERROR]         at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
[ERROR]         at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
[ERROR]         at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
[ERROR]         at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:367)
[ERROR]         at org.apache.maven.surefire.junit4.JUnit4Provider.executeWithRerun(JUnit4Provider.java:274)
[ERROR]         at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:238)
[ERROR]         at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:161)
[ERROR]         ... 3 more
[ERROR]
[ERROR] -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/PluginExecutionException
    * 
    */
//   @Mocked private Pattern mockPattern;
//   @Mocked private Matcher mockMatcher;
   
   String casUser = "testUser";
   String casPass = "testPass";
   String casUrl = "testUrl";

   @Test
   public void testConfigure() {
      CASClient casClient = null;
      try {
         casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      } catch (CASException e) {
         fail("Unexpected exception: " + e.toString());
      }
      assertEquals(casClient.getDdsCasUrl(), casUrl);
      assertEquals(casClient.getDdsCasUsername(), casUser);
   }

   @Test(expected = CASException.class)
   public void testConfigureException()
         throws CASException, HttpException {
      new Expectations() {
         {
            HttpClientFactory.build((SSLContext) any);
            result = new Exception();
         }
      };
      CASClient.configure(sslContext, casUrl, casUser, casPass);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testLogin() throws HttpException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations() {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;
            result = Status.OK;
            result = Status.OK;

            /* 
             * For some very odd reason, just having mocked objects of Pattern and Matcher causes
             * surefile plug-in to report the followig very odd and strange errors on all
             * subsequest test cases. Hence, we have to use real values so we don't have to mock
             * which is a better approach anyway.
             */
//            Pattern.compile(anyString);
//            result = mockPattern;
//            
//            mockPattern.matcher(anyString);
//            result = mockMatcher;
//
//            mockMatcher.matches();
//            result = true;
//            mockMatcher.group(1);
//            result = "TGT-1234-11112222333334444-cas01";

            mockResponse.getBody();
            result = "action=\"x/TGT-1234-11112222333334444-cas01\"";
            result = "ST-1234-1111222233334444-cas01";

            mockHttpClientFactory.createHttpClient().getCookies();
            result = cookies;
         }
      };

      CASClient casClient;
      String sessionId = "";
      try {
         casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
         sessionId = casClient.login(websocketURL);
         assertEquals(casClient.getSessionID(), sessionId);
         assertEquals("1bif45f-testSessionId", sessionId);
      } catch (CASException e) {
         fail("Unexpected exception: " + e.toString());
      }

      new Verifications() {
         {
            HttpClient httpClient = mockHttpClientFactory.createHttpClient();
            minTimes = 3;

            httpClient.post(anyString, null, (Map<String, String>) any, anyString);
            minTimes = 2;

            httpClient.get(anyString, null, (Map<String, String>) any);
            minTimes = 1;

            httpClient.close();
            minTimes = 3;

            Pattern.compile(anyString);
         }
      };
   }

   @Test(expected = CASException.class)
   public void testLoginExceptionInGetTicket1() throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations() {
         {
            mockResponse.getStatusCode();
            result = Status.BAD_REQUEST;
         }
      };

      CASClient casClient;

      casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }

   @Test(expected = CASException.class)
   public void testLoginExceptionInGetTicket2() throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations() {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;

            /* 
             * For some very odd reason, just having mocked objects of Pattern and Matcher causes
             * surefile plug-in to report the followig very odd and strange errors on all
             * subsequest test cases. Hence, we have to use real values so we don't have to mock
             * which is a better approach anyway.
             */
//            Pattern.compile(anyString);
//            result = mockPattern;
//            
//            mockPattern.matcher(anyString);
//            result = mockMatcher;
//
//            mockMatcher.matches();
//            result = false;
         }
      };

      CASClient casClient;

      casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }

   @Test(expected = CASException.class)
   public void testLoginExceptionInGetServiceTicket() 
         throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations() {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;
            result = Status.BAD_REQUEST;

            /* 
             * For some very odd reason, just having mocked objects of Pattern and Matcher causes
             * surefile plug-in to report the followig very odd and strange errors on all
             * subsequest test cases. Hence, we have to use real values so we don't have to mock
             * which is a better approach anyway.
             */
//            Pattern.compile(anyString);
//            result = mockPattern;
//            
//            mockPattern.matcher(anyString);
//            result = mockMatcher;
//
//            mockMatcher.matches();
//            result = true;
//            mockMatcher.group(1);
//            result = "TGT-1234-11112222333334444-cas01";

            mockResponse.getBody();
            result = "action=\"x/TGT-1234-11112222333334444-cas01\"";
            result = "ST-1234-1111222233334444-cas01";
         }
      };

      CASClient casClient;

      casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }


   @Test(expected = CASException.class)
   public void testLoginExceptionInGetServiceCall() 
               throws HttpException, CASException {
      String websocketURL = "wss://url.websocket.com";
      Map<String, String> cookies = new ConcurrentHashMap<String, String>();
      cookies.put("JSESSIONID", "1bif45f-testSessionId");
      new Expectations() {
         {
            mockResponse.getStatusCode();
            result = Status.CREATED;
            result = Status.OK;
            result = Status.BAD_REQUEST;

            /* 
             * For some very odd reason, just having mocked objects of Pattern and Matcher causes
             * surefile plug-in to report the followig very odd and strange errors on all
             * subsequest test cases. Hence, we have to use real values so we don't have to mock
             * which is a better approach anyway.
             */
//            Pattern.compile(anyString);
//            result = mockPattern;
//            
//            mockPattern.matcher(anyString);
//            result = mockMatcher;
//
//            mockMatcher.matches();
//            result = true;
//            mockMatcher.group(1);
//            result = "TGT-1234-11112222333334444-cas01";

            mockResponse.getBody();
            result = "action=\"x/TGT-1234-11112222333334444-cas01\"";
            result = "ST-1234-1111222233334444-cas01";

         }
      };

      CASClient casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
      casClient.login(websocketURL);
   }
}
