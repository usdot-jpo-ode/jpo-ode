package us.dot.its.jpo.ode.plugin;

import java.io.IOException;
import java.util.Properties;

public interface Plugin {

	void load(Properties properties) throws ClassNotFoundException, IOException;

}
