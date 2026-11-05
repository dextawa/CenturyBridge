package top.dext.centurybridge.step.v26_2.rt;

import java.util.function.Consumer;

/**
 * The 1.21.6 layer's close hook for its legacy uniform buffers (ObjectShare entry {@value #KEY}, published by that
 * layer's rt/CbS6GlCloseChannel once it made a uniform view; absent otherwise, then nothing owns buffers to free).
 */
public final class CbS26GlUniformClose {
    public static final String KEY = "centurybridge:gl_uniforms_closed";
    private static volatile Consumer<Object> hook;

    private CbS26GlUniformClose() {
    }

    @SuppressWarnings("unchecked")
    public static void closed(Object backend) {
        Consumer<Object> h = hook;
        if (h == null) {
            Object o = net.fabricmc.loader.api.FabricLoader.getInstance().getObjectShare().get(KEY);
            if (!(o instanceof Consumer)) {
                return;
            }
            hook = h = (Consumer<Object>) o;
        }
        h.accept(backend);
    }
}
