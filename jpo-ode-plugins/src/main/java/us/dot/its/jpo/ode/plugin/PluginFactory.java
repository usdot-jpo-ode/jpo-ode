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
package us.dot.its.jpo.ode.plugin;

import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Return concrete implementations for specific, known interfaces. */
public final class PluginFactory {

   private static Logger logger = LoggerFactory.getLogger(PluginFactory.class);

   private PluginFactory() {
      throw new UnsupportedOperationException();
   }

   /**
    * Read in configuration data that maps names of interfaces to names of
    * corresponding concrete implementation classes. Called early upon startup,
    * before any implementations are needed by the rest of the program.
    * 
    * <P>
    * Example of a possible entry in such a config file : myapp.TimeSource =
    * myapp.TimeSourceOneDayAdvance
    */
   public static void init() {
      // TODO
      // perhaps a properties file is read, perhaps some other source is used
   }

   /*
    * Another variation: allow the caller to swap in different implementation
    * classes at runtime, after calling init. This allows testing code to swap
    * in various implementations.
    */

   /**
    * Return the concrete implementation of a OdePlugin interface.
    * 
    * @param coderClassName
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws ClassNotFoundException
    */
   public static OdePlugin getPluginByName(String coderClassName)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      logger.info("Getting Plugin: {}", coderClassName);
      OdePlugin result = (OdePlugin) buildObject(coderClassName);

      if (null != result) {
         String resultStr = result.toString();
         logger.info("Got Plugin: {}", resultStr);
      } else {
         logger.info("Failed to load plugin for {}", coderClassName);
      }

      return result;
   }

   private static Object buildObject(String aClassName)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      Object result = null;
      try {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();

         printClasspath(cl);

         result = buildObject(cl, aClassName);
      } catch (Exception ex) {
         logger.error("Error instantiating an object of " + aClassName, ex);

         ClassLoader cl = ClassLoader.getSystemClassLoader();
         printClasspath(cl);
         result = buildObject(cl, aClassName);
      }
      return result;
   }

   private static Object buildObject(ClassLoader cl, String aClassName)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      Object result;
      logger.info("Getting class: {}", aClassName);

      // note that, with this style, the implementation needs to have a
      // no-argument constructor!
      Class<?> implClass = cl.loadClass(aClassName);

      logger.info("creating an instance of: {}", implClass);
      result = implClass.newInstance();
      return result;
   }

   private static void printClasspath(ClassLoader cl) {
      URL[] urls = ((URLClassLoader) cl).getURLs();

      for (URL url : urls) {
         logger.info("Classpath: {}", url.getFile());
      }
   }
}
