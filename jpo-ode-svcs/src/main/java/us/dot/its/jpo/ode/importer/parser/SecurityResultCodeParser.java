package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;

public class SecurityResultCodeParser extends LogFileParser {

   private final Logger logger = LoggerFactory.getLogger(this.getClass());

   public static final int SECURITY_RESULT_CODE_LENGTH = 1;

   protected SecurityResultCode securityResultCode;

   public SecurityResultCodeParser() {
      super();
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      ParserStatus status = ParserStatus.INIT;
      try {
         // parse SecurityResultCode
         if (getStep() == 0) {
            status = parseStep(bis, SECURITY_RESULT_CODE_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setSecurityResultCode(readBuffer[0]);
         }
         
         resetStep();
         status = ParserStatus.COMPLETE;

      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }

      return status;

   }

   public SecurityResultCode getSecurityResultCode() {
      return securityResultCode;
   }

   public void setSecurityResultCode(SecurityResultCode securityResultCode) {
      this.securityResultCode = securityResultCode;
   }

   public void setSecurityResultCode(byte code) {
      try {
         setSecurityResultCode(SecurityResultCode.values()[code]);
      } catch (Exception e) {
         logger.error("Invalid SecurityResultCode: {}. Valid values are {}: ", 
            code, SecurityResultCode.values());
         setSecurityResultCode(SecurityResultCode.unknown);
      }
   }

}
