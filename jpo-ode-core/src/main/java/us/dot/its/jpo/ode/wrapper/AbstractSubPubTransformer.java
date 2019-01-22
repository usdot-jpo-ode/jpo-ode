/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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

   public MessageProducer<K, P> getProducer() {
      return producer;
   }

   public void setProducer(MessageProducer<K, P> producer) {
      this.producer = producer;
   }

   public String getOutputTopic() {
      return outputTopic;
   }

   public void setOutputTopic(String outputTopic) {
      this.outputTopic = outputTopic;
   }
    
    
}
