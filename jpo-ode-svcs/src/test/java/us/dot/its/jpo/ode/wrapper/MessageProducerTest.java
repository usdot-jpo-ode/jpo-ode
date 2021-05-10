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
package us.dot.its.jpo.ode.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

public class MessageProducerTest {

	Properties mockProps = new Properties();
   @Mocked
   KafkaProducer<?, ?> mockKafkaProducer;
   @Mocked
   Producer<String, String> mockProducer;
   @Injectable
   ProducerRecord<String, String> mockProducerRecord;

   @Before
   public void setUp() {
      new Expectations() {
         {
            new KafkaProducer<>((Properties) any);
         }
      };
   }

   @Test @Ignore
   public void shouldConstruct() {

      new MessageProducer<String, String>("testBrokers", null,
            "testPartitioner", mockProps, Collections.singleton("testTopic"));
   }

   @Test @Ignore
   public void testSendNoTopic() {

      MessageProducer<String, String> testMessageProducer = 
            new MessageProducer<String, String>("testBrokers", null,
            "testPartitioner", mockProps, Collections.singleton("testTopic"));
      testMessageProducer.send(mockProducerRecord);
   }

   @Test @Ignore
   public void testSendWithTopic() {

      MessageProducer<String, String> testMessageProducer = 
            new MessageProducer<String, String>("testBrokers", null,
            "testPartitioner", mockProps, Collections.singleton("testTopic"));

      testMessageProducer.setProducer(mockProducer);
      testMessageProducer.send("testTopic", "testKey", "testValue");
   }

   @Test @Ignore
   public void testSendWithTopicNullKey() {

      MessageProducer<String, String> testMessageProducer = 
            new MessageProducer<String, String>("testBrokers", null,
            "testPartitioner", mockProps, Collections.singleton("testTopic"));

      testMessageProducer.send("testTopic", null, "testValue");
      assertEquals(KafkaProducer.class, testMessageProducer.getProducer().getClass());
   }

   @Test @Ignore
   public void testClose() {

      MessageProducer<String, String> testMessageProducer = 
            new MessageProducer<String, String>("testBrokers", null,
            "testPartitioner", mockProps, Collections.singleton("testTopic"));
      testMessageProducer.close();
   }

   @Test @Ignore
	public void testDefaultStringMessageProducer() {

      String testBrokers = "bootstrap.servers";
      String testType = "testType123";

      MessageProducer<String, String> actualProducer = MessageProducer.defaultStringMessageProducer(testBrokers,
         testType, Collections.singleton("testTopic"));
      assertNotNull(actualProducer);

      new Verifications() {
         {
				mockProps.put("bootstrap.servers", testBrokers);
         }
      };
   }

}
