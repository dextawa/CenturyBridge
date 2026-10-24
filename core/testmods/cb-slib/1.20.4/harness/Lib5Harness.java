package cb.slib;

import static org.lwjgl.system.MemoryUtil.*;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.util.datetime.DateParser;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.apache.logging.log4j.core.util.datetime.FastDateParser;
import org.apache.logging.log4j.core.util.datetime.Format;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.ExitMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.slf4j.Log4jEventBuilder;
import org.apache.logging.slf4j.Log4jMarkerFactory;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.linux.liburing.IOUringNotificationRegister;
import org.lwjgl.system.linux.liburing.IOUringNotificationSlot;

/**
 * 1.20.4-era (log4j 2.19.0, DFU 6.0.8, LWJGL 3.3.2) uses of the library API 1.20.5 changed, written as a 1.20.4 mod
 * would. lib5_check.sh runs it on 1.20.4's libraries (the reference) and, converted 1.20.4 -> 1.20.5, on 1.20.5's with
 * the step layer; the two outputs must be identical.
 */
public final class Lib5Harness {
    static PrintStream out;

    static void p(String label, Object value) {
        out.println(label + ": " + value);
    }

    static void tryP(String label, ThrowingSupplier s) {
        try {
            p(label, s.get());
        } catch (Throwable t) {
            p(label, "threw " + t.getClass().getName() + ": " + t.getMessage()
                + (t instanceof ParseException pe ? " @" + pe.getErrorOffset() : ""));
        }
    }

    interface ThrowingSupplier {
        Object get() throws Exception;
    }

    static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static void main(String[] args) throws Exception {
        System.setProperty("log4j2.flowMessageFactory", MyFlow.class.getName());
        out = new PrintStream(new java.io.FileOutputStream(args[0]), true, "UTF-8");
        dates();
        ids();
        dfu();
        logs();
        lwjgl();
        out.close();
        System.out.println("LIB5-HARNESS done");
    }

    // ---- log4j datetime

    static final class MyFdp extends FastDateParser {
        MyFdp() {
            super("dd/MM/yyyy", UTC, Locale.ROOT);
        }
    }

    static final class MyParser implements DateParser {
        private final FastDateParser d = new MyFdp();

        public Date parse(String s) throws ParseException { return d.parse(s); }
        public Date parse(String s, ParsePosition p) { return d.parse(s, p); }
        public boolean parse(String s, ParsePosition p, Calendar c) { return d.parse(s, p, c); }
        public String getPattern() { return "my:" + d.getPattern(); }
        public TimeZone getTimeZone() { return d.getTimeZone(); }
        public Locale getLocale() { return d.getLocale(); }
        public Object parseObject(String s) throws ParseException { return d.parseObject(s); }
        public Object parseObject(String s, ParsePosition p) { return d.parseObject(s, p); }
    }

    static final class MyFormat extends Format {
        private final boolean fail;

        MyFormat(boolean fail) {
            this.fail = fail;
        }

        @Override
        public StringBuilder format(Object obj, StringBuilder toAppendTo, FieldPosition pos) {
            return toAppendTo.append(obj);
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            if (fail) {
                pos.setErrorIndex(2);
                return null;
            }
            pos.setIndex(source.length());
            return "P:" + source;
        }
    }

    static final class CenturyFormat extends FastDateFormat {
        CenturyFormat(Date centuryStart) {
            super("yy-MM-dd", UTC, Locale.ROOT, centuryStart);
        }
    }

    static final class OverFormat extends FastDateFormat {
        OverFormat() {
            super("yyyy", UTC, Locale.ROOT);
        }

        @Override
        public Date parse(String source) {
            return new Date(42);
        }

        Date superParse(String s) throws ParseException {
            return super.parse(s);
        }
    }

