package us.dot.its.jpo.ode.wrapper.serdes;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used as a test data model in serialization and deserialization processes.
 * It encapsulates a numeric value, a string, and a decimal number.
 * Provides a no-argument constructor and a parameterized constructor to initialize its fields.
 */
@Data
@NoArgsConstructor
public class TestData {
  private BigDecimal bigDecimal;
  private int anInt;
  private String str;

  /**
   * Constructs a new instance of the TestData class with the specified numeric value, string, and decimal number.
   *
   * @param i           an integer value
   * @param string      a string value
   * @param bigDecimal  a decimal number
   */
  public TestData(int i, String string, BigDecimal bigDecimal) {
    super();
    this.anInt = i;
    this.str = string;
    this.bigDecimal = bigDecimal;
  }
}
