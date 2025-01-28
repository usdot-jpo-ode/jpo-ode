package us.dot.its.jpo.ode.udp.controller;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(prefix = "ode.receivers")
@Data
@Primary
public class UDPReceiverProperties {
    private ReceiverProperties generic;
    private ReceiverProperties bsm;
    private ReceiverProperties map;
    private ReceiverProperties psm;
    private ReceiverProperties spat;
    private ReceiverProperties srm;
    private ReceiverProperties ssm;
    private ReceiverProperties tim;

    @Data
    public static class ReceiverProperties {
        private int receiverPort;
        private int bufferSize;
    }
}
