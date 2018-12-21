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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OdePluginImpl implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(OdePluginImpl.class);

    public static class OdePluginException extends Exception {

        private static final long serialVersionUID = 1L;

        public OdePluginException(String string, Exception e) {
            super(string, e);
        }

    }

    private List<Class<?>> classes = new ArrayList<>();

    @Override
    public void load(Properties properties) throws OdePluginException {
        Policy.setPolicy(new PluginPolicy());
        System.setSecurityManager(new SecurityManager());

        File plugins = new File("plugins");

        if (plugins.exists() && plugins.isDirectory()) {
            File[] files = plugins.listFiles();

            for (File pluginJarFile : files) {
                loadAllClasses(pluginJarFile);
            }
        }
    }

    private void loadAllClasses(File file) throws OdePluginException {

        URLClassLoader loader = null;
        try (JarFile jarFile = new JarFile(file)) {

            Enumeration<JarEntry> e = jarFile.entries();

            loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                classes.add(loader.loadClass(className));

            }
            loader.close();
            jarFile.close();

        } catch (Exception e) {
            throw new OdePluginException("Error loading plugins", e);
        } finally {
            if (null != loader)
                try {
                    loader.close();
                } catch (IOException e) {
                    logger.debug("Error closing URLClassLoader: {}", e);
                }
        }
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

}
