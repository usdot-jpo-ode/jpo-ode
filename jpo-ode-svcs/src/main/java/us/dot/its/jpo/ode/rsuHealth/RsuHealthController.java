package us.dot.its.jpo.ode.rsuHealth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class RsuHealthController {

    @RequestMapping(value="/rsuHeartbeat", method=RequestMethod.GET, produces= "application/json")
    @ResponseBody
    public String heartBeat(@RequestParam("ip") String ip) throws Exception {
        System.out.print(ip);
        return "{\"success\":"+ ip+"}" ;
    }


}
