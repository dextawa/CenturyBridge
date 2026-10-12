package top.dext.centurybridge.step.v1_21_7.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.6's getModelIdentity() / clearModelIdentity() on a TrackingItemStackRenderState: its own identity
 * list, which 1.21.7 answers through its getModelIdentity() and never clears (1.21.7's GUI makes a new
 * tracking state for every item it draws).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_11540.class)
abstract class CbTrackingItemStackRenderStateIdentity {

    @Shadow
    @Final
    private List<Object> field_61069;

    @Shadow
    public abstract Object method_72239();

    /** 1.21.6's getModelIdentity(): the tracking state's own identity */
    public Object method_70949() {
        return method_72239();
    }

    /** 1.21.6's clearModelIdentity() */
    public void method_71658() {
        field_61069.clear();
    }
}
