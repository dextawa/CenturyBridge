package cb.smx6.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.20.6 FollowOwnerGoal's private fields (1.21: given back by the step layer) */
@Mixin(net.minecraft.class_1350.class)
public interface FollowOwnerAccess {
    @Accessor("field_6445")
    net.minecraft.class_4538 cb$level();

    @Accessor("field_21078")
    boolean cb$canFly();

    @Accessor("field_30207")
    static int cb$maxHorizontal() {
        throw new AssertionError();
    }
}
