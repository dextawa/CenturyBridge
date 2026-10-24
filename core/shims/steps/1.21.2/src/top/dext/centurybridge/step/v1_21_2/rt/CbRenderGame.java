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

    /** GameRenderer's 1.21.1 blitShader field (CbRenderGameMixin) */
    public interface Blit {
        net.minecraft.class_5944 cb$blitShader();

        void cb$setBlitShader(net.minecraft.class_5944 shader);
    }

    /**
     * read of 1.21.1's GameRenderer.blitShader: the shader a mod set there, else 1.21.2's blit_screen program -- the
     * "blit_screen" core shader 1.21.1 loaded into the field, which RenderTarget.blitToScreen draws with
     */
    public static net.minecraft.class_5944 blitShader(net.minecraft.class_757 renderer) {
        net.minecraft.class_5944 shader = ((Blit) renderer).cb$blitShader();
        return shader != null ? shader : CbRenderShaders.program(net.minecraft.class_10142.field_53868);
    }

    /** write of 1.21.1's GameRenderer.blitShader: RenderTarget.blitToScreen draws with it (CbRenderTargetMixin) */
    public static void setBlitShader(net.minecraft.class_757 renderer, net.minecraft.class_5944 shader) {
        ((Blit) renderer).cb$setBlitShader(shader);
    }
}
