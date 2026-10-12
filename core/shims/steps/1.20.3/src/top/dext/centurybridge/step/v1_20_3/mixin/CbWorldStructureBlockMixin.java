package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2515;
import net.minecraft.class_2633;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldLegacyStructureBlock;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStructureBlockV;

/**
 * A LOAD-mode structure block's redstone trigger: 1.20.2 called loadStructure(level, false) on its block entity,
 * 1.20.3 calls placeStructure(level). A block entity class converted from 1.20.2 is asked the 1.20.2 way.
 */
@Mixin(class_2515.class)
public abstract class CbWorldStructureBlockMixin {

    @Redirect(method = "method_10703", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2633;method_54878(Lnet/minecraft/class_3218;)V"))
    private void cb$triggerLoad(class_2633 be, class_3218 level) {
        if (be instanceof CbWorldLegacyStructureBlock) {
            ((CbWorldStructureBlockV) be).method_11368(level, false);
        } else {
            be.method_54878(level);
        }
    }
}
