package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;

public class DistressMsgFileParser extends LogFileParser {

   public DistressMsgFileParser() {
      super();
      setLocationParser(new LocationParser());
      setTimeParser(new TimeParser());
      setSecResCodeParser(new SecurityResultCodeParser());
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
