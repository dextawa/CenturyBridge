package top.dext.centurybridge.step.v1_20_3.facade;

import java.util.Optional;

/**
 * Facade for 1.20.2 CommandFunction.CacheableFunction (class_2158$class_2159), gone in 1.20.3
 * (net.minecraft.commands.CacheableFunction, class_2159, took its place, and CommandFunction
 * became an interface). The 1.20.2 class whole: a function reference by id resolved lazily, once,
 * against the server's functions -- or one built around a function object, already resolved --
 * and NONE, the reference to nothing. Every member is public (no access widener reaches a facade).
 * Where 1.20.3's API wants its own CacheableFunction (AdvancementRewards), CbAdvJson.function
 * converts by id.
 */
public class class_2158$class_2159 {

    /** NONE */
    public static final class_2158$class_2159 field_9809 = new class_2158$class_2159((net.minecraft.class_2960) null);

    /** id */
    public final net.minecraft.class_2960 field_9807;
    /** resolved */
    public boolean field_9810;
    /** function */
    public Optional<net.minecraft.class_2158> field_9808 = Optional.empty();

    public class_2158$class_2159(net.minecraft.class_2960 id) {
        this.field_9807 = id;
    }

    public class_2158$class_2159(net.minecraft.class_2158 function) {
        this.field_9810 = true;
        this.field_9807 = null;
        this.field_9808 = Optional.of(function);
    }

    /** get(functions): resolved by id the first time, the same answer after */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Optional<net.minecraft.class_2158> method_9196(net.minecraft.class_2991 functions) {
        if (!field_9810) {
            if (field_9807 != null) {
                field_9808 = (Optional) functions.method_12905(field_9807);
            }
            field_9810 = true;
        }
        return field_9808;
    }

    /** getId(): the resolved function's id, else the id it was made with */
    public net.minecraft.class_2960 method_9197() {
        return field_9808.map(f -> f.comp_1994()).orElse(field_9807);
    }
}
