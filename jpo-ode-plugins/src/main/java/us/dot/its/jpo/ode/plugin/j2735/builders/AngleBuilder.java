package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class AngleBuilder {
    
    private AngleBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericAngle(JsonNode angle) {

        if (angle.asInt() < 0 || angle.asInt() > 28800) {
            throw new IllegalArgumentException("Angle value out of bounds");
        }

        BigDecimal result = null;

        if (angle.asLong() != 28800) {
            result = longToDecimal(angle.asLong());
        }

        return result;
    }

    public static BigDecimal longToDecimal(long longValue) {
        
        BigDecimal result = null;
        
        if (longValue != 28800) {
            result = BigDecimal.valueOf(longValue * 125, 4);
        }
        
        return result;
    }
    
    public static JsonNode angle(long ang) {
       return JsonUtils.newObjectNode("angle", 
          BigDecimal.valueOf(ang).divide(BigDecimal.valueOf(0.0125)).intValue());
    }
    
    public static int angle(BigDecimal ang) {
       return ang.divide(BigDecimal.valueOf(0.0125)).intValue();
    }

}
