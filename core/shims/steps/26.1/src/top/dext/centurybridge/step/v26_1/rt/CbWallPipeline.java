package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import top.dext.centurybridge.step.v26_1.facade.DepthTestFunction;
import top.dext.centurybridge.step.v26_1.facade.LogicOp;
import top.dext.centurybridge.step.v26_1.mixin.CbWallPipelineBuilderAccess;
import top.dext.centurybridge.step.v26_1.mixin.CbWallRenderPipelinesAccess;

/**
 * 1.21.11's render pipeline depth and colour state on 26.1.
 * <p>
 * 1.21.11 kept seven independent optional settings per builder and snippet (depth test function, depth
 * write, colour write, alpha write, colour logic, blend function; depth bias on builders only) and filled
 * in its defaults when the pipeline was built: LEQUAL_DEPTH_TEST, writes on, LogicOp.NONE, no blend.
 * 26.1 keeps one optional DepthStencilState (compare op, depth write, bias) and one optional
 * ColorTargetState (blend, write mask); a pipeline built without a depth state has no depth test at all,
 * and there is no colour logic.
 * <p>
 * So a 1.21.11 mod's builder carries its 1.21.11 settings here ({@link VState}, per builder): every
 * 1.21.11 setter updates them and writes the 26.1 state they amount to -- defaults applied, as 1.21.11's
 * build() applied them -- into the builder, so 26.1's build() makes the pipeline 1.21.11 would have.
 * NO_DEPTH_TEST is "no depth state" (1.21.11's GL backend disabled the depth test for it, so the depth
 * write setting did nothing). Snippets carry their 1.21.11 view the same way: one a mod built, one made
 * by its 1.21.11 constructor, and 26.1's vanilla snippets, which answer what their 1.21.11 namesakes set
 * (read off 1.21.11's RenderPipelines initializer: the GUI, GUI_TEXTURED, GUI_TEXT, OUTLINE,
 * POST_PROCESSING and ANIMATE_SPRITE snippets set NO_DEPTH_TEST, the last three and DEBUG_FILLED depth
 * write off, TEXT, CLOUDS, LINES, DEBUG_FILLED, WEATHER and the three GUI ones the translucent blend;
 * every other snippet set none of it). A snippet of unknown origin (a 26.1 mod's) answers what its
 * 26.1 state says. A built pipeline answers the 1.21.11 values it was built with; one built by 26.1
 * code answers what its 26.1 state amounts to.
 */
@Environment(EnvType.CLIENT)
public final class CbWallPipeline {

    private CbWallPipeline() {
    }

    /** 1.21.11 builder / snippet / pipeline settings of the state 26.1 reshaped */
    public static final class VState {
        Optional<DepthTestFunction> depthTest = Optional.empty();
        Optional<Boolean> writeDepth = Optional.empty();
        Optional<Boolean> writeColor = Optional.empty();
        Optional<Boolean> writeAlpha = Optional.empty();
        Optional<LogicOp> colorLogic = Optional.empty();
        Optional<BlendFunction> blend = Optional.empty();
        /** builders only in 1.21.11 (snippets carry no bias); here also a 26.1 snippet's own */
        Optional<float[]> bias = Optional.empty();

        VState copy() {
            VState s = new VState();
            s.depthTest = depthTest;
            s.writeDepth = writeDepth;
            s.writeColor = writeColor;
            s.writeAlpha = writeAlpha;
            s.colorLogic = colorLogic;
            s.blend = blend;
            s.bias = bias;
            return s;
        }

        /** 1.21.11 Builder.withSnippet: what the snippet sets replaces what the builder had */
        void merge(VState snippet) {
            if (snippet.depthTest.isPresent()) depthTest = snippet.depthTest;
            if (snippet.writeColor.isPresent()) writeColor = snippet.writeColor;
            if (snippet.writeAlpha.isPresent()) writeAlpha = snippet.writeAlpha;
            if (snippet.writeDepth.isPresent()) writeDepth = snippet.writeDepth;
            if (snippet.colorLogic.isPresent()) colorLogic = snippet.colorLogic;
            if (snippet.blend.isPresent()) blend = snippet.blend;
            if (snippet.bias.isPresent()) bias = snippet.bias;
        }

