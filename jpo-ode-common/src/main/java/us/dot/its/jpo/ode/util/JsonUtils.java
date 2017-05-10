/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
	   private static Gson gsonCompact;
	   private static Gson gsonVerbose;
	   private static ObjectMapper mapper;
	   private static Logger logger;
    private JsonUtils() {
    	logger = LoggerFactory.getLogger(JsonUtils.class);
    }
   
  
   
   static {
      gsonCompact = new GsonBuilder().create();
      gsonVerbose = new GsonBuilder().serializeNulls().create();
      mapper = new ObjectMapper();
   }

   public static String toJson(Object o, boolean verbose) {

      // convert java object to JSON format,
      // and returned as JSON formatted string
      return verbose ? gsonVerbose.toJson(o) : gsonCompact.toJson(o);
//      String json = null;
//      try {
//         json = mapper.writeValueAsString(o);
//      } catch (JsonProcessingException e) {
//         e.printStackTrace();
//      }
//      return json;
   }

   public static Object fromJson(String s, Class<?> clazz) {
      return gsonCompact.fromJson(s, clazz);
      /*Object o = null;
      try {
         o = mapper.readValue(s, clazz);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return o;*/
   }
   
// This method does not seem to work so commenting it out.
//   public static Object fromObjectNode(JsonNode s, Class<?> clazz) {
//      Object o = null;
//      try {
//         o = mapper.treeToValue(s, clazz);
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
//      return o;
//   }
   
   public static String newJson(String key, Object value) {
   	return newObjectNode(key, value).toString();
   }

   public static ObjectNode newObjectNode(String key, Object value) {
      
//    JsonObject json = new JsonObject();
//    
//    json.add(key, gson.toJsonTree(value));

      ObjectNode json = mapper.createObjectNode();
      json.putPOJO(key, value);
      return json;
   }
   
   public static ObjectNode addNode(ObjectNode tree, String fieldName, Object fieldValue) {
      tree.putPOJO(fieldName, fieldValue);
      return tree;
   }
   
   public static JsonNode getJsonNode(String tree, String fieldName) {
      JsonNode node = null;
      try {
         JsonNode jsonNode = mapper.readTree(tree);
         node = jsonNode.get(fieldName);
         
      } catch (IOException e) {
    	  logger.error("IOException", e);
      }
      return node;
   }

   public static ObjectNode newNode() {
      return mapper.createObjectNode();
   }

   public static ObjectNode toObjectNode(String tree) 
         throws JsonProcessingException, IOException {
      ObjectNode jsonNode = (ObjectNode) mapper.readTree(tree);
      return jsonNode;
   }
   
   public static boolean isValid(String tree) throws IOException {
      try {
         ObjectNode jsonNode = (ObjectNode) mapper.readTree(tree);
         return jsonNode != null;
      } catch (JsonProcessingException jpe) {
         return false;
      }
   }

   public static HashMap<String, JsonNode> jsonNodeToHashMap(
         JsonNode jsonNode) {
      HashMap<String, JsonNode> nodeProps = new HashMap<String, JsonNode>();
      Iterator<Entry<String, JsonNode>> iter = jsonNode.fields();
      
      while(iter.hasNext()) {
         Entry<String, JsonNode> element = iter.next();
         nodeProps.put(element.getKey(), element.getValue());
      }
      return nodeProps;
   }

}
