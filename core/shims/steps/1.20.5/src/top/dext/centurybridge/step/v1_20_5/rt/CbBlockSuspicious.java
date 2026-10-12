package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's suspicious stew effects on 1.20.5. 1.20.4 described them as a List of
 * SuspiciousEffectHolder.EffectEntry(MobEffect effect, int duration); 1.20.5 moved that record --
 * intermediary keeps its name, class_8751 -- into SuspiciousStewEffects as Entry(Holder&lt;MobEffect&gt;,
 * int duration) and wraps the list in SuspiciousStewEffects(List&lt;Entry&gt;). A converted mod's
 * EffectEntry is renamed to the 1.20.5 Entry (classRenames), so its lists hold 1.20.5's own entries;
 * between the list and SuspiciousStewEffects nothing converts but the wrapper. What changed on the
 * entry itself -- effect() answers the effect's holder, the constructor takes one, LIST_CODEC is gone
 * -- is bridged here.
 *
 * <p>FlowerBlock / WitherRoseBlock(MobEffect, int seconds, ...) built 1.20.4's list with
 * makeEffectList: an instantaneous effect lasted that many ticks, any other that many seconds
 * (seconds * 20 ticks). 1.20.5's (Holder, float seconds) always multiplies by 20, so the old
 * constructor keeps 1.20.4's durations here.
 */
public final class CbBlockSuspicious {

    /** the 1.20.4 SuspiciousEffectHolder a mod's lambda implements (lambda adapter) */
    @FunctionalInterface
    public interface LegacyHolder {
        java.util.List<?> method_53233();
    }

    /** a 1.20.4 effect list (of 1.20.5 entries, after the rename) as SuspiciousStewEffects */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9298 stew(java.util.List effects) {
        return effects == null ? null : new net.minecraft.class_9298(effects);
    }

    /** SuspiciousStewEffects as 1.20.4's effect list */
    @SuppressWarnings("rawtypes")
    public static java.util.List entries(net.minecraft.class_9298 stew) {
        return stew == null ? null : stew.comp_2416();
    }

    /** 1.20.4's FlowerBlock.makeEffectList(effect, seconds) */
    public static java.util.List<net.minecraft.class_9298.class_8751> method_54015(net.minecraft.class_1291 effect, int seconds) {
        int duration = effect.method_5561() ? seconds : seconds * 20;
        return java.util.List.of(new net.minecraft.class_9298.class_8751(CbHolders.mobEffect(effect), duration));
    }

    /** the old constructors' effect and seconds as 1.20.5's SuspiciousStewEffects, durations as 1.20.4 made them */
    public static net.minecraft.class_9298 effects(net.minecraft.class_1291 effect, int seconds) {
        return new net.minecraft.class_9298(method_54015(effect, seconds));
    }

    /** a mod's 1.20.4 holder lambda as 1.20.5's SuspiciousEffectHolder */
    public static net.minecraft.class_7917 holder(LegacyHolder legacy) {
        return () -> stew(legacy.method_53233());
    }

    private static com.mojang.serialization.Codec<java.util.List<net.minecraft.class_9298.class_8751>> listCodec;
    private static com.mojang.serialization.MapCodec<java.util.List<net.minecraft.class_9298.class_8751>> effectsField;

    /** EffectEntry.LIST_CODEC: the entry codec as a list, made once like 1.20.4's static final */
    public static synchronized com.mojang.serialization.Codec<java.util.List<net.minecraft.class_9298.class_8751>> field_45783() {
        if (listCodec == null) {
            listCodec = net.minecraft.class_9298.class_8751.field_45782.listOf();
        }
        return listCodec;
    }

    /**
     * FlowerBlock.EFFECTS_FIELD: 1.20.4's LIST_CODEC.fieldOf("suspicious_stew_effects"). 1.20.5's field
     * reads and writes the same JSON (SuspiciousStewEffects.CODEC is the entry list), wrapped.
     */
    public static synchronized com.mojang.serialization.MapCodec<java.util.List<net.minecraft.class_9298.class_8751>> field_46354() {
        if (effectsField == null) {
            effectsField = field_45783().fieldOf("suspicious_stew_effects");
        }
        return effectsField;
    }

    private CbBlockSuspicious() {
    }
}
