package us.dot.its.jpo.ode.codec.ffmlib;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import j2735ffm.ConvertException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FfmlibNativeErrorsTest {

  @ParameterizedTest
  @ValueSource(strings = {
      "Unrecognized PDU: malformed TIM",
      "MessageFrame: Error decoding PDU",
      "Decoding was successful, but constraint check failed, can't re-encode: value"})
  void malformedInputMessagesAreClassifiedAsInput(String message) {
    assertTrue(FfmlibNativeErrors.isMalformedInputMessage(message));
    assertTrue(FfmlibNativeErrors.isMalformedInput(new ConvertException(message)));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "MessageFrame: Error encoding to UPER",
      "Output buffer too small",
      "Error decoding PDU in the middle of a message",
      ""})
  void operationalMessagesAreNotClassifiedAsInput(String message) {
    assertFalse(FfmlibNativeErrors.isMalformedInputMessage(message));
    assertFalse(FfmlibNativeErrors.isMalformedInput(new ConvertException(message)));
  }

  @Test
  void nullMessagesAndMissingNativeCausesAreNotInput() {
    assertFalse(FfmlibNativeErrors.isMalformedInputMessage(null));
    assertFalse(FfmlibNativeErrors.isMalformedInput(null));
    assertFalse(FfmlibNativeErrors.isMalformedInput(
        new IllegalStateException("MessageFrame: Error decoding PDU")));
  }

  @Test
  void nestedConvertExceptionIsFound() {
    ConvertException nativeError = new ConvertException("MessageFrame: Error decoding PDU");
    RuntimeException wrapped = new RuntimeException("service",
        new IllegalStateException("codec", nativeError));

    assertSame(nativeError, FfmlibNativeErrors.findConvertException(wrapped));
    assertTrue(FfmlibNativeErrors.isMalformedInput(wrapped));
  }

  @Test
  void cyclicCauseChainTerminates() {
    RuntimeException first = new RuntimeException("first");
    RuntimeException second = new RuntimeException("second", first);
    first.initCause(second);

    assertNull(FfmlibNativeErrors.findConvertException(first));
    assertFalse(FfmlibNativeErrors.isMalformedInput(first));
  }
}
