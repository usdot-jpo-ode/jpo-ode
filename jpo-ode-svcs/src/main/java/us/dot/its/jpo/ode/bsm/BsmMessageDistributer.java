package us.dot.its.jpo.ode.bsm;

import javax.websocket.Session;

import org.springframework.util.SerializationUtils;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.WebSocketUtils;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class BsmMessageDistributer extends MessageProcessor<String, byte[]> {
	
	private Session clientSession;
	
	@Override
	public Object call() throws Exception {
	    J2735Bsm bsm = (J2735Bsm) SerializationUtils.deserialize(record.value());
	    
		// send the data to client
		WebSocketUtils.send(clientSession, bsm.toJson());
		return null;
	}

	public Session getClientSession() {
		return clientSession;
	}

	public void setClientSession(Session clientSession) {
		this.clientSession = clientSession;
	}

}
