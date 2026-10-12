package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayDeque;
import java.util.Optional;

/**
 * Food on 1.21.
 *
 * <ul>
 * <li>FoodData.eat(stack) became eat(food): 1.20.6's ate the stack's food component and did nothing for a
 *     stack without one. A 1.20.6 call keeps that body; a 1.20.6 FoodData subclass that overrides eat(stack)
 *     is handed the stack being eaten -- 1.21 eats through Player.eat(level, stack, food), which keeps the
 *     stack at hand for it (CbItemFoodMixin) -- or, when 1.21 code eats food outside that call, a stack of
 *     the item whose default food that is (none known: an empty stack).</li>
 * <li>FoodProperties(nutrition, saturation, canAlwaysEat, eatSeconds, effects) gained usingConvertsTo
 *     (the item left after eating): 1.20.6's food left nothing ({@link #noConversion}).</li>
 * </ul>
 */
public final class CbItemFood {

    private CbItemFood() {
    }

    /** FoodProperties' usingConvertsTo for a 1.20.6 constructor call: none (superCtorReshapes push) */
    public static Optional<net.minecraft.class_1799> noConversion() {
        return Optional.empty();
    }

    private static final ThreadLocal<ArrayDeque<net.minecraft.class_1799>> EATING = ThreadLocal.withInitial(ArrayDeque::new);

    /** Player.eat(level, stack, food) began (CbItemFoodMixin) */
    public static void pushEating(net.minecraft.class_1799 stack) {
        EATING.get().push(stack);
    }

    /** Player.eat(level, stack, food) returned (CbItemFoodMixin) */
    public static void popEating() {
        ArrayDeque<net.minecraft.class_1799> d = EATING.get();
        if (!d.isEmpty()) {
            d.pop();
        }
    }

    /** the 1.20.6 FoodData.eat(ItemStack) a mod's FoodData subclass overrides (adapter of the bridge hook) */
    public interface LegacyEat {
        void method_7579(net.minecraft.class_1799 stack);
    }

    /** the accessor a FoodData subclass that super-calls eat gets: super's eat(FoodProperties) */
    public interface EatSuper {
        void cb$super$method_7579(net.minecraft.class_4174 food);
    }

    /** 1.21 eats food: a 1.20.6 FoodData subclass's eat, handed the stack the food came from (see the class comment) */
    public static void eat(LegacyEat data, net.minecraft.class_4174 food) {
        data.method_7579(stackOf(food));
    }

    private static net.minecraft.class_1799 stackOf(net.minecraft.class_4174 food) {
        net.minecraft.class_1799 eating = EATING.get().peek();
        if (eating != null) {
            return eating;
        }
        for (net.minecraft.class_1792 item : net.minecraft.class_7923.field_41178) {
            if (food.equals(item.method_57347().method_57829(net.minecraft.class_9334.field_50075))) {
                return new net.minecraft.class_1799(item);
            }
        }
        return net.minecraft.class_1799.field_8037;
    }

    /** a FoodData subclass's super.eat(stack): 1.20.6's body, through super's eat(food) */
    public static void superEat(EatSuper self, net.minecraft.class_1799 stack) {
        net.minecraft.class_4174 food = stack.method_57824(net.minecraft.class_9334.field_50075);
        if (food != null) {
            self.cb$super$method_7579(food);
        }
    }

    /** FoodData.eat(stack) called by a 1.20.6 mod (instanceRedirect): a 1.20.6 subclass's own, else 1.20.6's body */
    public static void method_7579(net.minecraft.class_1702 data, net.minecraft.class_1799 stack) {
        if (data instanceof LegacyEat legacy) {
            legacy.method_7579(stack);
            return;
        }
        net.minecraft.class_4174 food = stack.method_57824(net.minecraft.class_9334.field_50075);
        if (food != null) {
            data.method_7579(food);
        }
    }
}
