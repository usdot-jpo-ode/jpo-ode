/*******************************************************************************
 * Copyright 2020 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.rsu;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier;

public class RsuDepositor extends Thread {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private volatile boolean running = true;
	private OdeProperties odeProperties; 
	private ArrayList<RsuDepositorEntry> depositorEntries = new ArrayList<RsuDepositorEntry>();
	


	class RsuDepositorEntry{
		public RsuDepositorEntry(ServiceRequest request, String encodedMsg) {
			this.request = request;
			this.encodedMsg = encodedMsg;
		}
		ServiceRequest request;
		String encodedMsg;		
	}
	
	public RsuDepositor(OdeProperties odeProperties) {
		this.odeProperties = odeProperties;		
	}

	public OdeProperties getOdeProperties() {
		return odeProperties;
	}

	public void setOdeProperties(OdeProperties odeProperties) {
		this.odeProperties = odeProperties;
	}
	
	public void shutdown() {
		running = false;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void run() {
		try {
			while (running)
			{
				RsuDepositorEntry[] entryList = new RsuDepositorEntry[0];
				synchronized(depositorEntries)
				{
					entryList = new RsuDepositorEntry[depositorEntries.size()];
					entryList = depositorEntries.toArray(entryList);
					depositorEntries.clear();
				}

				for (RsuDepositorEntry entry : entryList) {
					HashMap<String, String> responseList = new HashMap<>();
					for (RSU curRsu : entry.request.getRsus()) {

						TimTransmogrifier.updateRsuCreds(curRsu, odeProperties);

						ResponseEvent rsuResponse = null;

						String httpResponseStatus;
						try {
							rsuResponse = SnmpSession.createAndSend(entry.request.getSnmp(), curRsu, entry.encodedMsg, entry.request.getOde().getVerb());
							if (null == rsuResponse || null == rsuResponse.getResponse()) {
								// Timeout
								httpResponseStatus = "Timeout";
							} else if (rsuResponse.getResponse().getErrorStatus() == 0) {
								// Success
								httpResponseStatus = "Success";
							} else if (rsuResponse.getResponse().getErrorStatus() == 5) {
								// Error, message already exists
								httpResponseStatus = "Message already exists at ".concat(Integer.toString(curRsu.getRsuIndex()));
							} else {
								// Misc error
								httpResponseStatus = "Error code " + rsuResponse.getResponse().getErrorStatus() + " "
										+ rsuResponse.getResponse().getErrorStatusText();
							}
						} catch (IOException | ParseException e) {
							String msg = "Exception caught in TIM RSU deposit loop.";
							EventLogger.logger.error(msg, e);
							logger.error(msg, e);
							httpResponseStatus = e.getClass().getName() + ": " + e.getMessage();
						}
						responseList.put(curRsu.getRsuTarget(), httpResponseStatus);

						if (null == rsuResponse || null == rsuResponse.getResponse()) {
							// Timeout
							logger.error("Error on RSU SNMP deposit to {}: timed out.", curRsu.getRsuTarget());
						} else if (rsuResponse.getResponse().getErrorStatus() == 0) {
							// Success
							logger.info("RSU SNMP deposit to {} successful.", curRsu.getRsuTarget());
						} else if (rsuResponse.getResponse().getErrorStatus() == 5) {
							// Error, message already exists
							Integer destIndex = curRsu.getRsuIndex();
							logger.error("Error on RSU SNMP deposit to {}: message already exists at index {}.", curRsu.getRsuTarget(),
									destIndex);
						} else {
							// Misc error
							logger.error("Error on RSU SNMP deposit to {}: {}", curRsu.getRsuTarget(), "Error code "
									+ rsuResponse.getResponse().getErrorStatus() + " " + rsuResponse.getResponse().getErrorStatusText());
						}

					}
					logger.info("TIM deposit response {}", responseList);					
				}
				Thread.sleep(100);
			}
		}
		catch (InterruptedException e) {
			logger.error("RsuDepositor thread interrupted", e);
		}
	}
	
	public void deposit(ServiceRequest request, String encodedMsg) {
		synchronized(depositorEntries)
		{
			depositorEntries.add(new RsuDepositorEntry(request, encodedMsg));			
		}
	}

}
