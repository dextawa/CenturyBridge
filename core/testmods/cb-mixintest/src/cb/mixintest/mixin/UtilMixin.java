package cb.mixintest.mixin;

import cb.mixintest.T;
import java.nio.file.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.class_156.class)
public abstract class UtilMixin {
    /** safeReplaceOrMoveFile returns a boolean in 1.20.3: the handler's CallbackInfo becomes a CallbackInfoReturnable */
    @Inject(method = "safeReplaceOrMoveFile", at = @At("HEAD"))
    private static void cbtest$onReplace(Path current, Path latest, Path backup, boolean restore, CallbackInfo ci) {
        T.ran("inject-ci-to-cir", "latest=" + latest.getFileName());
    }
}
