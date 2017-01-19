package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.EventDescription;
import us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Description;
import us.dot.its.jpo.ode.j2735.dsrc.Extent;
import us.dot.its.jpo.ode.j2735.dsrc.HeadingSlice;
import us.dot.its.jpo.ode.j2735.dsrc.Priority;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.plugin.j2735.J2735EventDescription;

/**
 * -- Summary --
 * JUnit test class for OssEventDescription
 * 
 * Verifies correct conversion from generic EventDescription object to compliant-J2735EventDescription
 * 
 * -- Documentation --
 * Data Frame: DF_EventDescription
 * Use: The EventDescription data frame provides a short summary of an event or incident. It is used by a 
 * sending device (often a public safety vehicle) to inform nearby equipped devices about an event or about 
 * the driving action the sending device is taking or is about to take. Typical use cases include such concepts 
 * as a slow moving vehicle as well as fire/police movement with flashing light details.
 * ASN.1 Representation:
 *    EventDescription ::= SEQUENCE {
 *       typeEvent ITIS.ITIScodes,
 *          -- A category and an item from that category
 *          -- all ITS stds use the same types here
 *          -- to explain the type of the
 *          -- alert / danger / hazard involved
 *       description SEQUENCE (SIZE(1..8)) OF ITIS.ITIScodes OPTIONAL,
 *          -- Up to eight ITIS code set entries to further
 *          -- describe the event, give advice, or any
 *          -- other ITIS codes
 *       priority Priority OPTIONAL,
 *          -- The urgency of this message, a relative
 *          -- degree of merit compared with other
 *          -- similar messages for this type (not other
 *          -- messages being sent by the device), nor
 *          -- is it a priority of display urgency
 *       heading HeadingSlice OPTIONAL,
 *          -- Applicable headings/direction
 *       extent Extent OPTIONAL,
 *          -- The spatial distance over which this
 *          -- message applies and should be presented to the driver
 *       regional SEQUENCE (SIZE(1..4)) OF RegionalExtension {{REGION.Reg-EventDescription}} OPTIONAL,
 *       ...
 *       }
 * 
 * Data Element: DE_Priority
 * Use: A priority for the alert message, giving urgency of this message. A relative degree of merit compared 
 * with other similar messages for this type (not other messages being sent by the device, nor a priority of 
 * display urgency at the receiver). At this time, the lower five bits are reserved and shall be set to zero. 
 * This effectively reduces the number of priority levels to eight. The value of all zeros shall be used for 
 * "routine" messages, such as roadside signage, where not displaying the message to the driver has only modest 
 * impact. The value 111xxxxx shall be the highest level of priority and shall be considered the most important 
 * level. When choices of display order or transmission order are considered, messages with this level of priority 
 * shall be given precedence. The remaining 6 levels shall be used as determined by local conventions.
 * ASN.1 Representation:
 *    Priority ::= OCTET STRING (SIZE(1))
 *       -- Follow definition notes on setting these bits
 * 
 * Data Element: DE_HeadingSlice
 * Use: The DE_HeadingSlice data element is used to define a set of sixteen 22.5 degree slices of a unit circle 
 * (defined as 0~360 degrees of heading) which, when a given slice is set to one, indicates that travel, or motion, 
 * or message applicability along that slice of angles is allowed. Typically used to indicate a gross range of the 
 * direction to which the enclosing message or data frame applies. For example, in a use case indicating what 
 * directions of travel are to be considered, a value of 0x8181 would indicate travel in the direction of either 
 * due East or due West with a 45 degree cone about each of the cardinal axis.
 * ASN.1 Representation:
 *    HeadingSlice ::= BIT STRING { 
 *       -- Each bit 22.5 degree starting from 
 *       -- North and moving Eastward (clockwise) as one bit 
 *       -- a value of noHeading means no bits set, while a 
 *       -- a value of allHeadings means all bits would be set
 *       from000-0to022-5degrees (0),
 *       from022-5to045-0degrees (1),
 *       from045-0to067-5degrees (2),
 *       from067-5to090-0degrees (3),
 *       [...]
 *       from292-5to315-0degrees (13),
 *       from315-0to337-5degrees (14),
 *       from337-5to360-0degrees (15)
 *       } (SIZE (16))
 * 
 * Data Element: DE_Extent
 * Use: The spatial distance over which this message applies and should be presented to the driver. Under certain 
 * conditions some messages may never be shown to the driver of a vehicle if they are short in duration and other 
 * conflicting needs supersede access to the display until such time as the subject message is no longer relevant.
 * ASN.1 Representation:
 *    Extent ::= ENUMERATED {
 *       useInstantlyOnly (0),
 *       useFor3meters (1),
 *       useFor10meters (2),
 *       useFor50meters (3),
 *       useFor100meters (4),
 *       useFor500meters (5),
 *       useFor1000meters (6),
 *       useFor5000meters (7),
 *       useFor10000meters (8),
 *       useFor50000meters (9),
 *       useFor100000meters (10),
 *       useFor500000meters (11),
 *       useFor1000000meters (12),
 *       useFor5000000meters (13),
 *       useFor10000000meters (14),
 *       forever (15) -- very wide area
 *       } -- Encoded as a 4 bit value
 *
 */
