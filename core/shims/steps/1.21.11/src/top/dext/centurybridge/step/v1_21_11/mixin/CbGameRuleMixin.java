package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.serialization.DataResult;
import net.minecraft.class_12279;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.11's GameRule.serialize / getCommandResult / deserialize / getDescriptionId consulting the 1.21.10 value and
 * key of the rule (CbGameRules.routeSerialize / routeCommandResult / routeDeserialize / routeDescriptionId): each
 * answer is 1.21.11's own unless a mod's 1.21.10 mixin makes the 1.21.10 member answer otherwise than its body.
 *
 * <p>Applied after other mods' mixins (priority 2000, a route, not a restored member): its RETURN callbacks sit right
 * before each return, after every other RETURN callback on these methods, so a 1.21.11 mod's mixin on them runs
 * first (HEAD and RETURN), sees 1.21.11's answer, and when it answers itself (cancels) this route does not run.
 */
@Mixin(value = class_12279.class, priority = 2000)
public abstract class CbGameRuleMixin {
    @Inject(method = "method_76146", at = @At("RETURN"), cancellable = true)
    private void cb$serialize(Object value, CallbackInfoReturnable<String> cir) {
        String answer = cir.getReturnValue();
        String routed = CbGameRules.routeSerialize((class_12279<?>) (Object) this, value, answer);
        if (!routed.equals(answer)) {
            cir.setReturnValue(routed);
        }
    }

    @Inject(method = "method_76149", at = @At("RETURN"), cancellable = true)
    private void cb$commandResult(Object value, CallbackInfoReturnable<Integer> cir) {
        int answer = cir.getReturnValue();
        int routed = CbGameRules.routeCommandResult((class_12279<?>) (Object) this, value, answer);
        if (routed != answer) {
            cir.setReturnValue(routed);
        }
    }

    @Inject(method = "method_76147", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void cb$deserialize(String s, CallbackInfoReturnable<DataResult> cir) {
        DataResult answer = cir.getReturnValue();
        DataResult routed = CbGameRules.routeDeserialize((class_12279<?>) (Object) this, s, answer);
        if (routed != answer) {
            cir.setReturnValue(routed);
        }
    }

    @Inject(method = "method_76150", at = @At("RETURN"), cancellable = true)
    private void cb$descriptionId(CallbackInfoReturnable<String> cir) {
        String answer = cir.getReturnValue();
        String routed = CbGameRules.routeDescriptionId((class_12279<?>) (Object) this, answer);
        if (!routed.equals(answer)) {
            cir.setReturnValue(routed);
        }
    }
}