        DepthTestFunction depthTestOrDefault() {
            return depthTest.orElse(DepthTestFunction.LEQUAL_DEPTH_TEST);
        }

        float biasScale() {
            return bias.map(b -> b[0]).orElse(0.0F);
        }

        float biasConstant() {
            return bias.map(b -> b[1]).orElse(0.0F);
        }

        /** the 26.1 depth state 1.21.11 would have built (null: no depth test) */
        DepthStencilState depthStencil() {
            return depthStencilOf(depthTestOrDefault(), writeDepth.orElse(true), biasScale(), biasConstant());
        }

        ColorTargetState colorTarget() {
            return colorTargetOf(blend, writeColor.orElse(true), writeAlpha.orElse(true));
        }
    }

    private static final Map<RenderPipeline.Builder, VState> BUILDERS = Collections.synchronizedMap(new WeakHashMap<>());
    /** snippets are records: kept by identity (two equal snippets may have different 1.21.11 views) */
    private static final Map<RenderPipeline.Snippet, VState> SNIPPETS = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Map<RenderPipeline, VState> PIPELINES = Collections.synchronizedMap(new WeakHashMap<>());
    private static volatile boolean vanillaKnown;
    /** set once a pipeline with a colour logic operation exists: the GL mixin looks pipelines up only then */
    private static volatile boolean anyLogic;

    // ---------------------------------------------------------------- 26.1 state from 1.21.11 values

    static DepthStencilState depthStencilOf(DepthTestFunction test, boolean writeDepth, float biasScale, float biasConstant) {
        CompareOp op = switch (test) {
            case NO_DEPTH_TEST -> null;
            case EQUAL_DEPTH_TEST -> CompareOp.EQUAL;
            case LEQUAL_DEPTH_TEST -> CompareOp.LESS_THAN_OR_EQUAL;
            case LESS_DEPTH_TEST -> CompareOp.LESS_THAN;
            case GREATER_DEPTH_TEST -> CompareOp.GREATER_THAN;
        };
        return op == null ? null : new DepthStencilState(op, writeDepth, biasScale, biasConstant);
    }

    static ColorTargetState colorTargetOf(Optional<BlendFunction> blend, boolean writeColor, boolean writeAlpha) {
        int mask = (writeColor ? ColorTargetState.WRITE_COLOR : 0) | (writeAlpha ? ColorTargetState.WRITE_ALPHA : 0);
        return new ColorTargetState(blend, mask);
    }

    /** 1.21.11's depth test function of a 26.1 compare op */
    static DepthTestFunction depthTestOf(CompareOp op) {
        return switch (op) {
            case EQUAL -> DepthTestFunction.EQUAL_DEPTH_TEST;
            case LESS_THAN_OR_EQUAL -> DepthTestFunction.LEQUAL_DEPTH_TEST;
            case LESS_THAN -> DepthTestFunction.LESS_DEPTH_TEST;
            case GREATER_THAN -> DepthTestFunction.GREATER_DEPTH_TEST;
            // 1.21.11's NO_DEPTH_TEST was GL_ALWAYS as a depth function (GlConst.toGl)
            case ALWAYS_PASS -> DepthTestFunction.NO_DEPTH_TEST;
            default -> throw new UnsupportedOperationException(
                    "depth compare op " + op + " (a 26.1 pipeline's) has no 1.21.11 DepthTestFunction");
        };
    }

    // ---------------------------------------------------------------- 1.21.11 views of 26.1 state

