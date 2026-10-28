package cb.smx6v.mixin;

import cb.smx6v.Driver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * A native 1.21 mod's @WrapOperation on Leashable.tickLeash's restoreLeashFromSave call (class_9817.method_60965 ->
 * method_60958), the call the 1.21 step layer routes (CbEntityLeashSaveRouteMixin wraps it). Counts and passes on.
 */
@Mixin(net.minecraft.class_9817.class)
public interface LeashRestoreWrapMixin {

    @WrapOperation(method = "method_60965(Lnet/minecraft/class_1297;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9817;method_60958(Lnet/minecraft/class_1297;Lnet/minecraft/class_9817$class_9818;)V"))
    private static void cbsmx6v$restore(net.minecraft.class_1297 entity, net.minecraft.class_9817.class_9818 data, Operation<Void> original) {
        Driver.nativeLeashRestoreWraps++;
        original.call(entity, data);
    }
}
