package us.dot.its.jpo.ode.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtilsTest {

   class A {
      int i;
      float f;
      double d;
      String s;
      
      public A() {
      }

      public A(int i, float f, double d, String s) {
         super();
         this.i = i;
         this.f = f;
         this.d = d;
         this.s = s;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         long temp;
         temp = Double.doubleToLongBits(d);
         result = prime * result + (int) (temp ^ (temp >>> 32));
         result = prime * result + Float.floatToIntBits(f);
         result = prime * result + i;
         result = prime * result + ((s == null) ? 0 : s.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         A other = (A) obj;
         if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
            return false;
         if (Float.floatToIntBits(f) != Float.floatToIntBits(other.f))
            return false;
         if (i != other.i)
            return false;
         if (s == null) {
            if (other.s != null)
               return false;
         } else if (!s.equals(other.s))
            return false;
         return true;
      }

      private JsonUtilsTest getOuterType() {
         return JsonUtilsTest.this;
      }
      
      
   }
   
   private A a = new A(11, 2.2f, 33333.33333333333d, "hello");
   private String aJsonString = "{\"i\":11,\"f\":2.2,\"d\":33333.33333333333,\"s\":\"hello\"}";
   
   private A a2 = new A(11, 2.2f, 33333.33333333333d, null);
   private String a2Compact = "{\"i\":11,\"f\":2.2,\"d\":33333.33333333333}";
   private String a2Verbose = "{\"i\":11,\"f\":2.2,\"d\":33333.33333333333,\"s\":null}";
   
   @Test
   public void testToJson() {
      String aj = JsonUtils.toJson(a, false);
      assertEquals(aJsonString, aj);
      String aj2Compact = JsonUtils.toJson(a2, false);
      assertEquals(a2Compact, aj2Compact);
      String aj2Verbose = JsonUtils.toJson(a2, true);
      assertEquals(a2Verbose, aj2Verbose);
   }

   @Test
   public void testFromJson() {
      A aj = (A) JsonUtils.fromJson(aJsonString, A.class);
      assertEquals(a, aj);
   }

   @Test
   public void testNewJson() {
      String j = JsonUtils.newJson("key", "value");
      assertEquals("{\"key\":value}", j);
    }

   @Test
   public void testNewObjectNode() {
      ObjectNode j = JsonUtils.newObjectNode("key", "value");
      assertEquals("{\"key\":value}", j.toString());
   }

   @Test
   public void testAddNode() {
      ObjectNode j = JsonUtils.newObjectNode("key", "value");
      ObjectNode j2 = JsonUtils.addNode(j, "key2", "value2");
      assertEquals("{\"key\":value,\"key2\":value2}", j.toString());
      assertEquals("{\"key\":value,\"key2\":value2}", j2.toString());
   }

   @Test
   public void testGetJsonNode() {
      Object v = JsonUtils.getJsonNode("{\"key\":\"value\"}", "key");
      assertEquals("\"value\"", v.toString());
   }

   @Test
   public void testNewNode() {
      ObjectNode n = JsonUtils.newNode();
      assertEquals("{}", n.toString());
   }

   @Test
   public void testToObjectNode() throws JsonProcessingException, IOException {
      ObjectNode on = JsonUtils.toObjectNode("{\"key\":\"value\"}");
      assertEquals("{\"key\":\"value\"}", on.toString());
   }

   @Test
   public void testIsValid() throws IOException {
      assertTrue(JsonUtils.isValid("{\"key\":\"value\"}"));
      assertFalse(JsonUtils.isValid("{\"key\":value}"));
   }

   @Test
   public void testJsonNodeToHashMap() {
      ObjectNode on = JsonUtils.newObjectNode("key", "value");
      HashMap<String, JsonNode> hm = JsonUtils.jsonNodeToHashMap(on);
      assertEquals("value", hm.get("key").asText());
   }

}
