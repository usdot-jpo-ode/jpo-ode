package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.DdsGeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.OdeGeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;

public class GeoRegionBuilderTest {
  
  private OdeGeoRegion odeGeoRegion;
  
  @Before
  public void setup() {
    OdePosition3D nwCorner = new OdePosition3D(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
    OdePosition3D seCorner = new OdePosition3D(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);
    odeGeoRegion = new OdeGeoRegion(nwCorner, seCorner);
  }

  @Test
  public void testGenericGeoRegion() throws JsonUtilsException {
    ObjectNode geoRegion = JsonUtils.newNode();
    geoRegion.set("nwCorner", JsonUtils.toObjectNode(odeGeoRegion.getNwCorner().toJson()));
    geoRegion.set("seCorner", JsonUtils.toObjectNode(odeGeoRegion.getSeCorner().toJson()));

    OdeGeoRegion actual = GeoRegionBuilder.genericGeoRegion(geoRegion);
    
    assertEquals(odeGeoRegion, actual);
  }

  @Test
  public void testDdsGeoRegion() {
    DdsGeoRegion ddsGeoRegion = GeoRegionBuilder.ddsGeoRegion(odeGeoRegion);
    
    DdsGeoRegion expected = new DdsGeoRegion();
    expected.setNwCorner(Position3DBuilder.dsrcPosition3D(odeGeoRegion.getNwCorner()));
    expected.setSeCorner(Position3DBuilder.dsrcPosition3D(odeGeoRegion.getSeCorner()));
    assertEquals(expected , ddsGeoRegion);
  }

}
