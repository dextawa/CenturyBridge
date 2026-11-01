package cb.s215n.mixin;

import cb.s215n.Seen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.21.5 mod's own mixin on ItemStack.set, which the 1.21.5 layer routes for JukeboxPlayable values */
@Mixin(net.minecraft.class_1799.class)
public abstract class JukeboxSetSeenMixin {
    @Inject(method = "method_57379", at = @At("HEAD"), remap = false)
    private void cbs215n$set(net.minecraft.class_9331<?> type, Object value, CallbackInfoReturnable<Object> cir) {
        if (type == net.minecraft.class_9334.field_52175) {
            Seen.jukeboxSet++;
        }
    }
}