    /** what a depth / colour state set by 26.1 code says in 1.21.11 terms (each part only when present) */
    static VState viewOf(Optional<DepthStencilState> depth, Optional<ColorTargetState> color) {
        VState s = new VState();
        depth.ifPresent(d -> {
            s.depthTest = Optional.of(depthTestOf(d.depthTest()));
            s.writeDepth = Optional.of(d.writeDepth());
            if (d.depthBiasScaleFactor() != 0.0F || d.depthBiasConstant() != 0.0F) {
                s.bias = Optional.of(new float[] {d.depthBiasScaleFactor(), d.depthBiasConstant()});
            }
        });
        color.ifPresent(c -> {
            s.blend = c.blendFunction();
            int mask = c.writeMask();
            if (mask != ColorTargetState.WRITE_ALL) {
                s.writeColor = Optional.of((mask & ColorTargetState.WRITE_COLOR) == ColorTargetState.WRITE_COLOR);
                s.writeAlpha = Optional.of((mask & ColorTargetState.WRITE_ALPHA) != 0);
            }
        });
        return s;
    }

    private static void knowVanilla() {
        if (vanillaKnown) {
            return;
        }
        synchronized (SNIPPETS) {
            if (vanillaKnown) {
                return;
            }
            Optional<BlendFunction> translucent = Optional.of(BlendFunction.TRANSLUCENT);
            for (RenderPipeline.Snippet s : new RenderPipeline.Snippet[] {CbWallRenderPipelinesAccess.cb$text(),
                    CbWallRenderPipelinesAccess.cb$clouds(), CbWallRenderPipelinesAccess.cb$lines(),
                    CbWallRenderPipelinesAccess.cb$weather()}) {
                VState v = new VState();
                v.blend = translucent;
                SNIPPETS.putIfAbsent(s, v);
            }
            VState debugFilled = new VState();
            debugFilled.blend = translucent;
            debugFilled.writeDepth = Optional.of(false);
            SNIPPETS.putIfAbsent(CbWallRenderPipelinesAccess.cb$debugFilled(), debugFilled);
            for (RenderPipeline.Snippet s : new RenderPipeline.Snippet[] {CbWallRenderPipelinesAccess.cb$gui(),
                    CbWallRenderPipelinesAccess.cb$guiTextured(), CbWallRenderPipelinesAccess.cb$guiText()}) {
                VState v = new VState();
                v.blend = translucent;
                v.depthTest = Optional.of(DepthTestFunction.NO_DEPTH_TEST);
                SNIPPETS.putIfAbsent(s, v);
            }
            for (RenderPipeline.Snippet s : new RenderPipeline.Snippet[] {CbWallRenderPipelinesAccess.cb$outline(),
                    RenderPipelines.POST_PROCESSING_SNIPPET, RenderPipelines.ANIMATE_SPRITE_SNIPPET}) {
                VState v = new VState();
                v.depthTest = Optional.of(DepthTestFunction.NO_DEPTH_TEST);
                v.writeDepth = Optional.of(false);
                SNIPPETS.putIfAbsent(s, v);
            }
            // every other 26.1 vanilla snippet that has a 1.21.11 namesake set none of this state: the
            // depth state 26.1 gives some of them (DepthStencilState.DEFAULT) was 1.21.11's build default
            for (RenderPipeline.Snippet s : new RenderPipeline.Snippet[] {CbWallRenderPipelinesAccess.cb$matricesProjection(),
                    CbWallRenderPipelinesAccess.cb$fog(), CbWallRenderPipelinesAccess.cb$globals(),
                    CbWallRenderPipelinesAccess.cb$matricesFog(), CbWallRenderPipelinesAccess.cb$matricesFogLightDir(),
                    CbWallRenderPipelinesAccess.cb$genericBlocks(), CbWallRenderPipelinesAccess.cb$terrain(),
                    CbWallRenderPipelinesAccess.cb$block(), CbWallRenderPipelinesAccess.cb$entity(),
                    CbWallRenderPipelinesAccess.cb$entityEmissive(), CbWallRenderPipelinesAccess.cb$beaconBeam(),
                    CbWallRenderPipelinesAccess.cb$endPortal(), CbWallRenderPipelinesAccess.cb$particle()}) {
                SNIPPETS.putIfAbsent(s, new VState());
            }
            vanillaKnown = true;
        }
    }

    static VState snippetView(RenderPipeline.Snippet snippet) {
        knowVanilla();
        VState v = SNIPPETS.get(snippet);
        return v != null ? v : viewOf(snippet.depthStencilState(), snippet.colorTargetState());
    }

