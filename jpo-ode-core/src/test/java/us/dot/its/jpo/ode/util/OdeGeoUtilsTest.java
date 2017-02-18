package us.dot.its.jpo.ode.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;

public class OdeGeoUtilsTest {

    private J2735GeoRegion testRegion;

    /**
     * Create a known geo region (roughly Pennsylvania)
     */
    @Before
    public void setupOdeGeoRegion() {

        BigDecimal nwLat = BigDecimal.valueOf(42.0);
        BigDecimal nwLon = BigDecimal.valueOf(-80.4);

        BigDecimal seLat = BigDecimal.valueOf(39.9);
        BigDecimal seLon = BigDecimal.valueOf(-75.2);

        BigDecimal elev = BigDecimal.ZERO;

        J2735Position3D nwPoint = new J2735Position3D(nwLat, nwLon, elev);
        J2735Position3D sePoint = new J2735Position3D(seLat, seLon, elev);

        testRegion = new J2735GeoRegion(nwPoint, sePoint);

    }

    /**
     * Test a point outside of that region (Denver, CO)
     */
    @Test
    public void shouldReturnInRegionFalse() {

        BigDecimal testLat = BigDecimal.valueOf(39.76);
        BigDecimal testLon = BigDecimal.valueOf(-105.0);

        BigDecimal elev = BigDecimal.ZERO;

        J2735Position3D testPoint = new J2735Position3D(testLat, testLon, elev);

        assertFalse(testRegion.contains(testPoint));

    }

    /**
     * Test a point inside of that region (Harrisburg, PA)
     */
    @Test
    public void shouldReturnInRegionTrue() {

        BigDecimal testLat = BigDecimal.valueOf(40.25);
        BigDecimal testLon = BigDecimal.valueOf(-76.9);

        BigDecimal elev = BigDecimal.ZERO;

        J2735Position3D testPoint = new J2735Position3D(testLat, testLon, elev);

        assertTrue(testRegion.contains(testPoint));

    }

}
