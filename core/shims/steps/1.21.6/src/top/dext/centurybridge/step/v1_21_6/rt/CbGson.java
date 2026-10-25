package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5 GsonHelper's overloads taking a lenient flag, which 1.21.6 removed (it keeps only the strict ones): 1.21.5's
 * bodies, line for line -- a JsonReader on the reader set lenient as asked, the type's adapter, and for the
 * fromJson forms the JsonParseException "JSON data was null or empty" on a null answer. parse(..) uses GsonHelper's
 * own Gson (field_15657, opened by the layer's access widener), as 1.21.5 did.
 */
public final class CbGson {

    private CbGson() {
    }

    /** fromNullableJson(Gson, Reader, TypeToken, lenient) */
    public static <T> T method_47992(com.google.gson.Gson gson, java.io.Reader reader, com.google.gson.reflect.TypeToken<T> type,
            boolean lenient) throws java.io.IOException {
        com.google.gson.stream.JsonReader json = new com.google.gson.stream.JsonReader(reader);
        json.setLenient(lenient);
        return gson.getAdapter(type).read(json);
    }

    /** fromNullableJson(Gson, Reader, Class, lenient) */
    public static <T> T method_47993(com.google.gson.Gson gson, java.io.Reader reader, Class<T> type, boolean lenient)
            throws java.io.IOException {
        com.google.gson.stream.JsonReader json = new com.google.gson.stream.JsonReader(reader);
        json.setLenient(lenient);
        return gson.getAdapter(type).read(json);
    }

    /** fromJson(Gson, Reader, Class, lenient) */
    public static <T> T method_15267(com.google.gson.Gson gson, java.io.Reader reader, Class<T> type, boolean lenient) {
        T value;
        try {
            value = method_47993(gson, reader, type, lenient);
        } catch (java.io.IOException e) {
            throw sneaky(e);
        }
        if (value == null) {
            throw new com.google.gson.JsonParseException("JSON data was null or empty");
        }
        return value;
    }

    /** fromJson(Gson, Reader, TypeToken, lenient) */
    public static <T> T method_15273(com.google.gson.Gson gson, java.io.Reader reader, com.google.gson.reflect.TypeToken<T> type,
            boolean lenient) {
        T value;
        try {
            value = method_47992(gson, reader, type, lenient);
        } catch (java.io.IOException e) {
            throw sneaky(e);
        }
        if (value == null) {
            throw new com.google.gson.JsonParseException("JSON data was null or empty");
        }
        return value;
    }

    /** fromJson(Gson, String, TypeToken, lenient) */
    public static <T> T method_15262(com.google.gson.Gson gson, String text, com.google.gson.reflect.TypeToken<T> type, boolean lenient) {
        return method_15273(gson, new java.io.StringReader(text), type, lenient);
    }

    /** fromJson(Gson, String, Class, lenient) */
    public static <T> T method_15279(com.google.gson.Gson gson, String text, Class<T> type, boolean lenient) {
        return method_15267(gson, new java.io.StringReader(text), type, lenient);
    }

    /** fromNullableJson(Gson, String, Class, lenient) */
    public static <T> T method_47994(com.google.gson.Gson gson, String text, Class<T> type, boolean lenient) {
        try {
            return method_47993(gson, new java.io.StringReader(text), type, lenient);
        } catch (java.io.IOException e) {
            throw sneaky(e);
        }
    }

    /** parse(String, lenient) */
    public static com.google.gson.JsonObject method_15298(String text, boolean lenient) {
        return method_15274(new java.io.StringReader(text), lenient);
    }

    /** parse(Reader, lenient) */
    public static com.google.gson.JsonObject method_15274(java.io.Reader reader, boolean lenient) {
        return method_15267(net.minecraft.class_3518.field_15657, reader, com.google.gson.JsonObject.class, lenient);
    }

    /** 1.21.5's methods declared IOException only where javac saw it; the JVM rethrows it unchanged */
    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException sneaky(Throwable e) throws E {
        throw (E) e;
    }
}
