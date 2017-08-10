package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Acceleration;
import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.AuxiliaryBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeAppliedStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeBoostApplied;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.StabilityControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.dsrc.TractionControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionState;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.plugin.j2735.J2735AccelerationSet4Way;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionalAccuracy;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAcceleration;
import us.dot.its.jpo.ode.j2735.dsrc.YawRate;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VsmEventFlag;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssVehicleSituationRecord;
public class OssVehicleSituationRecordTest {
	/*
	   @Test
	   public void convertBsmToVsrshouldReturnNewVehSitRecord() {

		   J2735Bsm testInput = new J2735Bsm();
		   VehSitRecord expectedValue = new VehSitRecord();


		   //expectedValue.tempID = new TemporaryID(CodecUtils.fromHex(testInput.getCoreData().getId()));
		   expectedValue.time = new DDateTime(new DYear(0), new DMonth(0), new DDay(0), new DHour(31), new DMinute(60),
		            new DSecond(65535), new DOffset(0));
		  // expectedValue.pos = convertPosition3D(testInput.getCoreData().getPosition());
		   //expectedValue.fundamental = createFundamentalSituationalStatus(testInput.getCoreData());

	      VehSitRecord actualValue = OssVehicleSituationRecord.convertBsmToVsr(testInput);


	      assertEquals(expectedValue.time.getHour(), actualValue.time.getHour());
	   }
*/
   @Test
   public void convertPosition3DshouldReturnCornerCaseMinimumLongitude() {

      BigDecimal testInput = BigDecimal.valueOf(-179.9999998);
      int expectedValue = -1799999998;

      J2735Position3D testPos = new J2735Position3D();
      testPos.setLongitude(testInput);
      testPos.setLatitude(null);
      testPos.setElevation(null);

      Position3D actualValue = OssVehicleSituationRecord.convertPosition3D(testPos);

      assertEquals(expectedValue, actualValue.get_long().intValue());
   }



   @Test
   public void convertPosition3DshouldReturnCornerCaseMinimumLatitude() {

      BigDecimal testInput = BigDecimal.valueOf(-89.9999998);
      int expectedValue = -899999998;

      J2735Position3D testPos = new J2735Position3D();
      testPos.setLongitude(null);
      testPos.setLatitude(testInput);
      testPos.setElevation(null);

      Position3D actualValue = OssVehicleSituationRecord.convertPosition3D(testPos);

      assertEquals(expectedValue, actualValue.getLat().intValue());
   }

   @Test
   public void convertPosition3DshouldReturnCornerCaseMinimumElevation() {

      BigDecimal testInput = BigDecimal.valueOf(-409.4);
      int expectedValue = -4094;

      J2735Position3D testPos = new J2735Position3D();
      testPos.setLongitude(null);
      testPos.setLatitude(null);
      testPos.setElevation(testInput);

      Position3D actualValue = OssVehicleSituationRecord.convertPosition3D(testPos);

      assertEquals(expectedValue, actualValue.getElevation().intValue());
   }


   @Test
   public void convertAccelerationNullInput() {
	   Acceleration expectedValue;

	   BigDecimal testInput = null;
	   expectedValue = new Acceleration(2001);

	   Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
	   assertEquals(expectedValue, actualValue);

   }

