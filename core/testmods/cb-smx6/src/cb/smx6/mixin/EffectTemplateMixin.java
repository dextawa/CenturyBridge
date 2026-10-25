package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod on MobEffect.AttributeTemplate.create(name, amplifier) (class_1291$class_8634.method_52522): records what
 * vanilla asked with, and doubles the modifier's amount while Driver.doubleEffect is set.
 */
@Mixin(targets = "net.minecraft.class_1291$class_8634")
public abstract class EffectTemplateMixin {
    @Inject(method = "method_52522(Ljava/lang/String;I)Lnet/minecraft/class_1322;", at = @At("HEAD"), remap = false)
    private void cbsmx6$asked(String name, int amplifier, CallbackInfoReturnable<net.minecraft.class_1322> cir) {
        Driver.templateAsked++;
        Driver.templateName = name;
        Driver.templateAmplifier = amplifier;
    }

    @Inject(method = "method_52522(Ljava/lang/String;I)Lnet/minecraft/class_1322;", at = @At("RETURN"), cancellable = true, remap = false)
    private void cbsmx6$double(String name, int amplifier, CallbackInfoReturnable<net.minecraft.class_1322> cir) {
        if (Driver.doubleEffect) {
            net.minecraft.class_1322 m = cir.getReturnValue();
            cir.setReturnValue(new net.minecraft.class_1322(m.comp_2447(), m.comp_2448(), m.comp_2449() * 2.0, m.comp_2450()));
        }
    }
}
