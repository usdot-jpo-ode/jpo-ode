package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class SNMPTest {
   

   @Test
   public void testGettersAndSetters() {
      
      SNMP testSNMP = new SNMP();
      
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
   public void testSnmpTimestampFromIso() throws ParseException {
      String snmpTS = SNMP.snmpTimestampFromIso("2017-05-04T21:55:00-05:00");
      assertEquals("050414111537", snmpTS);
   }
}
