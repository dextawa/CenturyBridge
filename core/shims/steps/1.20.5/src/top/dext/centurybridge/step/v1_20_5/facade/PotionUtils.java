package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import top.dext.centurybridge.step.v1_20_5.rt.CbEffects;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemTags;
import top.dext.centurybridge.step.v1_20_5.rt.CbPotions;

/**
 * 1.20.4's PotionUtils. Intermediary kept its name (class_1844) for 1.20.5's PotionContents record, a
 * different class; every 1.20.4 reference to it means this one (classRenames). 1.20.4 kept a stack's
 * potion in its NBT -- "Potion" (the potion's id; none for the empty potion), "CustomPotionColor",
 * "custom_potion_effects" -- and so does this class, on the stack's 1.20.4 NBT view (rt/CbItemTags,
 * which 1.20.5's potion_contents component reads as and writes from), with 1.20.4's bodies: the same
 * colours (1.20.4's own mixing, no alpha), the same empty potion (rt/CbPotions.EMPTY), the same
 * effects. The tooltip lines are 1.20.5's (PotionContents.addPotionTooltip, the same text).
 */
public class PotionUtils {

    /** TAG_CUSTOM_POTION_EFFECTS */
    public static final String field_30943 = "custom_potion_effects";

    /** TAG_CUSTOM_POTION_COLOR */
    public static final String field_30944 = "CustomPotionColor";

    /** TAG_POTION */
    public static final String field_30945 = "Potion";

    /** EMPTY_COLOR: the empty potion's colour */
    public static final int field_30946 = 16253176;

    /** NO_EFFECT: the tooltip line of a potion with no effects */
    public static final net.minecraft.class_2561 field_25817 =
        net.minecraft.class_2561.method_43471("effect.none").method_27692(net.minecraft.class_124.field_1080);

    public PotionUtils() {
    }

    /** getMobEffects(stack): the potion's effects and the custom ones */
    public static List<net.minecraft.class_1293> method_8067(net.minecraft.class_1799 stack) {
        return method_8066(CbItemTags.getTag(stack));
    }

    /** getAllEffects(potion, customEffects) */
    public static List<net.minecraft.class_1293> method_8059(net.minecraft.class_1842 potion, Collection<net.minecraft.class_1293> custom) {
        List<net.minecraft.class_1293> list = new ArrayList<>();
        list.addAll(potion.method_8049());
        list.addAll(custom);
        return list;
    }

    /** getAllEffects(tag) */
    public static List<net.minecraft.class_1293> method_8066(net.minecraft.class_2487 tag) {
        List<net.minecraft.class_1293> list = new ArrayList<>();
        list.addAll(method_8057(tag).method_8049());
        method_8058(tag, list);
        return list;
    }

    /** getCustomEffects(stack) */
    public static List<net.minecraft.class_1293> method_8068(net.minecraft.class_1799 stack) {
        return method_8060(CbItemTags.getTag(stack));
    }

    /** getCustomEffects(tag) */
    public static List<net.minecraft.class_1293> method_8060(net.minecraft.class_2487 tag) {
        List<net.minecraft.class_1293> list = new ArrayList<>();
        method_8058(tag, list);
        return list;
    }

    /** getCustomEffects(tag, list): each custom effect the tag holds, loaded */
    public static void method_8058(net.minecraft.class_2487 tag, List<net.minecraft.class_1293> list) {
        if (tag != null && tag.method_10573(field_30943, 9)) {
            net.minecraft.class_2499 effects = tag.method_10554(field_30943, 10);
            for (int i = 0; i < effects.size(); i++) {
                net.minecraft.class_1293 effect = net.minecraft.class_1293.method_5583(effects.method_10602(i));
                if (effect != null) {
                    list.add(effect);
                }
            }
        }
    }

    /** getColor(stack): the custom colour, else the empty potion's, else the effects' */
    public static int method_8064(net.minecraft.class_1799 stack) {
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        if (tag != null && tag.method_10573(field_30944, 99)) {
            return tag.method_10550(field_30944);
        }
        return method_8063(stack) == CbPotions.EMPTY ? field_30946 : method_8055(method_8067(stack));
    }

    /** getColor(potion) */
    public static int method_8062(net.minecraft.class_1842 potion) {
        return potion == CbPotions.EMPTY ? field_30946 : method_8055(potion.method_8049());
    }

