package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1946;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/** PortalForcer: its level, for 1.20.6's findPortalAround on 1.21's points of interest (CbEntityPortals) */
@Mixin(class_1946.class)
public abstract class CbEntityPortalForcerMixin implements CbEntityPortals.PortalForcerLevel {

    @Shadow
    @Final
    private class_3218 field_9286;

    @Override
    public class_3218 cb$level() {
        return this.field_9286;
    }
}
