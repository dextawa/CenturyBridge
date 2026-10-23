package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Camera's panoramic forward vector, for 1.21.11's GameRenderer.setPanoramicScreenshotParameters (CbWallGameRendererMixin) */
@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public interface CbWallCameraAccess {

    @Accessor("panoramicForwards")
    Vector3f cb$panoramicForwards();
}
