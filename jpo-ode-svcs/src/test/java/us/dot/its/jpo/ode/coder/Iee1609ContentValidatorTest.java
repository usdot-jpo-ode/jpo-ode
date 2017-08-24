package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Content;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SignedData;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SignedDataPayload;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.ToBeSignedData;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Opaque;
import mockit.Expectations;
import mockit.Mocked;

public class Iee1609ContentValidatorTest {
   // @Capturing
   // Iee1609ContentValidator captureIee1609ContentValidator;
   @Mocked
   Ieee1609Dot2Content mockIeee1609Dot2Content;
   @Mocked
   SignedData mockSignData;
   @Mocked
   ToBeSignedData mockToBeSignedData;
   @Mocked
   SignedDataPayload mockSignedDataPayload;
   @Mocked
   Ieee1609Dot2Data mockIeee1609Dot2Data;
   @Mocked
   Opaque mockOpaque;

   @Test
   public void contentHadUnsecureDataReturnFalse() {

      assertFalse(new Iee1609ContentValidator().contentHadUnsecureData(null));

   }

   @Test
   public void contentHadUnsecureDataTest() {

      new Expectations() {
         {
            mockIeee1609Dot2Content.getSignedData();
            result = null;

         }
      };

      assertFalse(new Iee1609ContentValidator().contentHadUnsecureData(mockIeee1609Dot2Content));

   }

   @Test
   public void contentHadUnsecureDataTestTBs() {

      new Expectations() {
         {
            mockIeee1609Dot2Content.getSignedData();
            result = mockSignData;

            mockSignData.getTbsData();
            result = null;
         }
      };

      assertFalse(new Iee1609ContentValidator().contentHadUnsecureData(mockIeee1609Dot2Content));

   }

   @Test
   public void contentHadUnsecureDataTestPayload() {

      new Expectations() {
         {
            mockIeee1609Dot2Content.getSignedData();
            result = mockSignData;

            mockSignData.getTbsData();
            result = mockToBeSignedData;

            mockToBeSignedData.getPayload();
            result = null;
         }
      };
      assertFalse(new Iee1609ContentValidator().contentHadUnsecureData(mockIeee1609Dot2Content));
   }

   @Test
   public void contentHadUnsecureDataTestGetData() {

      new Expectations() {
         {
            mockIeee1609Dot2Content.getSignedData();
            result = mockSignData;

            mockSignData.getTbsData();
            result = mockToBeSignedData;

            mockToBeSignedData.getPayload();
            result = mockSignedDataPayload;

            mockSignedDataPayload.getData();
            result = null;
         }
      };

      assertFalse(new Iee1609ContentValidator().contentHadUnsecureData(mockIeee1609Dot2Content));

   }
   
   @Test
   public void contentHadUnsecureDataTestGetContent() {

      new Expectations() {
         {
            mockIeee1609Dot2Content.getSignedData();
            result = mockSignData;

            mockSignData.getTbsData();
            result = mockToBeSignedData;

            mockToBeSignedData.getPayload();
            result = mockSignedDataPayload;

            mockSignedDataPayload.getData();
            result = mockIeee1609Dot2Data;
            
            mockIeee1609Dot2Data.getContent();
            result = null;
            
         }
      };

      assertFalse(new Iee1609ContentValidator().contentHadUnsecureData(mockIeee1609Dot2Content));

   }
   
   @Test
   public void contentHadUnsecureDataTestGetUnsecuredDatat() {

      new Expectations() {
         {
            mockIeee1609Dot2Content.getSignedData();
            result = mockSignData;

            mockSignData.getTbsData();
            result = mockToBeSignedData;

            mockToBeSignedData.getPayload();
            result = mockSignedDataPayload;

            mockSignedDataPayload.getData();
            result = mockIeee1609Dot2Data;
            
            mockIeee1609Dot2Data.getContent();
            result = mockIeee1609Dot2Content;
            
            mockIeee1609Dot2Content.getUnsecuredData();
            result = null;
            
         }
      };

      assertFalse(new Iee1609ContentValidator().contentHadUnsecureData(mockIeee1609Dot2Content));

   }
   
   
   @Test
   public void contentHadUnsecureDataTrue() {

      new Expectations() {
         {
            mockIeee1609Dot2Content.getSignedData();
            result = mockSignData;

            mockSignData.getTbsData();
            result = mockToBeSignedData;

            mockToBeSignedData.getPayload();
            result = mockSignedDataPayload;

            mockSignedDataPayload.getData();
            result = mockIeee1609Dot2Data;
            
            mockIeee1609Dot2Data.getContent();
            result = mockIeee1609Dot2Content;
            
            mockIeee1609Dot2Content.getUnsecuredData();
            result = mockOpaque;
            
         }
      };

      assertTrue(new Iee1609ContentValidator().contentHadUnsecureData(mockIeee1609Dot2Content));

   }






}
