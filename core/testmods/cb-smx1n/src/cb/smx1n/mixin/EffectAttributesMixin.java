package cb.smx1n.mixin;

import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.2's MobEffect.addAttributeModifiers(attributes, amplifier) / removeAttributeModifiers(attributes), what 1.20.2's
 * living entity calls: for Speed, a modifier "cbsmx1-native" of its own goes onto the movement speed with the effect
 * and comes off with it.
 */
@Mixin(net.minecraft.class_1291.class)
public abstract class EffectAttributesMixin {
    private static final UUID CBSMX1N_ID = UUID.fromString("6c1f9a52-3f0e-4c1e-9d55-0c2b5e0a1b01");

    @Inject(method = "method_5555(Lnet/minecraft/class_5131;I)V", at = @At("RETURN"), remap = false)
    private void cbsmx1n$add(net.minecraft.class_5131 attributes, int amplifier, CallbackInfo ci) {
        if ((Object) this == net.minecraft.class_1294.field_5904) {
            net.minecraft.class_1324 speed = attributes.method_26842(net.minecraft.class_5134.field_23719);
            if (speed != null && speed.method_6199(CBSMX1N_ID) == null) {
                speed.method_26837(new net.minecraft.class_1322(CBSMX1N_ID, "cbsmx1-native", 0.01,
                    net.minecraft.class_1322.class_1323.field_6328));
            }
        }
    }

    @Inject(method = "method_5562(Lnet/minecraft/class_5131;)V", at = @At("RETURN"), remap = false)
    private void cbsmx1n$remove(net.minecraft.class_5131 attributes, CallbackInfo ci) {
        if ((Object) this == net.minecraft.class_1294.field_5904) {
            net.minecraft.class_1324 speed = attributes.method_26842(net.minecraft.class_5134.field_23719);
            if (speed != null) {
                speed.method_6200(CBSMX1N_ID);
            }
        }
    }
}
