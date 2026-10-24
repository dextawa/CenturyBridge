package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityHurt;

/**
 * Entity's final 1.21.2 hurt entry points, where a mod class's 1.21.1 overrides answer what 1.21.1 asked them:
 * hurtOrSimulate on a client level (1.21.1's callers called hurt there too; 1.21.2's go to hurtClient, which has
 * no amount), and isInvulnerableToBase for a non-living entity (1.21.2's vanilla entities ask it where 1.21.1's
 * asked the overridable isInvulnerableTo). See CbEntityHurt.
 */
@Mixin(class_1297.class)
public abstract class CbEntityHurtMixin {

    @Inject(method = "method_64420", at = @At("HEAD"), cancellable = true)
    private void cb$clientHurt(class_1282 source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Boolean r = CbEntityHurt.clientHurt((class_1297) (Object) this, source, amount);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }

    @Inject(method = "method_64421", at = @At("HEAD"), cancellable = true)
    private void cb$baseInvulnerable(class_1282 source, CallbackInfoReturnable<Boolean> cir) {
        Boolean r = CbEntityHurt.baseInvulnerable((class_1297) (Object) this, source);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