    static VState state(RenderPipeline.Builder builder) {
        VState s = BUILDERS.get(builder);
        if (s == null) {
            // a builder no 1.21.11 call started: its 1.21.11 view is what it holds
            CbWallPipelineBuilderAccess a = (CbWallPipelineBuilderAccess) builder;
            s = viewOf(a.cb$depthStencilState(), a.cb$colorTargetState());
            BUILDERS.put(builder, s);
        }
        return s;
    }

    static VState pipelineView(RenderPipeline pipeline) {
        VState v = PIPELINES.get(pipeline);
        if (v != null) {
            return v;
        }
        // built by 26.1 code: the values its state amounts to (no depth state is NO_DEPTH_TEST without writes)
        VState s = viewOf(Optional.ofNullable(pipeline.getDepthStencilState()), Optional.of(pipeline.getColorTargetState()));
        if (s.depthTest.isEmpty()) {
            s.depthTest = Optional.of(DepthTestFunction.NO_DEPTH_TEST);
            s.writeDepth = Optional.of(false);
        }
        return s;
    }

    private static RenderPipeline.Builder apply(RenderPipeline.Builder builder, VState s) {
        builder.withDepthStencilState(Optional.ofNullable(s.depthStencil()));
        builder.withColorTargetState(s.colorTarget());
        return builder;
    }

    // ---------------------------------------------------------------- 1.21.11 RenderPipeline (static)

    /** 1.21.11 RenderPipeline.builder(Snippet...): the snippets' 1.21.11 settings, 1.21.11's defaults for the rest */
    public static RenderPipeline.Builder builder(RenderPipeline.Snippet... snippets) {
        RenderPipeline.Builder b = RenderPipeline.builder(snippets);
        VState s = new VState();
        for (RenderPipeline.Snippet snippet : snippets) {
            s.merge(snippetView(snippet));
        }
        BUILDERS.put(b, s);
        return apply(b, s);
    }

    // ---------------------------------------------------------------- 1.21.11 RenderPipeline.Builder

    public static RenderPipeline.Builder withDepthTestFunction(RenderPipeline.Builder b, DepthTestFunction f) {
        VState s = state(b);
        s.depthTest = Optional.of(f);
        return apply(b, s);
    }

    public static RenderPipeline.Builder withBlend(RenderPipeline.Builder b, BlendFunction blend) {
        VState s = state(b);
        s.blend = Optional.of(blend);
        return apply(b, s);
    }

    public static RenderPipeline.Builder withoutBlend(RenderPipeline.Builder b) {
        VState s = state(b);
        s.blend = Optional.empty();
        return apply(b, s);
    }

    /** 1.21.11 withColorWrite(boolean): colour and alpha writes both */
    public static RenderPipeline.Builder withColorWrite(RenderPipeline.Builder b, boolean color) {
        VState s = state(b);
        s.writeColor = Optional.of(color);
        s.writeAlpha = Optional.of(color);
        return apply(b, s);
    }

    public static RenderPipeline.Builder withColorWrite(RenderPipeline.Builder b, boolean color, boolean alpha) {
        VState s = state(b);
        s.writeColor = Optional.of(color);
        s.writeAlpha = Optional.of(alpha);
        return apply(b, s);
    }

    public static RenderPipeline.Builder withDepthWrite(RenderPipeline.Builder b, boolean depth) {
        VState s = state(b);
        s.writeDepth = Optional.of(depth);
        return apply(b, s);
    }

    public static RenderPipeline.Builder withColorLogic(RenderPipeline.Builder b, LogicOp op) {
        VState s = state(b);
        s.colorLogic = Optional.of(op);
        return apply(b, s);
    }

    public static RenderPipeline.Builder withDepthBias(RenderPipeline.Builder b, float scaleFactor, float constant) {
        VState s = state(b);
        s.bias = Optional.of(new float[] {scaleFactor, constant});
        return apply(b, s);
    }

