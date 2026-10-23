package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.class_1282;
import net.minecraft.class_1295;
import net.minecraft.class_1297;
import net.minecraft.class_1303;
import net.minecraft.class_1309;
import net.minecraft.class_1508;
import net.minecraft.class_1511;
import net.minecraft.class_1538;
import net.minecraft.class_1540;
import net.minecraft.class_1541;
import net.minecraft.class_1542;
import net.minecraft.class_1571;
import net.minecraft.class_1657;
import net.minecraft.class_1669;
import net.minecraft.class_1672;
import net.minecraft.class_1674;
import net.minecraft.class_1676;
import net.minecraft.class_3218;
import net.minecraft.class_4050;
import net.minecraft.class_6335;
import net.minecraft.class_7260;
import net.minecraft.class_8103;
import net.minecraft.class_8113;
import net.minecraft.class_8150;
import net.minecraft.class_8836;
import net.minecraft.class_8949;
import net.minecraft.class_9461;
import net.minecraft.class_9691;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityHurtInvoker;

/**
 * 1.21.1's Entity.hurt(source, amount) and isInvulnerableTo(source) on 1.21.2.
 *
 * <p>1.21.2 split hurt by side: hurtServer(level, source, amount) (abstract on Entity, final on twelve
 * vanilla classes: AreaEffectCloud, Display, ExperienceOrb, Interaction, EvokerFangs, Marker,
 * OminousItemSpawner, EnderDragonPart, EndCrystal, FallingBlockEntity, ItemEntity, PrimedTnt) and
 * hurtClient(source) (false on Entity); the final hurtOrSimulate(source, amount) picks one by the
 * entity's level, and every 1.21.2 caller that 1.21.1 had call hurt(source, amount) on either side
 * (Player.attack, AbstractArrow, ThrownTrident, ...) calls it. isInvulnerableTo became
 * LivingEntity.isInvulnerableTo(level, source); what Entity's answered is the final
 * isInvulnerableToBase(source).
 *
 * <ul>
 * <li>a mod's call of hurt(source, amount) is hurtOrSimulate: on the server the entity's hurtServer (where
 *     a mod class's old hurt is hooked), on a client its hurtClient -- or the mod class's old hurt, which
 *     1.21.1 called there too (CbEntityHurtMixin on hurtOrSimulate);</li>
 * <li>a mod class's old hurt answers hurtServer (override hook; under the twelve final classes
 *     CbEntityHurtFinalMixin hands the call to it), and its super.hurt(..) is {@link #superHurt};</li>
 * <li>isInvulnerableTo: on a LivingEntity on the server, 1.21.2's (hooked to a mod's old override); on a
 *     client, 1.21.1's bodies, which needed no server ({@link #invulnerableOnClient}); on any other entity
 *     the base test, or the mod class's own old override (CbEntityHurtBaseMixin on isInvulnerableToBase,
 *     which 1.21.2's vanilla non-living entities ask where 1.21.1's asked isInvulnerableTo).</li>
 * </ul>
 */
public final class CbEntityHurt {

    /** the 1.21.1 hurt(source, amount) a mod class of Entity declares (an adapter hook gives it this interface) */
    public interface Legacy {
        boolean method_5643(class_1282 source, float amount);
    }

    /** the 1.21.1 isInvulnerableTo(source) a mod class of Entity declares */
    public interface LegacyInvulnerable {
        boolean method_5679(class_1282 source);
    }

    /** super's 1.21.2 halves of hurt, for a mod class whose old hurt calls super.hurt(..) */
    public interface Super {
        boolean cb$super$method_64397(class_3218 level, class_1282 source, float amount);

        boolean cb$super$method_5643(class_1282 source);
    }

    /** super's 1.21.2 isInvulnerableTo, for a mod LivingEntity whose old isInvulnerableTo calls super */
    public interface SuperInvulnerable {
        boolean cb$super$method_5679(class_3218 level, class_1282 source);
    }

