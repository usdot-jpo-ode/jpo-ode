package us.dot.its.jpo.ode.importer;

public interface LogFileParser {

    public enum ParserStatus {
        UNKNOWN, INIT, NA, PARTIAL, COMPLETE, EOF
    }
    

}
