package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher.LogFileToAsn1CodecPublisherException;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public interface Asn1CodecPublisher {
   
  public void publish(BufferedInputStream bis, String fileName, ImporterFileType fileType) 
      throws LogFileToAsn1CodecPublisherException;
}
