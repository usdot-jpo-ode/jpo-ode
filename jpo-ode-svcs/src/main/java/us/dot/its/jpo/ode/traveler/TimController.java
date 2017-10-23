package us.dot.its.jpo.ode.traveler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@Controller
public class TimController {

   public static class TimControllerException extends Exception {

      private static final long serialVersionUID = 1L;

      public TimControllerException(String errMsg, Exception e) {
         super(errMsg, e);
      }

   }

   private static final Logger logger = LoggerFactory.getLogger(TimController.class);

   private static final String ERRSTR = "error";
   private static final int THREADPOOL_MULTIPLIER = 3; // multiplier of threads
                                                       // needed

   private OdeProperties odeProperties;
   private MessageProducer<String, String> messageProducer;

   private final ExecutorService threadPool;

   @Autowired
   public TimController(OdeProperties odeProperties) {
      super();
      this.odeProperties = odeProperties;

      this.messageProducer = MessageProducer.defaultStringMessageProducer(
         odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType());

      this.threadPool = Executors.newFixedThreadPool(odeProperties.getRsuSrmSlots() * THREADPOOL_MULTIPLIER);
   }

   /**
    * Checks given RSU for all TIMs set
    * 
    * @param jsonString
    *           Request body containing RSU info
    * @return list of occupied TIM slots on RSU
    */
   @ResponseBody
   @CrossOrigin
   @RequestMapping(value = "/tim/query", method = RequestMethod.POST)
   public synchronized ResponseEntity<String> asyncQueryForTims(@RequestBody String jsonString) { // NOSONAR

      if (null == jsonString || jsonString.isEmpty()) {
         logger.error("Empty request.");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, "Empty request."));
      }

      ConcurrentSkipListMap<Integer, Integer> resultTable = new ConcurrentSkipListMap<>();
      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);

      SnmpSession snmpSession = null;
      try {
         snmpSession = new SnmpSession(queryTarget);
         snmpSession.startListen();
      } catch (IOException e) {
         logger.error("Error creating SNMP session.", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(jsonKeyValue(ERRSTR, "Failed to create SNMP session."));
      }

      // Repeatedly query the RSU to establish set rows
      List<Callable<Object>> queryThreadList = new ArrayList<>();

      for (int i = 0; i < odeProperties.getRsuSrmSlots(); i++) {
         ScopedPDU pdu = new ScopedPDU();
         pdu.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(i)))));
         pdu.setType(PDU.GET);
         queryThreadList.add(Executors
               .callable(new TimQueryThread(snmpSession.getSnmp(), pdu, snmpSession.getTarget(), resultTable, i)));
      }

      try {
         threadPool.invokeAll(queryThreadList);
      } catch (InterruptedException e) { // NOSONAR
         logger.error("Error submitting query threads for execution.", e);
         threadPool.shutdownNow();
      }

      try {
         snmpSession.endSession();
      } catch (IOException e) {
         logger.error("Error closing SNMP session.", e);
      }

      if (resultTable.containsValue(TimQueryThread.TIMEOUT_FLAG)) {
         logger.error("TIM query timed out.");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(jsonKeyValue(ERRSTR, "Query timeout, increase retries."));
      } else {
         logger.info("TIM query successful: {}", resultTable.keySet());
         return ResponseEntity.status(HttpStatus.OK).body("{\"indicies_set\":" + resultTable.keySet() + "}");
      }
   }

   @ResponseBody
   @CrossOrigin
   @RequestMapping(value = "/tim", method = RequestMethod.DELETE)
   public ResponseEntity<String> deleteTim(@RequestBody String jsonString,
         @RequestParam(value = "index", required = true) Integer index) { // NOSONAR

      if (null == jsonString) {
         logger.error("Empty request");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, "Empty request"));
      }

      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);

      logger.info("TIM delete call, RSU info {}", queryTarget);

      SnmpSession ss = null;
      try {
         ss = new SnmpSession(queryTarget);
      } catch (IOException e) {
         logger.error("Error creating TIM delete SNMP session", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonKeyValue(ERRSTR, e.getMessage()));
      } catch (NullPointerException e) {
         logger.error("TIM query error, malformed JSON", e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, "Malformed JSON"));
      }

      PDU pdu = new ScopedPDU();
      pdu.add(new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(index))), new Integer32(6)));
      pdu.setType(PDU.SET);

      ResponseEvent rsuResponse = null;
      try {
         rsuResponse = ss.set(pdu, ss.getSnmp(), ss.getTarget(), false);
      } catch (IOException e) {
         logger.error("Error sending TIM query PDU to RSU", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonKeyValue(ERRSTR, e.getMessage()));
      }

      // Try to explain common errors
      HttpStatus returnCode = null;
      String bodyMsg = "";
      if (null == rsuResponse || null == rsuResponse.getResponse()) {
         // Timeout
         returnCode = HttpStatus.REQUEST_TIMEOUT;
         bodyMsg = jsonKeyValue(ERRSTR, "Timeout.");
      } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
         // Success
         returnCode = HttpStatus.OK;
         bodyMsg = jsonKeyValue("deleted_msg", Integer.toString(index));
      } else if (rsuResponse.getResponse().getErrorStatus() == 12) {
         // Message previously deleted or doesn't exist
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = jsonKeyValue(ERRSTR, "No message at index ".concat(Integer.toString(index)));
      } else if (rsuResponse.getResponse().getErrorStatus() == 10) {
         // Invalid index
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = jsonKeyValue(ERRSTR, "Invalid index ".concat(Integer.toString(index)));
      } else {
         // Misc error
         returnCode = HttpStatus.BAD_REQUEST;
         bodyMsg = jsonKeyValue(ERRSTR, rsuResponse.getResponse().getErrorStatusText());
      }

      logger.info("Delete call response code: {}, message: {}", returnCode, bodyMsg);

      return ResponseEntity.status(returnCode).body(bodyMsg);
   }

   /**
    * Deposit a TIM
    * 
    * @param jsonString
    *           TIM in JSON
    * @return list of success/failures
    */
   @ResponseBody
   @RequestMapping(value = "/tim", method = RequestMethod.POST, produces = "application/json")
   @CrossOrigin
   public ResponseEntity<String> postTim(@RequestBody String jsonString) {

      // Check empty
      if (null == jsonString || jsonString.isEmpty()) {
         String errMsg = "Empty request.";
         logger.error(errMsg);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      // Convert JSON to POJO
      ObjectNode travelerinputData = null;
      try {
         travelerinputData = JsonUtils.toObjectNode(jsonString);

         logger.debug("J2735TravelerInputData: {}", jsonString);

      } catch (Exception e) {
         String errMsg = "Malformed or non-compliant JSON.";
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      // TODO 
      //((ObjectNode) travelerinputData.get("ode")).put("index", travelerinputData.get("tim").get("index").asInt());
   
      // Craft ASN-encodable TIM
      
      ObjectNode encodableTim;
      try {
         encodableTim = TravelerMessageFromHumanToAsnConverter
               .changeTravelerInformationToAsnValues(travelerinputData);
         
         logger.debug("Encodable TIM: {}", encodableTim);
         
      } catch (Exception e) {
         String errMsg = "Error converting to encodable TIM.";
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      // Encode TIM
      try {
         publish(encodableTim.toString());
      } catch (Exception e) {
         String errMsg = "Error sending data to ASN.1 Encoder module: " + e.getMessage();
         logger.error(errMsg, e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonKeyValue(ERRSTR, errMsg));
      }

      return ResponseEntity.status(HttpStatus.OK).body("Success");
   }

   /**
    * Takes in a key, value pair and returns a valid json string such as
    * {"error":"message"}
    * 
    * @param key
    * @param value
    * @return
    */
   public String jsonKeyValue(String key, String value) {
      return "{\"" + key + "\":\"" + value + "\"}";
   }

   private void publish(String request) throws JsonUtilsException, XmlUtilsException {
      JSONObject requestObj = JsonUtils.toJSONObject(request);
      
      //Create valid payload from scratch
      OdeMsgPayload payload = new OdeMsgPayload();
      payload.setDataType("MessageFrame");
      JSONObject payloadObj = JsonUtils.toJSONObject(payload.toJson());

      //Create TravelerInformation
      JSONObject timObject = new JSONObject();
      //requestObj = new JSONObject(requestObj.toString().replace("\"tcontent\":","\"content\":"));
      timObject.put("TravelerInformation", requestObj.remove("tim")); //with "tim" removed, the remaining requestObject must go in as "request" element of metadata
      
      //Create a MessageFrame
      JSONObject mfObject = new JSONObject();
      mfObject.put("value", timObject);//new JSONObject().put("TravelerInformation", requestObj));
      mfObject.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
      
      JSONObject dataObj = new JSONObject();
      dataObj.put("MessageFrame", mfObject);
      
      payloadObj.put(AppContext.DATA_STRING, dataObj);
      
      //Create a valid metadata from scratch
      OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
      
      JSONObject metaObject = JsonUtils.toJSONObject(metadata.toJson());
      metaObject.put("request", requestObj);
      
      //Create an encoding element
      //Asn1Encoding enc = new Asn1Encoding("/payload/data/MessageFrame", "MessageFrame", EncodingRule.UPER);
      Asn1Encoding enc = new Asn1Encoding("root", "MessageFrame", EncodingRule.UPER);
      
      // TODO this nesting is to match encoder schema
      metaObject.put("encodings", new JSONObject().put("encodings", JsonUtils.toJSONObject(enc.toJson())));
      
      JSONObject message = new JSONObject();
      message.put(AppContext.METADATA_STRING, metaObject);
      message.put(AppContext.PAYLOAD_STRING, payloadObj);
      
      JSONObject root = new JSONObject();
      root.put("OdeAsn1Data", message);
      
      // workaround for the "content" bug
      String outputXml = XML.toString(root);
      String fixedXml = outputXml.replaceAll("tcontent>","content>");
      
      // workaround for self-closing tags: transform all "null" fields into empty tags
      fixedXml = fixedXml.replaceAll("EMPTY_TAG", "");
      
      logger.debug("Fixed XML: {}", fixedXml);
      messageProducer.send(odeProperties.getKafkaTopicAsn1EncoderInput(), null, fixedXml);
      //messageProducer.send(odeProperties.getKafkaTopicAsn1EncoderInput(), null, "<OdeAsn1Data><metadata><request><ode><version>2</version></ode><sdw><serviceRegion><nwCorner><latitude>44.998459</latitude><longitude>-111.040817</longitude></nwCorner><seCorner><latitude>41.104674</latitude><longitude>-104.111312</longitude></seCorner></serviceRegion><ttl>oneweek</ttl></sdw><rsus><rsuTarget>127.0.0.1</rsuTarget><rsuUsername>v3user</rsuUsername><rsuRetries>0</rsuRetries><rsuTimeout>2000</rsuTimeout><rsuPassword>password</rsuPassword></rsus><rsus><rsuTarget>127.0.0.2</rsuTarget><rsuUsername>v3user</rsuUsername><rsuRetries>1</rsuRetries><rsuTimeout>1000</rsuTimeout><rsuPassword>password</rsuPassword></rsus><rsus><rsuTarget>127.0.0.3</rsuTarget><rsuUsername>v3user</rsuUsername><rsuRetries>1</rsuRetries><rsuTimeout>1000</rsuTimeout><rsuPassword>password</rsuPassword></rsus><snmp><mode>1</mode><deliverystop>2018-12-01T17:47:11-05:15</deliverystop><rsuid>0083</rsuid><deliverystart>2017-12-01T17:47:11-05:00</deliverystart><enable>1</enable><channel>178</channel><msgid>31</msgid><interval>1</interval><status>4</status></snmp></request><payloadType>us.dot.its.jpo.ode.model.OdeTimPayload</payloadType><serialId><streamId>cfaa63fb-e3f8-4c01-9516-648a95bd1cbe</streamId><bundleSize>1</bundleSize><bundleId>2</bundleId><recordId>2</recordId><serialNumber>0</serialNumber></serialId><receivedAt>2017-10-04T13:41:47.862Z[UTC]</receivedAt><schemaVersion>2</schemaVersion><generatedAt>2017-08-10T21:02:14.799Z[UTC]</generatedAt><logFileName>TimLogDuringEvent_1504882236_2001_3A470_3A41af_3A1_3A0_3A63ff_3Afead_3A525.csv</logFileName><validSignature>true</validSignature><sanitized>false</sanitized><encodings><encodings><elementName>root</elementName><elementType>MessageFrame</elementType><encodingRule>UPER</encodingRule></encodings></encodings></metadata><payload><dataType>us.dot.its.jpo.ode.model.OdeHexByteArray</dataType><data><MessageFrame><messageId>31</messageId><value><TravelerInformation><msgCnt>1</msgCnt><timeStamp>309505</timeStamp><packetID>000000000000000000</packetID><urlB>null</urlB><dataFrames><TravelerDataFrame><sspTimRights>0</sspTimRights><frameType><advisory/></frameType><msgId><roadSignID><position><lat>416784730</lat><long>-1087827750</long><elevation>9171</elevation></position><viewAngle>0101010101010100</viewAngle><mutcdCode><guide/></mutcdCode><crc>0000</crc></roadSignID></msgId><startYear>2017</startYear><startTime>308065</startTime><duratonTime>1</duratonTime><priority>0</priority><sspLocationRights>3</sspLocationRights><regions><GeographicalPath><name>TestingTIM</name><id><region>0</region><id>33</id></id><anchor><lat>412500807</lat><long>-1110093847</long><elevation>20206</elevation></anchor><laneWidth>700</laneWidth><directionality><both/></directionality><closedPath><false/></closedPath><direction>0000000000010100</direction><description><path><scale>0</scale><offset><ll><nodes><NodeLL><delta><node-LL3><lon>14506</lon><lat>31024</lat></node-LL3></delta></NodeLL><NodeLL><delta><node-LL3><lon>14568</lon><lat>30974</lat></node-LL3></delta></NodeLL><NodeLL><delta><node-LL3><lon>14559</lon><lat>30983</lat></node-LL3></delta></NodeLL><NodeLL><delta><node-LL3><lon>14563</lon><lat>30980</lat></node-LL3></delta></NodeLL><NodeLL><delta><node-LL3><lon>14562</lon><lat>30982</lat></node-LL3></delta></NodeLL></nodes></ll></offset></path></description></GeographicalPath></regions><sspMsgRights1>2</sspMsgRights1><sspMsgRights2>3</sspMsgRights2><content><advisory><SEQUENCE><item><itis>513</itis></item></SEQUENCE></advisory></content><url>null</url></TravelerDataFrame></dataFrames></TravelerInformation></value></MessageFrame></data></payload></OdeAsn1Data>");
   }


}