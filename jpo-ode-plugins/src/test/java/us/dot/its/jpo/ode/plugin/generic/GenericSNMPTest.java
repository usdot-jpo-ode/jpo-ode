package us.dot.its.jpo.ode.plugin.generic;

import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.GenericSnmp;
import us.dot.its.jpo.ode.plugin.GenericSnmp.SNMP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

@RunWith(JMockit.class)
public class GenericSNMPTest {
   
   @Tested
   SNMP testSNMP;

   @Test
   public void testGettersAndSetters() {
      
      String id = "a";
      testSNMP.setRsuid(id);
      assertEquals(id,testSNMP.getRsuid());
      
      int msgid = 5;
      testSNMP.setMsgid(msgid);
      assertEquals(msgid,testSNMP.getMsgid());
      
      int mode = 3;
      testSNMP.setMode(mode);
      assertEquals(mode,testSNMP.getMode());
      
      int channel = 4;
      testSNMP.setChannel(channel);
      assertEquals(channel,testSNMP.getChannel());
      
      int interval = 6;
      testSNMP.setInterval(interval);
      assertEquals(interval,testSNMP.getInterval());
      
      String deliverystart = "120";
      testSNMP.setDeliverystart(deliverystart);
      assertEquals(deliverystart,testSNMP.getDeliverystart());
      
      String deliverystop = "130";
      testSNMP.setDeliverystop(deliverystop);
      assertEquals(deliverystop,testSNMP.getDeliverystop());
      
      int enable = 1;
      testSNMP.setEnable(enable);
      assertEquals(enable,testSNMP.getEnable());
      
      int status = 2;
      testSNMP.setStatus(status);
      assertEquals(status,testSNMP.getStatus());
   }
   
   @Test
   public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
     Constructor<GenericSnmp > constructor = GenericSnmp.class.getDeclaredConstructor();
     assertTrue(Modifier.isPrivate(constructor.getModifiers()));
     constructor.setAccessible(true);
     try {
       constructor.newInstance();
       fail("Expected IllegalAccessException.class");
     } catch (Exception e) {
       assertEquals(InvocationTargetException.class, e.getClass());
     }
   }
}
