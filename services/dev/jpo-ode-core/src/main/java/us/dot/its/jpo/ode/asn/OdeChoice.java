package us.dot.its.jpo.ode.asn;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeObject;

public abstract class OdeChoice extends OdeObject {

   private static final long serialVersionUID = 2187214491924559144L;
   
   private static final Logger logger = LoggerFactory.getLogger(OdeChoice.class);

   private String chosenFieldName;

   public OdeChoice() {
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
