package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PropelledInformation extends Asn1Object  {
    
    private static final long serialVersionUID = 1L;

    private J2735HumanPropelledType human;
    private J2735AnimalPropelledType animal;
    private J2735MotorizedPropelledType motor;

    public J2735HumanPropelledType getHuman() {
        return this.human;
    }

    public void setHuman(J2735HumanPropelledType human) {
        this.human = human;
    }

    public J2735AnimalPropelledType getAnimal() {
        return this.animal;
    }

    public void setAnimal(J2735AnimalPropelledType animal) {
        this.animal = animal;
    }

    public J2735MotorizedPropelledType getMotor() {
        return this.motor;
    }

    public void setMotor(J2735MotorizedPropelledType motor) {
        this.motor = motor;
    }

}