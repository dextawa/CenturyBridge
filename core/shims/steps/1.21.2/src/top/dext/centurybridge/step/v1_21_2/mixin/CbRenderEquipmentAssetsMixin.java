package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderArmorModels;

/**
 * EquipmentAssetManager: an id it has no model json for answers the model synthesized for a 1.21.1 armour material
 * of that model id, when there is one (CbRenderArmorModels); the synthesized models are dropped on every reload.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10201.class)
public abstract class CbRenderEquipmentAssetsMixin {

    @Shadow
    @Final
    public static net.minecraft.class_10186 field_54189;

    @Inject(method = "method_64088", at = @At("TAIL"))
    private void cb$reload(java.util.Map<net.minecraft.class_2960, net.minecraft.class_10186> models, net.minecraft.class_3300 resources,
                           net.minecraft.class_3695 profiler, CallbackInfo ci) {
        CbRenderArmorModels.reload();
    }

    @Inject(method = "method_64087", at = @At("RETURN"), cancellable = true)
    private void cb$legacyModel(net.minecraft.class_2960 id, CallbackInfoReturnable<net.minecraft.class_10186> cir) {
        if (cir.getReturnValue() == field_54189) {
            net.minecraft.class_10186 legacy = CbRenderArmorModels.model(id);
            if (legacy != null) {
                cir.setReturnValue(legacy);
            }
        }
    }
}
