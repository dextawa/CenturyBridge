package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.ArrayList;
import java.util.List;

/**
 * SuspiciousStewItem's 1.20.4 statics: the stew's effects kept in its NBT as "effects" (the entry list
 * codec), on the stack's 1.20.4 NBT view (CbItemTags), which 1.20.5's suspicious_stew_effects component
 * reads as and writes from. The entries are 1.20.5's own (SuspiciousEffectHolder.EffectEntry is renamed
 * to SuspiciousStewEffects.Entry, CbBlockSuspicious), encoded with their list codec as 1.20.4 did.
 */
public final class CbStew {

    private static final String EFFECTS = "effects";

    /** saveMobEffects(stack, effects): the effects as the stew's, replacing any it had */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void saveMobEffects(net.minecraft.class_1799 stack, List effects) {
        net.minecraft.class_2487 tag = CbItemTags.getOrCreateTag(stack);
        ((com.mojang.serialization.Codec) CbBlockSuspicious.field_45783()).encodeStart(net.minecraft.class_2509.field_11560, effects)
            .result().ifPresent(t -> tag.method_10566(EFFECTS, (net.minecraft.class_2520) t));
    }

    /** appendMobEffects(stack, effects): the stew's effects, then these */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void appendMobEffects(net.minecraft.class_1799 stack, List effects) {
        net.minecraft.class_2487 tag = CbItemTags.getOrCreateTag(stack);
        List all = new ArrayList();
        listPotionEffects(stack, all);
        all.addAll(effects);
        ((com.mojang.serialization.Codec) CbBlockSuspicious.field_45783()).encodeStart(net.minecraft.class_2509.field_11560, all)
            .result().ifPresent(t -> tag.method_10566(EFFECTS, (net.minecraft.class_2520) t));
    }

    /** the stew's effects as its NBT holds them (1.20.4's private listPotionEffects) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void listPotionEffects(net.minecraft.class_1799 stack, List out) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        if (tag != null && tag.method_10573(EFFECTS, 9)) {
            ((com.mojang.serialization.Codec) CbBlockSuspicious.field_45783()).parse(net.minecraft.class_2509.field_11560, tag.method_10554(EFFECTS, 10))
                .result().ifPresent(l -> out.addAll((List) l));
        }
    }

    private CbStew() {
    }
}
