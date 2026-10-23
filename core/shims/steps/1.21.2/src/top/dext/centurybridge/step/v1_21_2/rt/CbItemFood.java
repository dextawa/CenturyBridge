package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_10124;
import net.minecraft.class_10128;
import net.minecraft.class_10131;
import net.minecraft.class_10132;
import net.minecraft.class_4174;
import net.minecraft.class_4176;
import net.minecraft.class_9334;
import top.dext.centurybridge.step.v1_21_2.facade.FoodPossibleEffect;

/**
 * 1.21.1's FoodProperties on 1.21.2.
 *
 * <p>1.21.1's FoodProperties also carried how long eating takes, what the item turns into once eaten, and the
 * effects eating may give. 1.21.2's record keeps nutrition, saturation and can-always-eat; the rest moved to the
 * item's CONSUMABLE (consume seconds, ApplyStatusEffectsConsumeEffect per possible effect) and USE_REMAINDER
 * components. Here a 1.21.2 FoodProperties built by 1.21.1 code (its constructor, its Builder's fast / effect /
 * usingConvertsTo) carries those three as an {@link Extra} kept by identity (a record compares by value, and two
 * foods with the same numbers may differ in effects); 1.21.1's vanilla foods carry 1.21.1's, transcribed from its
 * Foods (a 1.21.1 mod's .food(Foods.GOLDEN_APPLE) still gives the golden apple's effects; 1.21.2's own items
 * pass their consumable with the food, which this does not touch). Item.Properties.food(food) (CbItemFoodPropertiesMixin) turns an Extra into the CONSUMABLE and
 * USE_REMAINDER 1.21.2 eats by: what 1.21.1's eating did with them. Any other FoodProperties (decoded, or built by
 * 1.21.2 code) answers 1.21.1's defaults: 1.6 seconds, nothing, no effects.
 */
public final class CbItemFood {

    /** 1.21.1's FoodProperties.DEFAULT_EAT_SECONDS */
    public static final float DEFAULT_EAT_SECONDS = 1.6F;

    /** what 1.21.1's FoodProperties held that 1.21.2's does not */
    public record Extra(float eatSeconds, Optional<class_1799> usingConvertsTo, List<FoodPossibleEffect> effects) {
        static final Extra DEFAULT = new Extra(DEFAULT_EAT_SECONDS, Optional.empty(), List.of());
    }

    private static final Map<class_4174, Extra> EXTRA = new IdentityHashMap<>();

    /** 1.21.1's stews (beetroot, mushroom, rabbit, suspicious): eaten into a bowl -- the stack made when asked */
    private static final Map<class_4174, Boolean> STEWS = new IdentityHashMap<>();

    private static boolean vanillaLoaded;

    /**
     * 1.21.1's Foods whose eat time, remainder or effects were not the defaults, transcribed from its bytecode.
     * Loaded on first use, not on class init: this class is first reached while Foods itself initializes (its
     * builders' build()), when Foods' fields are not set yet.
     */
    private static void loadVanilla() {
        if (vanillaLoaded) {
            return;
        }
        vanillaLoaded = true;
        put(class_4176.field_18645, 1.6F, effect(new class_1293(class_1294.field_5903, 600, 0), 0.3F)); // chicken
        put(class_4176.field_18656, 0.8F); // dried kelp: fast
        put(class_4176.field_18657, 1.6F, // enchanted golden apple
            effect(new class_1293(class_1294.field_5924, 400, 1), 1.0F),
            effect(new class_1293(class_1294.field_5907, 6000, 0), 1.0F),
            effect(new class_1293(class_1294.field_5918, 6000, 0), 1.0F),
            effect(new class_1293(class_1294.field_5898, 2400, 3), 1.0F));
        put(class_4176.field_18658, 1.6F, // golden apple
            effect(new class_1293(class_1294.field_5924, 100, 1), 1.0F),
            effect(new class_1293(class_1294.field_5898, 2400, 0), 1.0F));
        put(class_4176.field_18663, 1.6F, effect(new class_1293(class_1294.field_5899, 100, 0), 0.6F)); // poisonous potato
        put(class_4176.field_18628, 1.6F, // pufferfish
            effect(new class_1293(class_1294.field_5899, 1200, 1), 1.0F),
            effect(new class_1293(class_1294.field_5903, 300, 2), 1.0F),
            effect(new class_1293(class_1294.field_5916, 300, 0), 1.0F));
        put(class_4176.field_18632, 1.6F, effect(new class_1293(class_1294.field_5903, 600, 0), 0.8F)); // rotten flesh
        put(class_4176.field_18634, 1.6F, effect(new class_1293(class_1294.field_5899, 100, 0), 1.0F)); // spider eye
        for (class_4174 stew : new class_4174[] {class_4176.field_18642, class_4176.field_18661, class_4176.field_18631,
                class_4176.field_18635}) {
            STEWS.put(stew, Boolean.TRUE);
        }
    }

