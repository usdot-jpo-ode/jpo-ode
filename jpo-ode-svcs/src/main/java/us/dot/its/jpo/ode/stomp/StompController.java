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
package us.dot.its.jpo.ode.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(value="/newMessage")
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
