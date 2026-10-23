package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderArmorModels;

/**
 * EquipmentModel.Layer.getTextureLocation(layerType): a layer synthesized for a 1.21.1 armour material layer answers
 * 1.21.1's texture, textures/models/armor/&lt;asset&gt;_layer_&lt;1|2&gt;&lt;suffix&gt;.png (CbRenderArmorModels).
 */
@Mixin(net.minecraft.class_10186.class_10189.class)
public abstract class CbRenderEquipmentLayerMixin {

    @Inject(method = "method_64007", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTexture(net.minecraft.class_10186.class_10190 type, CallbackInfoReturnable<net.minecraft.class_2960> cir) {
        net.minecraft.class_2960 legacy = CbRenderArmorModels.texture((net.minecraft.class_10186.class_10189) (Object) this);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }
}
