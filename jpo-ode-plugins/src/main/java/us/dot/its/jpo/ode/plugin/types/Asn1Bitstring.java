package us.dot.its.jpo.ode.plugin.types;

import static us.dot.its.jpo.ode.plugin.utils.BitUtils.reverseBits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.BitSet;
import java.util.HexFormat;
import us.dot.its.jpo.ode.plugin.serialization.BitstringSerializer;

/**
 * Base class for an ASN.1 bitstring.
 */
@JsonSerialize(using = BitstringSerializer.class)
public abstract class Asn1Bitstring implements Asn1Type {

  @JsonIgnore
  final BitSet bits;

  @JsonIgnore
  final int size;

  @JsonIgnore
  final boolean hasExtensionMarker;

  @JsonIgnore
  final String[] names;

  /**
   * Creates a Asn1Bitstring with the specified number of items and name values.
   *
   * @param size The length of the bitstring.
   * @param hasExtensionMarker Presence of any ASN.1 bitstring extension values.
   * @param names The String name values corresponding to each bit in the bitstring.
   */
  public Asn1Bitstring(int size, boolean hasExtensionMarker, String[] names) {
    this.size = size;
    this.hasExtensionMarker = hasExtensionMarker;
    this.bits = new BitSet(size);
    this.names = names;
  }

  public int size() {
    return size;
  }

  public boolean hasExtensionMarker() {
    return hasExtensionMarker;
  }

  public boolean get(int bitIndex) {
    return bits.get(bitIndex);
  }

  public void set(int bitIndex, boolean value) {
    bits.set(bitIndex, value);
  }

  /**
   * Set the corresponding bit from the bitstring based on the name value.
   *
   * @param name The name String value for the corresponding bit in the bitstring.
   * @param value The value for the bit to be set.
   */
  public void set(String name, boolean value) {
    for (int i = 0; i < size; i++) {
      if (name(i).equals(name)) {
        set(i, value);
        return;
      }
    }
    throw new IllegalArgumentException("Unknown name " + name);
  }

  /**
   * Gets the String value of the bitstring represented in binary.
   */
  public String binaryString() {
    char[] chars = new char[size];
    for (int i = 0; i < size; i++) {
      chars[i] = get(i) ? '1' : '0';
    }
    return new String(chars);
  }

  public String hexString() {
    HexFormat hex = HexFormat.of();
    return hex.formatHex(reverseBits(bits.toByteArray()));
  }

  /**
   * Sets the Asn1Bitstring values from a String binary representation of a bitstring.
   *
   * @param str The bitstring represented in binary.
   */
  public void fromBinaryString(String str) {
    if (str == null) {
      bits.clear();
      return;
    }
    char[] chars = str.toCharArray();
    if (chars.length < size) {
      throw new IllegalArgumentException("Not enough characters in string " + str);
    }
    for (int i = 0; i < size; i++) {
      char c = chars[i];
      set(i, c == '1');
    }
  }

  /**
   * Sets the Asn1Bitstring values from a String hex representation of a bitstring.
   *
   * @param str The bitstring represented in hex.
   */
  public void fromHexString(String str) {
    System.out.println(str);
    if (str == null) {
      bits.clear();
      return;
    }
    HexFormat hex = HexFormat.of();
    byte[] bytes = reverseBits(hex.parseHex(str));
    System.out.println(bytes.length);
    BitSet newBits = BitSet.valueOf(bytes);
    System.out.println(newBits);

    bits.clear();
    bits.or(newBits);
    System.out.println(binaryString());
  }

  /**
   * Get the name representing the requested index.
   *
   * @param index The index value of the bitstring being requested.
   */
  public String name(int index) {
    if (index < 0 || index >= size()) {
      throw new IllegalArgumentException(
        String.format("Index %s out of range %s-%s", index, 0, size()));
    }
    return names[index];
  }

  @Override
  public int hashCode() {
    return bits.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Asn1Bitstring bitstring) {
      return bits.equals(bitstring.bits);
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return binaryString();
  }
}
