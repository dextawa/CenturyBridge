package top.dext.centurybridge.step.v1_21_6.facade;

/**
 * 1.21.5 LowerCaseEnumTypeAdapterFactory (net/minecraft/class_3530), which 1.21.6 removed: a Gson factory writing
 * an enum constant as its lower-cased name (toString() for a non-enum value) and reading it back from that map;
 * 1.21.5's body, line for line. The intermediary name of its helper is kept.
 */
public class LowerCaseEnumTypeAdapterFactory implements com.google.gson.TypeAdapterFactory {

    public LowerCaseEnumTypeAdapterFactory() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> com.google.gson.TypeAdapter<T> create(com.google.gson.Gson gson, com.google.gson.reflect.TypeToken<T> type) {
        Class<T> raw = (Class<T>) type.getRawType();
        if (!raw.isEnum()) {
            return null;
        }
        java.util.Map<String, T> byName = com.google.common.collect.Maps.newHashMap();
        for (T constant : raw.getEnumConstants()) {
            byName.put(method_15334(constant), constant);
        }
        return new com.google.gson.TypeAdapter<T>() {
            @Override
            public void write(com.google.gson.stream.JsonWriter out, T value) throws java.io.IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(method_15334(value));
                }
            }

            @Override
            public T read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                return byName.get(in.nextString());
            }
        };
    }

    public String method_15334(Object value) {
        return value instanceof Enum<?> e ? e.name().toLowerCase(java.util.Locale.ROOT)
            : value.toString().toLowerCase(java.util.Locale.ROOT);
    }
}
