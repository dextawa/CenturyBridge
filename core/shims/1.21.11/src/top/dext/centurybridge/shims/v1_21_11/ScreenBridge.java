package top.dext.centurybridge.shims.v1_21_11;

import net.minecraft.class_332;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;

/**
 * First entry of the CLIENT shim battery (opened by live play-testing).
 * 1.20.2 gave Screen.renderBackground mouse/delta parameters; the legacy
 * single-arg overload delegates with neutral values -- the background blur
 * gradient just ignores the cursor.
 */
@Mixin(class_437.class)
public abstract class ScreenBridge {

    /**
     * 1.20.1 renderBackground(DrawContext). Since 1.21.6 the retained GUI
     * allows one background blur per frame and Screen.render draws the
     * background itself, so an old override's own call is idempotent here.
     */
    public void method_25420(class_332 context) {
        try {
            ((class_437) (Object) this).method_25420(context, 0, 0, 0.0F);
        } catch (IllegalStateException alreadyBlurred) {
            // background already drawn this frame
        }
    }
}
