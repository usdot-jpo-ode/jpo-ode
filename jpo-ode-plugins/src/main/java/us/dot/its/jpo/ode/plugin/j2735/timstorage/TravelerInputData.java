package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.ServiceRequest;

public class TravelerInputData extends OdeObject {

   private static final long serialVersionUID = 1L;
   
   private ServiceRequest request;
   private TravelerInformation tim;

   public ServiceRequest getRequest() {
    return request;
  }

  public void setRequest(ServiceRequest request) {
    this.request = request;
  }

  public TravelerInformation getTim() {
      return tim;
   }

   public void setTim(TravelerInformation tim) {
      this.tim = tim;
   }

}
