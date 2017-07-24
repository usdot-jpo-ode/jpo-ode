package us.dot.its.jpo.ode.model;

public class OdeAggregatePayload extends OdeMsgPayload {

    private static final long serialVersionUID = 7061315628111448390L;

    public OdeAggregatePayload() {
        this(new OdeAggregateData());
    }

    public OdeAggregatePayload(OdeAggregateData agg) {
        super(agg);
        this.setData(agg);
    }

}
