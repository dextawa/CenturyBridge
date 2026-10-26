package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11 Camera members 26.1 reshaped.
 * <ul>
 * <li>getNearPlane(): 1.21.11 spanned the near plane with the fov option (degrees, unmodified by effects); 26.1's
 *     getNearPlane(fov) takes the fov.</li>
 * <li>getPartialTickTime(): 1.21.11 kept the partial tick setup() was given -- the game renderer's, 1 when the
 *     camera entity is frozen by the tick rate manager. 26.1's update(deltaTracker) computes the same value through
 *     getCameraEntityPartialTicks; this asks it of the game's delta tracker.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(value = Camera.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallCameraLegacyMixin {

    public Camera.NearPlane getNearPlane() {
        return ((Camera) (Object) this).getNearPlane(Minecraft.getInstance().options.fov().get().floatValue());
    }

    public float getPartialTickTime() {
        return ((Camera) (Object) this).getCameraEntityPartialTicks(Minecraft.getInstance().getDeltaTracker());
    }
}
