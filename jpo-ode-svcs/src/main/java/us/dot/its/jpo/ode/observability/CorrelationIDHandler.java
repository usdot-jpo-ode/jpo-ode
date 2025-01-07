package us.dot.its.jpo.ode.observability;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * This class provides functionality to handle correlation IDs using a Tracer.
 * It retrieves the current trace context and extracts the trace ID, which can
 * be used as a correlation ID for tracing and logging purposes.
 */
@Component
public class CorrelationIDHandler {

  private final Tracer tracer;

  public CorrelationIDHandler(Tracer tracer) {
    this.tracer = tracer;
  }

  public String getCorrelationId() {
    return Optional.of(tracer).map(Tracer::currentTraceContext).map(CurrentTraceContext::context).map(TraceContext::traceId).orElse("");
  }
}