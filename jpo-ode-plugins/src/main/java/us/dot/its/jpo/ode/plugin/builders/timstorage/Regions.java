package us.dot.its.jpo.ode.plugin.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.model.OdeObject;

public class Regions extends OdeObject
{
   private static final long serialVersionUID = 1L;
   
   @JsonProperty("GeographicalPath")
   private GeographicalPath geographicalPath;

    public GeographicalPath getGeographicalPath ()
    {
        return geographicalPath;
    }

    public void setGeographicalPath (GeographicalPath GeographicalPath)
    {
        this.geographicalPath = GeographicalPath;
    }
}