package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod on RecipeCache.Entry.matches(inputs) (class_8884$class_8885.method_54472(List)): records what vanilla asked
 * with, and answers true while Driver.forceMatch is set (the cached recipe then serves any input).
 */
@Mixin(targets = "net.minecraft.class_8884$class_8885")
public abstract class RecipeCacheEntryMixin {
    @Inject(method = "method_54472(Ljava/util/List;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx6$matches(java.util.List<net.minecraft.class_1799> inputs, CallbackInfoReturnable<Boolean> cir) {
        Driver.matchesAsked++;
        Driver.matchesSize = inputs.size();
        if (Driver.forceMatch) {
            cir.setReturnValue(true);
        }
    }
}
