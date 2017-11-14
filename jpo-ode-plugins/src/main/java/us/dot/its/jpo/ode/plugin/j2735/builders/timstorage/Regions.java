package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Regions extends Asn1Object
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