package us.dot.its.jpo.ode.kafka.listeners.json;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeObject;

class RawEncodedJsonServicePublishTest {

  @Test
  void externalModePublishesDecoderInput() {
    RawEncodedJsonService service = new RawEncodedJsonService(new ObjectMapper());
    OdeAsn1Data data = mock(OdeAsn1Data.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, OdeObject> template = mock(KafkaTemplate.class);

    service.publish(data, "bsm-key", template, "topic.Asn1DecoderInput");

    verify(template).send("topic.Asn1DecoderInput", "bsm-key", data);
  }

}
