package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.AuxiliaryBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeAppliedStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeBoostApplied;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.j2735.dsrc.StabilityControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.TractionControlStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;

/**
 * Test conversion from BrakeSystemStatus to ASN.1-compliant
 * J2735BrakeSystemStatus
 *
 */
public class OssBrakeSystemStatusTest {
    
    // Brake status tests

    /**
     * Test that integer value 0 returns "unavailable" status for all brake
     * system statuses
     */
    @Test
    public void shouldCreateBrakeStatusUnavailable() {

        Integer testWheelBrakesBitString = 0b0000; // {false, false, false,
                                                   // false}

        Integer testTractionControlEnum = 0; // "unavailable"
        Integer testAbsEnum = 0; // "unavailable"
        Integer testScsEnum = 0; // "unavailable"
        Integer testBrakeBoostEnum = 0; // "unavailable"
        Integer testAuxBrakesEnum = 0; // "unavailable"

        byte[] testWheelBrakesBytes = { testWheelBrakesBitString.byteValue() };

        BrakeAppliedStatus testWheelBrakesStatus = new BrakeAppliedStatus(testWheelBrakesBytes);
        TractionControlStatus testTractionStatus = new TractionControlStatus(testTractionControlEnum);
        AntiLockBrakeStatus testAbsStatus = new AntiLockBrakeStatus(testAbsEnum);
        StabilityControlStatus testScsStatus = new StabilityControlStatus(testScsEnum);
        BrakeBoostApplied testBrakeBoostStatus = new BrakeBoostApplied(testBrakeBoostEnum);
        AuxiliaryBrakeStatus testAuxBrakesStatus = new AuxiliaryBrakeStatus(testAuxBrakesEnum);

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(testWheelBrakesStatus, testTractionStatus,
                testAbsStatus, testScsStatus, testBrakeBoostStatus, testAuxBrakesStatus);

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);

