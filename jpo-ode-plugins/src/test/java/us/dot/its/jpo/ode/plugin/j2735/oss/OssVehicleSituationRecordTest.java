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
import us.dot.its.jpo.ode.j2735.dsrc.Heading;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.StabilityControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.j2735.dsrc.TractionControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionState;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.plugin.j2735.J2735AccelerationSet4Way;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionalAccuracy;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAcceleration;
import us.dot.its.jpo.ode.j2735.dsrc.YawRate;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.VsmEventFlag;

public class OssVehicleSituationRecordTest {
	
	

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
	   
	   BigDecimal testInput = BigDecimal.valueOf(888.77);
	   expectedValue = new Acceleration(8);

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
	   
	   BigDecimal testInput = BigDecimal.valueOf(888.77);;
	   expectedValue = new VerticalAcceleration(44438);

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
	  String testInput = new String("off");
	  
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
   public void convertTractionControlStatusShouldReturnOff() {
 	  
	 long StateValue = 1; 
	 String testInput = new String("off");
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertTractionControlStatusShouldReturnOn() {
 	  
	 long StateValue = 2; 
	 String testInput = new String("on");
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertTractionControlStatusShouldReturnEngaged() {
 	  
	 long StateValue = 3; 
	 String testInput = new String("engaged");
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   
   
   @Test
   public void convertTractionControlStatusShouldReturnUNAVAILABLE() {
 	  
	 long StateValue = 0; 
	 String testInput = new String("other");
 	 TractionControlStatus expectedValue;
 	 expectedValue = new TractionControlStatus(StateValue);

 	 TractionControlStatus actualValue = OssVehicleSituationRecord.convertTractionControlStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   
   @Test
   public void convertAntiLockBrakeStatusShouldReturnOff() {
 	  
	 long StateValue = 1; 
	 String testInput = new String("off");
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertAntiLockBrakeStatusShouldReturnOn() {
 	  
	 long StateValue = 2; 
	 String testInput = new String("on");
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertAntiLockBrakeStatusShouldReturnEngaged() {
 	  
	 long StateValue = 3; 
	 String testInput = new String("engaged");
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   
   
   @Test
   public void convertAntiLockBrakeStatusReturnUNAVAILABLE() {
 	  
	 long StateValue = 0; 
	 String testInput = new String("other");
	 AntiLockBrakeStatus expectedValue;
 	 expectedValue = new AntiLockBrakeStatus(StateValue);

 	AntiLockBrakeStatus actualValue = OssVehicleSituationRecord.convertAntiLockBrakeStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   
   
   
   @Test
   public void convertStabilityControlStatusShouldReturnOff() {
 	  
	 long StateValue = 1; 
	 String testInput = new String("off");
	 StabilityControlStatus expectedValue;
 	 expectedValue = new StabilityControlStatus(StateValue);

 	StabilityControlStatus actualValue = OssVehicleSituationRecord.convertStabilityControlStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertStabilityControlStatusShouldReturnOn() {
 	  
	 long StateValue = 2; 
	 String testInput = new String("on");
	 StabilityControlStatus expectedValue;
 	 expectedValue = new StabilityControlStatus(StateValue);

 	StabilityControlStatus actualValue = OssVehicleSituationRecord.convertStabilityControlStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   @Test
   public void convertStabilityControlStatusShouldReturnEngaged() {
 	  
	 long StateValue = 3; 
	 String testInput = new String("engaged");
	 StabilityControlStatus expectedValue;
 	 expectedValue = new StabilityControlStatus(StateValue);

 	StabilityControlStatus actualValue = OssVehicleSituationRecord.convertStabilityControlStatus(testInput);
      
 	  assertEquals(expectedValue.longValue(), actualValue.longValue());
   }
   
   
   @Test
   public void convertStabilityControlStatusStatusReturnUNAVAILABLE() {
 	  
	 long StateValue = 0; 
	 String testInput = new String("other");
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
	  String testInput = new String("on");
	  
	  int actualValueInt = 2;
	  expectedValue = new AuxiliaryBrakeStatus(actualValueInt);
     
	  AuxiliaryBrakeStatus actualValue = OssVehicleSituationRecord.convertAuxiliaryBrakeStatus(testInput);
     
	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  
  @Test
  public void convertAuxiliaryBrakeStatusShouldReturnBrakeValThree() {
	  AuxiliaryBrakeStatus expectedValue;
	  String testInput = new String("reserved");
	  
	  int actualValueInt = 3;
	  expectedValue = new AuxiliaryBrakeStatus(actualValueInt);
     
	  AuxiliaryBrakeStatus actualValue = OssVehicleSituationRecord.convertAuxiliaryBrakeStatus(testInput);
     
	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  
  @Test
  public void convertAuxiliaryBrakeStatusShouldReturnBrakeValDefault() {
	  AuxiliaryBrakeStatus expectedValue;
	  String testInput = new String("fake");
	  
	  int actualValueInt = 0;
	  expectedValue = new AuxiliaryBrakeStatus(actualValueInt);
     
	  AuxiliaryBrakeStatus actualValue = OssVehicleSituationRecord.convertAuxiliaryBrakeStatus(testInput);
     
	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  
  
  @Test
  public void convertBrakeBoostAppliedShouldReturnBrakeValOne() {
	  BrakeBoostApplied expectedValue;
	  String testInput = new String("off");
	  
	  int actualValueInt = 1;
	  expectedValue = new BrakeBoostApplied(actualValueInt);
     
	  BrakeBoostApplied actualValue = OssVehicleSituationRecord.convertBrakeBoostApplied(testInput);
     
	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }
  @Test
  public void convertBrakeBoostAppliedShouldReturnBrakeValTwo() {
	  BrakeBoostApplied expectedValue;
	  String testInput = new String("on");
	  
	  int actualValueInt = 2;
	  expectedValue = new BrakeBoostApplied(actualValueInt);
     
	  BrakeBoostApplied actualValue = OssVehicleSituationRecord.convertBrakeBoostApplied(testInput);
     
	  assertEquals(expectedValue.longValue(), actualValue.longValue());
  }

  @Test
  public void convertBrakeBoostAppliedShouldReturnBrakeValDefault() {
	  BrakeBoostApplied expectedValue;
	  String testInput = new String("fake");
	  
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
 

}
