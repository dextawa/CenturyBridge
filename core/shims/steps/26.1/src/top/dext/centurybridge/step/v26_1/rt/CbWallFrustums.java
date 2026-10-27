package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * 1.21.11's Frustum(modelView, projection) took two Matrix4f; 26.1's takes the model-view matrix as a Matrix4fc
 * (the same body otherwise, javap of both): a 1.21.11 call passes its Matrix4f unchanged; a subclass's super
 * constructor is retargeted to 26.1's (superCtorReshapes).
 */
@Environment(EnvType.CLIENT)
public final class CbWallFrustums {
    private CbWallFrustums() {
    }

    /** new Frustum(modelView, projection) */
    public static Frustum frustum(Matrix4f modelView, Matrix4f projection) {
        return new Frustum(modelView, projection);
    }

    /** a subclass's model-view argument, as the Matrix4fc 26.1's super constructor declares (the same object) */
    public static Matrix4fc modelView(Matrix4f modelView) {
        return modelView;
    }
}
