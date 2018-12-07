package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Description extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private Path path;

   private Geometry geometry;

   public Path getPath() {
      return path;
   }

   public void setPath(Path path) {
      this.path = path;
   }

   public Geometry getGeometry() {
      return geometry;
   }

   public void setGeometry(Geometry geometry) {
      this.geometry = geometry;
   }
}