    static void dates() throws Exception {
        FastDateFormat f = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS", UTC, Locale.ROOT);
        String s = "2024-04-23 10:11:12.345";
        tryP("fdf.parse", () -> f.parse(s).getTime());
        ParsePosition pos = new ParsePosition(3);
        tryP("fdf.parse.pos", () -> f.parse("xx " + s, pos).getTime() + " idx=" + pos.getIndex());
        ParsePosition bad = new ParsePosition(0);
        tryP("fdf.parse.pos.bad", () -> f.parse("nope", bad) + " err=" + bad.getErrorIndex());
        Calendar c = Calendar.getInstance(UTC, Locale.ROOT);
        c.clear();
        tryP("fdf.parse.cal", () -> f.parse(s, new ParsePosition(0), c) + " " + c.getTimeInMillis());
        tryP("fdf.parseObject.pos", () -> ((Date) f.parseObject(s, new ParsePosition(0))).getTime());
        tryP("fdf.parse.bogus", () -> f.parse("bogus"));
        DateParser dp = f;
        tryP("dp.parse", () -> dp.parse(s).getTime());
        tryP("dp.parse.pos", () -> dp.parse(s, new ParsePosition(0)).getTime());
        tryP("dp.meta", () -> dp.getPattern() + " " + dp.getTimeZone().getID() + " " + dp.getLocale());
        tryP("dp.parseObject", () -> ((Date) dp.parseObject(s)).getTime());
        tryP("dp.parseObject.bogus", () -> dp.parseObject("bogus"));
        Format fm = f;
        tryP("format.parseObject", () -> ((Date) fm.parseObject(s)).getTime());
        tryP("format.parseObject.bogus", () -> fm.parseObject("bogus"));
        tryP("format.parseObject.pos", () -> ((Date) fm.parseObject(s, new ParsePosition(0))).getTime());
        tryP("myformat", () -> new MyFormat(false).parseObject("abc"));
        tryP("myformat.fail", () -> new MyFormat(true).parseObject("abc"));
        Format mf = new MyFormat(false);
        tryP("myformat.pos", () -> mf.parseObject("xyz", new ParsePosition(0)));
        DateParser mine = new MyParser();
        tryP("myparser", () -> mine.parse("23/04/2024").getTime() + " " + mine.getPattern());
        FastDateParser fdp = new MyFdp();
        tryP("myfdp", () -> fdp.parse("23/04/2024").getTime() + " " + fdp + " " + fdp.getPattern());
        tryP("myfdp.bogus", () -> fdp.parse("x"));
        Calendar cs = Calendar.getInstance(UTC, Locale.ROOT);
        cs.clear();
        cs.set(1950, 0, 1);
        FastDateFormat c1950 = new CenturyFormat(cs.getTime());
        cs.set(2010, 0, 1);
        FastDateFormat c2010 = new CenturyFormat(cs.getTime());
        Calendar y = Calendar.getInstance(UTC, Locale.ROOT);
        tryP("century.1950", () -> { y.setTime(c1950.parse("05-01-01")); return y.get(Calendar.YEAR); });
        tryP("century.2010", () -> { y.setTime(c2010.parse("05-01-01")); return y.get(Calendar.YEAR); });
        OverFormat of = new OverFormat();
        FastDateFormat ofBase = of;
        tryP("override.parse", () -> ofBase.parse("1999").getTime());
        tryP("override.super", () -> of.superParse("1999").getTime());
        DateParser ofDp = of;
        tryP("override.dp", () -> ofDp.parse("1999").getTime());
    }

    // ---- StructuredDataId.makeId(String, int)

    static final class MyId extends StructuredDataId {
        MyId() {
            super("base", null, null);
        }

        @Override
        public StructuredDataId makeId(String defaultId, int anEnterpriseNumber) {
            return new StructuredDataId("over-" + defaultId, anEnterpriseNumber + 1000, null, null);
        }

        StructuredDataId superMakeId(String d, int n) {
            return super.makeId(d, n);
        }
    }

    static void ids() {
        StructuredDataId id = new MyId();
        tryP("sdid.override", () -> id.makeId("x", 7));
        tryP("sdid.super", () -> ((MyId) id).superMakeId("x", 7));
        tryP("sdid.base", () -> new StructuredDataId("a", null, null).makeId("b", 6));
        tryP("sdid.reserved", () -> StructuredDataId.TIME_QUALITY.makeId("q", 3));
    }

    // ---- DFU

    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class MyKdc extends KeyDispatchCodec {
        MyKdc(boolean assumeMap, Codec mapc) {
            super("type", Codec.STRING, v -> DataResult.success(v instanceof Integer ? "int" : "map"),
                k -> DataResult.success("int".equals(k) ? Codec.INT : mapc),
                v -> DataResult.success(v instanceof Integer ? Codec.INT : mapc), assumeMap);
        }
    }

