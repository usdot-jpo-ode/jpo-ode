package us.dot.its.jpo.ode.messageframe;

import javax.websocket.Session;

import org.springframework.util.SerializationUtils;

import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.util.WebSocketUtils;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class WebsocketMessageDistributor extends MessageProcessor<String, byte[]> {

    private Session clientSession;

    @Override
    public Object call() throws Exception {
        J2735MessageFrame mfm = (J2735MessageFrame) SerializationUtils.deserialize(record.value());

        // send the data to client
        WebSocketUtils.send(clientSession, mfm.toJson());
        return null;
    }

    public Session getClientSession() {
        return clientSession;
    }

    public void setClientSession(Session clientSession) {
        this.clientSession = clientSession;
    }

}
