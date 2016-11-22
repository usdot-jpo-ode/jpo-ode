package us.dot.its.jpo.ode.plugin;

import java.io.IOException;
import java.util.Properties;

public interface OdePlugin {
	void load (Properties properties) throws ClassNotFoundException, IOException;

}
