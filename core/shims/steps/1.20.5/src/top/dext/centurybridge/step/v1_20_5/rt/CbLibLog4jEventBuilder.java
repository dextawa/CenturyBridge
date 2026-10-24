package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.BridgeAware;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.slf4j.Log4jEventBuilder;
import org.apache.logging.slf4j.Log4jMarker;
import org.apache.logging.slf4j.Log4jMarkerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LoggingEventBuilder;

/**
 * log4j-slf4j2-impl 2.19.0's Log4jEventBuilder(Log4jMarkerFactory, LogBuilder) (1.20.4): an slf4j event builder over a
 * log4j LogBuilder handed in. 2.22.1's takes a logger and a level and makes its LogBuilder at log(); this subclass is
 * 2.19.0's class (its source, Apache License 2.0), every member over the LogBuilder given, so a converted
 * {@code new Log4jEventBuilder(factory, builder)} (ctorRedirects) and a mod subclass of it (superRenames) behave as on
 * 2.19.0. Its entry point for caller location is its own class, as 2.19.0's was its own.
 */
public class CbLibLog4jEventBuilder extends Log4jEventBuilder {

    private static final String FQCN = CbLibLog4jEventBuilder.class.getName();

    private final Log4jMarkerFactory markerFactory;
    private final LogBuilder logBuilder;
    private final List<Object> arguments = new ArrayList<>();
    private String message = null;
    private Map<String, String> keyValuePairs = null;

    public CbLibLog4jEventBuilder(final Log4jMarkerFactory markerFactory, final LogBuilder logBuilder) {
        super(markerFactory, null, null);
        this.markerFactory = markerFactory;
        this.logBuilder = logBuilder;
        if (logBuilder instanceof BridgeAware) {
            ((BridgeAware) logBuilder).setEntryPoint(FQCN);
        }
    }

    /** new Log4jEventBuilder(markerFactory, logBuilder) (ctorRedirects) */
    public static Log4jEventBuilder create(final Log4jMarkerFactory markerFactory, final LogBuilder logBuilder) {
        return new CbLibLog4jEventBuilder(markerFactory, logBuilder);
    }

    @Override
    public LoggingEventBuilder setCause(Throwable cause) {
        logBuilder.withThrowable(cause);
        return this;
    }

    @Override
    public LoggingEventBuilder addMarker(Marker marker) {
        logBuilder.withMarker(log4jMarker(marker));
        return this;
    }

    /** Log4jMarkerFactory.getLog4jMarker (package-private), its body through the factory's public members */
    private org.apache.logging.log4j.Marker log4jMarker(final Marker marker) {
        if (marker == null) {
            return null;
        } else if (marker instanceof Log4jMarker) {
            return ((Log4jMarker) marker).getLog4jMarker();
        } else {
            return ((Log4jMarker) markerFactory.getMarker(marker)).getLog4jMarker();
        }
    }

    @Override
    public LoggingEventBuilder addArgument(Object p) {
        arguments.add(p);
        return this;
    }

    @Override
    public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
        arguments.add(objectSupplier.get());
        return this;
    }

    @Override
    public LoggingEventBuilder addKeyValue(String key, Object value) {
        if (keyValuePairs == null) {
            keyValuePairs = new HashMap<>();
        }
        keyValuePairs.put(key, String.valueOf(value));
        return this;
    }

    @Override
    public LoggingEventBuilder addKeyValue(String key, Supplier<Object> valueSupplier) {
        if (keyValuePairs == null) {
            keyValuePairs = new HashMap<>();
        }
        keyValuePairs.put(key, String.valueOf(valueSupplier.get()));
        return this;
    }

    @Override
    public LoggingEventBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
        this.message = messageSupplier.get();
        return this;
    }

    @Override
    public void log() {
        if (keyValuePairs == null || keyValuePairs.isEmpty()) {
            logBuilder.log(message, arguments.toArray());
        } else {
            try (Instance c = CloseableThreadContext.putAll(keyValuePairs)) {
                logBuilder.log(message, arguments.toArray());
            }
        }
    }

    @Override
    public void log(String message) {
        setMessage(message);
        log();
    }

    @Override
    public void log(String message, Object arg) {
        setMessage(message);
        addArgument(arg);
        log();
    }

    @Override
    public void log(String message, Object arg0, Object arg1) {
        setMessage(message);
        addArgument(arg0);
        addArgument(arg1);
        log();
    }

    @Override
    public void log(String message, Object... args) {
        setMessage(message);
        for (final Object arg : args) {
            addArgument(arg);
        }
        log();
    }

    @Override
    public void log(Supplier<String> messageSupplier) {
        setMessage(messageSupplier);
        log();
    }
}
