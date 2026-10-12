package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_9787;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/** PortalProcessor: a 1.20.6 write of Entity.portalTime sets the processor's count (CbEntityPortals.setPortalTime) */
@Mixin(class_9787.class)
public abstract class CbEntityPortalProcessorMixin implements CbEntityPortals.PortalProcessorTime {

    @Shadow
    private int field_52000;

    @Override
    public void cb$setPortalTime(int time) {
        this.field_52000 = time;
    }
}
