package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.util.JsonUtils;

public class MessageFrameStreamDecoderPublisher extends BsmStreamDecoderPublisher {
    
    public MessageFrameStreamDecoderPublisher(OdeProperties properties) {
        super(properties);
    }

    @Override
    public Asn1Object decode(String line) {
       return asn1Coder.decodeUPERMessageFrameHex(line);
    }

    @Override
    public Asn1Object decode(InputStream is) {
        return asn1Coder.decodeUPERMessageFrameStream(is);
    }

    @Override
    public void decodeJsonAndPublish(InputStream is) throws IOException {
       String line = null;
       J2735MessageFrame msgFrame = null;

       try (Scanner scanner = new Scanner(is)) {

           boolean empty = true;
           while (scanner.hasNextLine()) {
               empty = false;
               line = scanner.nextLine();

               msgFrame = (J2735MessageFrame) JsonUtils.fromJson(line, J2735MessageFrame.class);
               publish(msgFrame.getValue());
           }
           if (empty) {
               EventLogger.logger.info("Empty file received");
               throw new IOException("Empty file received");
           }
       } catch (IOException e) {
           EventLogger.logger.info("Error occurred while decoding message: {}", line);
           throw new IOException("Error decoding data: " + line, e);
       }
    }

    @Override
    public void decodeBinaryAndPublish(InputStream is) throws IOException {
        J2735MessageFrame msgFrame;

        try {
            do {
                msgFrame = (J2735MessageFrame) decode(is);
                if (msgFrame != null) {
                    logger.debug("Decoded: {}", msgFrame);
                    publish(msgFrame.getValue());
                }
            } while (msgFrame != null);

        } catch (Exception e) {
            throw new IOException("Error decoding data." + e);
        }
    }
    
    @Override
    public Logger getLogger() {
       return LoggerFactory.getLogger(this.getClass());
    }


}
