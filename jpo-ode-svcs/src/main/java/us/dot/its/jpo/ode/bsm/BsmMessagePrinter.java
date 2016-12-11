package us.dot.its.jpo.ode.bsm;

import org.springframework.util.SerializationUtils;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class BsmMessagePrinter extends MessageProcessor<String, byte[]> {
	
	@Override
	public Object call() throws Exception {
	    J2735Bsm bsm = (J2735Bsm) SerializationUtils.deserialize(record.value());
	    System.out.println(bsm.toJson());
		return null;
	}

}
