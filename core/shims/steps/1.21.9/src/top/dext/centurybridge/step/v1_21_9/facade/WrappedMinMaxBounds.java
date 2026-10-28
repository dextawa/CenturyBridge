package top.dext.centurybridge.step.v1_21_9.facade;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.class_2096;
import net.minecraft.class_2561;
import net.minecraft.class_3518;

/**
 * 1.21.8's WrappedMinMaxBounds (class_2152, gone in 1.21.9: the entity selector's x_rotation / y_rotation bounds are
 * MinMaxBounds.FloatDegrees there), its bodies as 1.21.8 had them (javap -c class_2152): a nullable min and max,
 * matches / matchesSqr, the JSON form (1.21.8 writes "max" with the min's value -- kept), and the reader the
 * selector options parsed with. A final class, not a record: a facade's members are public under their intermediary
 * names (a mod's access widener may have opened them), which a record's fields cannot be; equals / hashCode / toString
 * are the record's (toString as 1.21.8's read on an intermediary runtime). The selector's getters / setters convert to
 * and from FloatDegrees (rt/CbSelectorBounds).
 */
public final class WrappedMinMaxBounds {
    public final Float comp_1840;
    public final Float comp_1841;

    /** WrappedMinMaxBounds.ANY */
    public static final WrappedMinMaxBounds field_9780 = new WrappedMinMaxBounds(null, null);
    /** WrappedMinMaxBounds.ERROR_INTS_ONLY */
    public static final SimpleCommandExceptionType field_9781 =
            new SimpleCommandExceptionType(class_2561.method_43471("argument.range.ints"));

    public WrappedMinMaxBounds(Float min, Float max) {
        this.comp_1840 = min;
        this.comp_1841 = max;
    }

    /** min() */
    public Float comp_1840() {
        return this.comp_1840;
    }

    /** max() */
    public Float comp_1841() {
        return this.comp_1841;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WrappedMinMaxBounds other && Objects.equals(this.comp_1840, other.comp_1840)
                && Objects.equals(this.comp_1841, other.comp_1841);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_1840) + Objects.hashCode(this.comp_1841);
    }

    @Override
    public String toString() {
        return "class_2152[comp_1840=" + this.comp_1840 + ", comp_1841=" + this.comp_1841 + "]";
    }

    /** exactly(value) */
    public static WrappedMinMaxBounds method_35401(float value) {
        return new WrappedMinMaxBounds(value, value);
    }

    /** between(min, max) */
    public static WrappedMinMaxBounds method_35402(float min, float max) {
        return new WrappedMinMaxBounds(min, max);
    }

    /** atLeast(min) */
    public static WrappedMinMaxBounds method_35406(float min) {
        return new WrappedMinMaxBounds(min, null);
    }

    /** atMost(max) */
    public static WrappedMinMaxBounds method_35408(float max) {
        return new WrappedMinMaxBounds(null, max);
    }

    /** matches(value) */
    public boolean method_35409(float value) {
        if (this.comp_1840 != null && this.comp_1841 != null && this.comp_1840 > this.comp_1841
                && this.comp_1840 > value && this.comp_1841 < value) {
            return false;
        }
        if (this.comp_1840 != null && this.comp_1840 > value) {
            return false;
        }
        return this.comp_1841 == null || !(this.comp_1841 < value);
    }

    /** matchesSqr(value) */
    public boolean method_35400(double value) {
        if (this.comp_1840 != null && this.comp_1841 != null && this.comp_1840 > this.comp_1841
                && (double) (this.comp_1840 * this.comp_1840) > value && (double) (this.comp_1841 * this.comp_1841) < value) {
            return false;
        }
        if (this.comp_1840 != null && (double) (this.comp_1840 * this.comp_1840) > value) {
            return false;
        }
        return this.comp_1841 == null || !((double) (this.comp_1841 * this.comp_1841) < value);
    }

    /** serializeToJson() */
    public JsonElement method_35407() {
        if (this == field_9780) {
            return JsonNull.INSTANCE;
        }
        if (this.comp_1840 != null && this.comp_1841 != null && this.comp_1840.equals(this.comp_1841)) {
            return new JsonPrimitive(this.comp_1840);
        }
        JsonObject object = new JsonObject();
        if (this.comp_1840 != null) {
            object.addProperty("min", this.comp_1840);
        }
        if (this.comp_1841 != null) {
            object.addProperty("max", this.comp_1840);
        }
        return object;
    }

    /** fromJson(json) */
    public static WrappedMinMaxBounds method_35403(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return field_9780;
        }
        if (class_3518.method_15275(json)) {
            float value = class_3518.method_15269(json, "value");
            return new WrappedMinMaxBounds(value, value);
        }
        JsonObject object = class_3518.method_15295(json, "value");
        Float min = object.has("min") ? class_3518.method_15259(object, "min") : null;
        Float max = object.has("max") ? class_3518.method_15259(object, "max") : null;
        return new WrappedMinMaxBounds(min, max);
    }

    /** fromReader(reader, allowFloats) */
    public static WrappedMinMaxBounds method_35404(StringReader reader, boolean allowFloats) throws CommandSyntaxException {
        return method_9172(reader, allowFloats, value -> value);
    }

    /** fromReader(reader, allowFloats, transform) */
    public static WrappedMinMaxBounds method_9172(StringReader reader, boolean allowFloats, Function<Float, Float> transform)
            throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw class_2096.field_9700.createWithContext(reader);
        }
        int start = reader.getCursor();
        Float min = method_9174(method_9176(reader, allowFloats), transform);
        Float max;
        if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
            reader.skip();
            reader.skip();
            max = method_9174(method_9176(reader, allowFloats), transform);
            if (min == null && max == null) {
                reader.setCursor(start);
                throw class_2096.field_9700.createWithContext(reader);
            }
        } else {
            if (!allowFloats && reader.canRead() && reader.peek() == '.') {
                reader.setCursor(start);
                throw field_9781.createWithContext(reader);
            }
            max = min;
        }
        if (min == null && max == null) {
            reader.setCursor(start);
            throw class_2096.field_9700.createWithContext(reader);
        }
        return new WrappedMinMaxBounds(min, max);
    }

    /** readNumber(reader, allowFloats) */
    public static Float method_9176(StringReader reader, boolean allowFloats) throws CommandSyntaxException {
        int start = reader.getCursor();
        while (reader.canRead() && method_9173(reader, allowFloats)) {
            reader.skip();
        }
        String number = reader.getString().substring(start, reader.getCursor());
        if (number.isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(number);
        } catch (NumberFormatException e) {
            if (allowFloats) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(reader, number);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, number);
        }
    }

    /** isAllowedNumber(reader, allowFloats) */
    public static boolean method_9173(StringReader reader, boolean allowFloats) {
        char c = reader.peek();
        if ((c >= '0' && c <= '9') || c == '-') {
            return true;
        }
        if (allowFloats && c == '.') {
            return !reader.canRead(2) || reader.peek(1) != '.';
        }
        return false;
    }

    /** optionallyFormat(value, transform) */
    public static Float method_9174(Float value, Function<Float, Float> transform) {
        return value == null ? null : transform.apply(value);
    }
}
