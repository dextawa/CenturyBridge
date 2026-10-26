package cb.s212.mixin;

import cb.s212.Driver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.1 TeleportCommand$LookAtPosition.perform(CommandSourceStack, Entity) (1.21.2 moved the record, unchanged, to
 * LookAt$LookAtPosition): records the entity it turns and the position it faces (a shadow of the record field).
 */
@Mixin(targets = "net.minecraft.class_3143$class_9252")
public abstract class TeleportLookAtMixin {
    @Shadow(remap = false)
    @Final
    private net.minecraft.class_243 comp_2359;

    @Inject(method = "method_13772(Lnet/minecraft/class_2168;Lnet/minecraft/class_1297;)V", at = @At("HEAD"), remap = false)
    private void cbs212$lookAt(net.minecraft.class_2168 source, net.minecraft.class_1297 entity, CallbackInfo ci) {
        Driver.lookAtAsked++;
        Driver.lookAtEntity = entity;
        Driver.lookAtPos = comp_2359;
    }
}
