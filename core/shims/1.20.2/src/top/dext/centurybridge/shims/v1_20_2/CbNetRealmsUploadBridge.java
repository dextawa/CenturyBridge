package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_4427;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * RealmsUploadScreen: leaving it after an upload ran the callback 1.20.1's constructor took (from the
 * mod that opened the chain, or handed down by CbNetRealmsSelectFileBridge); 1.20.2 always goes to a
 * new configure-world screen, as vanilla's own 1.20.1 callback did. A screen with no 1.20.1 callback
 * is left as it is.
 */
@Mixin(class_4427.class)
public abstract class CbNetRealmsUploadBridge implements CbNetRealmsAccess.Callback {

    @Unique
    private Runnable cb$callback;

    @Override
    public void cb$setLegacyCallback(Runnable callback) {
        this.cb$callback = callback;
    }

    @Override
    public Runnable cb$legacyCallback() {
        return cb$callback;
    }

    /** onBack(): 1.20.1's body was callback.run() */
    @Inject(method = "method_21525", at = @At("HEAD"), cancellable = true)
    private void cb$legacyBack(CallbackInfo ci) {
        if (cb$callback != null) {
            cb$callback.run();
            ci.cancel();
        }
    }
}
