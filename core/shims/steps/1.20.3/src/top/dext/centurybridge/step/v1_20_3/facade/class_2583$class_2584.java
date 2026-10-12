package top.dext.centurybridge.step.v1_20_3.facade;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import top.dext.centurybridge.step.v1_20_3.mixin.CbStyleAccess;
import top.dext.centurybridge.step.v1_20_3.rt.CbText;

/**
 * Facade for 1.20.2's net/minecraft/class_2583$class_2584 (Style.Serializer), the GSON adapter for a
 * style. 1.20.3 keeps the class name for the style codec holder; the adapter is gone, so a 1.20.2 mod's
 * references land here. 1.20.2's bodies are ported as they were: they only read and build what 1.20.3
 * still has (the style's fields, through an accessor; text colors, click and hover events through the
 * 1.20.2 bridges of their own parsers).
 */
public class class_2583$class_2584 implements JsonDeserializer<net.minecraft.class_2583>, JsonSerializer<net.minecraft.class_2583> {

    public class_2583$class_2584() {
    }

    /** deserialize(JsonElement, Type, JsonDeserializationContext): a style from a JSON object, null otherwise */
    public net.minecraft.class_2583 method_10991(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        if (json == null) {
            return null;
        }
        Boolean bold = method_27711(json, "bold");
        Boolean italic = method_27711(json, "italic");
        Boolean underlined = method_27711(json, "underlined");
        Boolean strikethrough = method_27711(json, "strikethrough");
        Boolean obfuscated = method_27711(json, "obfuscated");
        net.minecraft.class_5251 color = method_27715(json);
        String insertion = method_27714(json);
        net.minecraft.class_2558 click = method_27713(json);
        net.minecraft.class_2568 hover = method_27712(json);
        net.minecraft.class_2960 font = method_27710(json);
        return CbStyleAccess.cb$new(color, bold, italic, underlined, strikethrough, obfuscated, click, hover, insertion, font);
    }

    @Override
    public net.minecraft.class_2583 deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return method_10991(element, type, context);
    }

    public static net.minecraft.class_2960 method_27710(JsonObject json) {
        if (json.has("font")) {
            String font = net.minecraft.class_3518.method_15265(json, "font");
            try {
                return new net.minecraft.class_2960(font);
            } catch (net.minecraft.class_151 e) {
                throw new JsonSyntaxException("Invalid font name: " + font);
            }
        }
        return null;
    }

    public static net.minecraft.class_2568 method_27712(JsonObject json) {
        if (json.has("hoverEvent")) {
            net.minecraft.class_2568 hover = CbText.method_27664(net.minecraft.class_3518.method_15296(json, "hoverEvent"));
            if (hover != null && hover.method_10892().method_27667()) {
                return hover;
            }
        }
        return null;
    }

    public static net.minecraft.class_2558 method_27713(JsonObject json) {
        if (json.has("clickEvent")) {
            JsonObject click = net.minecraft.class_3518.method_15296(json, "clickEvent");
            String name = net.minecraft.class_3518.method_15253(click, "action", null);
            net.minecraft.class_2558.class_2559 action = name == null ? null : CbText.method_10848(name);
            String value = net.minecraft.class_3518.method_15253(click, "value", null);
            if (action != null && value != null && action.method_10847()) {
                return new net.minecraft.class_2558(action, value);
            }
        }
        return null;
    }

    public static String method_27714(JsonObject json) {
        return net.minecraft.class_3518.method_15253(json, "insertion", null);
    }

    public static net.minecraft.class_5251 method_27715(JsonObject json) {
        if (json.has("color")) {
            return CbText.method_27719(net.minecraft.class_3518.method_15265(json, "color"));
        }
        return null;
    }

    public static Boolean method_27711(JsonObject json, String name) {
        return json.has(name) ? json.get(name).getAsBoolean() : null;
    }

    /** serialize(Style, Type, JsonSerializationContext): null for the empty style, else its JSON object */
    public JsonElement method_10990(net.minecraft.class_2583 style, Type type, JsonSerializationContext context) {
        if (style.method_10967()) {
            return null;
        }
        CbStyleAccess s = (CbStyleAccess) (Object) style;
        JsonObject json = new JsonObject();
        if (s.cb$bold() != null) {
            json.addProperty("bold", s.cb$bold());
        }
        if (s.cb$italic() != null) {
            json.addProperty("italic", s.cb$italic());
        }
        if (s.cb$underlined() != null) {
            json.addProperty("underlined", s.cb$underlined());
        }
        if (s.cb$strikethrough() != null) {
            json.addProperty("strikethrough", s.cb$strikethrough());
        }
        if (s.cb$obfuscated() != null) {
            json.addProperty("obfuscated", s.cb$obfuscated());
        }
        if (s.cb$color() != null) {
            json.addProperty("color", s.cb$color().method_27721());
        }
        if (s.cb$insertion() != null) {
            json.add("insertion", context.serialize(s.cb$insertion()));
        }
        if (s.cb$clickEvent() != null) {
            JsonObject click = new JsonObject();
            click.addProperty("action", CbText.method_10846(s.cb$clickEvent().method_10845()));
            click.addProperty("value", s.cb$clickEvent().method_10844());
            json.add("clickEvent", click);
        }
        if (s.cb$hoverEvent() != null) {
            json.add("hoverEvent", ((CbText.CbHoverEvent) (Object) s.cb$hoverEvent()).method_27665());
        }
        if (s.cb$font() != null) {
            json.addProperty("font", s.cb$font().toString());
        }
        return json;
    }

    @Override
    public JsonElement serialize(net.minecraft.class_2583 style, Type type, JsonSerializationContext context) {
        return method_10990(style, type, context);
    }
}
