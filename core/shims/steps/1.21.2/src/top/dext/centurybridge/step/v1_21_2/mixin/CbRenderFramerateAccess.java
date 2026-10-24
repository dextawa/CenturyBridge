package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderWindow;

/** FramerateLimitTracker's stored framerate limit, for 1.21.1's Window.getFramerateLimit (CbRenderWindow) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_9919.class)
public abstract class CbRenderFramerateAccess implements CbRenderWindow.Limit {

    @Shadow
    private int field_52732;

    @Override
    public int cb$limit() {
        return this.field_52732;
    }
}
