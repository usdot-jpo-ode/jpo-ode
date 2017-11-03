package us.dot.its.jpo.ode.http;

import java.io.IOException;

import org.junit.Test;

public class InternalServerErrorExceptionTest {

   @Test
   public void testStringConstructor() {
      new InternalServerErrorException("testString");
   }

   @Test
   public void testStringAndExceptionConstructor() {
      new InternalServerErrorException("testString", new IOException());
   }

   @Test
   public void testExceptionConstructor() {
      new InternalServerErrorException(new IOException());
   }
}
