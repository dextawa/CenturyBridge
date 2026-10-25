package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_332;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 gave Screen.renderBackground mouse/delta parameters; the legacy
 * single-arg overload delegates with neutral values -- the background blur
 * gradient just ignores the cursor. (Ported from the 1.20.4 client battery;
 * the 1.20.2 wiring already listed this cover but no mixin provided it --
 * found by the real-client fixture run, not by the audit.)
 */
@Mixin(value = class_437.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class ScreenBridge {

    public void method_25420(class_332 context) {
        ((class_437) (Object) this).method_25420(context, 0, 0, 0.0F);
    }
}
