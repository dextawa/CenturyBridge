package cb.slib;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.ModInitializer;

/**
 * A 26.1.2-era mod (compiled against DFU 9.0.19, authlib, log4j-core 2.25, logging 1.6.11) for slib's library bridges of
 * the 26.1.2 -> 26.2 step. Each check prints "CB-SLIB ok|FAIL ..."; the last line is "CB-SLIB DONE passed/checks".
 */
@com.mojang.logging.annotations.MethodsReturnNonnullByDefault
public class SlibLibs262Test implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) FAILS.add(what);
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
    }

    /** a mod's own DynamicOps (JSON through JsonOps), relying on DFU 9's default boolean methods */
    static class MyOps implements DynamicOps<JsonElement> {
        static final JsonOps J = JsonOps.INSTANCE;

        public JsonElement empty() { return J.empty(); }
        public <U> U convertTo(DynamicOps<U> o, JsonElement e) { return J.convertTo(o, e); }
        public DataResult<Number> getNumberValue(JsonElement e) { return J.getNumberValue(e); }
        public JsonElement createNumeric(Number n) { return J.createNumeric(n); }
        public DataResult<String> getStringValue(JsonElement e) { return J.getStringValue(e); }
        public JsonElement createString(String s) { return J.createString(s); }
        public DataResult<JsonElement> mergeToList(JsonElement l, JsonElement v) { return J.mergeToList(l, v); }
        public DataResult<JsonElement> mergeToMap(JsonElement m, JsonElement k, JsonElement v) { return J.mergeToMap(m, k, v); }
        public DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement e) { return J.getMapValues(e); }
        public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> m) { return J.createMap(m); }
        public DataResult<Stream<JsonElement>> getStream(JsonElement e) { return J.getStream(e); }
        public JsonElement createList(Stream<JsonElement> l) { return J.createList(l); }
        public JsonElement remove(JsonElement e, String k) { return J.remove(e, k); }
    }

    @Override
    public void onInitialize() {
        run("DynamicOps defaults", () -> {
            MyOps ops = new MyOps();
            JsonElement t = ops.createBoolean(true);
            check(t instanceof JsonPrimitive p && p.getAsByte() == 1, "createBoolean(true) is the byte 1, as DFU 9's default");
            check(ops.getBooleanValue(new JsonPrimitive(2)).result().orElse(false), "getBooleanValue of a non-zero number is true");
            check(!ops.getBooleanValue(new JsonPrimitive(0)).result().orElse(true), "getBooleanValue of zero is false");
        });
        run("UserAttributesResponse", () -> {
            var r = new com.mojang.authlib.yggdrasil.response.UserAttributesResponse(null, null, null);
            check(r.privileges() == null && r.banStatus() == null, "the 3-argument response builds");
        });
        run("JmxRuntimeInputArgumentsLookup", () -> {
            var l = org.apache.logging.log4j.core.lookup.JmxRuntimeInputArgumentsLookup.JMX_SINGLETON;
            check(l != null && l.lookup(null, "0") != null, "the JVM's first input argument under key 0");
            var m = new org.apache.logging.log4j.core.lookup.JmxRuntimeInputArgumentsLookup(java.util.Map.of("a", "b"));
            check("b".equals(m.lookup(null, "a")) && m.lookup(null, null) == null, "a lookup over a given map");
        });
        System.out.println("CB-SLIB DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}
