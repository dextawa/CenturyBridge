package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbRecipeCacheEntries;

/**
 * RecipeCache (class_8884): get's test of a cached entry against the input goes through the restored 1.20.6
 * Entry.matches(List) (CbRecipeCacheEntryMixin) after 1.21's width/height test. With nothing mixed in, the answer is 1.21's.
 */
@Mixin(net.minecraft.class_8884.class)
public abstract class CbRecipeCacheMixin {

    @Redirect(method = "method_54470(Lnet/minecraft/class_1937;Lnet/minecraft/class_9694;)Ljava/util/Optional;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_8884$class_8885;method_54472(Lnet/minecraft/class_9694;)Z"))
    private boolean cb$matches(net.minecraft.class_8884.class_8885 entry, net.minecraft.class_9694 input) {
        return CbRecipeCacheEntries.matches(entry, input);
    }
}
