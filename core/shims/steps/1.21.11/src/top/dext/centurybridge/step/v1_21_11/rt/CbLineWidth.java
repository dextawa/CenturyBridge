package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import top.dext.centurybridge.step.v1_21_11.facade.RenderStateShard;

/**
 * The line width of a render type as 1.21.10 drew it, for the vertices a converted mod writes without one.
 *
 * <p>1.21.10 took a line's width from the render type's LineStateShard at draw time (RenderSystem.lineWidth);
 * 1.21.11's line formats carry it in each vertex (VertexConsumer.setLineWidth, POSITION_COLOR_NORMAL_LINE_WIDTH), and
 * BufferBuilder refuses a vertex that lacks it. 1.21.11's own callers always write it; a 1.21.10 mod's line vertices
 * never do. The render types a 1.21.10 line shard stands behind: 1.21.10's lines() (LineStateShard(empty): the
 * window-scaled width) and secondaryBlockOutline() (7.0), which 1.21.11's RenderTypes.lines() / secondaryBlockOutline()
 * are (rt/CbRenderTypes), and every render type 1.21.10's RenderType.create builds (its own line shard).
 */
@Environment(EnvType.CLIENT)
public final class CbLineWidth {
    private static final Map<class_1921, RenderStateShard.LineStateShard> SHARDS = new WeakHashMap<>();
    private static volatile boolean vanilla;

    private CbLineWidth() {
    }

    /** a render type 1.21.10's RenderType.create built, with its line shard */
    public static void register(class_1921 type, RenderStateShard.LineStateShard shard) {
        synchronized (SHARDS) {
            SHARDS.put(type, shard);
        }
    }

    private static RenderStateShard.LineStateShard shard(class_1921 type) {
        if (!vanilla) {
            synchronized (SHARDS) {
                if (!vanilla) {
                    SHARDS.put(class_12249.method_76015(), new RenderStateShard.LineStateShard(OptionalDouble.empty()));
                    SHARDS.put(class_12249.method_76017(), new RenderStateShard.LineStateShard(OptionalDouble.of(7.0)));
                    vanilla = true;
                }
            }
        }
        synchronized (SHARDS) {
            return SHARDS.get(type);
        }
    }

    /**
     * The width 1.21.10 drew the render type's lines at: its line shard's setup run against 1.21.10's line width
     * (rt/CbRenderSystem), which is put back after; DEFAULT_LINE (1.0) sets nothing and 1.21.10 drew at 1.0.
     * NaN: no 1.21.10 line shard stands behind the render type.
     */
    public static float width(class_1921 type) {
        RenderStateShard.LineStateShard s = shard(type);
        if (s == null) {
            return Float.NaN;
        }
        float saved = CbRenderSystem.getShaderLineWidth();
        CbRenderSystem.lineWidth(1.0F);
        s.method_23516();
        float w = CbRenderSystem.getShaderLineWidth();
        CbRenderSystem.lineWidth(saved);
        return w;
    }
}