/**
 * @author matthewschwartz
 *
 */
public class OssEventDescriptionTest {
    
    // typeEvent tests
    /**
     * Test that a typeEvent can be created and retrieved from a minimally-populated mock EventDescription
     */
    @Test
    public void shouldReturnKnownTypeEvent() {
        
        Integer testInput = 9123;
        Integer expectedValue = 9123;
        
        ITIScodes testITIScode = new ITIScodes(testInput);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(testITIScode);
        
        try {
           Integer actualValue = OssEventDescription
                   .genericEventDescription(testEventDescription)
                   .typeEvent.intValue();
           assertEquals(expectedValue, actualValue);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
    }
    
    // description tests
    /**
     * Test that a description (list of ITIS codes) can be created and retrieved from a mock EventDescription
     */
    @Test
    public void shouldReturnKnownDescriptions() {
        
        Integer testInput1 = 9123;
        Integer testInput2 = 8269;
        Integer testInput3 = 7230;
        Integer expectedValue1 = 9123;
        Integer expectedValue2 = 8269;
        Integer expectedValue3 = 7230;
        
        ITIScodes testITIScode1 = new ITIScodes(testInput1);
        ITIScodes testITIScode2 = new ITIScodes(testInput2);
        ITIScodes testITIScode3 = new ITIScodes(testInput3);
        
        Description testDescription = new Description();
        testDescription.add(testITIScode1);
        testDescription.add(testITIScode2);
        testDescription.add(testITIScode3);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9150));
        testEventDescription.setDescription(testDescription);
        
        List<Integer> actualDesc = OssEventDescription
                .genericEventDescription(testEventDescription)
                .description;
        
