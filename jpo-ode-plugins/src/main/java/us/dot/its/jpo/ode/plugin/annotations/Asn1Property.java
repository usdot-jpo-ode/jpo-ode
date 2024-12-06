package us.dot.its.jpo.ode.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides metadata for a property of an Asn.1 class: components of
 * Asn1Sequence, or alternatives of Asn1Choice.
 *
 * @author Ivan Yourshaw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Asn1Property {
  /**
   * Tag number property.
   *
   * @return Tag number indicating the canonical order of serialization
   */
  int tag();

  /**
   * Name property.
   *
   * @return Name of the original non-normalized property. Not required if the
   *         Java property name is the same as
   *         the ASN.1 name.
   */
  String name() default "";

  /**
   * Extensions present property.
   *
   * @return True if the property is an extension, false if part of the root
   */
  boolean extension() default false;

  /**
   * Optional field property.
   *
   * @return True if the ASN.1 OPTIONAL marker is present
   */
  boolean optional() default false;

  /**
   * Default value property.
   *
   * @return Default value specified by the ASN.1 DEFAULT marker. String can be
   *         converted to an integer for int types.
   */
  String defaultValue() default "";

  /**
   * Open type property.
   *
   * @return Indicates that the property is an ASN1 Open Type, so UPER encoding
   *         needs to use a length determinant
   *         as described in T-REC-X.691 (2021/2) section 11.2.
   */
  boolean openType() default false;

}
