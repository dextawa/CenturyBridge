package cb.s203.mixin;

import cb.s203.S203TestMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.2 Blocks.leaves(SoundType) (private, answers LeavesBlock): counts the leaves blocks it makes */
@Mixin(net.minecraft.class_2246.class)
public abstract class BlocksLeavesMixin {

    @Inject(method = "method_26106", at = @At("RETURN"))
    private static void cbs203$leaves(net.minecraft.class_2498 sound, CallbackInfoReturnable<net.minecraft.class_2397> cir) {
        S203TestMod.LEAVES.add(cir.getReturnValue());
    }
}
