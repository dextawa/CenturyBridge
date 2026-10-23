package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientGui;

/**
 * ChatScreen.handleChatInput(message, addToHistory): 1.20.4's answered whether the input was handled, and
 * keyPressed closed the screen only then; 1.20.5's answers nothing and keyPressed always closes it. A 1.20.4
 * chat screen class's own handleChatInput (rt/CbClientGui.CbLegacyChatInput) is the one Enter reaches, and
 * when it answers false the screen stays open, as in 1.20.4.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_408.class)
public abstract class CbClientChatScreenMixin {

    @Unique
    private boolean cb$unhandled;

    @Redirect(method = "method_25404", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_408;method_44056(Ljava/lang/String;Z)V"))
    private void cb$handleChatInput(net.minecraft.class_408 screen, String message, boolean addToHistory) {
        if (screen instanceof CbClientGui.CbLegacyChatInput legacy) {
            this.cb$unhandled = !legacy.method_44056(message, addToHistory);
        } else {
            screen.method_44056(message, addToHistory);
        }
    }

    @Inject(method = "method_25404", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_408;method_44056(Ljava/lang/String;Z)V",
        shift = At.Shift.AFTER), cancellable = true)
    private void cb$keepOpen(int key, int scan, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$unhandled) {
            this.cb$unhandled = false;
            cir.setReturnValue(true);
        }
    }
}
