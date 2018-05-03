package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;

public class DistressMsgFileParser extends LogFileParser {

   public DistressMsgFileParser(long bundleId) {
      super(bundleId);
      setLocationParser(new LocationParser(bundleId));
      setTimeParser(new TimeParser(bundleId));
      setSecResCodeParser(new SecurityResultCodeParser(bundleId));
      setPayloadParser(new PayloadParser(bundleId));
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
            status = nextStep(bis, fileName, secResCodeParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }

         if (getStep() == 4) {
            status = nextStep(bis, fileName, payloadParser);
            if (status != ParserStatus.COMPLETE)
               return status;
         }
         
         resetStep();
         status = ParserStatus.COMPLETE;

      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }

      return status;

   }
}