    /** 1.21.11 buildSnippet(): the snippet keeps the builder's 1.21.11 settings (never its depth bias) */
    public static RenderPipeline.Snippet buildSnippet(RenderPipeline.Builder b) {
        VState s = state(b).copy();
        RenderPipeline.Snippet snippet = b.buildSnippet();
        s.bias = Optional.empty();
        SNIPPETS.put(snippet, s);
        return snippet;
    }

    /** 1.21.11 build(): the pipeline answers the 1.21.11 values it was built with */
    public static RenderPipeline build(RenderPipeline.Builder b) {
        VState s = state(b).copy();
        RenderPipeline p = b.build();
        record(p, s);
        return p;
    }

    private static void record(RenderPipeline p, VState s) {
        PIPELINES.put(p, s);
        if (s.colorLogic.orElse(LogicOp.NONE) != LogicOp.NONE) {
            anyLogic = true;
        }
    }

    // ---------------------------------------------------------------- 1.21.11 RenderPipeline.Snippet

    /** 1.21.11 Snippet(15 optionals): a 26.1 snippet carrying the same settings, its 1.21.11 view kept */
    public static RenderPipeline.Snippet newSnippet(Optional<Identifier> vertexShader, Optional<Identifier> fragmentShader,
            Optional<ShaderDefines> shaderDefines, Optional<List<String>> samplers,
            Optional<List<RenderPipeline.UniformDescription>> uniforms, Optional<BlendFunction> blendFunction,
            Optional<DepthTestFunction> depthTestFunction, Optional<PolygonMode> polygonMode, Optional<Boolean> cull,
            Optional<Boolean> writeColor, Optional<Boolean> writeAlpha, Optional<Boolean> writeDepth,
            Optional<LogicOp> colorLogic, Optional<VertexFormat> vertexFormat, Optional<VertexFormat.Mode> vertexFormatMode) {
        VState s = new VState();
        s.blend = blendFunction;
        s.depthTest = depthTestFunction;
        s.writeColor = writeColor;
        s.writeAlpha = writeAlpha;
        s.writeDepth = writeDepth;
        s.colorLogic = colorLogic;
        // 26.1 content for 26.1 readers: the parts this snippet sets, 1.21.11's defaults for the rest of each
        Optional<ColorTargetState> color = blendFunction.isPresent() || writeColor.isPresent() || writeAlpha.isPresent()
                ? Optional.of(s.colorTarget()) : Optional.empty();
        Optional<DepthStencilState> depth = depthTestFunction.isPresent() || writeDepth.isPresent()
                ? Optional.ofNullable(s.depthStencil()) : Optional.empty();
        RenderPipeline.Snippet snippet = new RenderPipeline.Snippet(vertexShader, fragmentShader, shaderDefines, samplers,
                uniforms, color, depth, polygonMode, cull, vertexFormat, vertexFormatMode);
        SNIPPETS.put(snippet, s);
        return snippet;
    }

    public static Optional<BlendFunction> blendFunction(RenderPipeline.Snippet s) {
        return snippetView(s).blend;
    }

    public static Optional<DepthTestFunction> depthTestFunction(RenderPipeline.Snippet s) {
        return snippetView(s).depthTest;
    }

    public static Optional<Boolean> writeColor(RenderPipeline.Snippet s) {
        return snippetView(s).writeColor;
    }

    public static Optional<Boolean> writeAlpha(RenderPipeline.Snippet s) {
        return snippetView(s).writeAlpha;
    }

    public static Optional<Boolean> writeDepth(RenderPipeline.Snippet s) {
        return snippetView(s).writeDepth;
    }

    public static Optional<LogicOp> colorLogic(RenderPipeline.Snippet s) {
        return snippetView(s).colorLogic;
    }

    // ---------------------------------------------------------------- 1.21.11 RenderPipeline getters

