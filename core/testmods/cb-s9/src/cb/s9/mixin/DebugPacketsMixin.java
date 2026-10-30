package cb.s9.mixin;

import java.util.List;
import net.minecraft.class_1309;
import net.minecraft.class_4209;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.8 mod's mixins on DebugPackets: sendEntityBrain HEAD, getMemoryDescriptions RETURN (DebugPacketsTest) */
@Mixin(class_4209.class)
public abstract class DebugPacketsMixin {
    @Inject(method = "method_19774(Lnet/minecraft/class_1309;)V", at = @At("HEAD"))
    private static void cbs9$brain(class_1309 entity, CallbackInfo ci) {
        cb.s9.DebugPacketsTest.BRAIN_CALLS.incrementAndGet();
    }

    @Inject(method = "method_36157(Lnet/minecraft/class_1309;J)Ljava/util/List;", at = @At("RETURN"))
    private static void cbs9$memories(class_1309 entity, long gameTime, CallbackInfoReturnable<List<String>> cir) {
        cb.s9.DebugPacketsTest.MEMORIES_SEEN = cir.getReturnValue();
    }
}
