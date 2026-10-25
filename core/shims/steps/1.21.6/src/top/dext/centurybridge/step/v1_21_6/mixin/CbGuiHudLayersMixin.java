package top.dext.centurybridge.step.v1_21_6.mixin;

/**
 * Fabric API 1.21.5 invoked HudLayerRegistrationCallback from Gui's constructor (InGameHudMixin.registerLayers, after
 * Gui built its layers); 1.21.6's Gui still is built once, by Minecraft, after mods initialized, so the 1.21.5 event is
 * invoked at the end of its constructor. Gui's own code is unchanged.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(net.minecraft.class_329.class)
public abstract class CbGuiHudLayersMixin {

    @org.spongepowered.asm.mixin.injection.Inject(method = "<init>(Lnet/minecraft/class_310;)V",
        at = @org.spongepowered.asm.mixin.injection.At("TAIL"))
    private void cb$hudLayerRegistration(net.minecraft.class_310 minecraft,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_6.rt.CbHudLayers.fire();
    }
}