    /**
     * A 1.21.11 mod's RenderPipeline subclass that overrides one of 1.21.11's getters (adapterHooks add
     * it): 1.21.11's GL backend asked the pipeline through those getters, 26.1's asks
     * getDepthStencilState() / getColorTargetState(), which the hooks synthesize from them
     * ({@link #depthStencilState}, {@link #colorTargetState}). The getters the class does not override
     * answer what RenderPipeline's own did.
     */
    public interface Legacy {
        default DepthTestFunction getDepthTestFunction() {
            return superGetDepthTestFunction((RenderPipeline) this);
        }

        default Optional<BlendFunction> getBlendFunction() {
            return superGetBlendFunction((RenderPipeline) this);
        }

        default LogicOp getColorLogic() {
            return superGetColorLogic((RenderPipeline) this);
        }

        default boolean isWriteColor() {
            return superIsWriteColor((RenderPipeline) this);
        }

        default boolean isWriteAlpha() {
            return superIsWriteAlpha((RenderPipeline) this);
        }

        default boolean isWriteDepth() {
            return superIsWriteDepth((RenderPipeline) this);
        }

        default float getDepthBiasScaleFactor() {
            return superGetDepthBiasScaleFactor((RenderPipeline) this);
        }

        default float getDepthBiasConstant() {
            return superGetDepthBiasConstant((RenderPipeline) this);
        }
    }

    // one adapter per getter (the hook adds the ones whose getter the class declares): each re-declares
    // its getter abstract over Legacy's default, so the class's own method is the one that answers and
    // the getters it does not declare keep one default (Legacy's), whatever adapters the class gets

    public interface LegacyDepthTest extends Legacy {
        @Override
        DepthTestFunction getDepthTestFunction();
    }

    public interface LegacyBlend extends Legacy {
        @Override
        Optional<BlendFunction> getBlendFunction();
    }

    public interface LegacyColorLogic extends Legacy {
        @Override
        LogicOp getColorLogic();
    }

    public interface LegacyWriteColor extends Legacy {
        @Override
        boolean isWriteColor();
    }

    public interface LegacyWriteAlpha extends Legacy {
        @Override
        boolean isWriteAlpha();
    }

    public interface LegacyWriteDepth extends Legacy {
        @Override
        boolean isWriteDepth();
    }

    public interface LegacyBiasScale extends Legacy {
        @Override
        float getDepthBiasScaleFactor();
    }

    public interface LegacyBiasConstant extends Legacy {
        @Override
        float getDepthBiasConstant();
    }

    /** synthesized getDepthStencilState() of a {@link Legacy} pipeline */
    public static DepthStencilState depthStencilState(Legacy p) {
        return depthStencilOf(p.getDepthTestFunction(), p.isWriteDepth(), p.getDepthBiasScaleFactor(), p.getDepthBiasConstant());
    }

    /** synthesized getColorTargetState() of a {@link Legacy} pipeline */
    public static ColorTargetState colorTargetState(Legacy p) {
        return colorTargetOf(p.getBlendFunction(), p.isWriteColor(), p.isWriteAlpha());
    }

    // a call: the override when the pipeline's class has one, as the JVM's dispatch found it in 1.21.11

    public static DepthTestFunction getDepthTestFunction(RenderPipeline p) {
        return p instanceof Legacy l ? l.getDepthTestFunction() : superGetDepthTestFunction(p);
    }

    public static Optional<BlendFunction> getBlendFunction(RenderPipeline p) {
        return p instanceof Legacy l ? l.getBlendFunction() : superGetBlendFunction(p);
    }

    public static LogicOp getColorLogic(RenderPipeline p) {
        return p instanceof Legacy l ? l.getColorLogic() : superGetColorLogic(p);
    }

    public static boolean isWriteColor(RenderPipeline p) {
        return p instanceof Legacy l ? l.isWriteColor() : superIsWriteColor(p);
    }

    public static boolean isWriteAlpha(RenderPipeline p) {
        return p instanceof Legacy l ? l.isWriteAlpha() : superIsWriteAlpha(p);
    }

    public static boolean isWriteDepth(RenderPipeline p) {
        return p instanceof Legacy l ? l.isWriteDepth() : superIsWriteDepth(p);
    }

