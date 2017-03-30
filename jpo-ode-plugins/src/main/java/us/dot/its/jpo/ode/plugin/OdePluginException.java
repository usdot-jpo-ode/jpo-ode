package us.dot.its.jpo.ode.plugin;

public class OdePluginException extends Exception {
    private static final long serialVersionUID = 1L;

    public OdePluginException(String string, Exception e) {
        super(string, e);
    }
}
