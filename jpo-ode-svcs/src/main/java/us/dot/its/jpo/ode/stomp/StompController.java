package us.dot.its.jpo.ode.stomp;

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
public class StompController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/connect")
    @SendTo("/topic/StompContent")
    public StompContent greeting(RegistrationMessage message) throws InterruptedException {
        Thread.sleep(10); // simulated delay
        return new StompContent(message.getName());
    }

    @RequestMapping(value="/newMessage", method=RequestMethod.POST)
    @ResponseBody
    public String messages() {
        template.convertAndSend("/topic/messages", new StompContent("test"));
        return "{\"success\": true}" ;
    }

    @GetMapping("/")
    public String test() {
        return "index";
    }

}
