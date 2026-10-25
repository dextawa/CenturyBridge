package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12247;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import top.dext.centurybridge.step.v1_21_11.facade.CompositeState;
import top.dext.centurybridge.step.v1_21_11.facade.RenderStateShard;

/**
 * 1.21.10's RenderType.create(name, bufferSize, [affectsCrumbling, sortOnUpload,] pipeline, state) on 1.21.11's
 * RenderType.create(name, RenderSetup): the composite state's shards become the RenderSetup 1.21.11 builds vanilla's
 * render types from (RenderTypes): its textures as Sampler0.., lightmap / overlay use, the LayeringTransform /
 * OutputTarget / TextureTransform the shard carries (a mod's own layering / texturing shard runs its runnables where
 * 1.21.11's draw asks for the layering and the texture matrix: layering / texturing), buffer size, crumbling, sorting
 * and outline property. The line shard's width goes to the vertices a converted mod writes without one
 * (rt/CbLineWidth): 1.21.11 reads the line width from each vertex.
 * Also the CompositeRenderType constants 1.21.11 keeps as RenderTypes factories (LINES, SECONDARY_BLOCK_OUTLINE).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTypeCreate {
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
        b.method_75930(state.cb$layering.cb$layering != null ? state.cb$layering.cb$layering : layering(state.cb$layering));
        b.method_75931(state.field_57931.cb$output);
        b.method_75933(state.cb$texturing.cb$transform != null ? state.cb$texturing.cb$transform : texturing(state.cb$texturing));
        if (affectsCrumbling) {
            b.method_75936();
        }
        if (sortOnUpload) {
            b.method_75937();
        }
        b.method_75929(bufferSize);
        b.method_75932(state.field_21852);
        class_1921 type = class_1921.method_75940(name, b.method_75938());
        CbLineWidth.register(type, state.cb$line);
        return type;
    }

    /**
     * A mod's own layering shard on 1.21.11's LayeringTransform, whose modifier 1.21.11's draw runs on the model view
     * stack it has just pushed (and pops after the draw): the shard's setup runs on that stack as on 1.21.10 (it
     * pushes and changes it), the matrix it leaves is kept, its clear runs (it pops), and the kept matrix becomes
     * the draw's.
     */
    static net.minecraft.class_12245 layering(RenderStateShard.LayeringStateShard shard) {
        return new net.minecraft.class_12245(shard.field_21363, stack -> {
            shard.method_23516();
            org.joml.Matrix4f m = new org.joml.Matrix4f(stack);
            shard.method_23518();
            stack.set(m);
        });
    }

    /**
     * A mod's own texturing shard on 1.21.11's TextureTransform, whose matrix 1.21.11's draw asks for: the shard's
     * setup runs (it sets 1.21.10's texture matrix, rt/CbRenderSystem), the matrix is taken, its clear runs.
     */
    static net.minecraft.class_12250 texturing(RenderStateShard.TexturingStateShard shard) {
        return new net.minecraft.class_12250(shard.field_21363, () -> {
            shard.method_23516();
            org.joml.Matrix4f m = new org.joml.Matrix4f(CbRenderSystem.getTextureMatrix());
            shard.method_23518();
            return m;
        });
    }

    // ---- render types whose pipelines 1.21.11 deleted, rebuilt from 1.21.10's definitions (RenderPipelines' clinit,
    // RenderType's clinit; javap of both): the snippets they use are 1.21.11's, with 1.21.10's depth write where
    // 1.21.11's DEBUG_FILLED_SNIPPET changed it (it adds withDepthWrite(false)); the shaders (core/position_color)
    // and vertex modes (DEBUG_LINE_STRIP, QUADS) 1.21.11 still has.

    private static final RenderPipeline DEBUG_LINE_STRIP = RenderPipeline.builder(net.minecraft.class_10799.field_60125)
        .withLocation("pipeline/debug_line_strip").withVertexShader("core/position_color").withFragmentShader("core/position_color")
        .withCull(false).withVertexFormat(net.minecraft.class_290.field_1576, com.mojang.blaze3d.vertex.VertexFormat.class_5596.field_29345)
        .build();
    private static final RenderPipeline DEBUG_STRUCTURE_QUADS = RenderPipeline.builder(net.minecraft.class_10799.field_56860)
        .withLocation("pipeline/debug_structure_quads").withCull(false).withDepthWrite(false).build();
    private static final RenderPipeline DEBUG_SECTION_QUADS = RenderPipeline.builder(net.minecraft.class_10799.field_56860)
        .withLocation("pipeline/debug_section_quads").withDepthWrite(true).build();

    private static final java.util.function.Function<Double, class_1921> DEBUG_LINE_STRIP_TYPES = net.minecraft.class_156.method_34866(
        (Double width) -> method_24048("debug_line_strip", 1536, DEBUG_LINE_STRIP, CompositeState.method_23598()
            .method_23609(new RenderStateShard.LineStateShard(java.util.OptionalDouble.of(width))).method_23617(false)));
    private static class_1921 debugStructureQuads;
    private static class_1921 debugSectionQuads;

    /** RenderType.debugLineStrip(width): 1.21.10's memoized "debug_line_strip" type of that line width */
    public static class_1921 method_49043(double width) {
        return DEBUG_LINE_STRIP_TYPES.apply(width);
    }

    /** RenderType.debugStructureQuads(): "debug_structure_quads", sorted on upload */
    public static synchronized class_1921 method_61045() {
        if (debugStructureQuads == null) {
            debugStructureQuads = method_24049("debug_structure_quads", 1536, false, true, DEBUG_STRUCTURE_QUADS,
                CompositeState.method_23598().method_23617(false));
        }
        return debugStructureQuads;
    }

    /** RenderType.debugSectionQuads(): "debug_section_quads", view-offset layering, sorted on upload */
    public static synchronized class_1921 method_51456() {
        if (debugSectionQuads == null) {
            debugSectionQuads = method_24049("debug_section_quads", 1536, false, true, DEBUG_SECTION_QUADS,
                CompositeState.method_23598().method_23607(RenderStateShard.field_22241).method_23617(false));
        }
        return debugSectionQuads;
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
