package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1792;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_21_4.CbPending;

/** Item half of the pending-key repair (see AbstractBlockCtorBridge). */
@Mixin(class_1792.class)
public abstract class ItemCtorBridge {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1792$class_1793;method_63689()Ljava/lang/String;"))
    private String cb$translationKey(class_1792.class_1793 settings) {
        CbPending.ensureItemKey(this, settings);
        return ((Class1793Accessor) settings).cbTranslationKey();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1792$class_1793;method_63691()Lnet/minecraft/class_2960;"))
    private class_2960 cb$modelId(class_1792.class_1793 settings) {
        CbPending.ensureItemKey(this, settings);
        return ((Class1793Accessor) settings).cbModelId();
    }
}
