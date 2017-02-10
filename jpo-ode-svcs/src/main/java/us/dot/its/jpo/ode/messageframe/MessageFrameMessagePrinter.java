package us.dot.its.jpo.ode.messageframe;

import org.springframework.util.SerializationUtils;

import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class MessageFrameMessagePrinter extends MessageProcessor<String, byte[]> {

    @Override
    public Object call() throws Exception {
        J2735MessageFrame mfm = (J2735MessageFrame) SerializationUtils.deserialize(record.value());
        System.out.println(mfm.toJson());
        return null;
    }

}
