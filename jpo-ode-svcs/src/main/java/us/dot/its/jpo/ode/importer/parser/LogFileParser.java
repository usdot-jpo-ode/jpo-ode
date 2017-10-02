package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;

public abstract class LogFileParser {
   public static final int BUFFER_SIZE = 4096;
   public static final int UTC_TIME_IN_SEC_LENGTH = 4;
   public static final int MSEC_LENGTH = 2;
   public static final int VERIFICATION_STATUS_LENGTH = 1;
   public static final int LENGTH_LENGTH = 2;

   protected long bundleId;
   protected byte[] readBuffer = new byte[BUFFER_SIZE];
   protected int step = 0;

   protected String filename;
   protected long utcTimeInSec;
   protected short mSec;
   protected boolean validSignature;
   protected short length;
   protected byte[] payload;

   
   public static class LogFileParserException extends Exception {
      public LogFileParserException(String msg) {
         super(msg);
      }

      public LogFileParserException(String msg, Exception e) {
         super(msg, e);
      }

      private static final long serialVersionUID = 1L;

   }

   public enum ParserStatus {
      UNKNOWN, INIT, NA, PARTIAL, COMPLETE, EOF, ERROR
   }

   public LogFileParser(long bundleId) {
      super();
      this.bundleId = bundleId;
   }

   public ParserStatus parseStep(BufferedInputStream bis, int length) throws LogFileParserException {
      try {
         int numBytes;
         if (bis.markSupported()) {
            bis.mark(length);
         }
         numBytes = bis.read(readBuffer, 0, length);
         if (numBytes < 0) {
            return ParserStatus.EOF;
         } else if (numBytes < length) {
            if (bis.markSupported()) {
               try {
                  bis.reset();
               } catch (IOException ioe) {
                  throw new LogFileParserException("Error reseting Input Stream to marked position", ioe);
               }
            }
            return ParserStatus.PARTIAL;
         } else {
            step++;
            return ParserStatus.COMPLETE;
         }
      } catch (Exception e) {
         throw new LogFileParserException("Error parsing step " + step, e);
      }
   }


   public int getStep() {
      return step;
   }

   public void setStep(int step) {
      this.step = step;
   }

   public String getFilename() {
      return filename;
   }

   public LogFileParser setFilename(String filename) {
      this.filename = filename;
      return this;
   }

   public long getBundleId() {
      return bundleId;
   }

   public LogFileParser setBundleId(long bundleId) {
      this.bundleId = bundleId;
      return this;
   }

   public long getUtcTimeInSec() {
      return utcTimeInSec;
   }

   public LogFileParser setUtcTimeInSec(long utcTimeInSec) {
      this.utcTimeInSec = utcTimeInSec;
      return this;
   }

   public short getmSec() {
      return mSec;
   }

   public LogFileParser setmSec(short mSec) {
      this.mSec = mSec;
      return this;
   }

   public boolean isValidSignature() {
      return validSignature;
   }

   public LogFileParser setValidSignature(boolean validSignature) {
      this.validSignature = validSignature;
      return this;
   }

   public short getLength() {
      return length;
   }

   public LogFileParser setLength(short length) {
      this.length = length;
      return this;
   }

   public byte[] getPayload() {
      return payload;
   }

   public LogFileParser setPayload(byte[] payload) {
      this.payload = payload;
      return this;
   }

   public abstract ParserStatus parse(BufferedInputStream bis, String fileName) throws LogFileParserException;
}