        assertEquals("unavailable", actualStatus.getTraction());
        assertEquals("unavailable", actualStatus.getAbs());
        assertEquals("unavailable", actualStatus.getScs());
        assertEquals("unavailable", actualStatus.getBrakeBoost());
        assertEquals("unavailable", actualStatus.getAuxBrakes());

    }

    /**
     * Test that integer value 1 returns "off" status for all brake system
     * statuses
     */
    @Test
    public void shouldCreateBrakeStatusOff() {

        Integer testWheelBrakesBitString = 0b0000; // {false, false, false,
                                                   // false}

        Integer testTractionControlEnum = 1; // "off"
        Integer testAbsEnum = 1; // "off"
        Integer testScsEnum = 1; // "off"
        Integer testBrakeBoostEnum = 1; // "off"
        Integer testAuxBrakesEnum = 1; // "off"

        byte[] testWheelBrakesBytes = { testWheelBrakesBitString.byteValue() };

        BrakeAppliedStatus testWheelBrakesStatus = new BrakeAppliedStatus(testWheelBrakesBytes);
        TractionControlStatus testTractionStatus = new TractionControlStatus(testTractionControlEnum);
        AntiLockBrakeStatus testAbsStatus = new AntiLockBrakeStatus(testAbsEnum);
        StabilityControlStatus testScsStatus = new StabilityControlStatus(testScsEnum);
        BrakeBoostApplied testBrakeBoostStatus = new BrakeBoostApplied(testBrakeBoostEnum);
        AuxiliaryBrakeStatus testAuxBrakesStatus = new AuxiliaryBrakeStatus(testAuxBrakesEnum);

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(testWheelBrakesStatus, testTractionStatus,
                testAbsStatus, testScsStatus, testBrakeBoostStatus, testAuxBrakesStatus);

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);

        // assertEquals(false, actualStatus.wheelBrakes);

        assertEquals("off", actualStatus.getTraction());
        assertEquals("off", actualStatus.getAbs());
        assertEquals("off", actualStatus.getScs());
        assertEquals("off", actualStatus.getBrakeBoost());
        assertEquals("off", actualStatus.getAuxBrakes());

    }

    /**
     * Test that integer value 2 returns "on" status for all brake system
     * statuses
     */
    @Test
    public void shouldCreateBrakeStatusOn() {

        Integer testWheelBrakesBitString = 0b0000; // {false, false, false,
                                                   // false}

        Integer testTractionControlEnum = 2; // "on"
        Integer testAbsEnum = 2; // "on"
        Integer testScsEnum = 2; // "on"
        Integer testBrakeBoostEnum = 2; // "on"
        Integer testAuxBrakesEnum = 2; // "on"

        byte[] testWheelBrakesBytes = { testWheelBrakesBitString.byteValue() };

        BrakeAppliedStatus testWheelBrakesStatus = new BrakeAppliedStatus(testWheelBrakesBytes);
        TractionControlStatus testTractionStatus = new TractionControlStatus(testTractionControlEnum);
        AntiLockBrakeStatus testAbsStatus = new AntiLockBrakeStatus(testAbsEnum);
        StabilityControlStatus testScsStatus = new StabilityControlStatus(testScsEnum);
        BrakeBoostApplied testBrakeBoostStatus = new BrakeBoostApplied(testBrakeBoostEnum);
        AuxiliaryBrakeStatus testAuxBrakesStatus = new AuxiliaryBrakeStatus(testAuxBrakesEnum);

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(testWheelBrakesStatus, testTractionStatus,
                testAbsStatus, testScsStatus, testBrakeBoostStatus, testAuxBrakesStatus);

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);

        // assertEquals(false, actualStatus.wheelBrakes);

        assertEquals("on", actualStatus.getTraction());
        assertEquals("on", actualStatus.getAbs());
        assertEquals("on", actualStatus.getScs());
        assertEquals("on", actualStatus.getBrakeBoost());
        assertEquals("on", actualStatus.getAuxBrakes());

    }

    /**
     * Test that integer value 3 returns "engaged" status for TCS and ABS. All
     * other statuses are set to 0 - "unavailable".
     */
    @Test
    public void shouldCreateBrakeStatusThree() {

        Integer testWheelBrakesBitString = 0b0000; // {false, false, false,
                                                   // false}

        Integer testTractionControlEnum = 3; // "engaged"
        Integer testAbsEnum = 3; // "engaged"
        Integer testScsEnum = 0; // "unavailable"
        Integer testBrakeBoostEnum = 0; // "unavailable"
        Integer testAuxBrakesEnum = 0; // "unavailable"

        byte[] testWheelBrakesBytes = { testWheelBrakesBitString.byteValue() };

        BrakeAppliedStatus testWheelBrakesStatus = new BrakeAppliedStatus(testWheelBrakesBytes);
        TractionControlStatus testTractionStatus = new TractionControlStatus(testTractionControlEnum);
        AntiLockBrakeStatus testAbsStatus = new AntiLockBrakeStatus(testAbsEnum);
        StabilityControlStatus testScsStatus = new StabilityControlStatus(testScsEnum);
        BrakeBoostApplied testBrakeBoostStatus = new BrakeBoostApplied(testBrakeBoostEnum);
        AuxiliaryBrakeStatus testAuxBrakesStatus = new AuxiliaryBrakeStatus(testAuxBrakesEnum);

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(testWheelBrakesStatus, testTractionStatus,
                testAbsStatus, testScsStatus, testBrakeBoostStatus, testAuxBrakesStatus);

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);

        // assertEquals(false, actualStatus.wheelBrakes);

        assertEquals("engaged", actualStatus.getTraction());
        assertEquals("engaged", actualStatus.getAbs());

    }
    
    // WheelBrakes tests
    @Test
    public void shouldCreateWheelBrakesAllOff() {

        byte[] testWheelBrakesBytes = new byte[1];
        testWheelBrakesBytes[0] = 0b00000000;

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(
                new BrakeAppliedStatus(testWheelBrakesBytes), 
                new TractionControlStatus(0),
                new AntiLockBrakeStatus(0), 
                new StabilityControlStatus(0), 
                new BrakeBoostApplied(0), 
                new AuxiliaryBrakeStatus(0));

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);
        
        for (Map.Entry<String, Boolean> curVal : actualStatus.getWheelBrakes().entrySet()) {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
        }

    }
    
    @Test
    public void shouldCreateWheelBrakesAllOn() {
        
        byte[] testWheelBrakesBytes = new byte[1];
        testWheelBrakesBytes[0] = (byte) 0b11111000;

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(
                new BrakeAppliedStatus(testWheelBrakesBytes), 
                new TractionControlStatus(0),
                new AntiLockBrakeStatus(0), 
                new StabilityControlStatus(0), 
                new BrakeBoostApplied(0), 
                new AuxiliaryBrakeStatus(0));

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);
        
        for (Map.Entry<String, Boolean> curVal : actualStatus.getWheelBrakes().entrySet()) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
        }
    }
    
    @Test
    public void shouldCreateWheelBrakesUnavailable() {
        
        String elementTested = "unavailable";
        byte[] testWheelBrakesBytes = new byte[1];
        testWheelBrakesBytes[0] = (byte) 0b10000000;

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(
                new BrakeAppliedStatus(testWheelBrakesBytes), 
                new TractionControlStatus(0),
                new AntiLockBrakeStatus(0), 
                new StabilityControlStatus(0), 
                new BrakeBoostApplied(0), 
                new AuxiliaryBrakeStatus(0));

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);
        
        for (Map.Entry<String, Boolean> curVal : actualStatus.getWheelBrakes().entrySet()) {
            if (curVal.getKey().equals(elementTested)) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    @Test
    public void shouldCreateWheelBrakesLeftFront() {
        
        String elementTested = "leftFront";
        byte[] testWheelBrakesBytes = new byte[1];
        testWheelBrakesBytes[0] = (byte) 0b01000000;

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(
                new BrakeAppliedStatus(testWheelBrakesBytes), 
                new TractionControlStatus(0),
                new AntiLockBrakeStatus(0), 
                new StabilityControlStatus(0), 
                new BrakeBoostApplied(0), 
                new AuxiliaryBrakeStatus(0));

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);
        
        for (Map.Entry<String, Boolean> curVal : actualStatus.getWheelBrakes().entrySet()) {
            if (curVal.getKey().equals(elementTested)) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    @Test
    public void shouldCreateWheelBrakesRightRear() {
        
        String elementTested = "rightRear";
        byte[] testWheelBrakesBytes = new byte[1];
        testWheelBrakesBytes[0] = (byte) 0b00001000;

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(
                new BrakeAppliedStatus(testWheelBrakesBytes), 
                new TractionControlStatus(0),
                new AntiLockBrakeStatus(0), 
                new StabilityControlStatus(0), 
                new BrakeBoostApplied(0), 
                new AuxiliaryBrakeStatus(0));

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);
        
        for (Map.Entry<String, Boolean> curVal : actualStatus.getWheelBrakes().entrySet()) {
            if (curVal.getKey().equals(elementTested)) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    @Test
    public void shouldCreateWheelBrakesTwoOn() {
        
        String elementTested1 = "leftFront";
        String elementTested2 = "rightFront";
        byte[] testWheelBrakesBytes = new byte[1];
        testWheelBrakesBytes[0] = (byte) 0b01010000;

        BrakeSystemStatus testBrakeSystemStatus = new BrakeSystemStatus(
                new BrakeAppliedStatus(testWheelBrakesBytes), 
                new TractionControlStatus(0),
                new AntiLockBrakeStatus(0), 
                new StabilityControlStatus(0), 
                new BrakeBoostApplied(0), 
                new AuxiliaryBrakeStatus(0));

        J2735BrakeSystemStatus actualStatus = OssBrakeSystemStatus.genericBrakeSystemStatus(testBrakeSystemStatus);
        
        for (Map.Entry<String, Boolean> curVal : actualStatus.getWheelBrakes().entrySet()) {
            if (curVal.getKey().equals(elementTested1) || curVal.getKey().equals(elementTested2)) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssBrakeSystemStatus> constructor = OssBrakeSystemStatus.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
        constructor.newInstance();
        fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
        assertEquals(InvocationTargetException.class, e.getClass());
      }
    }

}
