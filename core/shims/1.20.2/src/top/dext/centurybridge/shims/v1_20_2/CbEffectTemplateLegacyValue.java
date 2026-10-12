package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1322;
import net.minecraft.class_8630;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbEffects;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMobEffectOverride;

/**
 * An effect template's create(amplifier) for an effect whose 1.20.1 value differs
 * (CbLegacyMobEffectOverride: AttackDamageMobEffect's multiplier, a converted
 * getAttributeModifierValue override): the modifier 1.20.1 added, worth the effect's
 * getAttributeModifierValue. Every 1.20.2 consumer of templates -- applying the effect, the potion
 * tooltip -- then sees 1.20.1's value; other effects are left to 1.20.2.
 */
@Mixin(targets = "net.minecraft.class_1291$class_8634")
public abstract class CbEffectTemplateLegacyValue {

    @Shadow
    @Final
    class_1291 field_45104;

    @Inject(method = "method_52522", at = @At("HEAD"), cancellable = true)
    private void cb$legacyValue(int amplifier, CallbackInfoReturnable<class_1322> cir) {
        if (this.field_45104 instanceof CbLegacyMobEffectOverride) {
            class_1322 base = CbEffects.baseModifier(this.field_45104, (class_8630) (Object) this);
            cir.setReturnValue(CbEffects.legacyModifier(this.field_45104, base, amplifier));
        }
    }
}
