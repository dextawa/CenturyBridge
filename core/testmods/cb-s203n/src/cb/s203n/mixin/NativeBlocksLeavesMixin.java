package cb.s203n.mixin;

import cb.s203n.S203Native;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.20.3: Blocks.leaves(SoundType), answering Block */
@Mixin(net.minecraft.class_2246.class)
public abstract class NativeBlocksLeavesMixin {

    @Inject(method = "method_26106", at = @At("RETURN"))
    private static void cbs203n$leaves(net.minecraft.class_2498 sound, CallbackInfoReturnable<net.minecraft.class_2248> cir) {
        S203Native.LEAVES[0]++;
    }
}
