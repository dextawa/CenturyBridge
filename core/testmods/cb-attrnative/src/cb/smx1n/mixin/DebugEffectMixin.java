package cb.smx1n.mixin;

import java.util.Map;
import net.minecraft.class_1291;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Test-only diagnostic for the Strength attribute template path. */
@Mixin(class_1291.class)
public abstract class DebugEffectMixin {
    @Shadow
    @Final
    private Map field_5885;

    @Inject(method = "method_5555(Lnet/minecraft/class_5131;I)V", at = @At("HEAD"), remap = false)
    private void debug(CallbackInfo ci) {
        for (Object entry : this.field_5885.entrySet()) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) entry;
            System.out.println("CB-ATTR-DEBUG effect=" + ((Object) this).getClass().getName()
                + " key=" + e.getKey().getClass().getName() + " value=" + e.getValue().getClass().getName());
        }
    }
}
