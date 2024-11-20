package us.dot.its.jpo.ode.coder.stream;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ode.file-importer")
@Data
public class FileImporterProperties {
    private int bufferSize;
    private String backupDir;
    private String failedDir;
    private int timePeriod;
    private String uploadLocationRoot;
    private String obuLogUploadLocation;
}
