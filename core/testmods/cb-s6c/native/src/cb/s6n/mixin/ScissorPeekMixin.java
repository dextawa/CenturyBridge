package cb.s6n.mixin;

import cb.s6n.Counter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.6: @Inject(HEAD) on GuiGraphics.ScissorStack.peek(), counted in a system property */
@Mixin(targets = "net.minecraft.class_332$class_8214")
public class ScissorPeekMixin {
    @Inject(method = "method_70863()Lnet/minecraft/class_8030;", at = @At("HEAD"))
    private void cbS6n$peek(CallbackInfoReturnable<net.minecraft.class_8030> cir) {
        Counter.bump("cb.s6n.scissorPeek");
    }
}
