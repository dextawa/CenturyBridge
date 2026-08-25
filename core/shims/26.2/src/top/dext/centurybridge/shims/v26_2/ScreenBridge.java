package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * First entry of the CLIENT shim battery (opened by live play-testing).
 * 1.20.2 gave Screen.renderBackground mouse/delta parameters; the legacy
 * single-arg overload delegates with neutral values -- the background blur
 * gradient just ignores the cursor.
 */
@Mixin(net.minecraft.client.gui.screens.Screen.class)
public abstract class ScreenBridge {
}
