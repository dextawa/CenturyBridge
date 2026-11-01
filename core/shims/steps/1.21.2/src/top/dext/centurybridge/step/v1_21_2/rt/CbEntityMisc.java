package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_4466;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1581;
import net.minecraft.class_1688;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_8153;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityFieldAccess;

/**
 * Smaller pieces of 1.21.1's Entity API that 1.21.2 moved or reshaped.
 *
 * <ul>
 * <li>getBoundingBoxForCulling(): 1.21.1 asked the entity (Entity: its bounding box; LivingEntity: inflated by 0.5
 *     with a dragon head on; AbstractMinecart: inflated by |display offset| / 16 with a custom display; Sniffer:
 *     LivingEntity's inflated by 0.6; Illusioner: inflated by 3 horizontally); 1.21.2 asks the renderer. A call answers
 *     the entity class's own override, else 1.21.1's vanilla body; the renderer asks a mod class's own override
 *     (CbEntityCullingMixin).</li>
 * <li>setOnGroundWithMovement(onGround, movement): 1.21.2 also sets horizontalCollision; 1.21.1's left it, so the call
 *     passes the entity's own.</li>
 * <li>isAlliedTo(entity): final in 1.21.2 (this, or either side's considersEntityAsAlly); a mod class's own 1.21.1
 *     override, renamed, answers it (CbEntityMiscMixin), its super call 1.21.2's isAlliedTo of the vanilla classes.</li>
 * <li>wasOnFire: private in 1.21.2, the same field.</li>
 * </ul>
 */
public final class CbEntityMisc {

    /** what CbEntityMiscMixin gives Entity */
    public interface Fields {
        boolean cb$noCulling();
    }

    /** a mod class's own 1.21.1 getBoundingBoxForCulling (adapter hook) */
    public interface LegacyCulling {
        class_238 method_5830();
    }

    /** a mod class's own 1.21.1 isAlliedTo, renamed (1.21.2's is final) */
    public interface LegacyAllied {
        boolean cb$method_5722(class_1297 other);
    }

    // ------------------------------------------------------------------------------------------ culling box

    public static class_238 getBoundingBoxForCulling(class_1297 entity) {
        if (entity instanceof LegacyCulling legacy) {
            return legacy.method_5830();
        }
        return vanillaCulling(entity);
    }

    /** 1.21.1's body of the vanilla class the entity is */
    public static class_238 vanillaCulling(class_1297 entity) {
        if (entity instanceof class_1581) {
            return entity.method_5829().method_1009(3.0, 0.0, 3.0);
        }
        if (entity instanceof class_8153 sniffer) {
            return livingCulling(sniffer).method_1014(0.6000000238418579);
        }
        if (entity instanceof class_1309 living) {
            return livingCulling(living);
        }
        if (entity instanceof class_1688 cart) {
            class_238 box = cart.method_5829();
            return cart.method_7510() ? box.method_1014((double) Math.abs(cart.method_7514()) / 16.0) : box;
        }
        return entity.method_5829();
    }

    private static class_238 livingCulling(class_1309 living) {
        if (living.method_6118(class_1304.field_6169).method_31574(class_1802.field_8712)) {
            return living.method_5829().method_1009(0.5, 0.5, 0.5);
        }
        return living.method_5829();
    }

    public static class_238 superGetBoundingBoxForCulling(class_1297 entity) {
        return entity.method_5829();
    }

    public static class_238 superGetBoundingBoxForCulling(class_1309 entity) {
        return livingCulling(entity);
    }

    public static class_238 superGetBoundingBoxForCulling(class_1688 entity) {
        return vanillaCulling(entity);
    }

    public static class_238 superGetBoundingBoxForCulling(class_8153 entity) {
        return vanillaCulling(entity);
    }

    public static class_238 superGetBoundingBoxForCulling(class_1581 entity) {
        return vanillaCulling(entity);
    }

    /** from the renderer: a mod class's own 1.21.1 culling box, or null for the renderer's */
    public static class_238 cullingBox(class_1297 entity) {
        return entity instanceof LegacyCulling legacy ? legacy.method_5830() : null;
    }

    // ------------------------------------------------------------------------------------------ ground

