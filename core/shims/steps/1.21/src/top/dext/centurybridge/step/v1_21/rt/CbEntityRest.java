package top.dext.centurybridge.step.v1_21.rt;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1314;
import net.minecraft.class_1321;
import net.minecraft.class_1350;
import net.minecraft.class_1767;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_3532;
import net.minecraft.class_4095;
import net.minecraft.class_4140;
import net.minecraft.class_4141;
import net.minecraft.class_4168;
import net.minecraft.class_6028;
import net.minecraft.class_6862;
import net.minecraft.class_7924;
import net.minecraft.class_8103;
import net.minecraft.class_8110;
import net.minecraft.class_8949;
import top.dext.centurybridge.step.v1_21.mixin.CbEntityBreezeAiInvoker;
import top.dext.centurybridge.step.v1_21.mixin.CbEntityFollowOwnerAccessor;

/**
 * The rest of 1.20.6's world/entity and damage source API that 1.21 reshaped: armour absorption without a victim,
 * EquipmentSlot.byTypeAndIndex, the sheep colour table, AnimalPanic's predicate, FollowOwnerGoal's canFly, the breeze
 * brain, the crafting grid's recipe hint, PoiManager's I/O reporter, loot parameter validation. Wired from
 * s6fin-entity.json.
 */
public final class CbEntityRest {

    private CbEntityRest() {
    }

    // ------------------------------------------------------------------------------------------------ CombatRules

    /**
     * 1.20.6 CombatRules.getDamageAfterAbsorb(damage, source, armor, toughness): 1.20.6's armour formula, the armour
     * fraction reduced by the breach level of the attacker's weapon (1.20.6's BreachEnchantment: 0.15 a level,
     * clamped to [0, 1]; the attacker is the source's entity, its level read off its slots as 1.20.6 did). 1.21's
     * takes the victim to run the weapon's enchantment effects in the victim's level, which a 1.20.6 caller does not
     * name.
     */
    public static float method_5496(float damage, class_1282 source, float armor, float toughness) {
        float f = 2.0F + toughness / 4.0F;
        float g = class_3532.method_15363(armor - damage / f, armor * 0.2F, 20.0F);
        float fraction = g / 25.0F;
        float effective = armorBreach(source.method_5529(), fraction);
        float rest = 1.0F - effective;
        return damage * rest;
    }

    private static float armorBreach(class_1297 attacker, float fraction) {
        if (attacker instanceof class_1309 living) {
            int level = living.method_37908().method_30349().method_30530(class_7924.field_41265).method_40264(class_1893.field_50158)
                .map(breach -> class_1890.method_8203(breach, living)).orElse(0);
            if (level > 0) {
                return class_3532.method_15363(fraction - 0.15F * level, 0.0F, 1.0F);
            }
        }
        return fraction;
    }

    // ------------------------------------------------------------------------------------------------ EquipmentSlot

    /** 1.20.6 EquipmentSlot.byTypeAndIndex(type, index): the slot of that type and index, else "Invalid slot 'type': index" */
    public static class_1304 method_20234(class_1304.class_1305 type, int index) {
        for (class_1304 slot : class_1304.values()) {
            if (slot.method_5925() == type && slot.method_5927() == index) {
                return slot;
            }
        }
        throw new IllegalArgumentException("Invalid slot '" + type + "': " + index);
    }

    // ------------------------------------------------------------------------------------------------ Sheep

    private static final Map<class_1767, float[]> SHEEP_COLORS = new EnumMap<>(class_1767.class);

    static {
        for (class_1767 dye : class_1767.values()) {
            float[] color;
            if (dye == class_1767.field_7952) {
                color = new float[]{0.9019608F, 0.9019608F, 0.9019608F};
            } else {
                float[] diffuse = CbItemMisc.rgbComponents(dye.method_7787());
                color = new float[]{diffuse[0] * 0.75F, diffuse[1] * 0.75F, diffuse[2] * 0.75F};
            }
            SHEEP_COLORS.put(dye, color);
        }
    }

    /**
     * 1.20.6 Sheep.getColorArray(dye): the wool colour as three components -- white 0.9019608 each, any other dye its
     * texture diffuse colour times 0.75 (1.20.6's table, the same components 1.21's int is made of).
     */
    public static float[] method_6634(class_1767 dye) {
        return SHEEP_COLORS.get(dye);
    }

    // ------------------------------------------------------------------------------------------------ AnimalPanic

    /** AnimalPanic's 1.20.6 should-panic predicate, kept by CbEntityAnimalPanicMixin */
    public interface LegacyPanic {
        void cb$setPanicPredicate(Predicate<class_1314> predicate);
    }

    /**
     * The panic-cause function a 1.20.6 AnimalPanic(speed, predicate) is made with: 1.21's default (DamageTypeTags
     * PANIC_CAUSES). It is never asked -- the 1.20.6 predicate decides ({@link #setPanicPredicate}).
     */
    public static Function<class_1314, class_6862<class_8110>> defaultPanicCauses() {
        return mob -> class_8103.field_51990;
    }

