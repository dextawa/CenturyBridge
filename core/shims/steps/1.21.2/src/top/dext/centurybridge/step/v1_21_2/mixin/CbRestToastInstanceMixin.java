package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_368;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestToasts;

/**
 * The toast manager's visibility read (ToastInstance.update): a 1.21.1 toast's answer from its own render
 * where it gave one (CbRestToasts), otherwise the toast's getWantedVisibility as before.
 */
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.class_374$class_375")
public abstract class CbRestToastInstanceMixin {

    @Redirect(method = "method_61995", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_368;method_61988()Lnet/minecraft/class_368$class_369;"))
    private class_368.class_369 cb$legacyVisibility(class_368 toast) {
        return CbRestToasts.managerVisibility(toast);
    }
}
