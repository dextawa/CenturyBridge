package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_437;
import net.minecraft.class_4410;
import net.minecraft.class_4416;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * RealmsSelectFileToUploadScreen: 1.20.1's took the after-upload callback in its constructor and
 * handed it to the upload screen it opened; its own (given by 1.20.1's constructor, see
 * CbNetRealms.cbLegacyCallback), else the one its reset-world screen was given -- which is what that
 * screen passed it in 1.20.1.
 */
@Mixin(class_4416.class)
public abstract class CbNetRealmsSelectFileBridge implements CbNetRealmsAccess.Callback {

    @Shadow
    @Final
    private class_4410 field_20050;

    @Unique
    private Runnable cb$callback;

    @Override
    public void cb$setLegacyCallback(Runnable callback) {
        this.cb$callback = callback;
    }

    @Override
    public Runnable cb$legacyCallback() {
        if (cb$callback != null) {
            return cb$callback;
        }
        return field_20050 instanceof CbNetRealmsAccess.Callback reset ? reset.cb$legacyCallback() : null;
    }

    /** the upload screen this one opens gets the callback, as 1.20.1's constructor gave it */
    @ModifyArg(method = "method_21401", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_310;method_1507(Lnet/minecraft/class_437;)V"), require = 1)
    private class_437 cb$handDownCallback(class_437 screen) {
        Runnable callback = cb$legacyCallback();
        if (callback != null && screen instanceof CbNetRealmsAccess.Callback next && next.cb$legacyCallback() == null) {
            next.cb$setLegacyCallback(callback);
        }
        return screen;
    }
}
