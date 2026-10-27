package top.dext.centurybridge.step.v1_21_7.mixin;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_7.rt.CbModelIdentity;
import top.dext.centurybridge.step.v1_21_7.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_7.rt.CbVanillaReads;

/**
 * 1.21.6's ItemStackRenderState.getModelIdentity() and clearModelIdentity(), given back (see
 * CbItemRenderStates). A plain state records the elements appended while it is updated again (1.21.7's
 * appendModelIdentityElement drops them; a TrackingItemStackRenderState overrides it and keeps its own
 * list, CbTrackingItemStackRenderStateIdentity), and empties the record when it is cleared -- every update
 * begins with clear() -- so a state 1.21.7 reuses frame after frame answers the identity of its latest
 * update rather than growing without bound as 1.21.6's did (the recording: CbItemStackRenderStateIdentityHooks,
 * default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_10444.class, priority = CbVanillaReads.PRIORITY)
abstract class CbItemStackRenderStateIdentity implements CbModelIdentity, CbPrioritySplitClient.RenderStateIdentity {

    @Unique
    private final List<Object> cb$modelIdentity = new ArrayList<>();

    @Override
    public List<Object> cb$v1_21_7$identityElements() {
        return cb$modelIdentity;
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