    /** 1.21.1 setOnGroundWithMovement(onGround, movement): horizontalCollision stays the entity's */
    public static void setOnGroundWithMovement(class_1297 entity, boolean onGround, class_243 movement) {
        entity.method_60607(onGround, entity.field_5976, movement);
    }

    /** frameDefaults: a mod's super call outside a hooked call passes the entity's own horizontalCollision */
    public static boolean horizontalCollision(class_1297 entity, boolean onGround, class_243 movement) {
        return entity.field_5976;
    }

    // ------------------------------------------------------------------------------------------ allies

    private static final ThreadLocal<Set<Object>> IN_ALLIED =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    /** from the mixin on the final isAlliedTo: the mod class's own 1.21.1 override, or null for 1.21.2's body */
    public static Boolean allied(class_1297 entity, class_1297 other) {
        if (!(entity instanceof LegacyAllied legacy)) {
            return null;
        }
        Set<Object> in = IN_ALLIED.get();
        if (!in.add(entity)) {
            return null;
        }
        try {
            return legacy.cb$method_5722(other);
        } finally {
            in.remove(entity);
        }
    }

    /** a mod class's super.isAlliedTo(other): 1.21.2's isAlliedTo, the vanilla classes' answer */
    public static boolean superIsAlliedTo(class_1297 entity, class_1297 other) {
        Set<Object> in = IN_ALLIED.get();
        boolean added = in.add(entity);
        try {
            return entity.method_5722(other);
        } finally {
            if (added) {
                in.remove(entity);
            }
        }
    }

    public static boolean superIsAlliedTo(net.minecraft.class_1321 entity, class_1297 other) {
        return superIsAlliedTo((class_1297) entity, other);
    }

    public static boolean superIsAlliedTo(net.minecraft.class_1543 entity, class_1297 other) {
        return superIsAlliedTo((class_1297) entity, other);
    }

    public static boolean superIsAlliedTo(net.minecraft.class_1564 entity, class_1297 other) {
        return superIsAlliedTo((class_1297) entity, other);
    }

    // ------------------------------------------------------------------------------------------ canAttack

    /** a mod class's own 1.21.1 canAttack(target, conditions) (adapter hook) */
    public interface LegacyCanAttack {
        boolean method_18391(class_1309 target, net.minecraft.class_4051 conditions);
    }

    /** 1.21.1 LivingEntity.canAttack(target, conditions), called: the override, else conditions.test(this, target) */
    public static boolean canAttack(class_1309 entity, class_1309 target, net.minecraft.class_4051 conditions) {
        if (entity instanceof LegacyCanAttack legacy) {
            return legacy.method_18391(target, conditions);
        }
        return superCanAttack(entity, target, conditions);
    }

    /** 1.21.1 LivingEntity's body: conditions.test(this, target), 1.21.1's test on any level (CbEntityTargeting) */
    public static boolean superCanAttack(class_1309 entity, class_1309 target, net.minecraft.class_4051 conditions) {
        return CbEntityTargeting.test(conditions, entity, target);
    }

    /**
     * smixin12, Kilt E8c (wiring mixinExpressionReturns, Bee.isHiveValid): the block entity 1.21.1's isHiveValid held
     * when it evaluated {@code blockEntity.getType() == BEEHIVE} -- hivePos set, not too far (the bee's own
     * isTooFarAway, as both releases' bodies call it), the level's block entity there non-null -- else null, where
     * 1.21.1 returned false before reaching the expression. Read-only: getBlockEntity is what 1.21.2's
     * getBlockEntity(pos, BEEHIVE) calls first.
     */
    public static class_2586 hiveCheckBlockEntity(class_4466 bee) {
        class_2338 pos = bee.method_23884();
        if (pos == null || bee.method_23990(pos)) {
            return null;
        }
        return bee.method_37908().method_8321(pos);
    }

    // ------------------------------------------------------------------------------------------ fields

    public static boolean getWasOnFire(class_1297 entity) {
        return ((CbEntityFieldAccess) entity).cb$wasOnFire();
    }

    public static void setWasOnFire(class_1297 entity, boolean value) {
        ((CbEntityFieldAccess) entity).cb$setWasOnFire(value);
    }

    private CbEntityMisc() {
    }
}
