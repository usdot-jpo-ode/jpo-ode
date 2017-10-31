package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class Description extends OdeObject
{
   private static final long serialVersionUID = 1L;

   private Path path;
    
    private Geometry geometry;

    public Path getPath ()
    {
        return path;
    }

    public void setPath (Path path)
    {
        this.path = path;
    }

   public Geometry getGeometry() {
      return geometry;
   }

   public void setGeometry(Geometry geometry) {
      this.geometry = geometry;
   }
}
