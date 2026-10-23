package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Map;
import net.minecraft.class_10352;
import net.minecraft.class_169;
import net.minecraft.class_176;
import net.minecraft.class_8567;

/**
 * 1.21.1's LootParams parameter API on 1.21.2. 1.21.1's LootParams held its parameters in a map of LootContextParam
 * (net/minecraft/class_169, which 1.21.2 reuses for ContextKey) to values: hasParam, getParameter (absent: a
 * NoSuchElementException naming the parameter), getOptionalParameter and getParamOrNull (absent: null). 1.21.2's
 * holds a ContextMap with the same lookups (has, getOrThrow with the same exception, getOptional); 1.21.1's
 * constructor map becomes that ContextMap, each of its parameters an optional key of the map's key set (1.21.1's
 * constructor checked none). A 1.21.1 subclass's overrides are reached by the calls (1.21.2 itself reads the map).
 */
public final class CbLevelLootParams {

    public interface LegacyHasParam {
        boolean method_51865(class_169<?> param);
    }

    public interface LegacyGetParameter {
        <T> T method_51867(class_169<T> param);
    }

    public interface LegacyGetOptionalParameter {
        <T> T method_51868(class_169<T> param);
    }

    public interface LegacyGetParamOrNull {
        <T> T method_51869(class_169<T> param);
    }

    public static boolean method_51865(class_8567 self, class_169<?> param) {
        return self instanceof LegacyHasParam l ? l.method_51865(param) : hasParam(self, param);
    }

    public static <T> T method_51867(class_8567 self, class_169<T> param) {
        return self instanceof LegacyGetParameter l ? l.method_51867(param) : getParameter(self, param);
    }

    public static <T> T method_51868(class_8567 self, class_169<T> param) {
        return self instanceof LegacyGetOptionalParameter l ? l.method_51868(param) : getOptionalParameter(self, param);
    }

    public static <T> T method_51869(class_8567 self, class_169<T> param) {
        return self instanceof LegacyGetParamOrNull l ? l.method_51869(param) : getParamOrNull(self, param);
    }

    public static boolean hasParam(class_8567 self, class_169<?> param) {
        return self.method_65014().method_64965(param);
    }

    public static <T> T getParameter(class_8567 self, class_169<T> param) {
        return self.method_65014().method_64967(param);
    }

    public static <T> T getOptionalParameter(class_8567 self, class_169<T> param) {
        return self.method_65014().method_64968(param);
    }

    public static <T> T getParamOrNull(class_8567 self, class_169<T> param) {
        return self.method_65014().method_64968(param);
    }

    /** a 1.21.1 constructor's parameter map, as 1.21.2's ContextMap */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_10352 contextMap(Map<class_169<?>, Object> params) {
        class_10352.class_10353 builder = new class_10352.class_10353();
        class_176.class_177 keys = new class_176.class_177();
        for (Map.Entry<class_169<?>, Object> e : params.entrySet()) {
            builder.method_64970((class_169) e.getKey(), e.getValue());
            keys.method_780(e.getKey());
        }
        return builder.method_64971(keys.method_782());
    }

    private CbLevelLootParams() {
    }
}