    static JsonElement sorted(JsonElement e) {
        if (e == null || !e.isJsonObject()) {
            return e;
        }
        com.google.gson.JsonObject o = new com.google.gson.JsonObject();
        new java.util.TreeMap<>(e.getAsJsonObject().asMap()).forEach((k, v) -> o.add(k, sorted(v)));
        return o;
    }

    static final class MyDyn extends DynamicLike<JsonElement> {
        private final Dynamic<JsonElement> d;

        MyDyn(JsonElement e) {
            super(JsonOps.INSTANCE);
            this.d = new Dynamic<>(JsonOps.INSTANCE, e);
        }

        public DataResult<Number> asNumber() { return d.asNumber(); }
        public DataResult<String> asString() { return d.asString(); }
        public DataResult<Stream<Dynamic<JsonElement>>> asStreamOpt() { return d.asStreamOpt(); }
        public DataResult<Stream<Pair<Dynamic<JsonElement>, Dynamic<JsonElement>>>> asMapOpt() { return d.asMapOpt(); }
        public DataResult<ByteBuffer> asByteBufferOpt() { return d.asByteBufferOpt(); }
        public DataResult<IntStream> asIntStreamOpt() { return d.asIntStreamOpt(); }
        public DataResult<LongStream> asLongStreamOpt() { return d.asLongStreamOpt(); }
        public OptionalDynamic<JsonElement> get(String key) { return d.get(key); }
        public DataResult<JsonElement> getGeneric(JsonElement key) { return d.getGeneric(key); }
        public DataResult<JsonElement> getElement(String key) { return d.getElement(key); }
        public DataResult<JsonElement> getElementGeneric(JsonElement key) { return d.getElementGeneric(key); }
        public <A> DataResult<Pair<A, JsonElement>> decode(Decoder<? extends A> decoder) { return d.decode(decoder); }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void dfu() {
        Codec mapc = Codec.unboundedMap(Codec.STRING, Codec.INT);
        for (boolean assumeMap : new boolean[] {false, true}) {
            KeyDispatchCodec k = new MyKdc(assumeMap, mapc);
            Codec c = k.codec();
            String tag = "kdc." + assumeMap;
            // content, keys sorted: DFU 7's KeyDispatchCodec writes the value's fields before the type key, for every
            // dispatch codec (vanilla's too); DFU 6 wrote the type first
            tryP(tag + ".int", () -> sorted((JsonElement) c.encodeStart(JsonOps.INSTANCE, 5).result().orElse(null)));
            tryP(tag + ".map", () -> sorted((JsonElement) c.encodeStart(JsonOps.INSTANCE, Map.of("a", 1)).result().orElse(null)));
            Object intJson = c.encodeStart(JsonOps.INSTANCE, 5).result().orElse(null);
            Object mapJson = c.encodeStart(JsonOps.INSTANCE, Map.of("a", 1)).result().orElse(null);
            tryP(tag + ".int.back", () -> intJson == null ? null : c.parse(JsonOps.INSTANCE, intJson).result().orElse(null));
            tryP(tag + ".map.back", () -> mapJson == null ? null : c.parse(JsonOps.INSTANCE, mapJson).result().orElse(null));
        }
        tryP("dyn.one", () -> new MyDyn(new com.google.gson.JsonPrimitive(1)).asBoolean(false));
        tryP("dyn.zero", () -> new MyDyn(new com.google.gson.JsonPrimitive(0)).asBoolean(true));
        tryP("dyn.str", () -> new MyDyn(new com.google.gson.JsonPrimitive("s")).asBoolean(true));
        tryP("dyn.256", () -> new MyDyn(new com.google.gson.JsonPrimitive(256)).asBoolean(false));
    }

    // ---- log4j logging

    static final List<String> LOGGED = new ArrayList<>();

    static final class Msg implements EntryMessage, ExitMessage {
        private static final long serialVersionUID = 1L;
        private final String text;
        private final Message message;

        Msg(String text, Message message) {
            this.text = text;
            this.message = message;
        }

        public String getText() { return text; }
        public Message getMessage() { return message; }
        public String getFormattedMessage() { return text + "[" + (message == null ? "null" : message.getFormattedMessage()) + "]"; }
        public String getFormat() { return text; }
        public Object[] getParameters() { return null; }
        public Throwable getThrowable() { return null; }
    }

