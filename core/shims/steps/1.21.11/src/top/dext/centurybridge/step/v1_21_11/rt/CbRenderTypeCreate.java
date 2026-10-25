package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12247;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_21_11.facade.CompositeState;
import top.dext.centurybridge.step.v1_21_11.facade.RenderStateShard;

/**
 * 1.21.10's RenderType.create(name, bufferSize, [affectsCrumbling, sortOnUpload,] pipeline, state) on 1.21.11's
 * RenderType.create(name, RenderSetup): the composite state's shards become the RenderSetup 1.21.11 builds vanilla's
 * render types from (RenderTypes): its textures as Sampler0.., lightmap / overlay use, the LayeringTransform /
 * OutputTarget / TextureTransform the shard carries, buffer size, crumbling, sorting and outline property. A shard
 * 1.21.11 has no piece for (a mod's own layering or texturing runnables, a line width other than vanilla's) is
 * reported once per shard name: 1.21.11 draws run no shard runnables and read the line width from each vertex.
 * Also the CompositeRenderType constants 1.21.11 keeps as RenderTypes factories (LINES, SECONDARY_BLOCK_OUTLINE).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTypeCreate {
    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/rendertype");
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    private CbRenderTypeCreate() {
    }

    /** RenderType.create(name, bufferSize, pipeline, state) */
    public static class_1921 method_24048(String name, int bufferSize, RenderPipeline pipeline, CompositeState state) {
        return method_24049(name, bufferSize, false, false, pipeline, state);
    }

    /** RenderType.create(name, bufferSize, affectsCrumbling, sortOnUpload, pipeline, state) */
    public static class_1921 method_24049(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                                          RenderPipeline pipeline, CompositeState state) {
        class_12247.class_12248 b = class_12247.method_75927(pipeline);
        List<class_2960> textures = state.field_21406.cb$textures();
        for (int i = 0; i < textures.size(); i++) {
            b.method_75934("Sampler" + i, textures.get(i));
        }
        if (state.cb$lightmap.field_21390) {
            b.method_75928();
        }
        if (state.cb$overlay.field_21390) {
            b.method_75935();
        }
        if (state.cb$layering.cb$layering != null) {
            b.method_75930(state.cb$layering.cb$layering);
        } else {
            report(name, state.cb$layering);
        }
        b.method_75931(state.field_57931.cb$output);
        if (state.cb$texturing.cb$transform != null) {
            b.method_75933(state.cb$texturing.cb$transform);
        } else {
            report(name, state.cb$texturing);
        }
        if (state.cb$line.field_21392.isPresent() && state.cb$line.field_21392.getAsDouble() != 1.0) {
            report(name, state.cb$line);
        }
        if (affectsCrumbling) {
            b.method_75936();
        }
        if (sortOnUpload) {
            b.method_75937();
        }
        b.method_75929(bufferSize);
        b.method_75932(state.field_21852);
        return class_1921.method_75940(name, b.method_75938());
    }

    private static void report(String type, RenderStateShard shard) {
        if (REPORTED.add(type + "/" + shard.field_21363)) {
            LOGGER.warn("render type {}: 1.21.10 shard {} has no 1.21.11 counterpart (1.21.11 draws run no shard runnables "
                + "and take line width per vertex); the render type is created without it", type, shard);
        }
    }

    /** RenderType.LINES */
    public static class_1921 field_21695() {
        return class_12249.method_76015();
    }

    /** RenderType.SECONDARY_BLOCK_OUTLINE */
    public static class_1921 field_54857() {
        return class_12249.method_76017();
    }
}
