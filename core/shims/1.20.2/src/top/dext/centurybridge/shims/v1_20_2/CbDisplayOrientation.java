package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_8113;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 1.20.1's Display.orientation() back (s1misc): the display's rotation as a quaternion,
 * rotationYXZ(-yRot, xRot, 0) in radians -- what 1.20.1 kept up to date on every rotation change;
 * here it is brought up to date when asked, in the same per-display instance 1.20.1 returned.
 * 1.20.2 renders from the rotation itself; a converted override is marked by an adapter hook
 * (CbLegacyDisplayOrientation) and drawn with again (CbDisplayRendererOrientation).
 */
@Mixin(class_8113.class)
public abstract class CbDisplayOrientation {

    @Unique
    private final Quaternionf cb$orientation = new Quaternionf();

    public Quaternionf method_48863() {
        class_8113 self = (class_8113) (Object) this;
        return this.cb$orientation.rotationYXZ(-0.017453292F * self.method_36454(), 0.017453292F * self.method_36455(), 0.0F);
    }
}