        assertEquals(expectedValue1, actualDesc.get(0));
        assertEquals(expectedValue2, actualDesc.get(1));
        assertEquals(expectedValue3, actualDesc.get(2));
    }
    
    // priority tests
    /**
     * Test known priority encoding "11100000" can be returned as hex "E0"
     */
    @Test
    public void shouldReturnKnownPriority() {
        
        Integer testInput = 0b11100000;
        String expectedValue = "E0"; // hex representation of testInput
        
        byte[] testInputBytes = {testInput.byteValue()};
        
        Priority testPriority = new Priority(testInputBytes);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setPriority(testPriority);
        
        String actualValue = OssEventDescription.genericEventDescription(testEventDescription).priority;
        assertEquals(expectedValue, actualValue);
    }
    
    // heading tests
    /**
     * Test heading slice bit string ("0000000000000000") returns no bits set (all false)
     */
    @Test
    public void shouldReturnHeadingSliceAllOff() {
        
        byte[] testInputBytes = new byte[2];
        testInputBytes[0] = 0b00000000;
        testInputBytes[1] = 0b00000000;
        
        HeadingSlice testHeadingSlice = new HeadingSlice(testInputBytes);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setHeading(testHeadingSlice);
        
        J2735EventDescription actualEventDescription = OssEventDescription.genericEventDescription(testEventDescription);
        
        for (Map.Entry<String, Boolean> curVal : actualEventDescription.heading.entrySet()) {     
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
        }
    }
    
    /**
     * Test heading slice bit string ("1111111111111111") returns all bits set (all true)
     */
    @Test
    public void shouldReturnHeadingSliceAllOn() {
        
        byte[] testInputBytes = new byte[2];
        testInputBytes[0] = (byte) 0b11111111;
        testInputBytes[1] = (byte) 0b11111111;
        
        HeadingSlice testHeadingSlice = new HeadingSlice(testInputBytes);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setHeading(testHeadingSlice);
        
        J2735EventDescription actualEventDescription = OssEventDescription.genericEventDescription(testEventDescription);
        
        for (Map.Entry<String, Boolean> curVal : actualEventDescription.heading.entrySet()) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
        }
    }
    
    /**
     * Test heading slice bit string ("1000000000000000") returns true for (from000-0to022-5degrees) only
     */
    @Test
    public void shouldReturnHeadingSliceBottomBit() {
        
        String elementTested = "from000-0to022-5degrees";
        
        byte[] testInputBytes = new byte[2];
        testInputBytes[0] = (byte) 0b10000000;
        testInputBytes[1] = (byte) 0b00000000;
        
        HeadingSlice testHeadingSlice = new HeadingSlice(testInputBytes);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setHeading(testHeadingSlice);
        
        J2735EventDescription actualEventDescription = OssEventDescription.genericEventDescription(testEventDescription);
        
        for (Map.Entry<String, Boolean> curVal : actualEventDescription.heading.entrySet()) {
            if (curVal.getKey().equals(elementTested)) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    /**
     * Test heading slice bit string ("0000000000000001") returns true for (from337-5to360-0degrees) only
     */
    @Test
    public void shouldReturnHeadingSliceTopBit() {
        
        String elementTested = "from337-5to360-0degrees";
        
        byte[] testInputBytes = new byte[2];
        testInputBytes[0] = (byte) 0b00000000;
        testInputBytes[1] = (byte) 0b00000001;
        
        HeadingSlice testHeadingSlice = new HeadingSlice(testInputBytes);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setHeading(testHeadingSlice);
        
        J2735EventDescription actualEventDescription = OssEventDescription.genericEventDescription(testEventDescription);
        
        for (Map.Entry<String, Boolean> curVal : actualEventDescription.heading.entrySet()) {
            if (curVal.getKey().equals(elementTested)) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }
    
    // extent tests
    /**
     * Test minimum extent value (0) returns (useInstantlyOnly)
     */
    @Test
    public void shouldReturnMinimumExtent() {
        
        Integer testInput = 0;
        String expectedValue = "useInstantlyOnly";
        
        Extent testExtent = new Extent(testInput);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setExtent(testExtent);
        
        String actualValue = OssEventDescription.genericEventDescription(testEventDescription).extent.name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known extent value (5) returns (useFor500meters)
     */
    @Test
    public void shouldReturnKnownExtent() {
        
        Integer testInput = 5;
        String expectedValue = "useFor500meters";
        
        Extent testExtent = new Extent(testInput);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setExtent(testExtent);
        
        String actualValue = OssEventDescription.genericEventDescription(testEventDescription).extent.name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum extent value (15) returns (forever)
     */
    @Test
    public void shouldReturnMaximumExtent() {
        
        Integer testInput = 15;
        String expectedValue = "forever";
        
        Extent testExtent = new Extent(testInput);
        
        EventDescription testEventDescription = new EventDescription();
        testEventDescription.setTypeEvent(new ITIScodes(9123));
        testEventDescription.setExtent(testExtent);
        
        String actualValue = OssEventDescription.genericEventDescription(testEventDescription).extent.name();
        
        assertEquals(expectedValue, actualValue);
    }

}
