package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.1 GameRenderer shapes 1.21.2 reshaped, for the override hooks (see CbRenderGameMixin) */
@Environment(EnvType.CLIENT)
public final class CbRenderGame {

    private CbRenderGame() {
    }

    /** a 1.21.1 GameRenderer overriding getProjectionMatrix(double fov) */
    public interface Projection {
        org.joml.Matrix4f method_22973(double fov);
    }

    /** 1.21.2's getProjectionMatrix(float fov) synthesized on such a renderer: its override, with the same fov */
    public static org.joml.Matrix4f projection(Projection self, float fov) {
        return self.method_22973(fov);
    }
}