    private static FoodPossibleEffect effect(class_1293 effect, float probability) {
        return new FoodPossibleEffect(effect, probability);
    }

    private static void put(class_4174 food, float eatSeconds, FoodPossibleEffect... effects) {
        EXTRA.put(food, new Extra(eatSeconds, Optional.empty(), List.of(effects)));
    }

    /** record what 1.21.1 code gave a food beyond 1.21.2's fields (nothing for 1.21.1's defaults) */
    public static synchronized class_4174 attach(class_4174 food, Extra extra) {
        if (!extra.equals(Extra.DEFAULT)) {
            EXTRA.put(food, extra);
        }
        return food;
    }

    /** a food's 1.21.1 extras, null when it has 1.21.1's defaults */
    public static synchronized Extra extra(class_4174 food) {
        loadVanilla();
        Extra e = EXTRA.get(food);
        if (e == null && STEWS.containsKey(food)) {
            e = new Extra(DEFAULT_EAT_SECONDS, Optional.of(new class_1799(class_1802.field_8428)), List.of());
        }
        return e;
    }

    private static Extra extraOrDefault(class_4174 food) {
        Extra e = extra(food);
        return e == null ? Extra.DEFAULT : e;
    }

    // ---- FoodProperties ----

    /** new FoodProperties(nutrition, saturation, canAlwaysEat, eatSeconds, usingConvertsTo, effects) */
    public static class_4174 newFoodProperties(int nutrition, float saturation, boolean canAlwaysEat, float eatSeconds,
                                               Optional<class_1799> usingConvertsTo, List<FoodPossibleEffect> effects) {
        return attach(new class_4174(nutrition, saturation, canAlwaysEat), new Extra(eatSeconds, usingConvertsTo, List.copyOf(effects)));
    }

    /** eatDurationTicks(): eatSeconds * 20 */
    public static int method_58399(class_4174 food) {
        return (int) (comp_2494(food) * 20.0F);
    }

    /** eatSeconds() */
    public static float comp_2494(class_4174 food) {
        return extraOrDefault(food).eatSeconds();
    }

    /** usingConvertsTo() */
    public static Optional<class_1799> comp_2794(class_4174 food) {
        return extraOrDefault(food).usingConvertsTo();
    }

    /** effects() */
    public static List<FoodPossibleEffect> comp_2495(class_4174 food) {
        return extraOrDefault(food).effects();
    }

    /** Foods' constant 1.21.2 dropped (nutrition 1, saturation modifier 0.1; 1.21.1's builder arithmetic) */
    private static class_4174 removedFood;

    public static synchronized class_4174 field_50138() {
        if (removedFood == null) {
            removedFood = new class_4174.class_4175().method_19238(1).method_19237(0.1F).method_19242();
        }
        return removedFood;
    }

