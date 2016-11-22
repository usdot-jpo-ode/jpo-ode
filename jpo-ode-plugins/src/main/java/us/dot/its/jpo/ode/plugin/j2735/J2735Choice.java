package us.dot.its.jpo.ode.plugin.j2735;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public abstract class J2735Choice implements Asn1Object {

   private static final long serialVersionUID = 2187214491924559144L;
   
   private static final Logger logger = LoggerFactory.getLogger(J2735Choice.class);

   private String chosenFieldName;

   public J2735Choice() {
      super();
   }

   public String getChosenFieldName() {
      return chosenFieldName;
   }

   public void setChosenFieldName(String fieldName) {
      this.chosenFieldName = fieldName;
   }

   public void setChosenField(String fieldName, Object fieldValue) {
      this.chosenFieldName = fieldName;
      Field field;
      try {
         field = this.getClass().getDeclaredField(fieldName);
         field.setAccessible(true);
         field.set(this, fieldValue);
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
         logger.error("Error Setting Chosen Field", e);
      }
      
   }

}
