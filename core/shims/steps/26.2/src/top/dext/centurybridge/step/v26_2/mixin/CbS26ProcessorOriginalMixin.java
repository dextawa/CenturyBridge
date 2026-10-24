package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26Processors;

/**
 * StructureTemplate.processBlockInfos hands each processor the original block info's position (26.2) where 26.1.2
 * handed it the info: its second read of StructureBlockInfo.pos (javap of 26.2: the GETFIELD on the original info
 * right before StructureProcessor.processBlock; the first is the template info's before the transform) is recorded
 * with its info for a 26.1.2 processor (rt/CbS26Processors.original).
 */
@Mixin(StructureTemplate.class)
public abstract class CbS26ProcessorOriginalMixin {

    @Redirect(method = "processBlockInfos", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 1,
        target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;pos:Lnet/minecraft/core/BlockPos;"))
    private static BlockPos cb$original(StructureTemplate.StructureBlockInfo info) {
        return CbS26Processors.recordOriginal(info);
    }

    @Inject(method = "processBlockInfos", at = @At("RETURN"))
    private static void cb$done(CallbackInfoReturnable<?> cir) {
        CbS26Processors.clearOriginal();
    }
}
