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
package us.dot.its.jpo.ode.snmp;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.plugin.SNMP;

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
