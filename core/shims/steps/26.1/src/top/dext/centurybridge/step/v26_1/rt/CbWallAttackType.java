package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.breeze.Breeze;

/**
 * 1.21.11's LivingEntity.canAttackType(EntityType) (class_1309.method_5973), restored on LivingEntity and the classes
 * that overrode it (mixin/CbWall*AttackTypeMixin), as an interface LivingEntity implements so the layer's own mixins
 * can ask it.
 * <p>
 * 1.21.11's TargetingConditions.test asked canAttack(target) and then canAttackType(target's type); 26.1 moved the
 * overrides' type checks into canAttack itself (Mob: GHAST, IronGolem: PLAYER if player-made / CREEPER, Breeze:
 * PLAYER or IRON_GOLEM; javap of both). 26.1's canAttack runs unchanged -- a native 26.1 mod's mixin on it or on the
 * type checks in it still acts. Its answer is replaced only where this entity's canAttackType answers otherwise than
 * 1.21.11's vanilla body for its class ({@link #unmixed}): a 1.21.11 mod mixed canAttackType, or a 1.21.11 subclass
 * overrides it. Then the answer is 1.21.11's: LivingEntity.canAttack(target) and canAttackType(type).
 */
public interface CbWallAttackType {

    boolean canAttackType(EntityType<?> type);

    /** LivingEntity.canAttack(target) called non-virtually: the part of 26.1's canAttack that is no type check */
    interface Base {
        boolean cb$livingCanAttack(LivingEntity target);
    }

    /**
     * What the restored canAttackType answers unmixed for this entity's vanilla class: the 1.21.11 bodies of
     * Phantom / Breeze / IronGolem / Mob / LivingEntity.canAttackType, as the restored members hold them.
     */
    static boolean unmixed(LivingEntity self, EntityType<?> type) {
        if (self instanceof Phantom) {
            return true;
        }
        if (self instanceof Breeze) {
            return type == EntityType.PLAYER || type == EntityType.IRON_GOLEM;
        }
        if (self instanceof IronGolem golem) {
            if (golem.isPlayerCreated() && type == EntityType.PLAYER) {
                return false;
            }
            return type != EntityType.CREEPER && type != EntityType.GHAST;
        }
        if (self instanceof Mob) {
            return type != EntityType.GHAST;
        }
        return true;
    }

    /** whether self's canAttackType answers for this type otherwise than unmixed, and so decides (1.21.11) */
    static boolean mixed(LivingEntity self, EntityType<?> type, boolean answer) {
        return answer != unmixed(self, type);
    }

    /**
     * The answer of a mob's canAttack(target): 26.1's w, unless the mob's canAttackType is mixed for the target's type
     * -- then 1.21.11's, LivingEntity.canAttack(target) and canAttackType(type).
     */
    static boolean canAttack(Mob self, LivingEntity target, boolean w) {
        EntityType<?> type = target.getType();
        boolean answer = ((CbWallAttackType) self).canAttackType(type);
        if (!mixed(self, type, answer)) {
            return w;
        }
        return answer && ((Base) self).cb$livingCanAttack(target);
    }
}
