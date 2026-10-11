package top.dext.centurybridge.res;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/** JSON in and out the way the game reads it: leniently, a leading BOM tolerated. */
public final class Json {

    private static final Gson OUT = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    /** the game parses data and assets leniently (comments, single quotes, trailing junk after the value) */
    public static JsonElement parse(byte[] bytes) {
        String s = new String(bytes, StandardCharsets.UTF_8);
        if (!s.isEmpty() && s.charAt(0) == '﻿') {
            s = s.substring(1);
        }
        JsonReader r = new JsonReader(new StringReader(s));
        r.setLenient(true);
        return JsonParser.parseReader(r);
    }

    /** null when the bytes are not JSON the game would read */
    public static JsonElement tryParse(byte[] bytes) {
        try {
            return parse(bytes);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static byte[] write(JsonElement e) {
        return (OUT.toJson(e) + "\n").getBytes(StandardCharsets.UTF_8);
    }

    private Json() {
    }
}
