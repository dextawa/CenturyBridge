package top.dext.centurybridge.step.v1_21_7.mixin;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_7.rt.CbModelIdentity;

/**
 * 1.21.6's ItemStackRenderState.getModelIdentity() and clearModelIdentity(), given back (see
 * CbItemRenderStates). A plain state records the elements appended while it is updated again (1.21.7's
 * appendModelIdentityElement drops them; a TrackingItemStackRenderState overrides it and keeps its own
 * list, CbTrackingItemStackRenderStateIdentity), and empties the record when it is cleared -- every update
 * begins with clear() -- so a state 1.21.7 reuses frame after frame answers the identity of its latest
 * update rather than growing without bound as 1.21.6's did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10444.class)
abstract class CbItemStackRenderStateIdentity implements CbModelIdentity {

    @Unique
    private final List<Object> cb$modelIdentity = new ArrayList<>();

    @Inject(method = "method_70946", at = @At("HEAD"))
    private void cb$recordModelIdentity(Object element, CallbackInfo ci) {
        cb$modelIdentity.add(element);
    }

    @Inject(method = "method_65605", at = @At("HEAD"))
    private void cb$resetModelIdentity(CallbackInfo ci) {
        cb$modelIdentity.clear();
    }

    /** 1.21.6's getModelIdentity() */
    public Object method_70949() {
        return cb$modelIdentity;
    }

    /** 1.21.6's clearModelIdentity() */
    public void method_71658() {
        cb$modelIdentity.clear();
    }

    @Override
    public Object cb$modelIdentity() {
        return method_70949();
    }

    @Override
    public void cb$clearModelIdentity() {
        method_71658();
    }
}
