package top.dext.centurybridge.step.v1_20_5.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import com.google.common.collect.MapMaker;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.apache.logging.log4j.core.util.datetime.Format;
import org.apache.logging.log4j.internal.DefaultLogBuilder;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.ExitMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StructuredDataId;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.util.Strings;

/**
 * log4j 2.19.0 (1.20.4) API that 2.22.1 (1.20.5) dropped or changed, with 2.19.0's behaviour (read off its source):
 * <ul>
 * <li>FastDateFormat's parsing (parse x3, parseObject) and its DateParser side: 2.19.0's FastDateFormat held a
 *     FastDateParser(pattern, time zone, locale, century start) and delegated to it; 2.22.1 formats only. The parser is
 *     2.19.0's own (CbLibLog4jFastDateParser, ported source), one per format, made as 2.19.0 made it. A call reaches a
 *     mod subclass's own override first, as the virtual call did on 2.19.0;</li>
 * <li>Format.parseObject(String) / parseObject(String, ParsePosition) (2.22.1's Format formats only);</li>
 * <li>the FlowMessageFactory / Logger members 2.22.1 added as abstract, for a 2.19.0 implementation that cannot have
 *     them: each answers what 2.19.0's AbstractLogger made of the same logging call;</li>
 * <li>AbstractLogger.logBuilder, 2.19.0's per-logger ThreadLocal of DefaultLogBuilder (2.22.1: one private static);</li>
 * <li>StructuredDataId.makeId(String, int), final in 2.22.1: a mod's override is renamed by the converter
 *     (overrideRenames) and reached through {@link StructuredDataIdOld}, as a virtual call reached it on 2.19.0.</li>
 * </ul>
 */
public final class CbLibLog4j {
    private CbLibLog4j() {
    }

    // ---- FastDateFormat's parser

    // weak, identity-keyed (FastDateFormat.equals compares pattern, zone and locale, not the century)
    private static final Map<FastDateFormat, Date> CENTURY = new MapMaker().weakKeys().makeMap();
    private static final Map<FastDateFormat, CbLibLog4jFastDateParser> PARSERS = new MapMaker().weakKeys().makeMap();

    /** superCtorReshapes post of FastDateFormat(pattern, timeZone, locale, centuryStart): 2.19.0 gave the century to its parser */
    public static void century(FastDateFormat format, Date centuryStart) {
        if (centuryStart != null) {
            CENTURY.put(format, centuryStart);
        }
    }

    /** the FastDateParser 2.19.0's FastDateFormat(pattern, timeZone, locale, centuryStart) constructor made */
    public static CbLibLog4jFastDateParser parser(FastDateFormat f) {
        return PARSERS.computeIfAbsent(f, k -> new CbLibLog4jFastDateParser(k.getPattern(), k.getTimeZone(), k.getLocale(), CENTURY.get(k)));
    }

