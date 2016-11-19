package us.dot.its.jpo.ode.metrics;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import us.dot.its.jpo.ode.context.AppContext;


public class OdeMetrics {
   private static OdeMetrics instance = null;

   private com.codahale.metrics.MetricRegistry registry = null; 
   
   private Counter cacheCounter;
   
   public void cacheIn() {
      cacheCounter.inc();
   }

   public void cacheOut() {
      cacheCounter.dec();
   }
   
   public static OdeMetrics getInstance() {
      if (null == instance) {
         synchronized (OdeMetrics.class) {
            if (null == instance)
               instance = new OdeMetrics();
         }
      }
      return instance;
   }

   private OdeMetrics() {
      registry = new com.codahale.metrics.MetricRegistry();   
      cacheCounter = counter("CacheCounter");
   }

   public class Meter {
      private com.codahale.metrics.Meter meter;

      private Meter(com.codahale.metrics.Meter meter) {
         this.meter = meter;
      }
      
      public void mark() {
         meter.mark();
      }
      
      public void mark(long n) {
         meter.mark(n);
      }
   }

   public interface Gauge<T> extends com.codahale.metrics.Gauge<T> {
      void register(String prefix, String... names);
   }

   public class Counter {
      private com.codahale.metrics.Counter counter;

      private Counter(com.codahale.metrics.Counter counter) {
         this.counter = counter;
      }
      
      public void inc() {
         counter.inc();
      }
      public void dec() {
         counter.dec();
      }

      public void inc(long n) {
         counter.inc(n);
      }
      public void dec(long n) {
         counter.dec(n);
      }
      
      public long getCount() {
         return counter.getCount();
      }
      
   }

   public class Histogram {
      private com.codahale.metrics.Histogram histogram;
      
      private Histogram(com.codahale.metrics.Histogram histogram) {
         this.histogram = histogram;
      }

      /**
       * Adds a recorded value.
       *
       * @param value the length of the value
       */
      public void update(long value) {
         histogram.update(value);
      }
   }
   

   public class Context {
      private com.codahale.metrics.Timer.Context context;
      
      private Context(com.codahale.metrics.Timer.Context context) {
         this.context= context;
      }
      
      public long stop() {
         return context.stop();
      }
   }
   
   public class Timer {
      private com.codahale.metrics.Timer timer;
      
      private Timer(com.codahale.metrics.Timer timer) {
         this.timer = timer;
      }

      /**
       * Times and records the duration of event.
       *
       * @param event a {@link Callable} whose {@link Callable#call()} method implements a process
       *              whose duration should be timed
       * @param <T>   the type of the value returned by {@code event}
       * @return the value returned by {@code event}
       * @throws Exception if {@code event} throws an {@link Exception}
       */
      public <T> T time(Callable<T> event) throws Exception {
         return timer.time(event);
      }

      /**
       * Returns a new {@link Context}.
       *
       * @return a new {@link Context}
       * @see Context
       */
      public Context time() {
         return new Context(timer.time());
      }

   }
   
   public Meter meter(String prefix, String... names) {
      com.codahale.metrics.Meter meter = registry.meter(
            com.codahale.metrics.MetricRegistry.name(prefix, names));
      return new Meter(meter);
   }

   public void registerGauge(Gauge<?> odeGauge, String prefix, String... names) {
      registry.register(com.codahale.metrics.MetricRegistry.name(prefix, names), odeGauge);
   }
   
   public Counter counter(String prefix, String... names) {
      com.codahale.metrics.Counter counter = registry.counter(
            com.codahale.metrics.MetricRegistry.name(prefix, names));
      return new Counter(counter);
   }

   public Histogram histogram(String prefix, String... names) {
      com.codahale.metrics.Histogram histogram = registry.histogram(
            com.codahale.metrics.MetricRegistry.name(prefix, names));
      return new Histogram(histogram);
   }

   public Timer timer(String prefix, String... names) {
      com.codahale.metrics.Timer timer = registry.timer(
            com.codahale.metrics.MetricRegistry.name(prefix, names));
      return new Timer(timer);
   }

   public static void main(String args[]) {
      MetricRegistry metrics = new com.codahale.metrics.MetricRegistry();
      ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
      reporter.start(1, TimeUnit.SECONDS);
      com.codahale.metrics.Meter requestsMeter = metrics.meter("requests");
      requestsMeter.mark();
      
      com.codahale.metrics.Counter requestsCounter = metrics.counter("requestsCounter");
      requestsCounter.inc();
      
      wait5Seconds();
   }

   public void startConsoleReport() {
//      Meter reporterMeter = OdeMetrics.getInstance().meter("reporter");
//      reporterMeter.mark();
      
      com.codahale.metrics.ConsoleReporter reporter = 
            com.codahale.metrics.ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
      reporter.start(
            AppContext.getInstance().getInt(
                  AppContext.METRICS_POLLING_RATE_SECONDS,
                  AppContext.DEFAULT_METRICS_POLLING_RATE_SECONDS), 
            TimeUnit.SECONDS);
   }

   public void startGraphiteReport() {
      final com.codahale.metrics.graphite.Graphite graphite = 
            new com.codahale.metrics.graphite.Graphite(
                  new InetSocketAddress(
                        AppContext.getInstance().getParam(
                              AppContext.METRICS_GRAPHITE_HOST),
                        AppContext.getInstance().getInt(
                              AppContext.METRICS_GRAPHITE_PORT,
                              AppContext.DEFAULT_METRICS_GRAPHITE_PORT)));
      final com.codahale.metrics.graphite.GraphiteReporter reporter = 
            com.codahale.metrics.graphite.GraphiteReporter.forRegistry(registry)
            .prefixedWith(
                  AppContext.getInstance().getParam(
                        AppContext.METRICS_PREFIX) +
                  AppContext.getInstance().getParam(
                        AppContext.ODE_HOSTNAME))
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(com.codahale.metrics.MetricFilter.ALL)
            .build(graphite);
      reporter.start(
            AppContext.getInstance().getInt(
                  AppContext.METRICS_POLLING_RATE_SECONDS,
                  AppContext.DEFAULT_METRICS_POLLING_RATE_SECONDS), 
            TimeUnit.SECONDS);
   }

   static void wait5Seconds() {
      try {
         Thread.sleep(5 * 1000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}