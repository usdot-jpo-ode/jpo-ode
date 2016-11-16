package us.dot.its.jpo.ode.util;

import java.lang.reflect.Field;

public class CommonUtils {
   
   public static long getPidOfProcess(Process p) {
      long pid = -1;

      try {
        if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
          Field f = p.getClass().getDeclaredField("pid");
          f.setAccessible(true);
          pid = f.getLong(p);
          f.setAccessible(false);
        }
      } catch (Exception e) {
        pid = -1;
      }
      return pid;
    }

}
