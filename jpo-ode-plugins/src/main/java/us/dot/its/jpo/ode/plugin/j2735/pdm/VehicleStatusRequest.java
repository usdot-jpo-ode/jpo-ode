package us.dot.its.jpo.ode.plugin.j2735.pdm;

import us.dot.its.jpo.ode.model.OdeObject;

public class VehicleStatusRequest extends OdeObject {

    private static final long serialVersionUID = 2137805895544104045L;
    
    private int tag;
    private int subTag;
    private int lessThenValue;
    private int moreThenValue;
    private int sendAll;
    private int status;

    public VehicleStatusRequest() {
        // empty constructor
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getSubTag() {
        return subTag;
    }

    public void setSubTag(int subTag) {
        this.subTag = subTag;
    }

    public int getLessThenValue() {
        return lessThenValue;
    }

    public void setLessThenValue(int lessThenValue) {
        this.lessThenValue = lessThenValue;
    }

    public int getMoreThenValue() {
        return moreThenValue;
    }

    public void setMoreThenValue(int moreThenValue) {
        this.moreThenValue = moreThenValue;
    }

    public int getSendAll() {
        return sendAll;
    }

    public void setSendAll(int sendAll) {
        this.sendAll = sendAll;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
