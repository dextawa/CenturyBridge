package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10149;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4668;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4671;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4672;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4685;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_4686;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_5942;
import top.dext.centurybridge.step.v1_21_5.facade.RenderStateShard$class_8559;
import top.dext.centurybridge.step.v1_21_5.facade.ShaderProgram;

/**
 * 1.21.4's RenderType.create(name, format, mode, bufferSize[, affectsCrumbling, sortOnUpload], state) on 1.21.5, where a
 * render type is built from a RenderPipeline (class_1921.method_24049(name, size, crumbling, sort, pipeline, state)) and
 * the CompositeState lost six shards: shader, transparency, depth test, cull, write mask, colour logic (1.21.5's command
 * encoder applies them from the pipeline at every draw, class_10860.method_68356, so they must be carried by it).
 *
 * <ul>
 * <li>The six dropped CompositeState.Builder setters (method_34578 / 23615 / 23604 / 23603 / 23616 / 51788) are
 * instanceRedirects that record the shard for that builder; createCompositeState (method_23617 / method_24297) builds
 * 1.21.5's state and keeps the recorded shards for it. Unset shards take 1.21.4's builder defaults (javap -c -p 1.21.4
 * class_4688$class_4689.&lt;init&gt;: NO_SHADER, NO_TRANSPARENCY, LEQUAL_DEPTH_TEST, CULL, COLOR_DEPTH_WRITE,
 * NO_COLOR_LOGIC).
 * <li>create builds the pipeline: the program's shader, defines, samplers and uniforms copied at run time from the
 * running game's pipeline for the same shader ({@link #shaderBuilder}); blend / depth test / cull / writes / colour
 * logic from the shards; depth bias from a polygon-offset layering shard (rt/CbRenderStates.depthBias); the vertex
 * format and mode of the call. Pipelines are cached by value.
 * <li>NO_SHADER (1.21.4 drew nothing: its draw returned on a null shader, class_291.method_34427): a pipeline on
 * core/position (donor pipeline/sky) that writes nothing -- no colour or depth write, no blend, no depth test. Known
 * difference: one draw call that writes nothing (release_notes_known.md).
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTypes {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private CbRenderTypes() {
    }

    /** the six shards 1.21.5's CompositeState no longer holds, as a 1.21.4 builder recorded them */
    public static final class Shards {
        public RenderStateShard$class_5942 shader;
        public RenderStateShard$class_4685 transparency;
        public RenderStateShard$class_4672 depthTest;
        public RenderStateShard$class_4671 cull;
        public RenderStateShard$class_4686 writeMask;
        public RenderStateShard$class_8559 colorLogic;

        Shards copy() {
            Shards s = new Shards();
            s.shader = this.shader;
            s.transparency = this.transparency;
            s.depthTest = this.depthTest;
            s.cull = this.cull;
            s.writeMask = this.writeMask;
            s.colorLogic = this.colorLogic;
            return s;
        }
    }

    private static final Map<class_1921.class_4688.class_4689, Shards> BUILDERS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<class_1921.class_4688, Shards> STATES = Collections.synchronizedMap(new WeakHashMap<>());

    private static Shards of(class_1921.class_4688.class_4689 b) {
        return BUILDERS.computeIfAbsent(b, k -> new Shards());
    }

    // ---- CompositeState.Builder: the dropped setters (1.21.4: store the shard, return this) ----

    public static class_1921.class_4688.class_4689 method_34578(class_1921.class_4688.class_4689 b, RenderStateShard$class_5942 s) {
        of(b).shader = s;
        return b;
    }

    public static class_1921.class_4688.class_4689 method_23615(class_1921.class_4688.class_4689 b, RenderStateShard$class_4685 s) {
        of(b).transparency = s;
        return b;
    }

    public static class_1921.class_4688.class_4689 method_23604(class_1921.class_4688.class_4689 b, RenderStateShard$class_4672 s) {
        of(b).depthTest = s;
        return b;
    }

    public static class_1921.class_4688.class_4689 method_23603(class_1921.class_4688.class_4689 b, RenderStateShard$class_4671 s) {
        of(b).cull = s;
        return b;
    }

    public static class_1921.class_4688.class_4689 method_23616(class_1921.class_4688.class_4689 b, RenderStateShard$class_4686 s) {
        of(b).writeMask = s;
        return b;
    }

    public static class_1921.class_4688.class_4689 method_51788(class_1921.class_4688.class_4689 b, RenderStateShard$class_8559 s) {
        of(b).colorLogic = s;
        return b;
    }

    // ---- createCompositeState: 1.21.5's own build, then the recorded shards kept for the state ----

    public static class_1921.class_4688 method_23617(class_1921.class_4688.class_4689 b, boolean outline) {
        class_1921.class_4688 state = b.method_23617(outline);
        keep(b, state);
        return state;
    }

    public static class_1921.class_4688 method_24297(class_1921.class_4688.class_4689 b, class_1921.class_4750 outline) {
        class_1921.class_4688 state = b.method_24297(outline);
        keep(b, state);
        return state;
    }

    private static void keep(class_1921.class_4688.class_4689 b, class_1921.class_4688 state) {
        Shards s = BUILDERS.get(b);
        // a builder is reusable in 1.21.4 (its fields stay set): the state gets a copy of what was set so far
        STATES.put(state, s == null ? new Shards() : s.copy());
    }

    /** the shards 1.21.4 would have put in this state (1.21.4's builder defaults for the unset ones) */
    public static Shards shards(class_1921.class_4688 state) {
        Shards s = STATES.get(state);
        Shards r = s == null ? new Shards() : s.copy();
        if (r.shader == null) {
            r.shader = CbRenderStates.field_29434();
        }
        if (r.transparency == null) {
            r.transparency = CbRenderStates.field_21364();
        }
        if (r.depthTest == null) {
            r.depthTest = CbRenderStates.field_21348();
        }
        if (r.cull == null) {
            r.cull = CbRenderStates.field_21344();
        }
        if (r.writeMask == null) {
            r.writeMask = CbRenderStates.field_21349();
        }
        if (r.colorLogic == null) {
            r.colorLogic = CbRenderStates.field_44815();
        }
        return r;
    }

    // ---- RenderType.create ----

    public static class_1921.class_4687 method_24048(String name, VertexFormat format, VertexFormat.class_5596 mode, int bufferSize,
            class_1921.class_4688 state) {
        // 1.21.4 method_24048 = method_24049(name, format, mode, size, false, false, state) (javap -c -p)
        return method_24049(name, format, mode, bufferSize, false, false, state);
    }

    public static class_1921.class_4687 method_24049(String name, VertexFormat format, VertexFormat.class_5596 mode, int bufferSize,
            boolean affectsCrumbling, boolean sortOnUpload, class_1921.class_4688 state) {
        RenderPipeline pipeline = pipeline(name, format, mode, shards(state), layering(state));
        return class_1921.method_24049(name, bufferSize, affectsCrumbling, sortOnUpload, pipeline, state);
    }

    private static class_4668.class_4675 layering(class_1921.class_4688 state) {
        for (class_4668 s : state.field_21422) {
            if (s instanceof class_4668.class_4675 l) {
                return l;
            }
        }
        return null;
    }

    private record Key(Optional<ShaderProgram> program, Optional<BlendFunction> blend, DepthTestFunction depth, boolean cull,
            boolean color, boolean depthWrite, LogicOp logic, VertexFormat format, VertexFormat.class_5596 mode, float biasScale,
            float biasConstant) {
    }

    private static final Map<Key, RenderPipeline> PIPELINES = new ConcurrentHashMap<>();

    private static RenderPipeline pipeline(String name, VertexFormat format, VertexFormat.class_5596 mode, Shards s,
            class_4668.class_4675 layering) {
        Optional<ShaderProgram> program = s.shader.field_29455;
        float[] bias = CbRenderStates.depthBias(layering);
        Key key;
        if (program.isEmpty()) {
            // NO_SHADER: writes nothing (see the class javadoc); cull is the state's, nothing else applies
            key = new Key(program, Optional.empty(), DepthTestFunction.NO_DEPTH_TEST, s.cull.cbCull, false, false, LogicOp.NONE,
                format, mode, 0.0F, 0.0F);
        } else {
            key = new Key(program, blend(name, s.transparency), depth(name, s.depthTest), s.cull.cbCull, s.writeMask.field_21400,
                s.writeMask.field_21401, logic(name, s.colorLogic), format, mode, bias == null ? 0.0F : bias[0], bias == null ? 0.0F : bias[1]);
        }
        return PIPELINES.computeIfAbsent(key, k -> build(name, k));
    }

    private static RenderPipeline build(String name, Key k) {
        RenderPipeline.Builder b;
        if (k.program.isEmpty()) {
            RenderPipeline sky = byLocation("pipeline/sky");
            if (sky == null) {
                throw new IllegalStateException("render type " + name + ": no pipeline/sky to describe core/position");
            }
            b = RenderPipeline.builder();
            b.withVertexShader(class_2960.method_60656("core/position"));
            b.withFragmentShader(class_2960.method_60656("core/position"));
            interfaceOf(b, sky, sky.getSamplers());
            b.withColorWrite(false, false);
        } else {
            b = shaderBuilder(k.program.get());
            if (b == null) {
                throw new IllegalStateException("render type " + name + ": shader " + k.program.get().comp_3113()
                    + " has no description in this game (no pipeline for that shader)");
            }
            b.withColorWrite(k.color);
        }
        b.withLocation(class_2960.method_60655("centurybridge", "rendertype/" + sanitize(name)));
        if (k.blend.isPresent()) {
            b.withBlend(k.blend.get());
        } else {
            b.withoutBlend();
        }
        b.withDepthTestFunction(k.depth);
        b.withCull(k.cull);
        b.withDepthWrite(k.depthWrite);
        b.withColorLogic(k.logic);
        b.withVertexFormat(k.format, k.mode);
        if (k.biasScale != 0.0F || k.biasConstant != 0.0F) {
            b.withDepthBias(k.biasScale, k.biasConstant);
        }
        return b.build();
    }

    private static String sanitize(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        for (char c : name.toLowerCase(java.util.Locale.ROOT).toCharArray()) {
            sb.append(class_2960.method_29184(c) ? c : '_');
        }
        return sb.toString();
    }

    private static Optional<BlendFunction> blend(String name, RenderStateShard$class_4685 t) {
        if (t.cbBlend != null) {
            return t.cbBlend;
        }
        LOGGER.warn("Render type {}: transparency shard {} was built from the mod's own runnables; its blend is not known, drawn without blending",
            name, t);
        return Optional.empty();
    }

    private static DepthTestFunction depth(String name, RenderStateShard$class_4672 d) {
        // GL functions 1.21.5's DepthTestFunction covers (GlConst.toGl: 519, 514, 515, 513, 516)
        return switch (d.cbFunction) {
            case 519 -> DepthTestFunction.NO_DEPTH_TEST;
            case 514 -> DepthTestFunction.EQUAL_DEPTH_TEST;
            case 515 -> DepthTestFunction.LEQUAL_DEPTH_TEST;
            case 513 -> DepthTestFunction.LESS_DEPTH_TEST;
            case 516 -> DepthTestFunction.GREATER_DEPTH_TEST;
            default -> {
                LOGGER.warn("Render type {}: depth function {} has no 1.21.5 DepthTestFunction; LEQUAL used", name, d.cbFunction);
                yield DepthTestFunction.LEQUAL_DEPTH_TEST;
            }
        };
    }

    private static LogicOp logic(String name, RenderStateShard$class_8559 l) {
        if (l.cbLogic != null) {
            return l.cbLogic;
        }
        LOGGER.warn("Render type {}: colour logic shard {} was built from the mod's own runnables; its logic op is not known, none used", name, l);
        return LogicOp.NONE;
    }

    // ---- the program's description, from the running game ----

    /**
     * A pipeline builder carrying 1.21.4 program {@code program}'s vertex / fragment shader, defines (the donor's, then
     * the program's own, as 1.21.4's ShaderManager combined them, class_10149.method_62928), samplers and uniforms,
     * copied from the running game's pipeline that uses that program's shader -- never named here, so the description
     * is the game's own on every release the layer climbs to. null when no running-game pipeline describes it. The
     * caller adds location, vertex format and render state. Donors: the pipeline at pipeline/&lt;name without
     * rendertype_&gt;, else the pipelines whose vertex and fragment shader are core/&lt;name&gt; when they agree (the
     * 1.21.5 naming, rt/CbShaderJsonsClient); for the five 1.21.4 programs with no 1.21.5 pipeline, a pipeline with the
     * same uniform interface: armor_entity_glint / glint_translucent / entity_glint -> pipeline/glint (core/glint, which
     * their 1.21.4 jsons named); position_color_lightmap / position_color_tex_lightmap -> their own shader, uniforms of
     * position_color / position_tex_color's pipeline (identical ModelViewMat / ProjMat / ColorModulator in the 1.21.4
     * jsons), samplers from the 1.21.4 json (Sampler2 / Sampler0). Mojang deleted those two shaders' sources at 1.21.9:
     * from there their pipelines fail to compile and draw nothing (release_notes_known.md).
     */
    public static RenderPipeline.Builder shaderBuilder(ShaderProgram program) {
        class_2960 id = program.comp_3113();
        if (!"minecraft".equals(id.method_12836()) || !id.method_12832().startsWith("core/")) {
            return null;
        }
        String name = id.method_12832().substring("core/".length());
        RenderPipeline donor;
        List<String> samplers;
        class_2960 shader;
        switch (name) {
            case "rendertype_armor_entity_glint", "rendertype_glint_translucent", "rendertype_entity_glint" -> {
                donor = byLocation("pipeline/glint");
                samplers = donor == null ? null : donor.getSamplers();
                shader = donor == null ? null : donor.getVertexShader();
            }
            case "position_color_lightmap" -> {
                donor = find("position_color");
                samplers = List.of("Sampler2");
                shader = id;
            }
            case "position_color_tex_lightmap" -> {
                donor = find("position_tex_color");
                samplers = List.of("Sampler0");
                shader = id;
            }
            default -> {
                donor = find(name);
                samplers = donor == null ? null : donor.getSamplers();
                shader = null;
            }
        }
        if (donor == null) {
            LOGGER.warn("Shader {}: no pipeline of this game describes it", id);
            return null;
        }
        RenderPipeline.Builder b = RenderPipeline.builder();
        b.withVertexShader(shader != null ? shader : donor.getVertexShader());
        b.withFragmentShader(shader != null ? shader : donor.getFragmentShader());
        class_10149 defines = (shader == id ? class_10149.field_53930 : donor.getShaderDefines()).method_62928(program.comp_3115());
        if (!defines(b, defines, id)) {
            return null;
        }
        interfaceOf(b, donor, samplers);
        return b;
    }

    private static void interfaceOf(RenderPipeline.Builder b, RenderPipeline donor, List<String> samplers) {
        for (String s : samplers) {
            b.withSampler(s);
        }
        for (RenderPipeline.UniformDescription u : donor.getUniforms()) {
            b.withUniform(u.name(), u.type());
        }
    }

    /**
     * The builder takes defines as flags and as int / float values (it stores String.valueOf of them); a value is
     * passed as the int or float whose String.valueOf is the value itself, so the define text is unchanged.
     */
    private static boolean defines(RenderPipeline.Builder b, class_10149 defines, class_2960 id) {
        for (String flag : defines.comp_3104()) {
            b.withShaderDefine(flag);
        }
        for (Map.Entry<String, String> e : defines.comp_3103().entrySet()) {
            String v = e.getValue();
            Integer i = null;
            Float f = null;
            try {
                int p = Integer.parseInt(v);
                if (String.valueOf(p).equals(v)) {
                    i = p;
                }
            } catch (NumberFormatException ignored) {
            }
            if (i == null) {
                try {
                    float p = Float.parseFloat(v);
                    if (String.valueOf(p).equals(v)) {
                        f = p;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            if (i != null) {
                b.withShaderDefine(e.getKey(), i);
            } else if (f != null) {
                b.withShaderDefine(e.getKey(), f);
            } else {
                LOGGER.warn("Shader {}: define {}={} cannot be given to a pipeline builder unchanged", id, e.getKey(), v);
                return false;
            }
        }
        return true;
    }

    private static RenderPipeline byLocation(String path) {
        class_2960 loc = class_2960.method_60656(path);
        for (RenderPipeline p : net.minecraft.class_10799.method_67886()) {
            if (p.getLocation().equals(loc)) {
                return p;
            }
        }
        return null;
    }

    private static RenderPipeline find(String name) {
        String location = name.startsWith("rendertype_") ? name.substring("rendertype_".length()) : name;
        RenderPipeline own = byLocation("pipeline/" + location);
        if (own != null) {
            return own;
        }
        class_2960 core = class_2960.method_60656("core/" + name);
        RenderPipeline byShader = null;
        for (RenderPipeline p : net.minecraft.class_10799.method_67886()) {
            if (core.equals(p.getVertexShader()) && core.equals(p.getFragmentShader())) {
                if (byShader != null && !sameProgram(byShader, p)) {
                    LOGGER.warn("Shader core/{}: the pipelines using it describe it differently", name);
                    return null;
                }
                if (byShader == null) {
                    byShader = p;
                }
            }
        }
        return byShader;
    }

    private static boolean sameProgram(RenderPipeline a, RenderPipeline b) {
        return a.getShaderDefines().equals(b.getShaderDefines()) && Set.copyOf(a.getSamplers()).equals(Set.copyOf(b.getSamplers()))
            && Set.copyOf(a.getUniforms()).equals(Set.copyOf(b.getUniforms()));
    }

    // ---- ShaderStateShard runnables ----

    /**
     * 1.21.4 ShaderStateShard setup: RenderSystem.setShader(program) / clearShader. The layer's shader slot belongs to
     * RenderSystem.setShader's give-back (s215i, rt/CbGlRenderSystem); until that lands this records the program only.
     * 1.21.5 draws a render type with its pipeline's program regardless (class_10860.method_68356).
     */
    public static void setupShader(Optional<ShaderProgram> program) {
        lastShader = Objects.requireNonNull(program);
    }

    private static volatile Optional<ShaderProgram> lastShader = Optional.empty();
}
