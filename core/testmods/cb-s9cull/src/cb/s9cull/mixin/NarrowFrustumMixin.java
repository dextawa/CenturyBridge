package cb.s9cull.mixin;

import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.21.8-era client mod narrowing the culling frustum: at the start of LevelRenderer.renderLevel it calls
 * prepareCullFrustum(cameraPos, view, projection) with a 10-degree square projection looking where the camera looks.
 * On 1.21.8 renderLevel culled with the frustum that call wrote, so only chunks in a narrow cone around the view
 * centre are drawn; the screen edges show sky / void. Logs "CB-S9CULL narrowed" once.
 */
@Mixin(class_761.class)
public abstract class NarrowFrustumMixin {

    private static boolean cb$logged;

    @Inject(method = "method_22710", at = @At("HEAD"))
    private void cb$narrow(CallbackInfo ci) {
        class_4184 camera = class_310.method_1551().field_1773.method_19418();
        class_243 pos = camera.method_19326();
        Matrix4f view = new Matrix4f().rotation(camera.method_23767().conjugate(new Quaternionf()));
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(10.0), 1.0F, 0.05F, 1024.0F);
        ((class_761) (Object) this).method_32133(pos, view, projection);
        if (!cb$logged) {
            cb$logged = true;
            System.out.println("CB-S9CULL narrowed");
        }
    }
}
