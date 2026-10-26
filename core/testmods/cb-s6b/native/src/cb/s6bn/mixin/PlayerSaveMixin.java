package cb.s6bn.mixin;

import cb.s6bn.Counter;
import net.minecraft.class_11368;
import net.minecraft.class_11372;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.6: HEAD and TAIL of ServerPlayer's save / load, the methods the layer's recipe book route sits in */
@Mixin(class_3222.class)
public class PlayerSaveMixin {
    @Inject(method = "method_5652(Lnet/minecraft/class_11372;)V", at = @At("HEAD"))
    private void cbS6bn$saveHead(class_11372 out, CallbackInfo ci) {
        Counter.bump("cb.s6bn.playerSave.head");
    }

    @Inject(method = "method_5652(Lnet/minecraft/class_11372;)V", at = @At("TAIL"))
    private void cbS6bn$saveTail(class_11372 out, CallbackInfo ci) {
        Counter.bump("cb.s6bn.playerSave.tail");
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_11368;)V", at = @At("HEAD"))
    private void cbS6bn$loadHead(class_11368 in, CallbackInfo ci) {
        Counter.bump("cb.s6bn.playerLoad.head");
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_11368;)V", at = @At("TAIL"))
    private void cbS6bn$loadTail(class_11368 in, CallbackInfo ci) {
        Counter.bump("cb.s6bn.playerLoad.tail");
    }
}
