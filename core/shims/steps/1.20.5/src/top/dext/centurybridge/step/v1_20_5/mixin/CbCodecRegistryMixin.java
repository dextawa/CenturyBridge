package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbCodecRegistries;

/**
 * MappedRegistry.register: a registration into one of the six registries 1.20.5 turned from codecs into
 * map codecs goes into its 1.20.4-shaped mirror as well (rt/CbCodecRegistries), whoever made it.
 */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbCodecRegistryMixin {

    /** register(key, value, registrationInfo) */
    @Inject(method = "method_10272", at = @At("RETURN"))
    private void cbS4$registered(net.minecraft.class_5321<?> key, Object value, net.minecraft.class_9248 info,
                                 CallbackInfoReturnable<?> cir) {
        CbCodecRegistries.registered((net.minecraft.class_2378<?>) (Object) this, key, value, info);
    }
}
