package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1510;
import net.minecraft.class_1512;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractDragonPhaseInstance's protected dragon: the level a 1.21.1 doServerTick() ran on (CbEntityLevels.dragonLevel). */
@Mixin(class_1512.class)
public interface CbEntityDragonPhaseAccess {

    @Accessor("field_7036")
    class_1510 cb$dragon();
}
