package top.dext.centurybridge.res;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stringified NBT as the game's StringNbtReader reads it and its writer prints it:
 * compounds, lists, typed arrays ([B;..] [I;..] [L;..]), quoted and bare strings,
 * numbers with their b/s/L/f/d suffixes, true/false as bytes. Old data files carry
 * item NBT this way ("nbt": "{Damage:0}"); 1.20.5 split it into components.
 */
public final class Snbt {

    /** a parsed tag: Map (compound), List, String, or Num */
    public record Num(Number value, char type) {
        @Override
        public String toString() {
            return switch (type) {
                case 'b' -> value.byteValue() + "b";
                case 's' -> value.shortValue() + "s";
                case 'l' -> value.longValue() + "L";
                case 'f' -> value.floatValue() + "f";
                case 'd' -> value.doubleValue() + "d";
                default -> String.valueOf(value.intValue());
            };
        }
    }

    /** a typed array, kept apart from a list of the same numbers */
    public record Arr(char type, long[] values) {
    }

    public static Object parse(String s) {
        Snbt p = new Snbt(s);
        Object v = p.value();
        p.ws();
        if (p.i != s.length()) {
            throw p.err("trailing data");
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> compound(String s) {
        Object v = parse(s);
        if (!(v instanceof Map)) {
            throw new IllegalArgumentException("not a compound: " + s);
        }
        return (Map<String, Object>) v;
    }

    /** the tag printed back as SNBT, keys quoted only where the reader needs it */
    @SuppressWarnings("unchecked")
    public static String write(Object v) {
        StringBuilder b = new StringBuilder();
        if (v instanceof Map<?, ?> m) {
            b.append('{');
            boolean first = true;
            for (var e : ((Map<String, Object>) m).entrySet()) {
                if (!first) {
                    b.append(',');
                }
                first = false;
                b.append(bare(e.getKey()) ? e.getKey() : quote(e.getKey())).append(':').append(write(e.getValue()));
            }
            return b.append('}').toString();
        }
        if (v instanceof List<?> l) {
            b.append('[');
            for (int k = 0; k < l.size(); k++) {
                if (k > 0) {
                    b.append(',');
                }
                b.append(write(l.get(k)));
            }
            return b.append(']').toString();
        }
        if (v instanceof Arr a) {
            b.append('[').append(Character.toUpperCase(a.type())).append(';');
            for (int k = 0; k < a.values().length; k++) {
                if (k > 0) {
                    b.append(',');
                }
                b.append(a.values()[k]).append(a.type() == 'b' ? "B" : a.type() == 'l' ? "L" : "");
            }
            return b.append(']').toString();
        }
        if (v instanceof String str) {
            return quote(str);
        }
        return v.toString();
    }

    private static boolean bare(String s) {
        if (s.isEmpty()) {
            return false;
        }
        for (int k = 0; k < s.length(); k++) {
            if (!bareChar(s.charAt(k))) {
                return false;
            }
        }
        return true;
    }

    private static boolean bareChar(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'
            || c == '_' || c == '-' || c == '.' || c == '+';
    }

    private static String quote(String s) {
        char q = s.indexOf('"') >= 0 && s.indexOf('\'') < 0 ? '\'' : '"';
        StringBuilder b = new StringBuilder().append(q);
        for (int k = 0; k < s.length(); k++) {
            char c = s.charAt(k);
            if (c == '\\' || c == q) {
                b.append('\\');
            }
            b.append(c);
        }
        return b.append(q).toString();
    }

    private final String s;
    private int i;

    private Snbt(String s) {
        this.s = s;
    }

    private IllegalArgumentException err(String what) {
        return new IllegalArgumentException("SNBT: " + what + " at " + i + " in " + s);
    }

    private void ws() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
    }

    private Object value() {
        ws();
        if (i >= s.length()) {
            throw err("value expected");
        }
        char c = s.charAt(i);
        if (c == '{') {
            return compoundBody();
        }
        if (c == '[') {
            return listBody();
        }
        if (c == '"' || c == '\'') {
            return quoted();
        }
        return scalar(bareToken());
    }

    private Map<String, Object> compoundBody() {
        Map<String, Object> m = new LinkedHashMap<>();
        i++;
        ws();
        if (i < s.length() && s.charAt(i) == '}') {
            i++;
            return m;
        }
        while (true) {
            ws();
            String key = i < s.length() && (s.charAt(i) == '"' || s.charAt(i) == '\'') ? quoted() : bareToken();
            if (key.isEmpty()) {
                throw err("key expected");
            }
            ws();
            expect(':');
            m.put(key, value());
            ws();
            if (i < s.length() && s.charAt(i) == ',') {
                i++;
                continue;
            }
            expect('}');
            return m;
        }
    }

    private Object listBody() {
        i++;
        ws();
        if (i + 1 < s.length() && s.charAt(i + 1) == ';' && "BIL".indexOf(s.charAt(i)) >= 0) {
            char type = Character.toLowerCase(s.charAt(i));
            i += 2;
            List<Long> vals = new ArrayList<>();
            ws();
            if (i < s.length() && s.charAt(i) == ']') {
                i++;
                return new Arr(type, new long[0]);
            }
            while (true) {
                Object v = scalar(bareToken());
                if (!(v instanceof Num n)) {
                    throw err("number expected in typed array");
                }
                vals.add(n.value().longValue());
                ws();
                if (i < s.length() && s.charAt(i) == ',') {
                    i++;
                    ws();
                    continue;
                }
                expect(']');
                long[] out = new long[vals.size()];
                for (int k = 0; k < out.length; k++) {
                    out[k] = vals.get(k);
                }
                return new Arr(type, out);
            }
        }
        List<Object> l = new ArrayList<>();
        if (i < s.length() && s.charAt(i) == ']') {
            i++;
            return l;
        }
        while (true) {
            l.add(value());
            ws();
            if (i < s.length() && s.charAt(i) == ',') {
                i++;
                continue;
            }
            expect(']');
            return l;
        }
    }

    private String quoted() {
        char q = s.charAt(i++);
        StringBuilder b = new StringBuilder();
        while (i < s.length()) {
            char c = s.charAt(i++);
            if (c == '\\' && i < s.length()) {
                b.append(s.charAt(i++));
            } else if (c == q) {
                return b.toString();
            } else {
                b.append(c);
            }
        }
        throw err("unterminated string");
    }

    private String bareToken() {
        int start = i;
        while (i < s.length() && bareChar(s.charAt(i))) {
            i++;
        }
        return s.substring(start, i);
    }

    private void expect(char c) {
        ws();
        if (i >= s.length() || s.charAt(i) != c) {
            throw err("'" + c + "' expected");
        }
        i++;
    }

    /** a bare token: a number when it reads as one (the reader's patterns), else a string */
    private Object scalar(String t) {
        if (t.isEmpty()) {
            throw err("value expected");
        }
        if (t.equals("true")) {
            return new Num((byte) 1, 'b');
        }
        if (t.equals("false")) {
            return new Num((byte) 0, 'b');
        }
        try {
            char last = Character.toLowerCase(t.charAt(t.length() - 1));
            String body = t.substring(0, t.length() - 1);
            if (t.matches("[-+]?(?:0|[1-9][0-9]*)[bB]")) {
                return new Num(Byte.parseByte(body), 'b');
            }
            if (t.matches("[-+]?(?:0|[1-9][0-9]*)[sS]")) {
                return new Num(Short.parseShort(body), 's');
            }
            if (t.matches("[-+]?(?:0|[1-9][0-9]*)[lL]")) {
                return new Num(Long.parseLong(body), 'l');
            }
            if (t.matches("[-+]?(?:0|[1-9][0-9]*)")) {
                return new Num(Integer.parseInt(t), 'i');
            }
            if (t.matches("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?[fF]")) {
                return new Num(Float.parseFloat(body), 'f');
            }
            if (t.matches("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?[dD]")) {
                return new Num(Double.parseDouble(body), 'd');
            }
            if (t.matches("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?")) {
                return new Num(Double.parseDouble(t), 'd');
            }
            if (last == 'b' || last == 's' || last == 'l') {
                return t; // out of range: the reader falls back to a string
            }
        } catch (NumberFormatException e) {
            return t;
        }
        return t;
    }
}
