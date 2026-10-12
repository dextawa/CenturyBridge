package cb.mixintest.mixin;

import cb.mixintest.T;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client only (not booted on a server): EditBox.moveCursorTo(int) takes a selection flag in 1.20.2
 * (step 1 override hook, argument map [0]); the handler taking the cursor gets a 1.20.2-shaped one.
 */
@Mixin(net.minecraft.class_342.class)
public abstract class TextFieldMixin {
    @Inject(method = "moveCursorTo", at = @At("HEAD"))
    private void cbtest$onMoveCursor(int cursor, CallbackInfo ci) {
        T.ran("client-inject-hook", "cursor=" + cursor);
    }
}
