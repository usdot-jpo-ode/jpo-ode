package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

public class TimQueryThreadTest {

   ConcurrentHashMap<Integer, Integer> testHashMap;

   @Mocked
   Snmp mockSnmp;
   @Injectable
   PDU injectablePdu;
   @Injectable
   UserTarget injectableUserTarget;

   @Mocked
   ResponseEvent mockResponseEvent;
   @Mocked
   PDU mockPDU;
   @Mocked
   VariableBinding mockVariableBinding;

   @Before
   public void createTestTimQueryThread() {
      testHashMap = new ConcurrentHashMap<Integer, Integer>();

   }

   @Test
   public void snmpExceptionShouldPutFlagValue() {
      try {
         new Expectations() {
            {
               mockSnmp.get(injectablePdu, injectableUserTarget);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      new TimQueryThread(mockSnmp, injectablePdu, injectableUserTarget, testHashMap, 0).run();
      assertEquals(TimQueryThread.TIMEOUT_FLAG, testHashMap.get(0));
   }

   @Test
   public void nullResponseShouldPutFlagValue() {
      try {
         new Expectations() {
            {
               mockSnmp.get(injectablePdu, injectableUserTarget);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      new TimQueryThread(mockSnmp, injectablePdu, injectableUserTarget, testHashMap, 1).run();
      assertEquals(TimQueryThread.TIMEOUT_FLAG, testHashMap.get(1));
   }

   @Test
   public void emptyResponseShouldPutFlagValue() {
      try {
         new Expectations() {
            {
               mockSnmp.get(injectablePdu, injectableUserTarget);
               result = mockResponseEvent;

               mockResponseEvent.getResponse();
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      new TimQueryThread(mockSnmp, injectablePdu, injectableUserTarget, testHashMap, 2).run();
      assertEquals(TimQueryThread.TIMEOUT_FLAG, testHashMap.get(2));
   }

   @Test
   public void shouldPut1IfStatus1() {
      try {
         new Expectations() {
            {
               mockSnmp.get(injectablePdu, injectableUserTarget);
               result = mockResponseEvent;

               mockResponseEvent.getResponse();
               result = mockPDU;

               mockPDU.getVariableBindings();
               result = new Vector<VariableBinding>(Arrays.asList(mockVariableBinding));

               mockVariableBinding.getVariable().toInt();
               result = 1;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      new TimQueryThread(mockSnmp, injectablePdu, injectableUserTarget, testHashMap, 3).run();
      assertEquals(Integer.valueOf(1), testHashMap.get(3));
   }

   @Test
   public void shouldNotPut1IfStatusNot1() {
      try {
         new Expectations() {
            {
               mockSnmp.get(injectablePdu, injectableUserTarget);
               result = mockResponseEvent;

               mockResponseEvent.getResponse();
               result = mockPDU;

               mockPDU.getVariableBindings();
               result = new Vector<VariableBinding>(Arrays.asList(mockVariableBinding));

               mockVariableBinding.getVariable().toInt();
               result = 129;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      new TimQueryThread(mockSnmp, injectablePdu, injectableUserTarget, testHashMap, 4).run();
      assertNull(testHashMap.get(4));
   }

}
