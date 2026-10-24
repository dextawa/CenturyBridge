package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.CappedProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26Processors;

/**
 * CappedProcessor.finalizeProcessing hands its delegate the original block info's position (26.2) where 26.1.2 handed
 * it the info: its one StructureBlockInfo.pos() call (javap of 26.2, the argument of the delegate's processBlock) is
 * recorded with its info for a 26.1.2 delegate (rt/CbS26Processors.original).
 */
@Mixin(CappedProcessor.class)
public abstract class CbS26CappedOriginalMixin {

    @Redirect(method = "finalizeProcessing", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;pos()Lnet/minecraft/core/BlockPos;"))
    private BlockPos cb$original(StructureTemplate.StructureBlockInfo info) {
        return CbS26Processors.recordOriginal(info);
    }

    @Inject(method = "finalizeProcessing", at = @At("RETURN"))
    private void cb$done(CallbackInfoReturnable<?> cir) {
        CbS26Processors.clearOriginal();
    }
}
