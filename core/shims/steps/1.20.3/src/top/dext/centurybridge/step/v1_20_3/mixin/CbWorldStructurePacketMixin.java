package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2633;
import net.minecraft.class_3218;
import net.minecraft.class_3244;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldLegacyStructureBlock;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStructureBlockV;

/**
 * The structure block screen's LOAD_AREA request: 1.20.2 called loadStructure(level), which is
 * loadStructure(level, true), on the block entity; 1.20.3 calls placeStructureIfSameSize(level). A block entity
 * class converted from 1.20.2 is asked the 1.20.2 way.
 */
@Mixin(class_3244.class)
public abstract class CbWorldStructurePacketMixin {

    @Redirect(method = "method_12051", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2633;method_54874(Lnet/minecraft/class_3218;)Z"))
    private boolean cb$loadArea(class_2633 be, class_3218 level) {
        if (be instanceof CbWorldLegacyStructureBlock) {
            return ((CbWorldStructureBlockV) be).method_11368(level, true);
        }
        return be.method_54874(level);
    }
}
