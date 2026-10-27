package cb.swall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.21.11 mixin into a lambda: ItemStack.CODEC's components getter (class_1799.method_58094, "stack ->
 * stack.components.asPatch()"), counting encodes. 26.1 numbers its lambdas anew (the wall table judges synthetic
 * members by body: 26.1's lambda$static$2), so the selector must land on the getter, not on another lambda.
 */
@Mixin(net.minecraft.class_1799.class)
public abstract class ItemStackCodecLambdaMixin {
    @Inject(method = "method_58094(Lnet/minecraft/class_1799;)Lnet/minecraft/class_9326;", at = @At("RETURN"), remap = false)
    private static void cbswall$countPatch(net.minecraft.class_1799 stack, CallbackInfoReturnable<net.minecraft.class_9326> cir) {
        cb.swall.SwallTestMod.CODEC_PATCH_CALLS.incrementAndGet();
    }
}
