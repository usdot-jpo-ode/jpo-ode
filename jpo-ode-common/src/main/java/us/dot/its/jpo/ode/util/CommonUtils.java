/*******************************************************************************
 * Copyright 2018 572682
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
package us.dot.its.jpo.ode.util;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
    
    private CommonUtils() {}
    
    /**
     * Wrapper method for System.getenv()
    * @param name Variable name
    */
   public static String getEnvironmentVariable(String name) {
       return System.getenv(name);
    }
   
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
   
   public static String enumToString(Class<?> clazz, String enumNameOrOrdinal) {
     
     String enumName = null;
     try {
       Object[] enumConstants = clazz.getEnumConstants();
       if (enumConstants != null) {
         int enumOrdinal = Integer.parseInt(enumNameOrOrdinal);
         enumName = enumConstants[enumOrdinal].toString();
       }
     } catch (NumberFormatException e) {
       enumName = enumNameOrOrdinal;
     }
     return enumName;
   }
}
