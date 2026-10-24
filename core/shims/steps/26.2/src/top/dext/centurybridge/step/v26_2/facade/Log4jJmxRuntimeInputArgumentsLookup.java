package top.dext.centurybridge.step.v26_2.facade;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.lookup.MapLookup;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * log4j-core 2.25's JmxRuntimeInputArgumentsLookup (26.1.2), gone from 26.2's log4j-core: the JVM's input arguments as a
 * MapLookup, its bodies as 2.25 had them -- each argument under its index and, as a key, mapped to the argument after it
 * (MapLookup.toMap / initMap); an empty map on Android or when the runtime MXBean is unavailable (a LinkageError, logged).
 */
public class Log4jJmxRuntimeInputArgumentsLookup extends MapLookup {
    private static final Logger LOGGER = StatusLogger.getLogger();
    public static final Log4jJmxRuntimeInputArgumentsLookup JMX_SINGLETON = new Log4jJmxRuntimeInputArgumentsLookup();

    public Log4jJmxRuntimeInputArgumentsLookup() {
        this(getMapFromJmx());
    }

    public Log4jJmxRuntimeInputArgumentsLookup(Map<String, String> map) {
        super(map);
    }

    @Override
    public String lookup(LogEvent event, String key) {
        if (key == null) {
            return null;
        }
        Map<String, String> map = getMap();
        return map == null ? null : map.get(key);
    }

    private static Map<String, String> getMapFromJmx() {
        if (!System.getProperty("java.vendor").contains("Android")) {
            try {
                return toMap(ManagementFactory.getRuntimeMXBean().getInputArguments());
            } catch (LinkageError e) {
                LOGGER.warn("Failed to get JMX arguments from JVM.", e);
            }
        }
        return Collections.emptyMap();
    }

    private static Map<String, String> toMap(List<String> args) {
        if (args == null) {
            return null;
        }
        String[] a = args.toArray(new String[0]);
        Map<String, String> map = new HashMap<>(a.length);
        for (int i = 0; i < a.length; i++) {
            map.put(Integer.toString(i), a[i]);
            map.put(a[i], i + 1 < a.length ? a[i + 1] : null);
        }
        return map;
    }
}
