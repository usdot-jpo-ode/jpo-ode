package us.dot.its.jpo.ode.wrapper;

/**
 * @author 572682
 *
 * This abstract class provides a basic pipeline functionality through the messaging
 * framework. The objects of this class subscribe to a topic, process received messages
 * and publish the results to another topic.
 *   
 * @param <K> Message Key type
 * @param <S> Received Message Value Type
 * @param <P> Published Message Value Type
 */
public abstract class AbstractSubPubTransformer<K, S, P> extends AbstractSubscriberProcessor<K, S> {

    protected int messagesPublished = 0;
    protected MessageProducer<K, P> producer;
    protected String outputTopic;

    public AbstractSubPubTransformer(MessageProducer<K, P> producer, String outputTopic) {
       super();
        this.producer = producer;
        this.outputTopic = outputTopic;
    }

    @Override
    public Object call() {
       @SuppressWarnings("unchecked")
       P toBePublished = (P) super.call();

       if (null != toBePublished) {
          producer.send(outputTopic, getRecord().key(), toBePublished);
       }
        
       return toBePublished;
    }
}
