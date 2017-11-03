package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public interface DecoderPublisher {
   
   public void decodeAndPublish(BufferedInputStream is, String fileName, ImporterFileType fileType) throws Exception;
}
