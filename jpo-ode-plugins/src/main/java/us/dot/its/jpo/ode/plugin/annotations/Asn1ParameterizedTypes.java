package us.dot.its.jpo.ode.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify how to deserialize an ASN1 parameterized type, which
 * is represented by an abstract generic class in Java.  Modeled after the JsonTypeInfo and
 * JsonSubTypes annotations in Jackson, but adding the ability to specify that the
 * id field is an integer, not restricted to being a string like in Jackson.
 * @author Ivan Yourshaw
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Asn1ParameterizedTypes {

    /**
     * @return Name of the property used to determine which type to deserialize
     */
    String idProperty();

    /**
     * @return Type of the id property, which may be integer or string
     */
    IdType idType();

    /**
     * @return Name of the value property containing the payload which can be various types
     * depending on the generic type parameters.
     */
    String valueProperty();

    /**
     * @return Array of value types mapped to ids.
     */
    Type[] value();

    enum IdType {
        INTEGER,
        STRING
    }

    /**
     * Annotation to specify the type corresponding to an id.
     */
    @interface Type {
        /**
         * @return The id if it is an integer
         */
        int intId() default -1;

        /**
         * @return The id if it is a string
         */
        String stringId() default "";

        /**
         * @return The specific class to deserialize to
         */
        Class<?> value();
    }

}
