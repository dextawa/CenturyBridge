package cb.s212n.mixin;

import cb.s212n.Counts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.2 LookAt$LookAtPosition.perform(CommandSourceStack, Entity) */
@Mixin(net.minecraft.class_3144.class_9252.class)
public abstract class NativeLookAtMixin {
    @Inject(method = "method_13772(Lnet/minecraft/class_2168;Lnet/minecraft/class_1297;)V", at = @At("HEAD"), remap = false)
    private void cbs212n$lookAt(net.minecraft.class_2168 source, net.minecraft.class_1297 entity, CallbackInfo ci) {
        Counts.lookAt++;
    }
}
