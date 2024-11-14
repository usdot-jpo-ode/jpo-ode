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
package us.dot.its.jpo.ode.coder;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.kafka.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.RawEncodedJsonTopics;

import java.io.BufferedInputStream;
import java.nio.file.Path;

@Slf4j
public class FileAsn1CodecPublisher {

   public static class FileAsn1CodecPublisherException extends Exception {

      private static final long serialVersionUID = 1L;

      public FileAsn1CodecPublisherException(String string, Exception e) {
         super (string, e);
      }

   }

   private final LogFileToAsn1CodecPublisher codecPublisher;

   public FileAsn1CodecPublisher(OdeKafkaProperties odeKafkaProperties, JsonTopics jsonTopics, RawEncodedJsonTopics rawEncodedJsonTopics) {
      StringPublisher messagePub = new StringPublisher(odeKafkaProperties.getBrokers(),
              odeKafkaProperties.getProducer().getType(),
              odeKafkaProperties.getDisabledTopics());

      this.codecPublisher = new LogFileToAsn1CodecPublisher(messagePub, jsonTopics, rawEncodedJsonTopics);
   }

   public void publishFile(Path filePath, BufferedInputStream fileInputStream, ImporterFileType fileType) 
         throws FileAsn1CodecPublisherException {
      String fileName = filePath.toFile().getName();

      log.info("Publishing file {}", fileName);
      
      try {
         log.info("Publishing data from {} to asn1_codec.", filePath);
         codecPublisher.publish(fileInputStream, fileName, fileType);
      } catch (Exception e) {
         throw new FileAsn1CodecPublisherException("Failed to publish file.", e);
      }
   }

}
