package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;

public class DriverAlertFileParser extends LogFileParser {

   private String alert;

   public DriverAlertFileParser() {
      super();
      setLocationParser(new LocationParser());
      setTimeParser(new TimeParser());
      setPayloadParser(new PayloadParser());
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      ParserStatus status;
      try {
         status = super.parseFile(bis, fileName);
         if (status != ParserStatus.COMPLETE)
            return status;

         if (getStep() == 1) {
            status = nextStep(bis, fileName, locationParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }
         
         if (getStep() == 2) {
            status = nextStep(bis, fileName, timeParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }

         if (getStep() == 3) {
            status = nextStep(bis, fileName, payloadParser);
            if (status != ParserStatus.COMPLETE)
               return status;
            setAlert(payloadParser.getPayload());
         }

         resetStep();
         status = ParserStatus.COMPLETE;

      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }

      return status;
   }

   public String getAlert() {
      return alert;
   }

   public void setAlert(byte[] alert) {
      this.alert = new String(alert);
   }

}
