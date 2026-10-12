package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.rt.v1_20_2.CbAdvAny;

/**
 * ContextAwarePredicate.ANY of 1.20.1 -- a predicate with no conditions, which 1.20.2 refuses to
 * build -- is the sentinel CbAdvAny.field_24388 (one always-true condition it never evaluates).
 * Where 1.20.2 code meets it (an element of a list of player predicates; a bridge passes ANY in
 * Optional form as absent everywhere else) it answers as 1.20.1's did: it matches without testing
 * anything (no random number drawn for its stand-in condition), and it writes JsonNull, as an empty
 * condition list did.
 */
@Mixin(net.minecraft.class_5258.class)
public abstract class CbAdvContextAny {

    @Inject(method = "method_27804()Lcom/google/gson/JsonElement;", at = @At("HEAD"), cancellable = true)
    private void cb$anyJson(CallbackInfoReturnable<com.google.gson.JsonElement> cir) {
        if ((Object) this == CbAdvAny.field_24388) {
            cir.setReturnValue(com.google.gson.JsonNull.INSTANCE);
        }
    }

    @Inject(method = "method_27806(Lnet/minecraft/class_47;)Z", at = @At("HEAD"), cancellable = true)
    private void cb$anyMatches(net.minecraft.class_47 context, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == CbAdvAny.field_24388) {
            cir.setReturnValue(true);
        }
    }
}
