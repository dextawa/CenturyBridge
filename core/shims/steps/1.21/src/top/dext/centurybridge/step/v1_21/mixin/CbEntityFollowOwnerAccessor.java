package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** FollowOwnerGoal's animal (CbEntityRest.setFollowCanFly) */
@Mixin(net.minecraft.class_1350.class)
public interface CbEntityFollowOwnerAccessor {

    @Accessor("field_6448")
    net.minecraft.class_1321 cb$tamable();
}
