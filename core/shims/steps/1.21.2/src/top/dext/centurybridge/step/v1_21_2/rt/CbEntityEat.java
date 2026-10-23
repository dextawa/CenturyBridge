package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_10124;
import net.minecraft.class_10131;
import net.minecraft.class_10134;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1839;
import net.minecraft.class_174;
import net.minecraft.class_1937;
import net.minecraft.class_3222;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3468;
import net.minecraft.class_4174;
import net.minecraft.class_5712;
import net.minecraft.class_5819;
import net.minecraft.class_9334;

/**
 * 1.21.1's eating and item-use feedback on LivingEntity, which 1.21.2 moved into the Consumable item component.
 *
 * <p>1.21.1: Item.finishUsingItem of a food called entity.eat(level, stack, food) -- the eating sound
 * (getEatingSound, overridable), the food's effects, one item consumed, the EAT game event; Player's eat also fed the
 * food data, counted the ITEM_USED stat, burped, fired CONSUME_ITEM and handed back usingConvertsTo. While an item was
 * used, triggerItemUseEffects(stack, count) -- overridable -- played getDrinkingSound for DRINK, item particles and
 * getEatingSound for EAT. 1.21.2's Consumable.onConsume and emitParticlesAndSounds do the same from the stack's
 * components and ask the entity nothing.
 *
 * <p>Calls answer the mod class's own 1.21.1 override, else 1.21.1's bodies on 1.21.2's API. Two 1.21.1 ingredients
 * live elsewhere on 1.21.2: a food's effects are its stack's CONSUMABLE on-consume effects (applied when the food
 * eaten is the stack's own FOOD; a 1.21.2 FoodProperties carries none of its own), usingConvertsTo is the stack's
 * USE_REMAINDER. A mod class overriding eat, triggerItemUseEffects, getEatingSound or getDrinkingSound is asked where
 * 1.21.1 asked it (CbEntityEatMixin on Consumable).
 */
public final class CbEntityEat {

    public interface LegacyEat {
        class_1799 method_18866(class_1937 level, class_1799 stack, class_4174 food);
    }

    public interface LegacyTrigger {
        void method_6098(class_1799 stack, int count);
    }

    public interface LegacyEatingSound {
        class_3414 method_18869(class_1799 stack);
    }

    public interface LegacyDrinkingSound {
        class_3414 method_18807(class_1799 stack);
    }

    // ------------------------------------------------------------------------------------------ sounds

    public static class_3414 getEatingSound(class_1309 entity, class_1799 stack) {
        if (entity instanceof LegacyEatingSound legacy) {
            return legacy.method_18869(stack);
        }
        return superGetEatingSound(entity, stack);
    }

    /** 1.21.1's body of the vanilla class: Fox, Goat (screaming or not), Armadillo, Sniffer their own; else the stack's */
    public static class_3414 superGetEatingSound(class_1309 entity, class_1799 stack) {
        if (entity instanceof net.minecraft.class_4019) {
            return class_3417.field_18060;
        }
        if (entity instanceof net.minecraft.class_6053 goat) {
            return goat.method_35178() ? class_3417.field_33440 : class_3417.field_33437;
        }
        if (entity instanceof net.minecraft.class_9069) {
            return class_3417.field_47711;
        }
        if (entity instanceof net.minecraft.class_8153) {
            return class_3417.field_42596;
        }
        return CbItemMembers.method_21833(stack);
    }

    public static class_3414 getDrinkingSound(class_1309 entity, class_1799 stack) {
        if (entity instanceof LegacyDrinkingSound legacy) {
            return legacy.method_18807(stack);
        }
        return superGetDrinkingSound(entity, stack);
    }

    /** 1.21.1's body of the vanilla class: WanderingTrader's milk / potion sounds, else the stack's drinking sound */
    public static class_3414 superGetDrinkingSound(class_1309 entity, class_1799 stack) {
        if (entity instanceof net.minecraft.class_3989) {
            return stack.method_31574(net.minecraft.class_1802.field_8103) ? class_3417.field_18316 : class_3417.field_18313;
        }
        return CbItemMembers.method_21832(stack);
    }

    public static class_3414 superGetDrinkingSound(net.minecraft.class_3989 entity, class_1799 stack) {
        return superGetDrinkingSound((class_1309) entity, stack);
    }

    /** a mod class's super.getEatingSound, typed as each vanilla class that declared it */
    public static class_3414 superGetEatingSound(net.minecraft.class_4019 entity, class_1799 stack) {
        return superGetEatingSound((class_1309) entity, stack);
    }

    public static class_3414 superGetEatingSound(net.minecraft.class_6053 entity, class_1799 stack) {
        return superGetEatingSound((class_1309) entity, stack);
    }

    public static class_3414 superGetEatingSound(net.minecraft.class_9069 entity, class_1799 stack) {
        return superGetEatingSound((class_1309) entity, stack);
    }

    public static class_3414 superGetEatingSound(net.minecraft.class_8153 entity, class_1799 stack) {
        return superGetEatingSound((class_1309) entity, stack);
    }

