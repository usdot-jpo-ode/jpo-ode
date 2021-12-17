package us.dot.its.jpo.ode.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class XmlUtilsTest {

    @Test
    public void testToJsonObject() throws XmlUtilsException {
        var myXml = "<recordId>5651E543</recordId>";
        var json = XmlUtils.toJSONObject(myXml);
        var parsedRecordId = json.getString("recordId");
        // JSON shouldn't be converted to scientific notation
        assertEquals("5651E543", parsedRecordId);
    }

}
