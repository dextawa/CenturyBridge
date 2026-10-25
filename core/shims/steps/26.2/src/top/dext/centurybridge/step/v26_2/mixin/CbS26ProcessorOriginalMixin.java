package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26Processors;

/**
 * StructureTemplate.processBlockInfos hands each processor the original block info's position (26.2) where 26.1.2
 * handed it the info (javap of 26.2: blockInfo, the first StructureBlockInfo local, stored once per template block; its
 * pos field is the processors' original-position argument). The info is recorded where it is stored, for a 26.1.2
 * processor (rt/CbS26Processors.original); the stored value and every 26.2 instruction stay as they are, so a 26.2
 * mod's mixins on the method still apply.
 */
@Mixin(StructureTemplate.class)
public abstract class CbS26ProcessorOriginalMixin {

    @ModifyVariable(method = "processBlockInfos", at = @At("STORE"), ordinal = 0, allow = 1)
    private static StructureTemplate.StructureBlockInfo cb$original(StructureTemplate.StructureBlockInfo info) {
        CbS26Processors.recordOriginal(info);
        return info;
    }

    @Inject(method = "processBlockInfos", at = @At("RETURN"))
    private static void cb$done(CallbackInfoReturnable<?> cir) {
        CbS26Processors.clearOriginal();
    }
}
