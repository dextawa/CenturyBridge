package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_3448;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
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
}
