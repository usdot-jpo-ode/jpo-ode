package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;

public class OdeTravelerInputData extends OdeObject {

   private static final long serialVersionUID = 8769107278440796699L;

   private ServiceRequest request;
   
   private OdeTravelerInformationMessage tim;
   
   public ServiceRequest getRequest() {
    return request;
  }

  public void setRequest(ServiceRequest request) {
    this.request = request;
  }

  public OdeTravelerInformationMessage getTim() {
      return tim;
   }

   public void setTim(OdeTravelerInformationMessage tim) {
      this.tim = tim;
   }

}
