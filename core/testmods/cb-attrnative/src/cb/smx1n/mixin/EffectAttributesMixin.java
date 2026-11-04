package cb.smx1n.mixin;

import net.minecraft.class_1322;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Native 1.21 control: Speed gains and loses a modifier by its native ResourceLocation id. */
@Mixin(net.minecraft.class_1291.class)
public abstract class EffectAttributesMixin {
    private static final class_2960 CBSMX1N_ID = class_2960.method_60655("minecraft", "6c1f9a52-3f0e-4c1e-9d55-0c2b5e0a1b01");

    @Inject(method = "method_5555(Lnet/minecraft/class_5131;I)V", at = @At("RETURN"), remap = false)
    private void cbsmx1n$add(net.minecraft.class_5131 attributes, int amplifier, CallbackInfo ci) {
        if ((Object) this == net.minecraft.class_1294.field_5904.comp_349()) {
            net.minecraft.class_1324 speed = attributes.method_45329(net.minecraft.class_5134.field_23719);
            if (speed != null && speed.method_6199(CBSMX1N_ID) == null) {
                speed.method_26837(new class_1322(CBSMX1N_ID, 0.01, class_1322.class_1323.field_6328));
            }
        }
    }

    @Inject(method = "method_5562(Lnet/minecraft/class_5131;)V", at = @At("RETURN"), remap = false)
    private void cbsmx1n$remove(net.minecraft.class_5131 attributes, CallbackInfo ci) {
        if ((Object) this == net.minecraft.class_1294.field_5904.comp_349()) {
            net.minecraft.class_1324 speed = attributes.method_45329(net.minecraft.class_5134.field_23719);
            if (speed != null) {
                speed.method_6200(CBSMX1N_ID);
            }
        }
    }
}
