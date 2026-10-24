package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Window's focus flag: 1.21.11's Minecraft kept it as windowActive, set by the window's focus callback through
 * WindowEventHandler.setWindowActive; 26.1's Window keeps it itself (isWindowActive reads it).
 */
@Environment(EnvType.CLIENT)
@Mixin(Window.class)
public interface CbWallWindowAccess {

    @Accessor("focused")
    void cb$setFocused(boolean focused);
}
