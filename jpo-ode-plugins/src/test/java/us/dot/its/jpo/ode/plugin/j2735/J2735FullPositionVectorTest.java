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
package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735FullPositionVectorTest {
   @Tested
   J2735FullPositionVector fpv;
   
   @Test
   public void testGettersAndSetters() {
      OdePosition3D position = new OdePosition3D();
      fpv.setPosition(position);
      assertEquals(position,fpv.getPosition());
      BigDecimal heading = BigDecimal.valueOf(1);
      fpv.setHeading(heading);
      assertEquals(heading,fpv.getHeading());
      J2735PositionalAccuracy posAccuracy = new J2735PositionalAccuracy();
      fpv.setPosAccuracy(posAccuracy);
      assertEquals(posAccuracy,fpv.getPosAccuracy());
      J2735PositionConfidenceSet posConfidence = new J2735PositionConfidenceSet();
      fpv.setPosConfidence(posConfidence);
      assertEquals(posConfidence,fpv.getPosConfidence());
      J2735TransmissionAndSpeed speed = new J2735TransmissionAndSpeed();
      fpv.setSpeed(speed);
      assertEquals(speed,fpv.getSpeed());
      J2735SpeedandHeadingandThrottleConfidence speedConfidence = new J2735SpeedandHeadingandThrottleConfidence();
      fpv.setSpeedConfidence(speedConfidence);
      assertEquals(speedConfidence,fpv.getSpeedConfidence());
      J2735TimeConfidence timeConfidence = null;
      fpv.setTimeConfidence(timeConfidence);
      assertEquals(timeConfidence,fpv.getTimeConfidence());
      J2735DDateTime utcTime = new J2735DDateTime();
      fpv.setUtcTime(utcTime);
      assertEquals(utcTime,fpv.getUtcTime());
   }
}
