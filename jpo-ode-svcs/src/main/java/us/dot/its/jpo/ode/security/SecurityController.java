package us.dot.its.jpo.ode.security;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;

@Controller
public class SecurityController {
	
	@Autowired
	protected SecurityController(OdeProperties odeProps) {
		super();
		
		Executors.newSingleThreadExecutor().submit(
		        new CertificateLoader(odeProps));
	}

}
