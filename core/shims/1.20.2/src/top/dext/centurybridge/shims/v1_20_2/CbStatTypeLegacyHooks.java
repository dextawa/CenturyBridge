package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_3448;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbStatTypes;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbStatTypeLegacy} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_3448.class)
public abstract class CbStatTypeLegacyHooks {

    @Unique
    private class_2561 cb$legacyName;

    @Inject(method = "<init>(Lnet/minecraft/class_2378;Lnet/minecraft/class_2561;)V", at = @At("RETURN"))
    private void cb$claimLegacy(class_2378<?> registry, class_2561 name, CallbackInfo ci) {
        CbStatTypes.claim((class_3448<?>) (Object) this);
    }

    @Inject(method = "method_30739", at = @At("HEAD"), cancellable = true)
    private void cb$legacyDisplayName(CallbackInfoReturnable<class_2561> cir) {
        if (CbStatTypes.isLegacy((class_3448<?>) (Object) this)) {
            if (this.cb$legacyName == null) {
                this.cb$legacyName = class_2561.method_43471(((CbStatTypes.Legacy) (Object) this).method_14957());
            }
            cir.setReturnValue(this.cb$legacyName);
        }
    }

    @Shadow
    @Final
    private class_2561 field_26382;

    /** whether getTranslationKey(), asked once, answered what it answers unmixed (then 1.20.2's name stands) */
    @Unique
    private Boolean cb$keyUnmixed;

    /**
     * Every other stat type whose 1.20.2 name is the one 1.20.1 computed (CbStatTypes.vanillaFormula): 1.20.2's
     * getDisplayName() still answers, and the restored getTranslationKey() is consulted once (1.20.1 asked it once and
     * kept the name). Only where it answers otherwise than its unmixed copy (CbStatTypes.goldenTranslationKey) -- a
     * converted mod's mixin changed it -- is the name the translation of that key, as 1.20.1's; unmixed, the answer
     * is exactly 1.20.2's. At RETURN, and only while the answer is still 1.20.2's own field, so another mixin on
     * getDisplayName() keeps what it did.
     */
    @Inject(method = "method_30739", at = @At("RETURN"), cancellable = true)
    private void cb$routeDisplayName(CallbackInfoReturnable<class_2561> cir) {
        if (!CbStatTypes.ROUTE || cir.getReturnValue() != this.field_26382 || Boolean.TRUE.equals(this.cb$keyUnmixed)) {
            return;
        }
        if (this.cb$keyUnmixed == null) {
            class_3448<?> self = (class_3448<?>) (Object) this;
            if (!CbStatTypes.vanillaFormula(self, this.field_26382)) {
                return; // not registered yet, or named another way: asked again later
            }
            String key = ((CbStatTypes.Legacy) (Object) this).method_14957();
            this.cb$keyUnmixed = key.equals(CbStatTypes.goldenTranslationKey(self));
            if (this.cb$keyUnmixed) {
                return;
            }
            this.cb$legacyName = class_2561.method_43471(key);
        }
        cir.setReturnValue(this.cb$legacyName);
    }
}
