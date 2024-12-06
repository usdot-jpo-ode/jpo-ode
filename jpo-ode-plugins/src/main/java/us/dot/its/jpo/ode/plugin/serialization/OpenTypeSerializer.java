package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import java.io.IOException;
import javax.xml.namespace.QName;
import us.dot.its.jpo.ode.plugin.types.Asn1Type;

/**
 * Serializer for ASN.1 "open types" which are fields without a specific type,
 * for dealing with parameterized fields with value sets of different types that
 * can be plugged in. In this Java implementation these are represented by type
 * parameters
 * in abstract types.
 *
 * <p>For example, the contents of the "value" field in a J2735 MessageFrame.
 *
 * <p>XER wraps open types with the type name like:
 * 
 * <pre>{@code
 *
 *     <MessageFrame>
 *         <messageId>19</messageId>
 *         <value>
 *             <SPAT>
 *                 ...
 *             </SPAT>
 *         </value>
 *     </MessageFrame>
 *
 *  }</pre>
 *
 * <p>See XMLOpenTypeFieldValue: ITU-T Rec. X.681 (02/2021) Section 14.6.
 *
 * <p>JER does not wrap them:
 * 
 * <pre>{@code
 *
 *     {
 *         "messageId": 19,
 *         "value": {
 *             ...
 *         }
 *     }
 *
 * }</pre>
 *
 * <p>See "Encoding of open type values", ITU-T Rec X.697 (02/2021), Sec 41.
 *
 * @author Ivan Yourshaw
 */
public abstract class OpenTypeSerializer<T extends Asn1Type> extends StdSerializer<T> {

  protected final QName wrapper;
  protected final QName wrapped;

  protected OpenTypeSerializer(Class<T> t, String wrapper, String wrapped) {
    super(t);
    this.wrapper = new QName(wrapper);
    this.wrapped = new QName(wrapped);
  }

  @Override
  public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
    if (serializerProvider instanceof XmlSerializerProvider) {
      // Wrapped XER
      var xmlGen = (ToXmlGenerator) jsonGenerator;
      xmlGen.startWrappedValue(wrapper, wrapped);
      xmlGen.writeObject(t);
      xmlGen.finishWrappedValue(wrapper, wrapped);
    } else {
      // Pass through JER
      jsonGenerator.writeObject(t);
    }

  }
}
