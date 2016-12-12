package us.dot.its.jpo.ode.subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class SubscriberController {

    @Autowired
    private SimpMessagingTemplate template;


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Subscriber greeting(HelloMessage message) throws Exception {
        Thread.sleep(10); // simulated delay
        return new Subscriber(message.getName());
    }

    @RequestMapping(value="/newMessage", method=RequestMethod.POST)
    @ResponseBody
    public String messages() throws Exception {
        template.convertAndSend("/topic/messages", new Subscriber("test"));
        return "{\"success\": true}" ;
    }

    @GetMapping("/helloworld")
    public String test() throws Exception {
        return "index";
    }

}
