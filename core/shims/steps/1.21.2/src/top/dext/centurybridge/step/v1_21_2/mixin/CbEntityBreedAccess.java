package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** BreedGoal's protected 1.21.2 level (a ServerLevel), read by CbEntityLiving. */
@Mixin(net.minecraft.class_1341.class)
public interface CbEntityBreedAccess {

    @Accessor("field_6405")
    net.minecraft.class_3218 cb$level();
}
