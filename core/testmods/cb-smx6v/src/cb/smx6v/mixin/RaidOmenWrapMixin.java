package cb.smx6v.mixin;

import cb.smx6v.Driver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * A native 1.21 mod's @WrapOperation on Raid Omen's Raids.createOrExtendRaid call (class_9457.method_5572 ->
 * class_3767.method_16540), the call the 1.21 step layer routes (CbEntityRaidOmenRouteMixin wraps it). Counts and passes on.
 */
@Mixin(targets = "net.minecraft.class_9457")
public abstract class RaidOmenWrapMixin {

    @WrapOperation(method = "method_5572(Lnet/minecraft/class_1309;I)Z", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3767;method_16540(Lnet/minecraft/class_3222;Lnet/minecraft/class_2338;)Lnet/minecraft/class_3765;"))
    private net.minecraft.class_3765 cbsmx6v$raid(net.minecraft.class_3767 raids, net.minecraft.class_3222 player, net.minecraft.class_2338 pos,
                                                  Operation<net.minecraft.class_3765> original) {
        Driver.nativeRaidWraps++;
        return original.call(raids, player, pos);
    }
}
