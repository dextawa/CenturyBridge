package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3417;
import net.minecraft.class_3481;
import net.minecraft.class_5134;
import top.dext.centurybridge.step.v1_21.mixin.CbEntityInvoker;

/**
 * Soul speed: 1.20.6 drove it from LivingEntity's own methods -- onChangedBlock removed the speed modifier when
 * shouldRemoveSoulSpeed(state) and then tryAddSoulSpeed(); baseTick spawned the soul particles when
 * canSpawnSoulSpeedParticle() -- each overridable (Player's onSoulSpeedBlock and shouldRemoveSoulSpeed look at
 * flying). 1.21 made soul speed data (the enchantment's location_changed and tick effects) and removed the
 * methods. A 1.20.6 caller's call answers 1.20.6's body here, through a 1.20.6 subclass's own override where it
 * has one (the Legacy* interfaces); for such a subclass CbEntitySoulSpeedMixin also runs 1.20.6's drive again
 * after 1.21's location effects, so its overrides decide as they did. The speed modifier is the one 1.21's
 * enchantment uses: 1.20.6's soul speed uuid maps to minecraft:enchantment.soul_speed (CbEntityAttributes).
 * Wired from s6world.json.
 */
public final class CbEntitySoulSpeed {

    public interface LegacyCanSpawnParticle {
        boolean method_27302();
    }

    public interface LegacySpawnParticle {
        void method_25937();
    }

    public interface LegacyOnSoulSpeedBlock {
        boolean method_27303();
    }

    public interface LegacyShouldRemove {
        boolean method_29500(class_2680 state);
    }

    public interface LegacyRemove {
        void method_29501();
    }

    public interface LegacyTryAdd {
        void method_29502();
    }

    /** the local player (CbEntitySoulSpeedClientMixin): 1.20.6's LocalPlayer spawned no soul particles while flying */
    public interface LocalPlayerParticles {
    }

