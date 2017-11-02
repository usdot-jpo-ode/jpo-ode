package us.dot.its.jpo.ode.stomp;

public class RegistrationMessage {

    private String name;

    public RegistrationMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
