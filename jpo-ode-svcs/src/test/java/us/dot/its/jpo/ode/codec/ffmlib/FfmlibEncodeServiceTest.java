package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import j2735ffm.AsnEncoding;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

class FfmlibEncodeServiceTest {

  @Test
  @SuppressWarnings("unchecked")
  void encodesAdvisorySituationDataWithoutExternalKafkaCodec() throws Exception {
    FfmlibMessageFrameCodec codec = mock(FfmlibMessageFrameCodec.class);
    ObjectProvider<FfmlibMessageFrameCodec> provider = mock(ObjectProvider.class);
    when(provider.getIfAvailable()).thenReturn(codec);
    when(codec.encodeFromXer(
        anyString(), eq("AdvisorySituationData"), eq(AsnEncoding.UPER)))
        .thenReturn(new byte[] {0x01, 0x02});
    FfmlibEncodeService service = new FfmlibEncodeService(provider, new XmlMapper());

    String input = """
        <OdeAsn1Data>
          <metadata><schemaVersion>9</schemaVersion></metadata>
          <payload><data><AdvisorySituationData>
            <dialogID>156</dialogID><seqID>5</seqID><groupID>00000000</groupID>
            <requestID>01020304</requestID><recordID>05060708</recordID><timeToLive>1</timeToLive>
            <asdmDetails><asdmID>01020304</asdmID><asdmType>2</asdmType>
              <distType>02</distType><advisoryMessage>0014</advisoryMessage>
            </asdmDetails>
          </AdvisorySituationData></data></payload>
        </OdeAsn1Data>
        """;

    String output = service.encodeOdeAsn1Xml(input);

    assertTrue(output.contains("<AdvisorySituationData><bytes>0102</bytes>"));
    verify(codec).encodeFromXer(
        anyString(), eq("AdvisorySituationData"), eq(AsnEncoding.UPER));
  }
}
