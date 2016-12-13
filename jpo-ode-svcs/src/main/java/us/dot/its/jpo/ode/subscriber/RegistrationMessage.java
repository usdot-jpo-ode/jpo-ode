package us.dot.its.jpo.ode.subscriber;

public class RegistrationMessage {

    private String name;

    public RegistrationMessage() {
    }

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
