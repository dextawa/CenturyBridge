package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26Processors;

/**
 * MappedRegistry for 26.2's structure_processor registry, which holds map codecs where 26.1.2's held
 * StructureProcessorTypes: what happens to it -- a registration (whoever made it), freezing (which binds its holders'
 * values), its tags being bound -- happens to the 26.1.2-shaped mirror 26.1.2 code holds (rt/CbS26Processors). Every
 * other registry passes through untouched (the mirror is looked up by identity).
 */
@Mixin(MappedRegistry.class)
public abstract class CbS26ProcessorRegistryMixin implements CbS26Processors.Frozen {

    @Shadow
    private boolean frozen;

    @Override
    public boolean cbS26$frozen() {
        return frozen;
    }

    @Inject(method = "register", at = @At("RETURN"))
    private void cbS26$registered(ResourceKey<?> key, Object value, RegistrationInfo info, CallbackInfoReturnable<?> cir) {
        CbS26Processors.registered((Registry<?>) (Object) this, key, value, info);
    }

    @Inject(method = "freeze", at = @At("RETURN"))
    private void cbS26$frozen(CallbackInfoReturnable<?> cir) {
        CbS26Processors.frozen((Registry<?>) (Object) this);
    }

    @Inject(method = "bindTags", at = @At("RETURN"))
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void cbS26$tags(java.util.Map tags, CallbackInfo ci) {
        CbS26Processors.tagsBound((Registry<?>) (Object) this, tags);
    }
}
