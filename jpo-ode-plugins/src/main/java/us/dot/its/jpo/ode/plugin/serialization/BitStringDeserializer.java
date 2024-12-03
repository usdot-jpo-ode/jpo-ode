package us.dot.its.jpo.ode.plugin.serialization;

import us.dot.its.jpo.ode.plugin.types.Asn1Bitstring;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

/**
 * Deserialize an ASN.1 Bitstring from XER or JER
 * @param <T> The bitstring type
 * @author Ivan Yourshaw
 */
public abstract class BitStringDeserializer<T extends Asn1Bitstring> extends StdDeserializer<T> {

    protected abstract T construct();

    protected BitStringDeserializer(Class<?> valueClass) {
        super(valueClass);
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String str = jsonParser.getText();
        T bitstring = construct();
        if (jsonParser.getCodec() instanceof XmlMapper) {
            // XML: binary
            bitstring.fromBinaryString(str);
        } else {
            // JSON: hex
            bitstring.fromHexString(str);
        }
        return bitstring;
    }

}