    // ---- FoodProperties.Builder (the parts 1.21.2's builder lost, kept on it by CbItemFoodBuilderMixin) ----

    /** the 1.21.1 builder state 1.21.2's Builder has no fields for */
    public interface BuilderExtras {
        float cb$eatSeconds();

        void cb$setEatSeconds(float seconds);

        Optional<class_1799> cb$convertsTo();

        void cb$setConvertsTo(Optional<class_1799> convertsTo);

        List<FoodPossibleEffect> cb$effects();
    }

    private static BuilderExtras b(class_4174.class_4175 builder) {
        return (BuilderExtras) (Object) builder;
    }

    /** a 1.21.1 Builder subclass overriding fast() / effect(..) / usingConvertsTo(..) (adapterHooks) */
    public interface LegacyFast {
        class_4174.class_4175 method_19241();
    }

    public interface LegacyEffect {
        class_4174.class_4175 method_19239(class_1293 effect, float probability);
    }

    public interface LegacyConvertsTo {
        class_4174.class_4175 method_60500(class_1935 item);
    }

    /** fast(): the class's override, else 1.21.1's body */
    public static class_4174.class_4175 method_19241(class_4174.class_4175 builder) {
        return builder instanceof LegacyFast l ? l.method_19241() : super_method_19241(builder);
    }

    /** 1.21.1's fast(): 0.8 seconds */
    public static class_4174.class_4175 super_method_19241(class_4174.class_4175 builder) {
        b(builder).cb$setEatSeconds(0.8F);
        return builder;
    }

    /** effect(effect, probability) */
    public static class_4174.class_4175 method_19239(class_4174.class_4175 builder, class_1293 effect, float probability) {
        return builder instanceof LegacyEffect l ? l.method_19239(effect, probability) : super_method_19239(builder, effect, probability);
    }

    public static class_4174.class_4175 super_method_19239(class_4174.class_4175 builder, class_1293 effect, float probability) {
        b(builder).cb$effects().add(new FoodPossibleEffect(effect, probability));
        return builder;
    }

    /** usingConvertsTo(item) */
    public static class_4174.class_4175 method_60500(class_4174.class_4175 builder, class_1935 item) {
        return builder instanceof LegacyConvertsTo l ? l.method_60500(item) : super_method_60500(builder, item);
    }

    /** 1.21.1's usingConvertsTo(item): a stack of it */
    public static class_4174.class_4175 super_method_60500(class_4174.class_4175 builder, class_1935 item) {
        b(builder).cb$setConvertsTo(Optional.of(new class_1799(item)));
        return builder;
    }

    /** the builder's 1.21.1 state, attached to what build() made */
    public static class_4174 built(class_4174.class_4175 builder, class_4174 food) {
        BuilderExtras x = b(builder);
        return attach(food, new Extra(x.cb$eatSeconds(), x.cb$convertsTo(), List.copyOf(x.cb$effects())));
    }

    /** a new builder's 1.21.1 state */
    public static List<FoodPossibleEffect> newEffects() {
        return new ArrayList<>();
    }

    // ---- Item.Properties.food(food) ----

    /**
     * the components 1.21.1's eating of a food with extras amounts to on 1.21.2: 1.21.2's default food consumption
     * with 1.21.1's eat time and an ApplyStatusEffectsConsumeEffect per possible effect (its probability), and the
     * remainder it turned into
     */
    public static class_1792.class_1793 food(class_1792.class_1793 props, class_4174 food, Extra extra) {
        class_10124.class_10125 c = class_10128.method_62858().method_62852(extra.eatSeconds());
        for (FoodPossibleEffect e : extra.effects()) {
            c.method_62854(new class_10132(e.comp_2496(), e.comp_2497()));
        }
        props.method_62833(food, c.method_62851());
        extra.usingConvertsTo().ifPresent(stack -> props.method_57349(class_9334.field_53965, new class_10131(stack)));
        return props;
    }

    private CbItemFood() {
    }
}
