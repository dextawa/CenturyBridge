package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v26_2.CbPending;

/** Item half of the pending-key repair (see AbstractBlockCtorBridge). */
@Mixin(net.minecraft.world.item.Item.class)
public abstract class ItemCtorBridge {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/net.minecraft.world.item.Item$class_1793;effectiveDescriptionId()Ljava/lang/String;"))
    private String cb$translationKey(net.minecraft.world.item.Item.Properties settings) {
        CbPending.ensureItemKey(this, settings);
        return ((Class1793Accessor) settings).cbTranslationKey();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/net.minecraft.world.item.Item$class_1793;effectiveModel()Lnet/minecraft/net.minecraft.resources.Identifier;"))
    private net.minecraft.resources.Identifier cb$modelId(net.minecraft.world.item.Item.Properties settings) {
        CbPending.ensureItemKey(this, settings);
        return ((Class1793Accessor) settings).cbModelId();
    }
}
