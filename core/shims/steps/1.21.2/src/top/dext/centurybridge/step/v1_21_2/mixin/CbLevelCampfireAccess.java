package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelBlockEntities;

/** CampfireBlockEntity's cooking arrays and markUpdated, for 1.21.1's placeFood body */
@Mixin(net.minecraft.class_3924.class)
public interface CbLevelCampfireAccess extends CbLevelBlockEntities.CampfireAccess {

    @Override
    @Accessor("field_17384")
    int[] cb$cookingProgress();

    @Override
    @Accessor("field_17385")
    int[] cb$cookingTime();

    @Override
    @Invoker("method_17510")
    void cb$markUpdated();
}
