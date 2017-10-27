package us.dot.its.jpo.ode.coder;

public class Iee1609ContentValidatorTest {
   //TODO open-ode
//   // @Capturing
//   // Iee1609ContentValidator captureIee1609ContentValidator;
//   @Mocked
//   Ieee1609Dot2Content mockIeee1609Dot2Content;
//   @Mocked
//   Ieee1609Dot2Content mockIeee1609Dot2Content2;
//   @Mocked
//   SignedData mockSignData;
//   @Mocked
//   ToBeSignedData mockToBeSignedData;
//   @Mocked
//   SignedDataPayload mockSignedDataPayload;
//   @Mocked
//   Ieee1609Dot2Data mockIeee1609Dot2Data;
//   @Mocked
//   Opaque mockOpaque;
//
//   private byte[] expectedResult = new byte[] {1, 2, 3, 4};
//
//   
//   @Test
//   public void getUnsecuredDataTestNull() {
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(null));
//
//   }
//
//   @Test
//   public void getUnsecuredDataTestUnsecuredDataOpaqueByteArrayValueNull() {
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(null));
//
//   }
//
//   @Test
//   public void getUnsecuredDataTestUnsecuredDataOpaqueByteArrayValueNotNull() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = mockOpaque;
//
//            mockOpaque.byteArrayValue();
//            result = expectedResult;
//         }
//      };
//
//      assertEquals(
//         Arrays.toString(expectedResult),
//         Arrays.toString(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content)));
//
//   }
//
//   @Test
//   public void getUnsecuredDataTestSignedDataNull() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = null;
//
//         }
//      };
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//
//   }
//
//   @Test
//   public void getUnsecuredDataTestTbsDataNull() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = mockSignData;
//
//            mockSignData.getTbsData();
//            result = null;
//         }
//      };
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//
//   }
//
//   @Test
//   public void getUnsecuredDataTestPayloadNull() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = mockSignData;
//
//            mockSignData.getTbsData();
//            result = mockToBeSignedData;
//
//            mockToBeSignedData.getPayload();
//            result = null;
//         }
//      };
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//   }
//
//   @Test
//   public void getUnsecuredDataTestDataNull() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = mockSignData;
//
//            mockSignData.getTbsData();
//            result = mockToBeSignedData;
//
//            mockToBeSignedData.getPayload();
//            result = mockSignedDataPayload;
//
//            mockSignedDataPayload.getData();
//            result = null;
//         }
//      };
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//
//   }
//   
//   @Test
//   public void getUnsecuredDataTestContent() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = mockSignData;
//
//            mockSignData.getTbsData();
//            result = mockToBeSignedData;
//
//            mockToBeSignedData.getPayload();
//            result = mockSignedDataPayload;
//
//            mockSignedDataPayload.getData();
//            result = mockIeee1609Dot2Data;
//            
//            mockIeee1609Dot2Data.getContent();
//            result = null;
//            
//         }
//      };
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//
//   }
//   
//   @Test
//   public void getUnsecuredDataTestUnsecuredDataNull2() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = mockSignData;
//
//            mockSignData.getTbsData();
//            result = mockToBeSignedData;
//
//            mockToBeSignedData.getPayload();
//            result = mockSignedDataPayload;
//
//            mockSignedDataPayload.getData();
//            result = mockIeee1609Dot2Data;
//            
//            mockIeee1609Dot2Data.getContent();
//            result = mockIeee1609Dot2Content2;
//            
//            mockIeee1609Dot2Content2.getUnsecuredData();
//            result = null;
//            
//         }
//      };
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//
//   }
//   
//   
//   @Test
//   public void getUnsecuredDataTestOpaqueNull() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = mockSignData;
//
//            mockSignData.getTbsData();
//            result = mockToBeSignedData;
//
//            mockToBeSignedData.getPayload();
//            result = mockSignedDataPayload;
//
//            mockSignedDataPayload.getData();
//            result = mockIeee1609Dot2Data;
//            
//            mockIeee1609Dot2Data.getContent();
//            result = mockIeee1609Dot2Content2;
//            
//            mockIeee1609Dot2Content2.getUnsecuredData();
//            result = null;
//         }
//      };
//
//      assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//
//   }
//
//   public void getUnsecuredDataTestOpagueByteArrayValueNull() {
//
//       new Expectations() {
//          {
//             mockIeee1609Dot2Content.getUnsecuredData();
//             result = null;
//
//             mockIeee1609Dot2Content.getSignedData();
//             result = mockSignData;
//
//             mockSignData.getTbsData();
//             result = mockToBeSignedData;
//
//             mockToBeSignedData.getPayload();
//             result = mockSignedDataPayload;
//
//             mockSignedDataPayload.getData();
//             result = mockIeee1609Dot2Data;
//             
//             mockIeee1609Dot2Data.getContent();
//             result = mockIeee1609Dot2Content2;
//             
//             mockIeee1609Dot2Content2.getUnsecuredData();
//             result = mockOpaque;
//             
//             mockOpaque.byteArrayValue();
//             result = null;
//          }
//       };
//
//       assertNull(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content));
//
//    }
//
//   @Test
//   public void getUnsecuredDataTestOpaqueValueNotNull() {
//
//      new Expectations() {
//         {
//            mockIeee1609Dot2Content.getUnsecuredData();
//            result = null;
//
//            mockIeee1609Dot2Content.getSignedData();
//            result = mockSignData;
//
//            mockSignData.getTbsData();
//            result = mockToBeSignedData;
//
//            mockToBeSignedData.getPayload();
//            result = mockSignedDataPayload;
//
//            mockSignedDataPayload.getData();
//            result = mockIeee1609Dot2Data;
//            
//            mockIeee1609Dot2Data.getContent();
//            result = mockIeee1609Dot2Content2;
//            
//            mockIeee1609Dot2Content2.getUnsecuredData();
//            result = mockOpaque;
//            
//            mockOpaque.byteArrayValue();
//            result = expectedResult;
//         }
//      };
//
//      assertEquals(
//          Arrays.toString(expectedResult),
//          Arrays.toString(Ieee1609ContentValidator.getUnsecuredData(mockIeee1609Dot2Content)));
//   }
//
//
//
//
}