    /** getColor(effects): 1.20.4's mix of the visible effects' colours, weighted by amplifier, as RGB */
    public static int method_8055(Collection<net.minecraft.class_1293> effects) {
        int base = 3694022;
        if (effects.isEmpty()) {
            return base;
        }
        float r = 0.0F;
        float g = 0.0F;
        float b = 0.0F;
        int weight = 0;
        for (net.minecraft.class_1293 effect : effects) {
            if (!effect.method_5581()) {
                continue;
            }
            int color = effect.method_5579().comp_349().method_5556();
            int l = effect.method_5578() + 1;
            r += (float) (l * (color >> 16 & 0xFF)) / 255.0F;
            g += (float) (l * (color >> 8 & 0xFF)) / 255.0F;
            b += (float) (l * (color >> 0 & 0xFF)) / 255.0F;
            weight += l;
        }
        if (weight == 0) {
            return 0;
        }
        r = r / (float) weight * 255.0F;
        g = g / (float) weight * 255.0F;
        b = b / (float) weight * 255.0F;
        return (int) r << 16 | (int) g << 8 | (int) b;
    }

    /** getPotion(stack) */
    public static net.minecraft.class_1842 method_8063(net.minecraft.class_1799 stack) {
        return method_8057(CbItemTags.getTag(stack));
    }

    /** getPotion(tag): the potion the tag names, the empty potion for none or an unknown one */
    public static net.minecraft.class_1842 method_8057(net.minecraft.class_2487 tag) {
        return tag == null ? CbPotions.EMPTY : CbPotions.byName(tag.method_10558(field_30945));
    }

    /** setPotion(stack, potion): the potion's id in the tag (removed for the empty potion) */
    public static net.minecraft.class_1799 method_8061(net.minecraft.class_1799 stack, net.minecraft.class_1842 potion) {
        net.minecraft.class_2960 id = CbPotions.idOf(potion);
        if (potion == CbPotions.EMPTY) {
            CbItemTags.removeTagKey(stack, field_30945);
        } else {
            CbItemTags.getOrCreateTag(stack).method_10582(field_30945, id.toString());
        }
        return stack;
    }

    /**
     * setCustomEffects(stack, effects): the effects in 1.20.4's NBT form. 1.20.4 asked for the existing list
     * as a list of lists, so it always began a new one: the new effects replace the old.
     */
    public static net.minecraft.class_1799 method_8056(net.minecraft.class_1799 stack, Collection<net.minecraft.class_1293> effects) {
        if (effects.isEmpty()) {
            return stack;
        }
        net.minecraft.class_2487 tag = CbItemTags.getOrCreateTag(stack);
        net.minecraft.class_2499 list = tag.method_10554(field_30943, 9);
        for (net.minecraft.class_1293 effect : effects) {
            list.add(CbEffects.save(effect, new net.minecraft.class_2487()));
        }
        tag.method_10566(field_30943, list);
        return stack;
    }

    /** addPotionTooltip(stack, lines, durationFactor, ticksPerSecond) */
    public static void method_47372(net.minecraft.class_1799 stack, List<net.minecraft.class_2561> lines, float durationFactor,
                                    float ticksPerSecond) {
        method_8065(method_8067(stack), lines, durationFactor, ticksPerSecond);
    }

    /** addPotionTooltip(effects, lines, durationFactor, ticksPerSecond): 1.20.5's lines for the effects */
    public static void method_8065(List<net.minecraft.class_1293> effects, List<net.minecraft.class_2561> lines, float durationFactor,
                                   float ticksPerSecond) {
        net.minecraft.class_1844.method_8065(effects, lines::add, durationFactor, ticksPerSecond);
    }

    /** addPotionTooltip(stack, lines, durationFactor): before 1.20.3 durations were told at 20 ticks a second */
    public static void method_47372(net.minecraft.class_1799 stack, List<net.minecraft.class_2561> lines, float durationFactor) {
        method_47372(stack, lines, durationFactor, 20.0F);
    }

    /** addPotionTooltip(effects, lines, durationFactor): the same, before 1.20.3 */
    public static void method_8065(List<net.minecraft.class_1293> effects, List<net.minecraft.class_2561> lines, float durationFactor) {
        method_8065(effects, lines, durationFactor, 20.0F);
    }
}
