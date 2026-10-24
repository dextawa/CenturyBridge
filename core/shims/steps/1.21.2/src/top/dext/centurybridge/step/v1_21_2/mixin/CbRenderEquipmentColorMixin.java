package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderArmorModels;

/**
 * EquipmentLayerRenderer: the tint of a layer synthesized for a 1.21.1 armour material layer is 1.21.1's for the stack
 * being drawn (CbRenderArmorModels.color), every other layer keeps 1.21.2's.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10197.class)
public abstract class CbRenderEquipmentColorMixin {

    @Inject(method = "method_64078", at = @At("HEAD"))
    private void cb$drawing(net.minecraft.class_10186.class_10190 type, net.minecraft.class_2960 model, net.minecraft.class_3879 armorModel,
                            net.minecraft.class_1799 stack, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light,
                            net.minecraft.class_2960 texture, CallbackInfo ci) {
        CbRenderArmorModels.drawing(stack);
    }

    @Inject(method = "method_64078", at = @At("RETURN"))
    private void cb$drawn(net.minecraft.class_10186.class_10190 type, net.minecraft.class_2960 model, net.minecraft.class_3879 armorModel,
                          net.minecraft.class_1799 stack, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light,
                          net.minecraft.class_2960 texture, CallbackInfo ci) {
        CbRenderArmorModels.drawing(null);
    }

    @Inject(method = "method_64076", at = @At("HEAD"), cancellable = true)
    private static void cb$legacyColor(net.minecraft.class_10186.class_10189 layer, int dyeColor, CallbackInfoReturnable<Integer> cir) {
        Integer legacy = CbRenderArmorModels.color(layer);
        if (legacy != null) {
            cir.setReturnValue(legacy);
        }
    }
}