    /** the 1.20.6 predicate, handed to the AnimalPanic just made (superCtorReshapes post) */
    @SuppressWarnings("unchecked")
    public static void setPanicPredicate(class_6028<?> panic, Predicate<?> predicate) {
        ((LegacyPanic) panic).cb$setPanicPredicate((Predicate<class_1314>) predicate);
    }

    /**
     * 1.20.6 AnimalPanic.shouldPanic(level, mob) with a 1.20.6 predicate: the predicate holds and the mob was hurt by
     * something or is panicking already (memories HURT_BY, IS_PANICKING).
     */
    public static boolean shouldPanic(Predicate<class_1314> predicate, class_1314 mob) {
        return predicate.test(mob) && (mob.method_18868().method_18896(class_4140.field_18451) || mob.method_18868().method_18896(class_4140.field_39408));
    }

    // ------------------------------------------------------------------------------------------------ FollowOwnerGoal

    /** TamableAnimal's 1.20.6 follow-goal canFly, kept by CbEntityTamableFlyMixin */
    public interface LegacyFlyToOwner {
        void cb$setCanFlyToOwner(boolean canFly);
    }

    /** FollowOwnerGoal's restored 1.20.6 canFly field (CbEntityFollowOwnerRestoredMixin) */
    public interface LegacyFollowCanFly {
        void cb$setCanFly(boolean canFly);
    }

    /**
     * 1.20.6 FollowOwnerGoal's canFly: the goal let its animal teleport onto leaves. 1.21's goal teleports through
     * the animal (TamableAnimal.tryToTeleportToOwner), which asks the animal's canFlyToOwner(); a goal made with
     * canFly = true makes its animal answer true there (CbEntityTamableFlyMixin), false leaves 1.21's answer. The goal's
     * restored canFly field (read by the restored 1.20.6 canTeleportTo and by mods' accessors) holds the argument.
     */
    public static void setFollowCanFly(class_1350 goal, boolean canFly) {
        ((LegacyFollowCanFly) goal).cb$setCanFly(canFly);
        if (canFly) {
            class_1321 animal = ((CbEntityFollowOwnerAccessor) goal).cb$tamable();
            ((LegacyFlyToOwner) animal).cb$setCanFlyToOwner(true);
        }
    }

    // ------------------------------------------------------------------------------------------------ BreezeAi

    /**
     * 1.20.6 BreezeAi.makeBrain(brain): core and fight activities as 1.21 adds them, the idle activity 1.20.6's --
     * 1.20.6's StopAttackingIfTargetInvalid.create() (1.21's checks the target against the breeze itself, which a
     * 1.20.6 caller does not hand over), then the breeze's four idle behaviours, CORE set, IDLE the default.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_4095<?> method_55016(class_4095<class_8949> brain) {
        CbEntityBreezeAiInvoker.cb$initCoreActivity(brain);
        CbEntityBreezeAiInvoker.cb$initFightActivity(brain);
        brain.method_24529(class_4168.field_22396,
            com.google.common.collect.ImmutableList.of(
                com.mojang.datafixers.util.Pair.of(0, (net.minecraft.class_7893) net.minecraft.class_4828.method_47132()),
                com.mojang.datafixers.util.Pair.of(1, new net.minecraft.class_8953()),
                com.mojang.datafixers.util.Pair.of(2, new net.minecraft.class_8952()),
                com.mojang.datafixers.util.Pair.of(3, new net.minecraft.class_9035()),
                com.mojang.datafixers.util.Pair.of(4, new net.minecraft.class_8954())),
            com.google.common.collect.ImmutableSet.of(com.mojang.datafixers.util.Pair.of(class_4140.field_22355, class_4141.field_18456),
                com.mojang.datafixers.util.Pair.of(class_4140.field_18445, class_4141.field_18457)));
        brain.method_18890(java.util.Set.of(class_4168.field_18594));
        brain.method_18897(class_4168.field_22396);
        brain.method_24536();
        return brain;
    }

    // ------------------------------------------------------------------------------------------------ misc

    /** 1.21's isDirect() of a 1.20.6 damage source class: its own isIndirect(), negated */
    public static boolean not(boolean indirect) {
        return !indirect;
    }

    /** the recipe hint a 1.20.6 slotChangedCraftingGrid passes: none (1.20.6 looked the recipe up without one) */
    public static net.minecraft.class_8786<net.minecraft.class_3955> noRecipeHint() {
        return null;
    }

    /** LootContextParamSet.validateUser(context, user) takes the context's problem reporter since 1.21 */
    public static net.minecraft.class_8942 reporterOf(net.minecraft.class_58 context) {
        return context.method_60288();
    }
}
