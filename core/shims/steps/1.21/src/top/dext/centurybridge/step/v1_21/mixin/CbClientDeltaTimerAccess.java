package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** DeltaTracker.Timer's partial tick as kept (1.20.6's Timer.partialTick) and its freeze flag (CbClientTime) */
@Mixin(net.minecraft.class_9779.class_9781.class)
public interface CbClientDeltaTimerAccess {

    @Accessor("field_51959")
    float cb$deltaTickResidual();

    @Accessor("field_51967")
    boolean cb$frozen();
}
