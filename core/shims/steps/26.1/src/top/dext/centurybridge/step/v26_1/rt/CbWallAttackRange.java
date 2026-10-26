package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.AttackRange;

/**
 * 1.21.11 LivingEntity.entityAttackRange() (class_1309.method_76693), restored on LivingEntity
 * (mixin/CbWallLivingEntityAttackRangeMixin).
 */
public interface CbWallAttackRange {

    /** 1.21.11 LivingEntity.entityAttackRange(): the attack range of the active item */
    AttackRange entityAttackRange();

    /**
     * What the restored entityAttackRange answers unmixed: 1.21.11's body, the active item's ATTACK_RANGE or the
     * entity's default -- the same as 26.1's getAttackRangeWith(active item) (javap of both).
     */
    static AttackRange unmixed(LivingEntity self) {
        AttackRange range = self.getActiveItem().get(DataComponents.ATTACK_RANGE);
        return range != null ? range : AttackRange.defaultFor(self);
    }
}