    /** a mod class's own declaration of name(params) under {@code base}, or null (log4j's classes declare none on 2.22.1) */
    private static MethodHandle own(Class<?> c, Class<?> base, String name, MethodType type) {
        for (Class<?> k = c; k != null && k != base && base.isAssignableFrom(k); k = k.getSuperclass()) {
            try {
                java.lang.reflect.Method m = k.getDeclaredMethod(name, type.parameterArray());
                if (!java.lang.reflect.Modifier.isStatic(m.getModifiers()) && !java.lang.reflect.Modifier.isPrivate(m.getModifiers())) {
                    m.setAccessible(true);
                    return MethodHandles.lookup().unreflect(m);
                }
            } catch (NoSuchMethodException e) {
                // not declared here
            } catch (ReflectiveOperationException | RuntimeException e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    private static final class Own extends ClassValue<MethodHandle[]> {
        private final Class<?> base;
        private final String[] names;
        private final MethodType[] types;

        Own(Class<?> base, String[] names, MethodType[] types) {
            this.base = base;
            this.names = names;
            this.types = types;
        }

        @Override
        protected MethodHandle[] computeValue(Class<?> c) {
            MethodHandle[] r = new MethodHandle[this.names.length];
            for (int i = 0; i < r.length; i++) {
                r[i] = own(c, this.base, this.names[i], this.types[i]);
            }
            return r;
        }
    }

    private static final MethodType PARSE_S = MethodType.methodType(Date.class, String.class);
    private static final MethodType PARSE_SP = MethodType.methodType(Date.class, String.class, ParsePosition.class);
    private static final MethodType PARSE_SPC = MethodType.methodType(boolean.class, String.class, ParsePosition.class, Calendar.class);
    private static final MethodType PARSE_OBJECT_SP = MethodType.methodType(Object.class, String.class, ParsePosition.class);
    private static final Own FORMAT_OWN = new Own(Format.class,
        new String[] {"parse", "parse", "parse", "parseObject"}, new MethodType[] {PARSE_S, PARSE_SP, PARSE_SPC, PARSE_OBJECT_SP});

    private static RuntimeException rethrow(Throwable t) {
        if (t instanceof RuntimeException r) {
            throw r;
        }
        if (t instanceof Error e) {
            throw e;
        }
        return new IllegalStateException(t);
    }

    /** FastDateFormat.parse(String) */
    public static Date parse(FastDateFormat self, String source) throws ParseException {
        MethodHandle h = FORMAT_OWN.get(self.getClass())[0];
        if (h != null) {
            try {
                return (Date) h.invoke(self, source);
            } catch (ParseException e) {
                throw e;
            } catch (Throwable t) {
                throw rethrow(t);
            }
        }
        return parser(self).parse(source);
    }

    /** FastDateFormat.parse(String, ParsePosition) */
    public static Date parse(FastDateFormat self, String source, ParsePosition pos) {
        MethodHandle h = FORMAT_OWN.get(self.getClass())[1];
        if (h != null) {
            try {
                return (Date) h.invoke(self, source, pos);
            } catch (Throwable t) {
                throw rethrow(t);
            }
        }
        return parser(self).parse(source, pos);
    }

    /** FastDateFormat.parse(String, ParsePosition, Calendar) */
    public static boolean parse(FastDateFormat self, String source, ParsePosition pos, Calendar calendar) {
        MethodHandle h = FORMAT_OWN.get(self.getClass())[2];
        if (h != null) {
            try {
                return (boolean) h.invoke(self, source, pos, calendar);
            } catch (Throwable t) {
                throw rethrow(t);
            }
        }
        return parser(self).parse(source, pos, calendar);
    }

    /** FastDateFormat.parseObject(String, ParsePosition) */
    public static Object parseObject(FastDateFormat self, String source, ParsePosition pos) {
        return parseObject((Format) self, source, pos);
    }

    /** Format.parseObject(String, ParsePosition): the class's own (abstract in 2.19.0), FastDateFormat's its parser's */
    public static Object parseObject(Format self, String source, ParsePosition pos) {
        MethodHandle h = FORMAT_OWN.get(self.getClass())[3];
        if (h != null) {
            try {
                return h.invoke(self, source, pos);
            } catch (Throwable t) {
                throw rethrow(t);
            }
        }
        if (self instanceof FastDateFormat f) {
            return parser(f).parseObject(source, pos);
        }
        throw new AbstractMethodError(self.getClass().getName() + ".parseObject(String, ParsePosition)");
    }

    /** Format.parseObject(String): 2.19.0's body */
    public static Object parseObject(Format self, String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Object result = parseObject(self, source, pos);
        if (pos.getIndex() == 0) {
            throw new ParseException("Format.parseObject(String) failed", pos.getErrorIndex());
        }
        return result;
    }

    // ---- DateParser, a FastDateFormat included (2.19.0's FastDateFormat implemented DateParser)

    /** DateParser.parse(String) */
    public static Date parse(CbLibLog4jDateParser self, String source) throws ParseException {
        return (Object) self instanceof FastDateFormat f ? parse(f, source) : self.parse(source);
    }

    /** DateParser.parse(String, ParsePosition) */
    public static Date parse(CbLibLog4jDateParser self, String source, ParsePosition pos) {
        return (Object) self instanceof FastDateFormat f ? parse(f, source, pos) : self.parse(source, pos);
    }

    /** DateParser.parse(String, ParsePosition, Calendar) */
    public static boolean parse(CbLibLog4jDateParser self, String source, ParsePosition pos, Calendar calendar) {
        return (Object) self instanceof FastDateFormat f ? parse(f, source, pos, calendar) : self.parse(source, pos, calendar);
    }

    /** DateParser.parseObject(String) */
    public static Object parseObject(CbLibLog4jDateParser self, String source) throws ParseException {
        return (Object) self instanceof FastDateFormat f ? parseObject((Format) f, source) : self.parseObject(source);
    }

    /** DateParser.parseObject(String, ParsePosition) */
    public static Object parseObject(CbLibLog4jDateParser self, String source, ParsePosition pos) {
        return (Object) self instanceof FastDateFormat f ? parseObject((Format) f, source, pos) : self.parseObject(source, pos);
    }

    /** DateParser.getPattern() */
    public static String getPattern(CbLibLog4jDateParser self) {
        return (Object) self instanceof FastDateFormat f ? f.getPattern() : self.getPattern();
    }

    /** DateParser.getTimeZone() */
    public static TimeZone getTimeZone(CbLibLog4jDateParser self) {
        return (Object) self instanceof FastDateFormat f ? f.getTimeZone() : self.getTimeZone();
    }

    /** DateParser.getLocale() */
    public static Locale getLocale(CbLibLog4jDateParser self) {
        return (Object) self instanceof FastDateFormat f ? f.getLocale() : self.getLocale();
    }

    // ---- flow messages

    /**
     * FlowMessageFactory.newEntryMessage(String, Object...) for a 2.19.0 factory: 2.22.1's AbstractLogger.entryMsg hands
     * the format and parameters to the factory; 2.19.0's built the message itself and gave the factory that.
     */
    public static EntryMessage newEntryMessage(FlowMessageFactory self, String format, Object... params) {
        int count = params == null ? 0 : params.length;
        if (count == 0) {
            if (Strings.isEmpty(format)) {
                return self.newEntryMessage((Message) null);
            }
            return self.newEntryMessage(new SimpleMessage(format));
        }
        if (format != null) {
            return self.newEntryMessage(new ParameterizedMessage(format, params));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("params(");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object parm = params[i];
            sb.append(parm instanceof Message m ? m.getFormattedMessage() : String.valueOf(parm));
        }
        sb.append(')');
        return self.newEntryMessage(new SimpleMessage(sb));
    }

    /**
     * FlowMessageFactory.newExitMessage(String, Object) for a 2.19.0 factory: 2.19.0's AbstractLogger.exit(fqcn, format,
     * result) did not consult the factory; it logged exitMsg(format, result), which this answers, unwrapped.
     */
    public static ExitMessage newExitMessage(FlowMessageFactory self, String format, Object result) {
        Message m;
        if (result == null) {
            m = new ParameterizedMessage(format == null ? "Exit" : "Exit: " + format);
        } else if (format == null) {
            m = new ParameterizedMessage("Exit with(" + result + ')');
        } else {
            m = new ParameterizedMessage("Exit: " + format, result);
        }
        return new Exit(m);
    }

    /** FlowMessageFactory.newExitMessage(Message) for a 2.19.0 factory: the message as given, as 2.19.0 logged it */
    public static ExitMessage newExitMessage(FlowMessageFactory self, Message message) {
        return new Exit(message);
    }

    /** an exit message that is exactly the message 2.19.0's logger logged */
    private static final class Exit implements ExitMessage {
        private static final long serialVersionUID = 1L;
        private final Message message;

        Exit(Message message) {
            this.message = message;
        }

        @Override
        public Message getMessage() {
            return this.message;
        }

        /** the logged text itself (2.19.0's exit message carried no separate flow text) */
        @Override
        public String getText() {
            return getFormattedMessage();
        }

        @Override
        public String getFormattedMessage() {
            return this.message == null ? null : this.message.getFormattedMessage();
        }

        @Override
        public String getFormat() {
            return this.message == null ? null : this.message.getFormat();
        }

        @Override
        public Object[] getParameters() {
            return this.message == null ? null : this.message.getParameters();
        }

        @Override
        public Throwable getThrowable() {
            return this.message == null ? null : this.message.getThrowable();
        }
    }

    /**
     * Logger.getFlowMessageFactory() for a 2.19.0 Logger implemented without AbstractLogger: the factory 2.19.0's
     * AbstractLogger gave every logger (an instance of DEFAULT_FLOW_MESSAGE_FACTORY_CLASS, log4j2.flowMessageFactory).
     */
    public static FlowMessageFactory getFlowMessageFactory(Logger self) {
        try {
            return AbstractLogger.DEFAULT_FLOW_MESSAGE_FACTORY_CLASS.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | RuntimeException e) {
            return new DefaultFlowMessageFactory();
        }
    }

    /** the same, for a class implementing ExtendedLogger (the fill names the interface the class implements) */
    public static FlowMessageFactory getFlowMessageFactory(ExtendedLogger self) {
        return getFlowMessageFactory((Logger) self);
    }

    // ---- AbstractLogger.logBuilder

    private static final Map<AbstractLogger, ThreadLocal<DefaultLogBuilder>> LOG_BUILDERS = new MapMaker().weakKeys().makeMap();

    /** AbstractLogger.logBuilder: 2.19.0's per-logger thread-local builder, made unused (no level) for that logger */
    public static ThreadLocal<DefaultLogBuilder> logBuilder(AbstractLogger self) {
        return LOG_BUILDERS.computeIfAbsent(self, l -> ThreadLocal.withInitial(() -> new DefaultLogBuilder(l, null)));
    }

    // ---- StructuredDataId.makeId(String, int)

    /** a mod class's makeId(String, int) override, renamed by the converter (final on 2.22.1) */
    public interface StructuredDataIdOld {
        StructuredDataId makeId$cb1_20_5(String defaultId, int anEnterpriseNumber);
    }

    /** StructuredDataId.makeId(String, int): the object's own override, as the virtual call reached it on 2.19.0 */
    public static StructuredDataId makeId(StructuredDataId self, String defaultId, int anEnterpriseNumber) {
        return (Object) self instanceof StructuredDataIdOld o ? o.makeId$cb1_20_5(defaultId, anEnterpriseNumber)
            : self.makeId(defaultId, anEnterpriseNumber);
    }
}