    /** 1.20.6's LivingEntity.SPEED_MODIFIER_SOUL_SPEED_UUID, as 1.21 names it */
    private static final class_2960 SPEED_MODIFIER =
        CbEntityAttributes.idOf(java.util.UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038"));

    // ------------------------------------------------------------------------------------------------ the calls
    public static boolean canSpawnSoulSpeedParticle(class_1309 entity) {
        if (entity instanceof LegacyCanSpawnParticle legacy) {
            return legacy.method_27302();
        }
        if (entity instanceof LocalPlayerParticles && ((class_1657) entity).method_31549().field_7479) {
            return false;
        }
        return superCanSpawnSoulSpeedParticle(entity);
    }

    public static void spawnSoulSpeedParticle(class_1309 entity) {
        if (entity instanceof LegacySpawnParticle legacy) {
            legacy.method_25937();
        } else {
            superSpawnSoulSpeedParticle(entity);
        }
    }

    public static boolean onSoulSpeedBlock(class_1309 entity) {
        if (entity instanceof LegacyOnSoulSpeedBlock legacy) {
            return legacy.method_27303();
        }
        return entity instanceof class_1657 player ? superOnSoulSpeedBlock(player) : superOnSoulSpeedBlock(entity);
    }

    public static boolean shouldRemoveSoulSpeed(class_1309 entity, class_2680 state) {
        if (entity instanceof LegacyShouldRemove legacy) {
            return legacy.method_29500(state);
        }
        return entity instanceof class_1657 player ? superShouldRemoveSoulSpeed(player, state) : superShouldRemoveSoulSpeed(entity, state);
    }

    public static void removeSoulSpeed(class_1309 entity) {
        if (entity instanceof LegacyRemove legacy) {
            legacy.method_29501();
        } else {
            superRemoveSoulSpeed(entity);
        }
    }

    public static void tryAddSoulSpeed(class_1309 entity) {
        if (entity instanceof LegacyTryAdd legacy) {
            legacy.method_29502();
        } else {
            superTryAddSoulSpeed(entity);
        }
    }

    // ------------------------------------------------------------------------------------------------ 1.20.6's bodies
    /** LivingEntity.canSpawnSoulSpeedParticle() */
    public static boolean superCanSpawnSoulSpeedParticle(class_1309 entity) {
        class_243 motion = entity.method_18798();
        return entity.field_6012 % 5 == 0 && motion.field_1352 != 0.0 && motion.field_1350 != 0.0 && !entity.method_7325()
            && CbEnchantmentHelper.method_25951(entity) && onSoulSpeedBlock(entity);
    }

    /** LivingEntity.spawnSoulSpeedParticle() */
    public static void superSpawnSoulSpeedParticle(class_1309 entity) {
        class_243 motion = entity.method_18798();
        net.minecraft.class_5819 random = entity.method_59922();
        entity.method_37908().method_8406(net.minecraft.class_2398.field_23114,
            entity.method_23317() + (random.method_43058() - 0.5) * entity.method_17681(), entity.method_23318() + 0.1,
            entity.method_23321() + (random.method_43058() - 0.5) * entity.method_17681(),
            motion.field_1352 * -0.2, 0.1, motion.field_1350 * -0.2);
        float volume = random.method_43057() * 0.4F + random.method_43057() > 0.9F ? 0.6F : 0.0F;
        entity.method_5783(class_3417.field_23060.comp_349(), volume, 0.6F + random.method_43057() * 0.4F);
    }

    /** LivingEntity.onSoulSpeedBlock(): the block that affects the entity's movement is a soul speed block */
    public static boolean superOnSoulSpeedBlock(class_1309 entity) {
        return entity.method_37908().method_8320(entity.method_23314()).method_26164(class_3481.field_23063);
    }

    /** Player.onSoulSpeedBlock(): not while flying */
    public static boolean superOnSoulSpeedBlock(class_1657 player) {
        return !player.method_31549().field_7479 && superOnSoulSpeedBlock((class_1309) player);
    }

    /** LivingEntity.shouldRemoveSoulSpeed(state): on anything but air, or while gliding */
    public static boolean superShouldRemoveSoulSpeed(class_1309 entity, class_2680 state) {
        return !state.method_26215() || entity.method_6128();
    }

    /** Player.shouldRemoveSoulSpeed(state): also while flying */
    public static boolean superShouldRemoveSoulSpeed(class_1657 player, class_2680 state) {
        return player.method_31549().field_7479 || superShouldRemoveSoulSpeed((class_1309) player, state);
    }

    /** LivingEntity.removeSoulSpeed() */
    public static void superRemoveSoulSpeed(class_1309 entity) {
        class_1324 speed = entity.method_5996(class_5134.field_23719);
        if (speed != null && speed.method_6199(SPEED_MODIFIER) != null) {
            speed.method_6200(SPEED_MODIFIER);
        }
    }

    /**
     * LivingEntity.tryAddSoulSpeed(): standing on something, with soul speed boots on a soul speed block, the
     * speed modifier 0.03 * (1 + level * 0.35), and a 4% chance to wear the boots by one. The modifier is added or
     * updated: for a subclass driving itself, 1.21's own location effect may already have put one of that id
     * there in the same block change, which 1.20.6 never did.
     */
    public static void superTryAddSoulSpeed(class_1309 entity) {
        if (((CbEntityInvoker) entity).cb$getBlockStateOnLegacy().method_26215()) {
            return;
        }
        int level = CbEnchantmentHelper.method_8203(CbEnchVanilla.field_23071(), entity);
        if (level > 0 && onSoulSpeedBlock(entity)) {
            class_1324 speed = entity.method_5996(class_5134.field_23719);
            if (speed == null) {
                return;
            }
            speed.method_55696(new class_1322(SPEED_MODIFIER, 0.03F * (1.0F + level * 0.35F), class_1322.class_1323.field_6328));
            if (entity.method_59922().method_43057() < 0.04F) {
                class_1799 boots = entity.method_6118(class_1304.field_6166);
                boots.method_7970(1, entity, class_1304.field_6166);
            }
        }
    }

    // ------------------------------------------------------------------------------------------------ the drive
    /** a 1.20.6 subclass that overrides any of the soul speed methods: 1.20.6 drove soul speed through them */
    public static boolean drivesItself(class_1309 entity) {
        return entity instanceof LegacyOnSoulSpeedBlock || entity instanceof LegacyShouldRemove
            || entity instanceof LegacyRemove || entity instanceof LegacyTryAdd;
    }

    /** 1.20.6's onChangedBlock, the soul speed part, through the entity's own methods */
    public static void onChangedBlock(class_1309 entity) {
        if (shouldRemoveSoulSpeed(entity, ((CbEntityInvoker) entity).cb$getBlockStateOnLegacy())) {
            removeSoulSpeed(entity);
        }
        tryAddSoulSpeed(entity);
    }

    /** 1.20.6's baseTick, the particle part, for a subclass that overrides either particle method */
    public static void baseTick(class_1309 entity) {
        if ((entity instanceof LegacyCanSpawnParticle || entity instanceof LegacySpawnParticle) && canSpawnSoulSpeedParticle(entity)) {
            spawnSoulSpeedParticle(entity);
        }
    }

    private CbEntitySoulSpeed() {
    }
}
