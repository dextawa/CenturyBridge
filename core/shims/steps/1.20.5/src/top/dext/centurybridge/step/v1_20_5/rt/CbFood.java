package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 1.20.4's FoodProperties API on 1.20.5's food record. The record keeps nutrition, saturation (the
 * points, where 1.20.4 kept a modifier: points = nutrition * modifier * 2), whether it can always be
 * eaten, how long eating takes (1.20.4: fast food 16 ticks, else 32) and its effects; 1.20.4's meat
 * flag became the wolves' food tag, so a food built with 1.20.4's meat() is remembered here
 * ({@link #isMeat}), as are the vanilla foods 1.20.4 built that way.
 */
public final class CbFood {

    /** a mod subclass of FoodProperties.Builder declaring 1.20.4's meat() */
    public interface MeatOverride {
        net.minecraft.class_4174.class_4175 method_19236();
    }

    /** a mod subclass of FoodData declaring 1.20.4's eat(Item, ItemStack) */
    public interface EatOverride {
        void method_7579(net.minecraft.class_1792 item, net.minecraft.class_1799 stack);
    }

    /** builders a 1.20.4 meat() was called on (mixin/CbFoodBuilderMixin hands their food to MEAT when built) */
    private static final Set<net.minecraft.class_4174.class_4175> MEAT_BUILDERS =
        Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    /** foods built as meat, by identity (records compare by value; a meat and a non-meat food may be equal) */
    private static final Set<net.minecraft.class_4174> MEAT = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));

    static {
        // 1.20.4's Foods built with meat(): beef, chicken, cooked beef / chicken / mutton / porkchop / rabbit,
        // mutton, porkchop, rabbit, rotten flesh (read off 1.20.4's Foods); 1.20.5 keeps the same constants
        Collections.addAll(MEAT, net.minecraft.class_4176.field_18640, net.minecraft.class_4176.field_18645,
            net.minecraft.class_4176.field_18648, net.minecraft.class_4176.field_18649, net.minecraft.class_4176.field_18651,
            net.minecraft.class_4176.field_18652, net.minecraft.class_4176.field_18653, net.minecraft.class_4176.field_18662,
            net.minecraft.class_4176.field_18626, net.minecraft.class_4176.field_18630, net.minecraft.class_4176.field_18632);
    }

    /** builder.meat(): the class's own override, else 1.20.4's body (the food it builds is meat) */
    public static net.minecraft.class_4174.class_4175 meat(net.minecraft.class_4174.class_4175 b) {
        return b instanceof MeatOverride m ? m.method_19236() : superMeat(b);
    }

    /** FoodProperties.Builder.meat()'s 1.20.4 body (super.meat() too) */
    public static net.minecraft.class_4174.class_4175 superMeat(net.minecraft.class_4174.class_4175 b) {
        MEAT_BUILDERS.add(b);
        return b;
    }

    /** CbFoodBuilderMixin: a builder made a food */
    public static void built(net.minecraft.class_4174.class_4175 b, net.minecraft.class_4174 food) {
        if (MEAT_BUILDERS.contains(b)) {
            MEAT.add(food);
        }
    }

    /** food.getNutrition() */
    public static int nutrition(net.minecraft.class_4174 f) {
        return f.comp_2491();
    }

    /**
     * food.getSaturationModifier(): 1.20.4's modifier, from 1.20.5's saturation points (points =
     * nutrition * modifier * 2, FoodConstants.saturationByModifier); 0 for a food without nutrition.
     */
    public static float saturationModifier(net.minecraft.class_4174 f) {
        return f.comp_2491() == 0 ? 0.0F : f.comp_2492() / (f.comp_2491() * 2.0F);
    }

    /** food.isMeat(): built with 1.20.4's meat() (or one of the vanilla foods 1.20.4 built so) */
    public static boolean isMeat(net.minecraft.class_4174 f) {
        return MEAT.contains(f);
    }

    /** food.canAlwaysEat() */
    public static boolean canAlwaysEat(net.minecraft.class_4174 f) {
        return f.comp_2493();
    }

    /** food.isFastFood(): eaten faster than 1.20.4's normal 32 ticks (1.20.4's fast food took 16) */
    public static boolean isFastFood(net.minecraft.class_4174 f) {
        return f.comp_2494() < 1.6F;
    }

    /** food.getEffects(): each effect with its probability, as 1.20.4's pairs */
    public static List<Pair<net.minecraft.class_1293, Float>> effects(net.minecraft.class_4174 f) {
        List<Pair<net.minecraft.class_1293, Float>> out = new ArrayList<>();
        for (net.minecraft.class_4174.class_9423 e : f.comp_2495()) {
            out.add(Pair.of(e.comp_2496(), e.comp_2497()));
        }
        return out;
    }

    /** foodData.eat(item, stack): the class's own 1.20.4 override, else 1.20.4's body */
    public static void eat(net.minecraft.class_1702 data, net.minecraft.class_1792 item, net.minecraft.class_1799 stack) {
        if (data instanceof EatOverride e) {
            e.method_7579(item, stack);
        } else {
            superEat(data, item, stack);
        }
    }

    /**
     * FoodData.eat(item, stack)'s 1.20.4 body (super.eat too): an edible item's nutrition and saturation
     * modifier (the item's own 1.20.4 answers where its class overrides them)
     */
    public static void superEat(net.minecraft.class_1702 data, net.minecraft.class_1792 item, net.minecraft.class_1799 stack) {
        if (CbItemProps.isEdible(item)) {
            net.minecraft.class_4174 f = CbItemProps.getFoodProperties(item);
            data.method_7585(f.comp_2491(), saturationModifier(f));
        }
    }

    /** the override hook's argument: the item of the stack 1.20.5's eat(stack) is handed */
    public static net.minecraft.class_1792 itemOf(net.minecraft.class_1799 stack) {
        return stack.method_7909();
    }

    private CbFood() {
    }
}