    public static final class MyFlow implements FlowMessageFactory {
        public EntryMessage newEntryMessage(Message message) { return new Msg("E", message); }
        public ExitMessage newExitMessage(Object result, Message message) { return new Msg("X(" + result + ")", message); }
        public ExitMessage newExitMessage(EntryMessage message) { return new Msg("XE", message); }
        public ExitMessage newExitMessage(Object result, EntryMessage message) { return new Msg("XE(" + result + ")", message); }
    }

    static final class TestLogger extends AbstractLogger {
        private static final long serialVersionUID = 1L;

        TestLogger() {
            super("cb.test");
        }

        ThreadLocal<?> builders() {
            return this.logBuilder;
        }

        @Override
        public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t) {
            LOGGED.add(level + "|" + (marker == null ? "-" : marker.getName()) + "|" + message.getFormattedMessage()
                + (t == null ? "" : "|" + t.getMessage()));
        }

        @Override
        public Level getLevel() { return Level.ALL; }

        public boolean isEnabled(Level l, Marker m, Message msg, Throwable t) { return true; }
        public boolean isEnabled(Level l, Marker m, CharSequence msg, Throwable t) { return true; }
        public boolean isEnabled(Level l, Marker m, Object msg, Throwable t) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Throwable t) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object... ps) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2, Object p3) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2, Object p3, Object p4) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) { return true; }
        public boolean isEnabled(Level l, Marker m, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) { return true; }
    }

    static void flush(String label) {
        p(label, String.join(" ;; ", LOGGED));
        LOGGED.clear();
    }

    @SuppressWarnings("deprecation")
    static void logs() {
        TestLogger log = new TestLogger();
        tryP("flow.class", () -> AbstractLogger.DEFAULT_FLOW_MESSAGE_FACTORY_CLASS.getSimpleName());
        log.traceEntry();
        flush("traceEntry()");
        log.traceEntry("fmt {} {}", 1, "two");
        flush("traceEntry(fmt)");
        log.traceEntry((String) null, "a", 3);
        flush("traceEntry(null, params)");
        log.traceEntry("only");
        flush("traceEntry(only)");
        EntryMessage em = log.traceEntry("e {}", 9);
        LOGGED.clear();
        log.traceExit(em);
        flush("traceExit(entry)");
        log.traceExit(em, 11);
        flush("traceExit(entry, r)");
        log.traceExit();
        flush("traceExit()");
        log.traceExit(5);
        flush("traceExit(r)");
        log.traceExit("res {}", 7);
        flush("traceExit(fmt, r)");
        log.traceExit((String) null, 7);
        flush("traceExit(null, r)");
        log.entry(1, 2);
        flush("entry(params)");
        log.exit(3);
        flush("exit(r)");
        ThreadLocal<?> b = log.builders();
        tryP("logBuilder", () -> (b != null) + " " + (b.get() != null) + " " + (b.get() == log.builders().get())
            + " " + b.get().getClass().getName());
        Log4jEventBuilder eb = new Log4jEventBuilder(new Log4jMarkerFactory(), log.atWarn());
        eb.addArgument(1).addArgument(() -> "sup").setCause(new RuntimeException("boom"));
        eb.log("event {} {}");
        flush("eventBuilder");
        new Log4jEventBuilder(new Log4jMarkerFactory(), log.atInfo()).log("plain {}", "x");
        flush("eventBuilder.plain");
    }

    // ---- LWJGL

    static final class MyStruct extends Struct {
        static final int SIZEOF, ALIGNOF, A, B;

        static {
            Layout layout = __struct(__member(4), __member(4));
            SIZEOF = layout.getSize();
            ALIGNOF = layout.getAlignment();
            A = layout.offsetof(0);
            B = layout.offsetof(1);
        }

        MyStruct(ByteBuffer container) {
            super(memAddress(container), __checkContainer(container, SIZEOF));
        }

        @Override
        public int sizeof() {
            return SIZEOF;
        }

        int a() { return memGetInt(address() + A); }
        int b() { return memGetInt(address() + B); }

        static MyStruct create(long address) {
            return wrap(MyStruct.class, address);
        }

        static MyStruct create(long address, ByteBuffer container) {
            return wrap(MyStruct.class, address, container);
        }

        static MyStruct copyOf(MyStruct s) {
            return wrap(MyStruct.class, s);
        }

        static Buffer create(long address, int capacity) {
            return wrap(Buffer.class, address, capacity);
        }

        static Buffer create(long address, int capacity, ByteBuffer container) {
            return wrap(Buffer.class, address, capacity, container);
        }

        static Buffer copyOf(Buffer b) {
            return wrap(Buffer.class, b);
        }

        static final class Buffer extends StructBuffer<MyStruct, Buffer> {
            private static final MyStruct ELEMENT_FACTORY = MyStruct.create(-1L);

            Buffer(ByteBuffer container) {
                super(container, container.remaining() / SIZEOF);
            }

            Buffer(long address, ByteBuffer container, int mark, int pos, int lim, int cap) {
                super(address, container, mark, pos, lim, cap);
            }

            @Override
            protected Buffer self() {
                return this;
            }

            @Override
            protected MyStruct getElementFactory() {
                return ELEMENT_FACTORY;
            }
        }
    }

    static void lwjgl() {
        ByteBuffer mem = memAlloc(3 * MyStruct.SIZEOF);
        IntBuffer ints = mem.asIntBuffer();
        for (int i = 0; i < 6; i++) {
            ints.put(i, 10 + i);
        }
        long a = memAddress(mem);
        tryP("struct.wrap", () -> MyStruct.create(a + MyStruct.SIZEOF).a() + " " + MyStruct.create(a + MyStruct.SIZEOF).b());
        tryP("struct.wrap.container", () -> MyStruct.create(a, mem).b());
        tryP("struct.wrap.copy", () -> MyStruct.copyOf(MyStruct.create(a + 2L * MyStruct.SIZEOF)).a());
        MyStruct.Buffer buf = MyStruct.create(a, 3);
        tryP("buffer.get", () -> buf.get(2).a() + " " + buf.get(2).b() + " " + buf.get(2).getClass().getSimpleName()
            + " cap=" + buf.capacity() + " lim=" + buf.limit() + " pos=" + buf.position());
        StringBuilder it = new StringBuilder();
        tryP("buffer.iterate", () -> {
            for (MyStruct s : buf) {
                it.append(s.a()).append(',').append(s.b()).append(';');
            }
            return it;
        });
        tryP("buffer.relative", () -> {
            MyStruct.Buffer b2 = MyStruct.create(a, 3, mem);
            b2.position(1);
            MyStruct.Buffer c = MyStruct.copyOf(b2);
            return c.get().a() + " cap=" + c.capacity() + " rem=" + c.remaining();
        });
        tryP("buffer.slice", () -> buf.slice(1, 2).get(1).b());
        memFree(mem);

        tryP("uring.sizes", () -> IOUringNotificationSlot.SIZEOF + " " + IOUringNotificationSlot.ALIGNOF + " "
            + IOUringNotificationSlot.TAG + " " + IOUringNotificationSlot.RESV + " " + IOUringNotificationRegister.SIZEOF + " "
            + IOUringNotificationRegister.NR_SLOTS + " " + IOUringNotificationRegister.RESV2 + " " + IOUringNotificationRegister.DATA
            + " " + IOUringNotificationRegister.RESV3);
        IOUringNotificationSlot.Buffer slots = IOUringNotificationSlot.calloc(2);
        slots.get(1).tag(77).resv(2, 5L);
        IOUringNotificationRegister reg = IOUringNotificationRegister.calloc();
        reg.nr_slots(2).data(slots.address()).resv3(9);
        tryP("uring.values", () -> slots.get(1).tag() + " " + slots.get(1).resv(2) + " " + slots.get(0).tag() + " "
            + reg.nr_slots() + " " + (reg.data() == slots.address()) + " " + reg.resv3() + " "
            + IOUringNotificationRegister.nnr_slots(reg.address()));
        LongBuffer resv = slots.get(1).resv();
        tryP("uring.resv", () -> resv.get(0) + "," + resv.get(1) + "," + resv.get(2) + " rem=" + resv.remaining());
        tryP("uring.iterate", () -> {
            StringBuilder sb = new StringBuilder();
            for (IOUringNotificationSlot s : slots) {
                sb.append(s.tag()).append(';');
            }
            return sb;
        });
        reg.free();
        slots.free();
    }
}
