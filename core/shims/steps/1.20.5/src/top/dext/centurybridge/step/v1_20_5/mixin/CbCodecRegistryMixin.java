package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbCodecRegistries;

/**
 * MappedRegistry for the six registries 1.20.5 turned from codecs into map codecs: what happens to one --
 * a registration (whoever made it), freezing (which binds its holders' values), its tags being bound --
 * happens to its 1.20.4-shaped mirror as well (rt/CbCodecRegistries).
 */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbCodecRegistryMixin implements CbCodecRegistries.Frozen {

    /** frozen */
    @Shadow
    private boolean field_36463;

    @Override
    public boolean cbS4$frozen() {
        return field_36463;
    }

    /** register(key, value, registrationInfo) */
    @Inject(method = "method_10272", at = @At("RETURN"))
    private void cbS4$registered(net.minecraft.class_5321<?> key, Object value, net.minecraft.class_9248 info,
                                 CallbackInfoReturnable<?> cir) {
        CbCodecRegistries.registered((net.minecraft.class_2378<?>) (Object) this, key, value, info);
    }

    /** freeze() */
    @Inject(method = "method_40276", at = @At("RETURN"))
    private void cbS4$frozen(CallbackInfoReturnable<?> cir) {
        CbCodecRegistries.frozen((net.minecraft.class_2378<?>) (Object) this);
    }

    /** bindTags(tags) */
    @Inject(method = "method_40257", at = @At("RETURN"))
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void cbS4$tags(java.util.Map tags, CallbackInfo ci) {
        CbCodecRegistries.tagsBound((net.minecraft.class_2378<?>) (Object) this, tags);
    }
}
