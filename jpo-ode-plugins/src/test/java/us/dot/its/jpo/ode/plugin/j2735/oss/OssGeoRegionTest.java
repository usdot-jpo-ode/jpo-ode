package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.j2735.semi.GeoRegion;
public class OssGeoRegionTest {

	
	@Capturing
	OssPosition3D captureing;
	@Mocked
	J2735Position3D mockJ2735Position3D;
	GeoRegion mockgeoRegion;
	
    @Test
    public void ossGeoRegionTestOne(){
    	
   
    	
    	
    
	new Expectations() {{
		OssPosition3D.genericPosition3D((Position3D) any);
		result = mockJ2735Position3D;
		
	
	}};
	
	
	
	OssGeoRegion.genericGeoRegion(mockgeoRegion);
	//mockJ2735Position3D.
    }
    
}
