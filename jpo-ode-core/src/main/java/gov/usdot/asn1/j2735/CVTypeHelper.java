package gov.usdot.asn1.j2735;

/**
 * Enums and helper methods for the various "Type" messages in the ASN.1 spec
 */
public class CVTypeHelper {

	public interface CVType {
		public int intValue();
		public byte byteValue();
		public byte[] arrayValue();
	}
	
	public enum VsmType implements CVType {
		FUND(1), VEHSTAT(2), WEATHER(4), ENV(8), ELVEH(16);
		private int value;

		private VsmType(int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
		
		public byte byteValue() {
			return (byte)value;
		}
		
		public byte[] arrayValue() {
			return new byte[] { (byte)this.value };
		}
	};
	
	public enum DistributionType implements CVType {
		RSU(1), IP(2), SATELLITE(4), RESERVED(8);
		private int value;

		private DistributionType(int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
		
		public byte byteValue() {
			return (byte)value;
		}
		
		public byte[] arrayValue() {
			return new byte[] { (byte)this.value };
		}
	};
	
	public enum IsdType implements CVType {
		FUND(1);
		private int value;

		private IsdType(int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
		
		public byte byteValue() {
			return (byte)value;
		}
		
		public byte[] arrayValue() {
			return new byte[] { (byte)this.value };
		}
	};
	
	public enum ServiceID implements CVType {
		SDC(1), SDW(2), SCMS(4), OTHER(8);
		private int value;

		private ServiceID(int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
		
		public byte byteValue() {
			return (byte)value;
		}
		
		public byte[] arrayValue() {
			return new byte[] { (byte)this.value };
		}
	};
	
	public static byte bitWiseOr(CVType... types) {
		byte result = 0x0;
		for (CVType type: types) {
			result|= type.byteValue();
		}
		return result;
	}
	
	public static byte bitWiseXOr(CVType... types) {
		byte result = 0x0;
		for (CVType type: types) {
			result^= type.byteValue();
		}
		return result;
	}
	
	public static byte bitWiseAnd(CVType... types) {
		byte result = (byte)0xFF;
		for (CVType type: types) {
			result&= type.byteValue();
		}
		return (byte)result;
	}
	
	public static byte[] singleByteArray(byte b) {
		return new byte[] { b };
	}
	
}
