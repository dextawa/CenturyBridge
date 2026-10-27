package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** {@link CbPrioritySplit}'s client-side counterparts */
@Environment(EnvType.CLIENT)
public final class CbPrioritySplitClient {
    private CbPrioritySplitClient() {
    }

    /** CbRenderGameMixin: 1.21.1's resetProjectionMatrix, called virtually */
    public interface GameProjection {
        void cb$v1_21_2$resetProjectionMatrix(org.joml.Matrix4f matrix);
    }

    /** CbRenderItemShaperMixin: the model manager and the baked cache, and 1.21.1's rebuildCache called virtually */
    public interface ItemShaper {
        net.minecraft.class_1092 cb$v1_21_2$modelManager();

        void cb$v1_21_2$modelManager(net.minecraft.class_1092 manager);

        it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraft.class_1087> cb$v1_21_2$cache();

        void cb$v1_21_2$rebuildCache();
    }

    /** CbRenderModelMixin: 1.21.1's renderToBuffer, called virtually */
    public interface ModelDraw {
        void cb$v1_21_2$renderToBuffer(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color);
    }

    /** CbRenderShaderMixin: 1.21.1's kept samplers, and a linked program's two Programs */
    public interface ShaderState {
        java.util.Map<String, Object> cb$v1_21_2$samplers();

        void cb$v1_21_2$programs(top.dext.centurybridge.step.v1_21_2.facade.Program vertex, top.dext.centurybridge.step.v1_21_2.facade.Program fragment);
    }

    /** CbRestTutorialMixin: 1.21.1's timed toasts */
    public interface TutorialToasts {
        java.util.List<Object[]> cb$v1_21_2$timedToasts();
    }
}
