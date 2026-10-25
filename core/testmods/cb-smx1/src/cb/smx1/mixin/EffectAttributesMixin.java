package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.1's MobEffect.addAttributeModifiers(entity, attributes, amplifier) / removeAttributeModifiers(entity,
 * attributes, amplifier) (method_5555 / method_5562): for Strength, a modifier "cbsmx1-v" of the mod's own goes onto
 * the attack damage with the effect and comes off with it; every call is counted.
 */
@Mixin(net.minecraft.class_1291.class)
public abstract class EffectAttributesMixin {
    @Inject(method = "method_5555(Lnet/minecraft/class_1309;Lnet/minecraft/class_5131;I)V", at = @At("RETURN"), remap = false)
    private void cbsmx1$add(net.minecraft.class_1309 entity, net.minecraft.class_5131 attributes, int amplifier, CallbackInfo ci) {
        Driver.count("effect-add-all");
        if ((Object) this == net.minecraft.class_1294.field_5910) {
            net.minecraft.class_1324 damage = attributes.method_26842(net.minecraft.class_5134.field_23721);
            if (damage != null && damage.method_6199(Driver.EFFECT_MODIFIER) == null) {
                damage.method_26837(new net.minecraft.class_1322(Driver.EFFECT_MODIFIER, "cbsmx1-v", 5.0,
                    net.minecraft.class_1322.class_1323.field_6328));
            }
        }
    }

    @Inject(method = "method_5562(Lnet/minecraft/class_1309;Lnet/minecraft/class_5131;I)V", at = @At("RETURN"), remap = false)
    private void cbsmx1$remove(net.minecraft.class_1309 entity, net.minecraft.class_5131 attributes, int amplifier, CallbackInfo ci) {
        Driver.count("effect-remove-all");
        if ((Object) this == net.minecraft.class_1294.field_5910) {
            net.minecraft.class_1324 damage = attributes.method_26842(net.minecraft.class_5134.field_23721);
            if (damage != null) {
                damage.method_6200(Driver.EFFECT_MODIFIER);
            }
        }
    }
}
