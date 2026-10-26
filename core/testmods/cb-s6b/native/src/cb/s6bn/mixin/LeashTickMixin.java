package cb.s6bn.mixin;

import cb.s6bn.Counter;
import net.minecraft.class_1297;
import net.minecraft.class_3218;
import net.minecraft.class_9817;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * native 1.21.6: Leashable.tickLeash, where the layer's leash route sits -- HEAD, TAIL, its checkElasticInteractions,
 * onElasticLeashPull and closeRangeLeashBehaviour calls -- counted per run in system properties
 */
@Mixin(class_9817.class)
public interface LeashTickMixin {
    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V", at = @At("HEAD"))
    private static void cbS6bn$head(class_3218 level, class_1297 entity, CallbackInfo ci) {
        Counter.bump("cb.s6bn.leash.head");
    }

    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V", at = @At("TAIL"))
    private static void cbS6bn$tail(class_3218 level, class_1297 entity, CallbackInfo ci) {
        Counter.bump("cb.s6bn.leash.tail");
    }

    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9817;method_70995(Lnet/minecraft/class_1297;Lnet/minecraft/class_9817$class_9818;)Z"))
    private static void cbS6bn$elastic(class_3218 level, class_1297 entity, CallbackInfo ci) {
        Counter.bump("cb.s6bn.leash.elastic");
    }

    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9817;method_71006()V"))
    private static void cbS6bn$pull(class_3218 level, class_1297 entity, CallbackInfo ci) {
        Counter.bump("cb.s6bn.leash.pull");
    }

    @Inject(method = "method_60965(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9817;method_60963(Lnet/minecraft/class_1297;)V"))
    private static void cbS6bn$close(class_3218 level, class_1297 entity, CallbackInfo ci) {
        Counter.bump("cb.s6bn.leash.close");
    }
}
