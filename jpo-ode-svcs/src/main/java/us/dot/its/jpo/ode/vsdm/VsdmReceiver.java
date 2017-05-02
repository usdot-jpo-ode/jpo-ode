package us.dot.its.jpo.ode.vsdm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.asn1.j2735.CVSampleMessageBuilder;
import gov.usdot.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;

public class VsdmReceiver implements Runnable{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DatagramSocket socket = null;
	private static Coder coder = J2735.getPERUnalignedCoder();
	
	public VsdmReceiver(){
		try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean flag = true;
		 while (flag) {
	            try {
	            	logger.info("---------------------- SDC: Listening on port 4445...");
	                byte[] buf = new byte[10000];

	                System.out.println("SDC: Waiting for VSD Deposit ServiceRequest ...");
	                DatagramPacket packet = new DatagramPacket(buf, buf.length);
	                socket.receive(packet);
	                System.out.println("SDC: Received VSD deposit ServiceRequest ...");
	    			if (buf != null && buf.length > 0) {
	    				AbstractData request = J2735Util.decode(coder, buf);
	    				if (request instanceof ServiceRequest) {
	    					System.out.println("SDC: Printing VSD deposit ServiceRequest ...");
	    					System.out.println(request);
	    				}
	    			}
	                
	                ServiceResponse sr = CVSampleMessageBuilder.buildVehicleSituationDataServiceResponse();
	        		
	        		ByteArrayOutputStream sink = new ByteArrayOutputStream();
	        		coder.encode(sr, sink);
	        		
	        		byte [] payload = sink.toByteArray();
	        		int length = payload.length;
	        		
	        		System.out.println("---------------------- SDC: Sending VSD deposit ServiceResponse ...");
	                InetAddress address = packet.getAddress();
	                int port = packet.getPort();
	        		socket.send(new DatagramPacket(
	        			payload,
	        			length,
	        			new InetSocketAddress(address, port)
	        		));
	        		
	                byte[] vsdBuffer = new byte[10000];

	                System.out.println("SDC: Waiting for VSD message ...");
	                packet = new DatagramPacket(vsdBuffer, vsdBuffer.length);
	                socket.receive(packet);
	                System.out.println("SDC: Received VSD message ...");
	    			if (vsdBuffer != null && vsdBuffer.length > 0) {
	    				AbstractData vsd = J2735Util.decode(coder, vsdBuffer);
	    				if (vsd instanceof VehSitDataMessage) {
	    					System.out.println("SDC: Printing VSD message ...");
	    					System.out.println(vsd);
	    				}
	    			}
	            } catch (IOException | DecodeFailedException | DecodeNotSupportedException | EncodeFailedException | EncodeNotSupportedException e) {
	                e.printStackTrace();
	            }
	        }
	}

}