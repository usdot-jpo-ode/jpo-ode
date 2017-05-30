package us.dot.its.jpo.ode.util;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    
    private CommonUtils() {}
   
   public static long getPidOfProcess(Process p) {
      long pid = -1;

      try {
        if (p.getClass().getName().equals("java.lang.UNIXProcess")) {	//NOSONAR
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

   public static List<URL> getClasspath() {
	   ArrayList<URL> classpath = new ArrayList<>();
	   
       ClassLoader cl = ClassLoader.getSystemClassLoader();

       URL[] urls = ((URLClassLoader)cl).getURLs();

       for(URL url: urls){
    	   classpath.add(url);
       }
       
       return classpath;
   }
}