    public static float getDepthBiasScaleFactor(RenderPipeline p) {
        return p instanceof Legacy l ? l.getDepthBiasScaleFactor() : superGetDepthBiasScaleFactor(p);
    }

    public static float getDepthBiasConstant(RenderPipeline p) {
        return p instanceof Legacy l ? l.getDepthBiasConstant() : superGetDepthBiasConstant(p);
    }

    // a mod's super.getX() (superRedirects), and what RenderPipeline's own getter answered

    public static DepthTestFunction superGetDepthTestFunction(RenderPipeline p) {
        return pipelineView(p).depthTestOrDefault();
    }

    public static Optional<BlendFunction> superGetBlendFunction(RenderPipeline p) {
        return pipelineView(p).blend;
    }

    public static LogicOp superGetColorLogic(RenderPipeline p) {
        if (!anyLogic) {
            return LogicOp.NONE;
        }
        VState v = PIPELINES.get(p);
        return v == null ? LogicOp.NONE : v.colorLogic.orElse(LogicOp.NONE);
    }

    public static boolean superIsWriteColor(RenderPipeline p) {
        return pipelineView(p).writeColor.orElse(true);
    }

    public static boolean superIsWriteAlpha(RenderPipeline p) {
        return pipelineView(p).writeAlpha.orElse(true);
    }

    public static boolean superIsWriteDepth(RenderPipeline p) {
        return pipelineView(p).writeDepth.orElse(true);
    }

    public static float superGetDepthBiasScaleFactor(RenderPipeline p) {
        return pipelineView(p).biasScale();
    }

    public static float superGetDepthBiasConstant(RenderPipeline p) {
        return pipelineView(p).biasConstant();
    }

    // ---------------------------------------------------------------- 1.21.11 RenderPipeline constructor

    /** super(...) of a 1.21.11 RenderPipeline subclass: its blend and colour / alpha writes */
    public static ColorTargetState colorTarget(Optional<BlendFunction> blend, boolean writeColor, boolean writeAlpha) {
        return colorTargetOf(blend, writeColor, writeAlpha);
    }

    /** super(...) of a 1.21.11 RenderPipeline subclass: its depth test, depth write and bias */
    public static DepthStencilState depthStencil(DepthTestFunction test, boolean writeDepth, float biasScale, float biasConstant) {
        return depthStencilOf(test, writeDepth, biasScale, biasConstant);
    }

    /** after that super(...): the 1.21.11 values it was constructed with */
    public static void constructed(RenderPipeline p, Optional<BlendFunction> blend, DepthTestFunction test,
            boolean writeColor, boolean writeAlpha, boolean writeDepth, LogicOp logic, float biasScale, float biasConstant) {
        VState s = new VState();
        s.blend = blend;
        s.depthTest = Optional.of(test);
        s.writeColor = Optional.of(writeColor);
        s.writeAlpha = Optional.of(writeAlpha);
        s.writeDepth = Optional.of(writeDepth);
        s.colorLogic = Optional.of(logic);
        s.bias = Optional.of(new float[] {biasScale, biasConstant});
        record(p, s);
    }

    // ---------------------------------------------------------------- GL

    /** 1.21.11 GlConst.toGl(DepthTestFunction) */
    public static int toGl(DepthTestFunction f) {
        return switch (f) {
            case NO_DEPTH_TEST -> 519;      // GL_ALWAYS
            case EQUAL_DEPTH_TEST -> 514;   // GL_EQUAL
            case LESS_DEPTH_TEST -> 513;    // GL_LESS
            case GREATER_DEPTH_TEST -> 516; // GL_GREATER
            case LEQUAL_DEPTH_TEST -> 515;  // GL_LEQUAL
        };
    }

    /** 1.21.11 GlStateManager._colorMask(r, g, b, a): 26.1 takes the channels as one write mask */
    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GlStateManager._colorMask((red ? ColorTargetState.WRITE_RED : 0) | (green ? ColorTargetState.WRITE_GREEN : 0)
                | (blue ? ColorTargetState.WRITE_BLUE : 0) | (alpha ? ColorTargetState.WRITE_ALPHA : 0));
    }
}
