package us.dot.its.jpo.ode.plugin;

import java.util.Properties;

import us.dot.its.jpo.ode.plugin.OdePluginException;

@FunctionalInterface
public interface Plugin {

	void load(Properties properties) throws OdePluginException;

}