    /**
     * The topmost vanilla classes declaring a concrete hurtServer (every other vanilla entity class is under
     * one of them; read off the 1.21.2 stub). A mod class under none of them extends Entity itself, whose
     * hurtServer is abstract: its super.hurt(..) is 1.21.1 Entity.hurt's body ({@link #entityHurt}).
     */
    private static final Class<?>[] CONCRETE = {
        class_1295.class, class_1303.class, class_1309.class, class_1508.class, class_1511.class, class_1538.class,
        class_1540.class, class_1541.class, class_1542.class, class_1669.class, class_1672.class, class_1676.class,
        class_6335.class, class_8113.class, class_8150.class, class_8836.class, class_9461.class, class_9691.class,
    };

    private static final ClassValue<Boolean> ABSTRACT_SUPER = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c : CONCRETE) {
                if (c.isAssignableFrom(type)) {
                    return false;
                }
            }
            return true;
        }
    };

    /** entities whose mod hurt the final-class mixin is running (a super call back into the final method falls through) */
    private static final ThreadLocal<Set<Object>> IN_FINAL =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    /** entities whose base invulnerability test a mod's old isInvulnerableTo is answering */
    private static final ThreadLocal<Set<Object>> IN_BASE =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    // ------------------------------------------------------------------------------------------ calls

    /** 1.21.1 hurt(source, amount), called on any entity: what 1.21.2's callers of the same moments call */
    public static boolean hurt(class_1297 entity, class_1282 source, float amount) {
        return entity.method_64420(source, amount);
    }

    /** 1.21.1 isInvulnerableTo(source), called on any entity */
    public static boolean isInvulnerableTo(class_1297 entity, class_1282 source) {
        if (entity instanceof class_1309 living) {
            if (entity.method_37908() instanceof class_3218 sl) {
                return living.method_5679(sl, source);
            }
            if (entity instanceof LegacyInvulnerable legacy) {
                return legacy.method_5679(source);
            }
            return invulnerableOnClient(living, source);
        }
        if (entity instanceof LegacyInvulnerable legacy) {
            return legacy.method_5679(source);
        }
        return ((CbEntityHurtInvoker) entity).cb$isInvulnerableToBase(source);
    }

    // ------------------------------------------------------------------------------------------ super calls

    /**
     * A mod class's super.hurt(source, amount): on the server super's hurtServer with the level vanilla passed
     * (the hook's frame) or the entity's own; on a client super's hurtClient, 1.21.2's half of what 1.21.1's
     * super.hurt did there. Under Entity itself (abstract hurtServer) 1.21.1 Entity.hurt's body on both sides.
     */
    public static boolean superHurt(Super self, class_1282 source, float amount) {
        class_1297 entity = (class_1297) self;
        if (ABSTRACT_SUPER.get(entity.getClass())) {
            return entityHurt(entity, source);
        }
        class_3218 level = frameLevel(entity);
        if (level != null) {
            return self.cb$super$method_64397(level, source, amount);
        }
        return self.cb$super$method_5643(source);
    }

    /**
     * A mod LivingEntity's super.isInvulnerableTo(source): on the server super's 1.21.2 isInvulnerableTo with
     * the level vanilla passed or the entity's own; on a client 1.21.1's answer of the vanilla class above.
     */
    public static boolean superIsInvulnerableTo(SuperInvulnerable self, class_1282 source) {
        class_1309 living = (class_1309) self;
        class_3218 level = frameLevel(living);
        if (level != null) {
            return self.cb$super$method_5679(level, source);
        }
        return invulnerableOnClient(living, source);
    }

    /**
     * A mod non-living entity's super.isInvulnerableTo(source): 1.21.1 Entity's body, which is 1.21.2's
     * isInvulnerableToBase. Marked, so the base mixin does not hand it back to the mod's override.
     */
    public static boolean superIsInvulnerableToBase(class_1297 entity, class_1282 source) {
        Set<Object> in = IN_BASE.get();
        boolean added = in.add(entity);
        try {
            return ((CbEntityHurtInvoker) entity).cb$isInvulnerableToBase(source);
        } finally {
            if (added) {
                in.remove(entity);
            }
        }
    }

    /** 1.21.2's hurtServer of a mod class extending Entity that declared no hurt: 1.21.1 Entity.hurt's body */
    public static boolean fillHurtServer(class_1297 entity, class_3218 level, class_1282 source, float amount) {
        return entityHurt(entity, source);
    }

    /** 1.21.1 Entity.hurt(source, amount): invulnerable -> false; else markHurt, false */
    private static boolean entityHurt(class_1297 entity, class_1282 source) {
        if (isInvulnerableTo(entity, source)) {
            return false;
        }
        ((CbEntityHurtInvoker) entity).cb$markHurt();
        return false;
    }

    // ------------------------------------------------------------------------------------------ mixin side

    /**
     * From the mixin on the final hurtServer of the twelve classes: the mod class's old hurt answers, with the
     * level vanilla passed on record for its super call; null to let vanilla's body run (no old hurt, or the
     * old hurt's own super call coming back).
     */
    public static Boolean finalHurtServer(class_1297 entity, class_3218 level, class_1282 source, float amount) {
        if (!(entity instanceof Legacy legacy)) {
            return null;
        }
        Set<Object> in = IN_FINAL.get();
        if (!in.add(entity)) {
            return null;
        }
        Guard.cbPush(entity, new Object[] {level, source, amount});
        try {
            return legacy.method_5643(source, amount);
        } finally {
            Guard.cbPop();
            in.remove(entity);
        }
    }

    /** from the mixin on hurtOrSimulate, on a client level: the mod class's old hurt, or null for hurtClient */
    public static Boolean clientHurt(class_1297 entity, class_1282 source, float amount) {
        if (entity.method_37908() instanceof class_3218 || !(entity instanceof Legacy legacy)) {
            return null;
        }
        return legacy.method_5643(source, amount);
    }

    /**
     * From the mixin on isInvulnerableToBase: a non-living mod class's old isInvulnerableTo answers what 1.21.1
     * asked through it (null: the base test runs). A LivingEntity's is hooked to its 1.21.2 isInvulnerableTo.
     */
    public static Boolean baseInvulnerable(class_1297 entity, class_1282 source) {
        if (entity instanceof class_1309 || !(entity instanceof LegacyInvulnerable legacy)) {
            return null;
        }
        Set<Object> in = IN_BASE.get();
        if (!in.add(entity)) {
            return null;
        }
        try {
            return legacy.method_5679(source);
        } finally {
            in.remove(entity);
        }
    }

    // ------------------------------------------------------------------------------------------ helpers

    /** the ServerLevel vanilla passed to the hooked call on record for the entity, else its own, else null */
    private static class_3218 frameLevel(class_1297 entity) {
        if (Guard.cbArgOrL(entity, 0, null) instanceof class_3218 sl) {
            return sl;
        }
        return entity.method_37908() instanceof class_3218 sl ? sl : null;
    }

    /**
     * 1.21.1 isInvulnerableTo(source) of the vanilla classes on a client level, where no ServerLevel exists:
     * LivingEntity's enchantment immunity was asked only on a ServerLevel, so Entity's base test; Player's
     * damage game rules never held on a client (below); Ghast's, Breeze's and Warden's tests read the entity alone.
     */
    public static boolean invulnerableOnClient(class_1309 living, class_1282 source) {
        if (living instanceof class_1571 ghast) {
            // (isInvulnerable && !bypasses) || (!isReflectedFireball(source) && super)
            if (ghast.method_5655() && !source.method_48789(class_8103.field_42242)) {
                return true;
            }
            boolean reflected = source.method_5526() instanceof class_1674 && source.method_5529() instanceof class_1657;
            return !reflected && livingOnClient(living, source);
        }
        if (living instanceof class_8949) {
            return source.method_5529() instanceof class_8949 || livingOnClient(living, source);
        }
        if (living instanceof class_7260 warden) {
            if ((warden.method_41328(class_4050.field_38100) || warden.method_41328(class_4050.field_38099))
                    && !source.method_48789(class_8103.field_42242)) {
                return true;
            }
            return livingOnClient(living, source);
        }
        // Player's added tests -- drowning / fall / fire / freezing damage while its game rule is off -- read the
        // client level's own GameRules in 1.21.1: ClientLevelData's new GameRules(), never updated from the
        // server, where all four rules default to true; so on a client they never held, and Player answers
        // LivingEntity's test
        return livingOnClient(living, source);
    }

    private static boolean livingOnClient(class_1309 living, class_1282 source) {
        return ((CbEntityHurtInvoker) living).cb$isInvulnerableToBase(source);
    }

    private CbEntityHurt() {
    }
}
