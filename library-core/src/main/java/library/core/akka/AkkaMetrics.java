package library.core.akka;

import com.codahale.metrics.*;

import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * @author Alexander Kuleshov
 */
public class AkkaMetrics {

    private static final MetricRegistry METRICS_REGISTRY = new MetricRegistry();

    private static final ConsoleReporter CONSOLE_REPORTER = ConsoleReporter.forRegistry(METRICS_REGISTRY).
            convertRatesTo(TimeUnit.SECONDS).
            convertDurationsTo(TimeUnit.MILLISECONDS).
            build();

    private static final JmxReporter JMX_REPORTER = JmxReporter.forRegistry(METRICS_REGISTRY).
            convertRatesTo(TimeUnit.SECONDS).
            convertDurationsTo(TimeUnit.MILLISECONDS).
            build();

    static {
        CONSOLE_REPORTER.start(10, TimeUnit.SECONDS);
        JMX_REPORTER.start();
    }

    private AkkaMetrics() {}

    public static <T extends Metric> T newMetric(Class<?> klass, String name, T metric) {
        return METRICS_REGISTRY.register(name(klass, name), metric);
    }

    public static Meter newMeter(Class<?> klass, String name) {
        return METRICS_REGISTRY.meter(name(klass, name));
    }

    public static Counter newCounter(Class<?> klass, String name) {
        return METRICS_REGISTRY.counter(name(klass, name));
    }
}