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

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.event.ResponseEvent;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class RsuDepositor extends Thread {
    private final boolean dataSigningEnabled;
    private volatile boolean running = true;
    private final RsuProperties rsuProperties;
    private final ArrayList<RsuDepositorEntry> depositorEntries = new ArrayList<>();

    protected static class RsuDepositorEntry {
        public RsuDepositorEntry(ServiceRequest request, String encodedMsg) {
            this.request = request;
            this.encodedMsg = encodedMsg;
        }

        ServiceRequest request;
        String encodedMsg;
    }

    public RsuDepositor(RsuProperties rsuProperties, boolean isDataSigningEnabled) {
        this.rsuProperties = rsuProperties;
        this.dataSigningEnabled = isDataSigningEnabled;
    }

    public void shutdown() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            while (running) {
                RsuDepositorEntry[] entryList;
                synchronized (depositorEntries) {
                    entryList = new RsuDepositorEntry[depositorEntries.size()];
                    entryList = depositorEntries.toArray(entryList);
                    depositorEntries.clear();
                }

                for (RsuDepositorEntry entry : entryList) {
                    HashMap<String, String> responseList = new HashMap<>();
                    for (RSU curRsu : entry.request.getRsus()) {

                        TimTransmogrifier.updateRsuCreds(curRsu, rsuProperties);
                        String httpResponseStatus;
                        try {
                            ResponseEvent rsuResponse = SnmpSession.createAndSend(entry.request.getSnmp(),
                                    curRsu,
                                    entry.encodedMsg,
                                    entry.request.getOde().getVerb(),
                                    dataSigningEnabled);
                            httpResponseStatus = getResponseStatus(rsuResponse, curRsu);
                        } catch (IOException | ParseException e) {
                            String msg = "Exception caught in TIM RSU deposit loop.";
                            EventLogger.logger.error(msg, e);
                            log.error(msg, e);
                            httpResponseStatus = e.getClass().getName() + ": " + e.getMessage();
                        }

                        responseList.put(curRsu.getRsuTarget(), httpResponseStatus);
                    }
                    log.info("TIM deposit response {}", responseList);
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            log.error("RsuDepositor thread interrupted", e);
        }
    }

    private String getResponseStatus(ResponseEvent rsuResponse, RSU curRsu) {
        String httpResponseStatus;

        if (null == rsuResponse || null == rsuResponse.getResponse()) {
            // Timeout
            httpResponseStatus = "Timeout";
            log.error("Error on RSU SNMP deposit to {}: timed out.", curRsu.getRsuTarget());
            return httpResponseStatus;
        }

        RsuResponseCode responseCode = RsuResponseCode.fromInt(rsuResponse.getResponse().getErrorStatus());
        switch (responseCode) {
            case SUCCESS:
                httpResponseStatus = "Success";
                log.info("RSU SNMP deposit to {} successful.", curRsu.getRsuTarget());
                break;
            case DUPLICATE_MESSAGE:
                httpResponseStatus = "Message already exists at ".concat(Integer.toString(curRsu.getRsuIndex()));
                Integer destIndex = curRsu.getRsuIndex();
                log.error("Error on RSU SNMP deposit to {}: message already exists at index {}.", curRsu.getRsuTarget(),
                        destIndex);
                break;
            case POSSIBLE_SNMP_PROTOCOL_MISMATCH:
                httpResponseStatus = "Possible SNMP protocol mismatch, check RSU configuration";
                log.error("Error on RSU SNMP deposit to {}: Possible SNMP protocol mismatch, check RSU configuration.",
                        curRsu.getRsuTarget());
                break;
            case null, default:
                httpResponseStatus = "Error code " + rsuResponse.getResponse().getErrorStatus() + " "
                        + rsuResponse.getResponse().getErrorStatusText();
                // Misc error
                log.error("Error on RSU SNMP deposit to {}: Error code '{}' '{}'", curRsu.getRsuTarget(), rsuResponse.getResponse().getErrorStatus(), rsuResponse.getResponse().getErrorStatusText() + "'");
                // Log the PDUs involved in the failed deposit
                log.debug("PDUs involved in failed RSU SNMP deposit to {} => Request PDU: {} Response PDU: {}", curRsu.getRsuTarget(), rsuResponse.getRequest(), rsuResponse.getResponse());
                break;
        }

        return httpResponseStatus;
    }

    public void deposit(ServiceRequest request, String encodedMsg) {
        synchronized (depositorEntries) {
            depositorEntries.add(new RsuDepositorEntry(request, encodedMsg));
        }
    }

    protected ArrayList<RsuDepositorEntry> getDepositorEntries() {
        synchronized (depositorEntries) {
            return depositorEntries;
        }
    }
}
