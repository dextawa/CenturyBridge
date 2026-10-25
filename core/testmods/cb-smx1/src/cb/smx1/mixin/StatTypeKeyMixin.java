package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * StatType.getTranslationKey() (method_14957), which 1.20.1's getDisplayName() translated. For the "minecraft:killed"
 * stat type only, a RETURN injector appends ".cbsmx1" to the key; every call is counted.
 */
@Mixin(net.minecraft.class_3448.class)
public abstract class StatTypeKeyMixin {
    @Inject(method = "method_14957()Ljava/lang/String;", at = @At("RETURN"), cancellable = true, remap = false)
    private void cbsmx1$probeKey(CallbackInfoReturnable<String> cir) {
        Driver.count("stat-type-key-all");
        if (Driver.PROBE_STAT_KEY.equals(cir.getReturnValue())) {
            Driver.count("stat-type-key");
            cir.setReturnValue(cir.getReturnValue() + ".cbsmx1");
        }
    }
}