    /** LivingEntity.USE_ITEM_INTERVAL (1.21.1's constant) */
    public static int field_30063() {
        return 4;
    }

    // ------------------------------------------------------------------------------------------ use effects

    public static void triggerItemUseEffects(class_1309 entity, class_1799 stack, int count) {
        if (entity instanceof LegacyTrigger legacy) {
            legacy.method_6098(stack, count);
            return;
        }
        superTriggerItemUseEffects(entity, stack, count);
    }

    /** 1.21.1 LivingEntity.triggerItemUseEffects */
    public static void superTriggerItemUseEffects(class_1309 entity, class_1799 stack, int count) {
        if (stack.method_7960() || !entity.method_6115()) {
            return;
        }
        if (stack.method_7976() == class_1839.field_8946) {
            class_5819 r = entity.method_37908().method_8409();
            entity.method_5783(getDrinkingSound(entity, stack), 0.5F, r.method_43057() * 0.1F + 0.9F);
        }
        if (stack.method_7976() == class_1839.field_8950) {
            entity.method_6037(stack, count);
            class_5819 r = entity.method_59922();
            entity.method_5783(getEatingSound(entity, stack), 0.5F + 0.5F * (float) r.method_43048(2),
                (r.method_43057() - r.method_43057()) * 0.2F + 1.0F);
        }
    }

    /** from the mixin on Consumable.emitParticlesAndSounds: 1.21.1's feedback when the entity's class changes it */
    public static boolean overridesUseEffects(class_1309 entity) {
        return entity instanceof LegacyTrigger || entity instanceof LegacyEatingSound || entity instanceof LegacyDrinkingSound;
    }

    // ------------------------------------------------------------------------------------------ eat

    /** 1.21.1 eat(level, stack): the stack's FOOD eaten, else the stack untouched */
    public static class_1799 eat(class_1309 entity, class_1937 level, class_1799 stack) {
        class_4174 food = stack.method_57824(class_9334.field_50075);
        return food != null ? eat(entity, level, stack, food) : stack;
    }

    public static class_1799 eat(class_1309 entity, class_1937 level, class_1799 stack, class_4174 food) {
        if (entity instanceof LegacyEat legacy) {
            return legacy.method_18866(level, stack, food);
        }
        return superEat(entity, level, stack, food);
    }

    /** 1.21.1's eat of the vanilla class: Player's, else LivingEntity's */
    public static class_1799 superEat(class_1309 entity, class_1937 level, class_1799 stack, class_4174 food) {
        if (!(entity instanceof class_1657 player)) {
            return livingEat(entity, level, stack, food);
        }
        class_10131 remainder = stack.method_57824(class_9334.field_53965);
        player.method_7344().method_7579(food);
        player.method_7259(class_3468.field_15372.method_14956(stack.method_7909()));
        class_5819 r = level.method_8409();
        level.method_43128(null, player.method_23317(), player.method_23318(), player.method_23321(), class_3417.field_19149,
            class_3419.field_15248, 0.5F, r.method_43057() * 0.1F + 0.9F);
        if (player instanceof class_3222 sp) {
            class_174.field_1198.method_8821(sp, stack);
        }
        class_1799 left = livingEat(player, level, stack, food);
        if (remainder != null && !player.method_56992()) {
            if (left.method_7960()) {
                return remainder.comp_3093().method_7972();
            }
            if (!level.method_8608()) {
                player.method_31548().method_7394(remainder.comp_3093().method_7972());
            }
        }
        return left;
    }

    public static class_1799 superEat(class_1657 entity, class_1937 level, class_1799 stack, class_4174 food) {
        return superEat((class_1309) entity, level, stack, food);
    }

    /** 1.21.1 LivingEntity.eat */
    private static class_1799 livingEat(class_1309 entity, class_1937 level, class_1799 stack, class_4174 food) {
        class_5819 r = level.method_8409();
        level.method_43128(null, entity.method_23317(), entity.method_23318(), entity.method_23321(), getEatingSound(entity, stack),
            class_3419.field_15254, 1.0F, 1.0F + (r.method_43057() - r.method_43057()) * 0.4F);
        if (food == stack.method_57824(class_9334.field_50075)) {
            class_10124 consumable = stack.method_57824(class_9334.field_53964);
            if (consumable != null && !level.method_8608()) {
                for (class_10134 effect : consumable.comp_3089()) {
                    effect.method_62866(level, stack, entity);
                }
            }
        }
        stack.method_57008(1, entity);
        entity.method_32876(class_5712.field_28735);
        return stack;
    }

    /** from the mixin on Consumable.onConsume: a mod class's own 1.21.1 eat for a food, or null for 1.21.2's */
    public static class_1799 consume(class_1937 level, class_1309 entity, class_1799 stack) {
        if (!(entity instanceof LegacyEat legacy)) {
            return null;
        }
        class_4174 food = stack.method_57824(class_9334.field_50075);
        return food == null ? null : legacy.method_18866(level, stack, food);
    }

    private CbEntityEat() {
    }
}
