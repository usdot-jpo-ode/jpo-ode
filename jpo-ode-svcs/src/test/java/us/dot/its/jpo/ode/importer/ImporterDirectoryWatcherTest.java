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
package us.dot.its.jpo.ode.importer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, FileImporterProperties.class, JsonTopics.class, RawEncodedJsonTopics.class})
class ImporterDirectoryWatcherTest {

    @Autowired
    FileImporterProperties injectableFileImporterProperties;
    @Autowired
    OdeKafkaProperties odeKafkaProperties;
    @Autowired
    JsonTopics jsonTopics;
    @Autowired
    RawEncodedJsonTopics rawEncodedJsonTopics;


    @Test
    void testConstructorCreatesThreeDirectories() {

        ImporterDirectoryWatcher testImporterDirectoryWatcher = new ImporterDirectoryWatcher(injectableFileImporterProperties,
                odeKafkaProperties,
                jsonTopics,
                ImporterDirectoryWatcher.ImporterFileType.LOG_FILE,
                rawEncodedJsonTopics);

        assertNotNull(testImporterDirectoryWatcher);
        Path inbox = Path.of(injectableFileImporterProperties.getUploadLocationRoot(), injectableFileImporterProperties.getObuLogUploadLocation());
        assertTrue(Files.exists(inbox));
        Path backups = Path.of(injectableFileImporterProperties.getUploadLocationRoot(), injectableFileImporterProperties.getBackupDir());
        assertTrue(Files.exists(backups));
        Path failures = Path.of(injectableFileImporterProperties.getUploadLocationRoot(), injectableFileImporterProperties.getFailedDir());
        assertTrue(Files.exists(failures));
    }

}
