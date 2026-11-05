package top.dext.centurybridge.step.v1_21_6.rt;

import java.util.function.Consumer;

/**
 * Publishes {@link CbS6GlUniforms#closed} as Fabric Loader ObjectShare entry {@value #KEY}, a Consumer&lt;Object&gt; taking
 * a render pass backend. 26.2 closes a pass in RenderPass.close() and GlRenderPass (this layer's close anchor,
 * CbS6GlUniformCloseMixin, retired at 26.2) has no close(); the 26.2 layer's CbS26RenderPassCloseMixin hands the pass's
 * backend here (layers are separate mods; neither links the other's classes). Published when the first legacy uniform
 * view is made (CbS6GlUniforms.field_57873): only passes with a view own buffers to free.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbS6GlCloseChannel {
    public static final String KEY = "centurybridge:gl_uniforms_closed";
    private static volatile boolean published;

    private CbS6GlCloseChannel() {
    }

    static void publish() {
        if (!published) {
            Consumer<Object> hook = backend -> {
                if (backend instanceof net.minecraft.class_10866 pass) {
                    CbS6GlUniforms.closed(pass);
                }
            };
            net.fabricmc.loader.api.FabricLoader.getInstance().getObjectShare().putIfAbsent(KEY, hook);
            published = true;
        }
    }
}
