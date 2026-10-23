package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1799;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityTake;

/**
 * 1.21.1 EntitySelector.MobCanWearArmorEntitySelector (net/minecraft/class_1301$class_1302), which 1.21.2 removed
 * with canTakeItem: alive, a LivingEntity, and canTakeItem(stack) (CbEntityTake, the entity's own 1.21.1 override
 * included).
 */
public class MobCanWearArmorEntitySelector implements Predicate<class_1297> {

    /** the stack */
    public final class_1799 field_6158;

    public MobCanWearArmorEntitySelector(class_1799 stack) {
        this.field_6158 = stack;
    }

    /** test(entity) */
    public boolean method_5916(class_1297 entity) {
        return CbEntityTake.selectorTest(entity, this.field_6158);
    }

    @Override
    public boolean test(class_1297 entity) {
        return method_5916(entity);
    }
}