   @Test
   public void convertAccelerationShouldReturnElse() {
	   Acceleration expectedValue;

	   BigDecimal testInput = BigDecimal.valueOf(-8.77);
	   expectedValue = new Acceleration(-877);

	   Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
	   assertEquals(expectedValue, actualValue);

   }
   
   
   @Test
   public void convertAccelerationTheUpperBound() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(20);
      expectedValue = new Acceleration(2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test
   public void convertAccelerationShouldReturnLowerBound() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-20);
      expectedValue = new Acceleration(-2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   
   @Test
   public void convertAccelerationShouldReturnUpperBoundEdge() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(2001);
      expectedValue = new Acceleration(2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test
   public void convertAccelerationShouldReturnLowerBoundEdge() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-2001);
      expectedValue = new Acceleration(-2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   
   @Test
   public void convertAccelerationShouldReturnZero() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(0);
      expectedValue = new Acceleration(0);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test
   public void convertAccelerationShouldReturnMiddle() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(12.00);
      expectedValue = new Acceleration(1200);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   
   @Test
   public void convertAccelerationShouldReturnUpperBound() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(20.01);
      expectedValue = new Acceleration(2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }

   @Test
   public void convertAccelerationWayAbove() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(2001);
      expectedValue = new Acceleration(2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test
   public void convertAccelerationError() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-20.01);
      expectedValue = new Acceleration(-2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test
   public void convertAccelerationEdgeAboveLower() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-19.999);
      expectedValue = new Acceleration(-1999);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test
   public void convertAccelerationEdgeBelowLower() {
      Acceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-20.001);
      expectedValue = new Acceleration(-2000);

      Acceleration actualValue = OssVehicleSituationRecord.convertAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test
   public void VerticalAccelerationNullEntry() {
	   VerticalAcceleration expectedValue;

	   BigDecimal testInput = null;
	   expectedValue = new VerticalAcceleration(-127);

	   VerticalAcceleration actualValue = OssVehicleSituationRecord.convertVerticalAcceleration(testInput);
	   assertEquals(expectedValue, actualValue);

   }
   
   @Test
   public void VerticalAccelerationShouldReturnElse() {
	   VerticalAcceleration expectedValue;

	   BigDecimal testInput = BigDecimal.valueOf(1.54);;
	   expectedValue = new VerticalAcceleration(77);

	   VerticalAcceleration actualValue = OssVehicleSituationRecord.convertVerticalAcceleration(testInput);
	   assertEquals(expectedValue, actualValue);

   }
   @Test 
   public void VerticalAccelerationShouldReturnUpperBound() {
      VerticalAcceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(2.99);;
      expectedValue = new VerticalAcceleration(127);

      VerticalAcceleration actualValue = OssVehicleSituationRecord.convertVerticalAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   
   @Test 
   public void VerticalAccelerationShouldReturnLowerBound() {
      VerticalAcceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-2.99);;
      expectedValue = new VerticalAcceleration(-126);

      VerticalAcceleration actualValue = OssVehicleSituationRecord.convertVerticalAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test 
   public void VerticalAccelerationShouldReturnLowerBoundTwo() {
      VerticalAcceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(2.54);;
      expectedValue = new VerticalAcceleration(127);

      VerticalAcceleration actualValue = OssVehicleSituationRecord.convertVerticalAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test 
   public void VerticalAccelerationShouldReturnLowerBoundThree() {
      VerticalAcceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-2.52);;
      expectedValue = new VerticalAcceleration(-126);

      VerticalAcceleration actualValue = OssVehicleSituationRecord.convertVerticalAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   @Test 
   public void VerticalAccelerationShouldReturnLowerBoundFour() {
      VerticalAcceleration expectedValue;

      BigDecimal testInput = BigDecimal.valueOf(-2.51);;
      expectedValue = new VerticalAcceleration(-125);

      VerticalAcceleration actualValue = OssVehicleSituationRecord.convertVerticalAcceleration(testInput);
      assertEquals(expectedValue, actualValue);

   }
   
   
   /*
   @Test
   public void convertYawRateNullEntry() {
	   YawRate expectedValue;

	   BigDecimal testInput = null;
	   expectedValue = null;

	   YawRate actualValue = OssVehicleSituationRecord.convertYawRate(testInput);
	   assertEquals(expectedValue, actualValue);

   }*/

   @Test
   public void convertYawRateShouldReturnElse() {
	   YawRate expectedValue;

	   BigDecimal testInput = BigDecimal.valueOf(888.4444);;
	   expectedValue = new YawRate(88844);

	   YawRate actualValue = OssVehicleSituationRecord.convertYawRate(testInput);
	   assertEquals(expectedValue, actualValue);

   }



   @Test
   public void convertSpeedshouldReturnCornerCaseNullSpeed() {
	   Velocity expectedValue;

	   BigDecimal testInput = null;
	   expectedValue = new Velocity(8191);

	   Velocity actualValue = OssVehicleSituationRecord.convertSpeed(testInput);
	   assertEquals(expectedValue, actualValue);

   }
   @Test
   public void convertSpeedshouldReturnVelocity() {
	   Velocity expectedValue;

	   BigDecimal testInput = BigDecimal.valueOf(1.9999998);
	   expectedValue = new Velocity(199);

	   Velocity actualValue = OssVehicleSituationRecord.convertSpeed(testInput);
	   assertEquals(expectedValue, actualValue);

   }

   @Test
   public void convertAuxiliaryBrakeStatusShouldReturnOne() {
	  AuxiliaryBrakeStatus expectedValue;
	  String testInput = "off";

	  int actualValueInt = 1;
	  expectedValue = new AuxiliaryBrakeStatus(actualValueInt);

	  AuxiliaryBrakeStatus actualValue = OssVehicleSituationRecord.convertAuxiliaryBrakeStatus(testInput);


      assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
  /*
   @Test
   public void createFundamentalSituationalStatusNormal() {
	  FundamentalSituationalStatus expectedValue;

	  BigDecimal all = BigDecimal.valueOf(100.5);

	  J2735BsmCoreData testInput = new J2735BsmCoreData();


	  TransmissionAndSpeed S = new TransmissionAndSpeed();
	  Heading H = new Heading();
	  SteeringWheelAngle SWA = new SteeringWheelAngle();
	  AccelerationSet4Way AS4 = new AccelerationSet4Way();
	  BrakeSystemStatus BSS = new BrakeSystemStatus();
	  VehicleSize VS = new VehicleSize();
	  VsmEventFlag VF = new VsmEventFlag();

	  expectedValue = new FundamentalSituationalStatus(S,H,SWA,AS4,BSS,VS,VF);

	  BigDecimal speed = all;
	  J2735TransmissionState transmission = J2735TransmissionState.NEUTRAL;
	  BigDecimal heading = all;
	  BigDecimal angle = all;
	  J2735AccelerationSet4Way accelSet = new J2735AccelerationSet4Way();
	  	accelSet.setAccelLat(all);
	  	accelSet.setAccelLong(all);
	  	accelSet.setAccelVert(all);
	  	accelSet.setAccelYaw(all);
	  J2735BrakeSystemStatus brakes = new J2735BrakeSystemStatus();
	   // J2735BitString wheelBrakes = new J2735BitString();
	    String allString = new String("test");

	    brakes.setTraction(allString);
	    brakes.setAbs(allString);
	    brakes.setScs(allString);
	    brakes.setBrakeBoost(allString);
	    brakes.setAuxBrakes(allString);
	  J2735VehicleSize size = new J2735VehicleSize(1,1);

	  testInput.setSpeed(speed);
	  testInput.setTransmission(transmission);
	  testInput.setHeading(heading);
	  testInput.setAngle(angle);
	  testInput.setAccelSet(accelSet);
	  testInput.setBrakes(brakes);
	  testInput.setSize(size);




	  FundamentalSituationalStatus actualValue = OssVehicleSituationRecord.createFundamentalSituationalStatus(testInput);



      assertEquals(expectedValue, actualValue);
   }
  */
   /*
   @Test
   public void convertBrakeAppliedStatusDefault() {
	  J2735BitString testInput = new J2735BitString("leftFront");


 	 BrakeAppliedStatus expectedValue;
 	  expectedValue = new BrakeAppliedStatus(StateValue);

 	 BrakeAppliedStatus actualValue = OssVehicleSituationRecord.convertBrakeAppliedStatus(testInput);

 	  assertEquals(expectedValue, actualValue);
   }
   */

   @Test
   public void convertBrakeAppliedStatusUnavailable() {

	 J2735BitString testInput = new J2735BitString();
	 testInput.put("unavailable", true);

	/*
	BrakeAppliedStatus expectedValue;
	byte[] expectedByte = new byte[]{Integer.valueOf(128).byteValue()};
 	expectedValue = new BrakeAppliedStatus(expectedByte);
 	*/
 	BrakeAppliedStatus actualValue = OssVehicleSituationRecord.convertBrakeAppliedStatus(testInput);

 	assertTrue(actualValue.getBit(0));

 	assertFalse(actualValue.getBit(1));
 	assertFalse(actualValue.getBit(2));
 	assertFalse(actualValue.getBit(3));
 	assertFalse(actualValue.getBit(4));
   }

   @Test
   public void convertBrakeAppliedStatusLeftRear() {

	   J2735BitString testInput = new J2735BitString();
	   testInput.put("leftRear", true);
	   BrakeAppliedStatus actualValue = OssVehicleSituationRecord.convertBrakeAppliedStatus(testInput);

	   assertFalse(actualValue.getBit(0));
	   assertFalse(actualValue.getBit(1));

	   assertTrue(actualValue.getBit(2));

	   assertFalse(actualValue.getBit(3));
	   assertFalse(actualValue.getBit(4));
   }

   @Test
   public void convertBrakeAppliedStatusLeftFront() {

	   J2735BitString testInput = new J2735BitString();
	   testInput.put("leftFront", true);
	   BrakeAppliedStatus actualValue = OssVehicleSituationRecord.convertBrakeAppliedStatus(testInput);

	   assertFalse(actualValue.getBit(0));

	   assertTrue(actualValue.getBit(1));

	   assertFalse(actualValue.getBit(2));
	   assertFalse(actualValue.getBit(3));
	   assertFalse(actualValue.getBit(4));
   }

   @Test
   public void convertBrakeAppliedStatusRightFront() {

	   J2735BitString testInput = new J2735BitString();
	   testInput.put("rightFront", true);
	   BrakeAppliedStatus actualValue = OssVehicleSituationRecord.convertBrakeAppliedStatus(testInput);

	   assertFalse(actualValue.getBit(0));
	   assertFalse(actualValue.getBit(1));
	   assertFalse(actualValue.getBit(2));

	   assertTrue(actualValue.getBit(3));

	   assertFalse(actualValue.getBit(4));
   }

   @Test
   public void convertBrakeAppliedStatusRightRear() {

	   J2735BitString testInput = new J2735BitString();
	   testInput.put("rightRear", true);
	   BrakeAppliedStatus actualValue = OssVehicleSituationRecord.convertBrakeAppliedStatus(testInput);

	   assertFalse(actualValue.getBit(0));
	   assertFalse(actualValue.getBit(1));
	   assertFalse(actualValue.getBit(2));
	   assertFalse(actualValue.getBit(3));

	   assertTrue(actualValue.getBit(4));
   }


   @Test
   public void convertTractionControlStatusShouldReturnOff() {

	 long StateValue = 1;
	 String testInput = "off";
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertTractionControlStatusShouldReturnOn() {

	 long StateValue = 2;
	 String testInput = "on";
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertTractionControlStatusShouldReturnEngaged() {

	 long StateValue = 3;
	 String testInput = "engaged";
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }


   @Test
   public void convertTractionControlStatusShouldReturnUnavailable() {

	 long StateValue = 0;
	 String testInput = "other";
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }

   @Test
   public void convertAntiLockBrakeStatusShouldReturnOff() {

	 long StateValue = 1;
	 String testInput = "off";
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertAntiLockBrakeStatusShouldReturnOn() {

	 long StateValue = 2;
	 String testInput = "on";
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertAntiLockBrakeStatusShouldReturnEngaged() {

	 long StateValue = 3;
	 String testInput = "engaged";
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }


   @Test
   public void convertAntiLockBrakeStatusReturnUNAVAILABLE() {

	 long StateValue = 0;
	 String testInput = "other";
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }



   @Test
   public void convertStabilityControlStatusShouldReturnOff() {

	 long StateValue = 1;
	 String testInput = "off";
	 StabilityControlStatus expectedValue;
 	 expectedValue = new StabilityControlStatus(StateValue);

 	StabilityControlStatus actualValue = OssVehicleSituationRecord.convertStabilityControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertStabilityControlStatusShouldReturnOn() {

	 long StateValue = 2;
	 String testInput = "on";
	 StabilityControlStatus expectedValue;
 	 expectedValue = new StabilityControlStatus(StateValue);

 	StabilityControlStatus actualValue = OssVehicleSituationRecord.convertStabilityControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertStabilityControlStatusShouldReturnEngaged() {

	 long StateValue = 3;
	 String testInput = "engaged";
	 StabilityControlStatus expectedValue;
 	 expectedValue = new StabilityControlStatus(StateValue);

 	StabilityControlStatus actualValue = OssVehicleSituationRecord.convertStabilityControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }


   @Test
   public void convertStabilityControlStatusStatusReturnUNAVAILABLE() {

	 long StateValue = 0;
	 String testInput = "other";
	 StabilityControlStatus expectedValue;
 	 expectedValue = new StabilityControlStatus(StateValue);

 	StabilityControlStatus actualValue = OssVehicleSituationRecord.convertStabilityControlStatus(testInput);

 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }

  @Test
   public void convertHeadingNull() {
	  Heading expectedValue;
	  BigDecimal testInput = null;

	  int actualValueInt = 28800;
	  expectedValue = new Heading(actualValueInt);

	  Heading actualValue = OssVehicleSituationRecord.convertHeading(testInput);



      assertEquals(expectedValue.longValue(), actualValue.longValue());
   }

  @Test
  public void convertHeadingCornerShouldReturnnHead() {
	  Heading expectedValue;
	  BigDecimal testInput = BigDecimal.valueOf(100.9999998);

	  int actualValueInt = 8079;
	  expectedValue = new Heading(actualValueInt);

	  Heading actualValue = OssVehicleSituationRecord.convertHeading(testInput);



     assertEquals(expectedValue.longValue(), actualValue.longValue());
  }

  @Test
  public void convertSteeringWheelAngleNull() {
	  SteeringWheelAngle expectedValue;
	  BigDecimal testInput = null;

	  int actualValueInt = 127;
	  expectedValue = new SteeringWheelAngle(actualValueInt);

	  SteeringWheelAngle actualValue = OssVehicleSituationRecord.convertSteeringWheelAngle(testInput);



     assertEquals(expectedValue.longValue(), actualValue.longValue());
  }

  @Test
  public void convertSteeringWheelAngleNotNull() {
	  SteeringWheelAngle expectedValue;
	  BigDecimal testInput = BigDecimal.valueOf(100.9999998);

	  int actualValueInt = 67;
	  expectedValue = new SteeringWheelAngle(actualValueInt);

	  SteeringWheelAngle actualValue = OssVehicleSituationRecord.convertSteeringWheelAngle(testInput);



     assertEquals(expectedValue.longValue(), actualValue.longValue());
  }


  @Test
  public void convertAuxiliaryBrakeStatusShouldReturnBrakeValTwo() {
	  AuxiliaryBrakeStatus expectedValue;
	  String testInput = "on";

	  int actualValueInt = 2;
	  expectedValue = new AuxiliaryBrakeStatus(actualValueInt);

	  AuxiliaryBrakeStatus actualValue = OssVehicleSituationRecord.convertAuxiliaryBrakeStatus(testInput);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }

  @Test
  public void convertAuxiliaryBrakeStatusShouldReturnBrakeValThree() {
	  AuxiliaryBrakeStatus expectedValue;
	  String testInput = "reserved";

	  int actualValueInt = 3;
	  expectedValue = new AuxiliaryBrakeStatus(actualValueInt);

	  AuxiliaryBrakeStatus actualValue = OssVehicleSituationRecord.convertAuxiliaryBrakeStatus(testInput);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }

  @Test
  public void convertAuxiliaryBrakeStatusShouldReturnBrakeValDefault() {
	  AuxiliaryBrakeStatus expectedValue;
	  String testInput = "fake";

	  int actualValueInt = 0;
	  expectedValue = new AuxiliaryBrakeStatus(actualValueInt);

	  AuxiliaryBrakeStatus actualValue = OssVehicleSituationRecord.convertAuxiliaryBrakeStatus(testInput);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }


  @Test
  public void convertBrakeBoostAppliedShouldReturnBrakeValOne() {
	  BrakeBoostApplied expectedValue;
	  String testInput = "off";

	  int actualValueInt = 1;
	  expectedValue = new BrakeBoostApplied(actualValueInt);

	  BrakeBoostApplied actualValue = OssVehicleSituationRecord.convertBrakeBoostApplied(testInput);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertBrakeBoostAppliedShouldReturnBrakeValTwo() {
	  BrakeBoostApplied expectedValue;
	  String testInput = "on";

	  int actualValueInt = 2;
	  expectedValue = new BrakeBoostApplied(actualValueInt);

	  BrakeBoostApplied actualValue = OssVehicleSituationRecord.convertBrakeBoostApplied(testInput);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }

  @Test
  public void convertBrakeBoostAppliedShouldReturnBrakeValDefault() {
	  BrakeBoostApplied expectedValue;
	  String testInput = "fake";

	  int actualValueInt = 0;
	  expectedValue = new BrakeBoostApplied(actualValueInt);

	  BrakeBoostApplied actualValue = OssVehicleSituationRecord.convertBrakeBoostApplied(testInput);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }

  @Test
  public void convertTransmissionStateShouldReturnNEUTRAL() {
	  long StateValue = 0;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.NEUTRAL);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertTransmissionStateShouldReturnPARK() {
	  long StateValue = 1;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.PARK);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertTransmissionStateShouldReturnFORWARDGEARS() {
	  long StateValue = 2;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.FORWARDGEARS);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertTransmissionStateShouldReturnRESERVED1() {
	  long StateValue = 4;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.RESERVED1);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertTransmissionStateShouldReturnRESERVED2() {
	  long StateValue = 5;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.RESERVED2);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertTransmissionStateShouldReturnRESERVED3() {
	  long StateValue = 6;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.RESERVED3);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertTransmissionStateShouldReturnREVERSEGEARS() {
	  long StateValue = 3;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.REVERSEGEARS);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertTransmissionStateShouldReturnUNAVAILABLE() {
	  long StateValue = 7;
	  TransmissionState expectedValue;
	  expectedValue = new TransmissionState(StateValue);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.UNAVAILABLE);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  /*@Test
  public void convertTransmissionStateShouldReturnDefault() {

	  IllegalArgumentException expectedValue;
	  expectedValue = new IllegalArgumentException("Invalid transmission state 55");

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(J2735TransmissionState.values());

	  assertEquals(expectedValue, actualValue);
  }*/
  @Test
  public void convertTransmissionStateShouldReturnNullUnavalible() {
	  TransmissionState expectedValue;
	  J2735TransmissionState testInput = null;

	  expectedValue = new TransmissionState(7);

	  TransmissionState actualValue = OssVehicleSituationRecord.convertTransmissionState(testInput);

	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  /*
  @Test
  public void convertVehicleSizeNullAndNotNull() {
	  J2735VehicleSize testInput = null;
	  VehicleWidth width = null;
	  VehicleLength length = new VehicleLength(1);
	  VehicleSize expectedValue = new VehicleSize(width,length);


	  VehicleSize actualValue = OssVehicleSituationRecord.convertVehicleSize(testInput);

	  assertEquals(expectedValue.getLength(), actualValue.getLength());
	 // assertEquals(expectedValue.getWidth(), actualValue.width);expectedValue.getLength()
  }
  */
  /*
  @Test
  public void convertVehicleSizeNull() {
	  J2735VehicleSize testInput = new J2735VehicleSize();
	  J2735VehicleSize expectedValue = new J2735VehicleSize();


	  testInput.setLength(null);
	  testInput.setWidth(1);
	  expectedValue.setLength(0);
	  expectedValue.setWidth(1);

	  VehicleSize actualValue = OssVehicleSituationRecord.convertVehicleSize(testInput);

	 // assertEquals(77, actualValue.getLength());
	 // assertEquals(expectedValue.getWidth(), actualValue.width);expectedValue.getLength()
  }

  @Test
  public void convertVehicleSizeShouldReturn() {
	  J2735VehicleSize testInput = new J2735VehicleSize();
	  J2735VehicleSize expectedValue = new J2735VehicleSize();



	  VehicleSize actualValue = OssVehicleSituationRecord.convertVehicleSize(testInput);

	  assertEquals(expectedValue, actualValue);
  }
  */

  @Test
  public void convertVehicleSizeNullInput() {
	  VehicleSize expectedValue = new VehicleSize();
	  J2735VehicleSize testInput = null;

	  expectedValue.width = new VehicleWidth(0);
	  expectedValue.length = new VehicleLength(0);
	  VehicleSize actualValue = OssVehicleSituationRecord.convertVehicleSize(testInput);

	  assertEquals(expectedValue.width, actualValue.width);
	  assertEquals(expectedValue.length, actualValue.length);
  }
  @Test
  public void convertVehicleSizeNullWidth() {
	  VehicleSize expectedValue = new VehicleSize();
	  J2735VehicleSize testInput = new J2735VehicleSize(null,1);

	  expectedValue.width = new VehicleWidth(0);
	  expectedValue.length = new VehicleLength(1);
	  VehicleSize actualValue = OssVehicleSituationRecord.convertVehicleSize(testInput);

	  assertEquals(expectedValue.width, actualValue.width);
	  assertEquals(expectedValue.length, actualValue.length);
  }
  @Test
  public void convertVehicleSizeNullLength() {
	  VehicleSize expectedValue = new VehicleSize();
	  J2735VehicleSize testInput = new J2735VehicleSize(1,null);

	  expectedValue.width = new VehicleWidth(1);
	  expectedValue.length = new VehicleLength(0);
	  VehicleSize actualValue = OssVehicleSituationRecord.convertVehicleSize(testInput);

	  assertEquals(expectedValue.width, actualValue.width);
	  assertEquals(expectedValue.length, actualValue.length);
  }
  @Test
  public void convertVehicleSizeBothNotNull() {
	  VehicleSize expectedValue = new VehicleSize();
	  J2735VehicleSize testInput = new J2735VehicleSize(1,1);

	  expectedValue.width = new VehicleWidth(1);
	  expectedValue.length = new VehicleLength(1);
	  VehicleSize actualValue = OssVehicleSituationRecord.convertVehicleSize(testInput);

	  assertEquals(expectedValue.width, actualValue.width);
	  assertEquals(expectedValue.length, actualValue.length);
  }


}
