package cb.s11n.mixin;

import cb.s11n.NativeDriver;
import net.minecraft.class_12279;
import net.minecraft.class_1928;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.11 mod's mixins on GameRules.get / set (keepInventory only): count calls, force get while asked */
@Mixin(class_1928.class)
public abstract class NativeRulesMixin {
    @Inject(method = "method_76185", at = @At("HEAD"), cancellable = true, remap = false)
    @SuppressWarnings("unchecked")
    private <T> void cbs11n$getHead(class_12279<T> rule, CallbackInfoReturnable<T> cir) {
        if (rule == class_1928.field_19389) {
            NativeDriver.getHeads++;
            if (NativeDriver.forceKeep) {
                cir.setReturnValue((T) Boolean.TRUE);
            }
        }
    }

    @Inject(method = "method_76185", at = @At("RETURN"), remap = false)
    private <T> void cbs11n$getReturn(class_12279<T> rule, CallbackInfoReturnable<T> cir) {
        if (rule == class_1928.field_19389) {
            NativeDriver.getReturns++;
            NativeDriver.lastReturn = cir.getReturnValue();
        }
    }

    @Inject(method = "method_76186", at = @At("HEAD"), remap = false)
    private <T> void cbs11n$setHead(class_12279<T> rule, T value, MinecraftServer server, CallbackInfo ci) {
        if (rule == class_1928.field_19389) {
            NativeDriver.setHeads++;
        }
    }

    @Inject(method = "method_76186", at = @At("TAIL"), remap = false)
    private <T> void cbs11n$setTail(class_12279<T> rule, T value, MinecraftServer server, CallbackInfo ci) {
        if (rule == class_1928.field_19389) {
            NativeDriver.setTails++;
            NativeDriver.lastSet = value;
        }
    }
}
