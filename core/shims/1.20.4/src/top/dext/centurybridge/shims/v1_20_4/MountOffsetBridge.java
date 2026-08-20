package top.dext.centurybridge.shims.v1_20_4;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 folded the rider Y-offset into the passenger-attachment system:
 * getPassengerRidingPos returns the full offset vector instead of a bare
 * height. The old scalar is that vector's Y component, so "it moved into a
 * system" is still a bridge -- the value is right there, just reached
 * differently.
 */
@Mixin(class_1297.class)
public abstract class MountOffsetBridge {

    /** 1.20.1 getMountedHeightOffset(): now the Y of the attachment vector. */
    public double method_5621() {
        class_1297 self = (class_1297) (Object) this;
        return self.method_52538(self).field_1351 - self.method_23318();
    }
}
