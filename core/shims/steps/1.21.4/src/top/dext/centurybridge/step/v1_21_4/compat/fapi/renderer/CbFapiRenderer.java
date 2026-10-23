package top.dext.centurybridge.step.v1_21_4.compat.fapi.renderer;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.GlintMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialView;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.util.TriState;

/**
 * Fabric API 1.21.3's Renderer API v1 on 1.21.4's (Renderer API 5).
 * <ul>
 * <li>Models: 1.21.3's emitBlockQuads(view, state, pos, random, context) / emitItemQuads(stack, random, context) are 1.21.4's
 *     emitBlockQuads(emitter, view, state, pos, random, cullTest) / emitItemQuads(emitter, random). A 1.21.3 override gets a
 *     {@link RenderContext} over what 1.21.4 passed: its emitter (which holds the transform stack now), the cull test for a block,
 *     and for an item the stack and display context the item being drawn was resolved for ({@link #beginItem}: 1.21.4's emit has
 *     neither). A call of the 1.21.3 shape becomes the 1.21.4 one, its context riding along in the cull test / random supplier,
 *     so a 1.21.3 model called by another gets the caller's context, as in 1.21.3.</li>
 * <li>Meshes: 1.21.3's MeshBuilder over 1.21.4's MutableMesh, and a 1.21.3 renderer's builder as a MutableMesh.</li>
 * <li>Quads and materials: the default methods 1.21.4 removed, with 1.21.3's bodies; an implementation's own override of one is
 *     still the one called (the {@code CbLegacy*} adapters); colorIndex is tintIndex, glint(TriState) is glintMode.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbFapiRenderer {
    private CbFapiRenderer() {
    }

    // ---- models ----

    /** a 1.21.3 model overriding emitBlockQuads */
    public interface CbLegacyBlockQuads {
        void emitBlockQuads(net.minecraft.class_1920 blockView, net.minecraft.class_2680 state, net.minecraft.class_2338 pos,
            Supplier<net.minecraft.class_5819> randomSupplier, RenderContext context);
    }

    /** a 1.21.3 model overriding emitItemQuads */
    public interface CbLegacyItemQuads {
        void emitItemQuads(net.minecraft.class_1799 stack, Supplier<net.minecraft.class_5819> randomSupplier, RenderContext context);
    }

    /** 1.21.4's emitBlockQuads on a 1.21.3 override: the caller's context when a 1.21.3 call made it, else one over what 1.21.4 passed */
    public static void emitBlockQuads(CbLegacyBlockQuads model, QuadEmitter emitter, net.minecraft.class_1920 blockView, net.minecraft.class_2680 state,
        net.minecraft.class_2338 pos, Supplier<net.minecraft.class_5819> randomSupplier, Predicate<net.minecraft.class_2350> cullTest) {
        RenderContext context = cullTest instanceof ContextCull carried ? carried.context : new BlockContext(emitter, cullTest);
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    /** 1.21.4's emitItemQuads on a 1.21.3 override: the caller's stack and context when a 1.21.3 call made it, else the drawn item's */
    public static void emitItemQuads(CbLegacyItemQuads model, QuadEmitter emitter, Supplier<net.minecraft.class_5819> randomSupplier) {
        if (randomSupplier instanceof ItemCall call) {
            model.emitItemQuads(call.stack, call.random, call.context);
            return;
        }
        net.minecraft.class_1799 stack = itemDepth > 0 ? itemStacks[itemDepth - 1] : null;
        net.minecraft.class_811 mode = itemDepth > 0 ? itemModes[itemDepth - 1] : null;
        model.emitItemQuads(stack != null ? stack : net.minecraft.class_1799.field_8037, randomSupplier,
            new ItemContext(emitter, mode != null ? mode : net.minecraft.class_811.field_4315));
    }

    /** a 1.21.3 call's context, for the emitter argument of 1.21.4's shape */
    public static QuadEmitter emitter(RenderContext context) {
        return context.getEmitter();
    }

    /** a 1.21.3 call's context, for the cull test of 1.21.4's emitBlockQuads (it carries the context to a 1.21.3 model) */
    public static Predicate<net.minecraft.class_2350> cullTest(RenderContext context) {
        return new ContextCull(context);
    }

    /** a 1.21.3 call's random, stack and context, for the random supplier of 1.21.4's emitItemQuads (it carries the rest to a 1.21.3 model) */
    public static Supplier<net.minecraft.class_5819> itemRandom(Supplier<net.minecraft.class_5819> random, net.minecraft.class_1799 stack,
        RenderContext context) {
        return new ItemCall(random, stack, context);
    }

    /** ForwardingBakedModel's 1.21.3 emitBlockQuads: the wrapped model's, with the same context */
    public static void emitBlockQuads(net.minecraft.class_1087 model, net.minecraft.class_1920 blockView, net.minecraft.class_2680 state,
        net.minecraft.class_2338 pos, Supplier<net.minecraft.class_5819> randomSupplier, RenderContext context) {
        ((FabricBakedModel) model).emitBlockQuads(context.getEmitter(), blockView, state, pos, randomSupplier, new ContextCull(context));
    }

    /** ForwardingBakedModel's 1.21.3 emitItemQuads: the wrapped model's, with the same stack and context */
    public static void emitItemQuads(net.minecraft.class_1087 model, net.minecraft.class_1799 stack, Supplier<net.minecraft.class_5819> randomSupplier,
        RenderContext context) {
        ((FabricBakedModel) model).emitItemQuads(context.getEmitter(), new ItemCall(randomSupplier, stack, context));
    }

    /** a ForwardingBakedModel subclass's super.getOverrides(): 1.21.3's ForwardingBakedModel answered the wrapped model's */
    public static top.dext.centurybridge.step.v1_21_4.facade.BakedOverrides superGetOverrides(ForwardingBakedModel model) {
        return top.dext.centurybridge.step.v1_21_4.rt.CbItemRender.getOverrides(model.wrapped);
    }

    /** the cull test of a 1.21.3 context, which it carries */
    static final class ContextCull implements Predicate<net.minecraft.class_2350> {
        final RenderContext context;

        ContextCull(RenderContext context) {
            this.context = context;
        }

        @Override
        public boolean test(net.minecraft.class_2350 face) {
            return this.context.isFaceCulled(face);
        }
    }

    /** the random supplier of a 1.21.3 item call, which carries its stack and context */
    static final class ItemCall implements Supplier<net.minecraft.class_5819> {
        final Supplier<net.minecraft.class_5819> random;
        final net.minecraft.class_1799 stack;
        final RenderContext context;

        ItemCall(Supplier<net.minecraft.class_5819> random, net.minecraft.class_1799 stack, RenderContext context) {
            this.random = random;
            this.stack = stack;
            this.context = context;
        }

        @Override
        public net.minecraft.class_5819 get() {
            return this.random.get();
        }
    }

    /**
     * A 1.21.3 context over 1.21.4's emitter. Transforms go on the emitter, which applies them as each quad is emitted (1.21.3's
     * context applied them the same way); hasTransform answers for the transforms pushed through this context -- one a 1.21.4
     * caller pushed before is applied all the same, so what a 1.21.3 model decides from the answer (emit a mesh directly or quad by
     * quad) draws the same.
     */
    abstract static class LegacyContext implements RenderContext {
        final QuadEmitter emitter;
        private int transforms;

        LegacyContext(QuadEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public QuadEmitter getEmitter() {
            return this.emitter;
        }

        @Override
        public boolean hasTransform() {
            return this.transforms > 0;
        }

        @Override
        public void pushTransform(QuadTransform transform) {
            if (transform == null) {
                throw new NullPointerException("Renderer extension attempted to push null transform.");
            }
            this.emitter.pushTransform(transform);
            this.transforms++;
        }

        @Override
        public void popTransform() {
            this.emitter.popTransform();
            this.transforms--;
        }
    }

    /** 1.21.3's block render context: faces culled as the block renderer's cull test says */
    static final class BlockContext extends LegacyContext {
        private final Predicate<net.minecraft.class_2350> cullTest;

        BlockContext(QuadEmitter emitter, Predicate<net.minecraft.class_2350> cullTest) {
            super(emitter);
            this.cullTest = cullTest;
        }

        @Override
        public boolean isFaceCulled(net.minecraft.class_2350 face) {
            return this.cullTest.test(face);
        }

        @Override
        public net.minecraft.class_811 itemTransformationMode() {
            throw new IllegalStateException("itemTransformationMode() can only be called on an item render context.");
        }
    }

    /** 1.21.3's item render context: the display context the item is drawn in */
    static final class ItemContext extends LegacyContext {
        private final net.minecraft.class_811 mode;

        ItemContext(QuadEmitter emitter, net.minecraft.class_811 mode) {
            super(emitter);
            this.mode = mode;
        }

        @Override
        public boolean isFaceCulled(net.minecraft.class_2350 face) {
            throw new IllegalStateException("isFaceCulled can only be called on a block render context.");
        }

        @Override
        public net.minecraft.class_811 itemTransformationMode() {
            return this.mode;
        }
    }

    // the item being drawn (render thread): its stack and display context, while its ItemStackRenderState renders (CbFapiItemStateMixin)
    private static net.minecraft.class_1799[] itemStacks = new net.minecraft.class_1799[4];
    private static net.minecraft.class_811[] itemModes = new net.minecraft.class_811[4];
    private static int itemDepth;

    public static void beginItem(net.minecraft.class_1799 stack, net.minecraft.class_811 mode) {
        if (itemDepth == itemStacks.length) {
            itemStacks = java.util.Arrays.copyOf(itemStacks, itemDepth * 2);
            itemModes = java.util.Arrays.copyOf(itemModes, itemDepth * 2);
        }
        itemStacks[itemDepth] = stack;
        itemModes[itemDepth] = mode;
        itemDepth++;
    }

    public static void endItem() {
        if (itemDepth > 0) {
            itemDepth--;
            itemStacks[itemDepth] = null;
            itemModes[itemDepth] = null;
        }
    }

    /** an ItemStackRenderState that knows the stack it was resolved for (CbFapiItemStateMixin) */
    public interface CbItemStackHolder {
        void cb$setStack(net.minecraft.class_1799 stack);
    }

    // ---- renderer and meshes ----

    /** was Renderer.meshBuilder(): a 1.21.3 renderer's own builder, else one over 1.21.4's mutable mesh */
    public static MeshBuilder meshBuilder(Renderer renderer) {
        if (renderer instanceof CbLegacyRenderer legacy) {
            return legacy.meshBuilder();
        }
        return new MutableMeshBuilder(renderer.mutableMesh());
    }

    /** a 1.21.3 renderer implementation (its meshBuilder) */
    public interface CbLegacyRenderer {
        MeshBuilder meshBuilder();
    }

    /** 1.21.4's mutableMesh() on a 1.21.3 renderer: a mutable mesh over its builder */
    public static MutableMesh mutableMesh(CbLegacyRenderer renderer) {
        return new BuilderMesh(renderer.meshBuilder());
    }

    /** a 1.21.3 Mesh implementation's size(), which 1.21.3 had none of: its quads, counted */
    public static int size(Mesh mesh) {
        int[] count = new int[1];
        mesh.forEach(quad -> count[0]++);
        return count[0];
    }

    /** 1.21.3's MeshBuilder over 1.21.4's MutableMesh: build() answers the quads so far and empties the builder */
    static final class MutableMeshBuilder implements MeshBuilder {
        private final MutableMesh mesh;

        MutableMeshBuilder(MutableMesh mesh) {
            this.mesh = mesh;
        }

        @Override
        public QuadEmitter getEmitter() {
            return this.mesh.emitter();
        }

        @Override
        public Mesh build() {
            Mesh built = this.mesh.immutableCopy();
            this.mesh.clear();
            return built;
        }
    }

    /**
     * 1.21.4's MutableMesh over a 1.21.3 renderer's builder, whose only read is build() (which empties it): every read builds the
     * mesh and emits its quads back into the builder, so the builder holds what it held.
     */
    static final class BuilderMesh implements MutableMesh {
        private final MeshBuilder builder;

        BuilderMesh(MeshBuilder builder) {
            this.builder = builder;
        }

        private Mesh read() {
            Mesh built = this.builder.build();
            built.outputTo(this.builder.getEmitter());
            return built;
        }

        @Override
        public QuadEmitter emitter() {
            return this.builder.getEmitter();
        }

        @Override
        public void forEachMutable(Consumer<? super MutableQuadView> action) {
            Mesh built = this.builder.build();
            QuadEmitter emitter = this.builder.getEmitter();
            built.forEach(quad -> {
                emitter.copyFrom(quad);
                action.accept(emitter);
                emitter.emit();
            });
        }

        @Override
        public Mesh immutableCopy() {
            return this.read();
        }

        @Override
        public void clear() {
            this.builder.build();
        }

        @Override
        public int size() {
            return this.read().size();
        }

        @Override
        public void forEach(Consumer<? super QuadView> action) {
            this.read().forEach(action);
        }

        @Override
        public void outputTo(QuadEmitter emitter) {
            this.read().outputTo(emitter);
        }
    }

    // ---- quads: 1.21.3's default methods ----

    public interface CbLegacyViewSpriteColor {
        int spriteColor(int vertexIndex, int spriteIndex);
    }

    public interface CbLegacyViewSpriteU {
        float spriteU(int vertexIndex, int spriteIndex);
    }

    public interface CbLegacyViewSpriteV {
        float spriteV(int vertexIndex, int spriteIndex);
    }

    public interface CbLegacyViewCopyTo {
        void copyTo(MutableQuadView target);
    }

    public interface CbLegacyViewToVanilla {
        void toVanilla(int spriteIndex, int[] target, int targetIndex, boolean isItem);
    }

    public interface CbLegacyViewToBakedQuad {
        net.minecraft.class_777 toBakedQuad(int spriteIndex, net.minecraft.class_1058 sprite, boolean isItem);
    }

    /** was QuadView.colorIndex() */
    public static int colorIndex(QuadView quad) {
        return quad.tintIndex();
    }

    /** QuadView.faceNormal() answers a Vector3fc since 1.21.4; 1.21.3's Vector3f was the quad's own vector */
    public static org.joml.Vector3f faceNormal(org.joml.Vector3fc normal) {
        return normal instanceof org.joml.Vector3f vector ? vector : new org.joml.Vector3f(normal);
    }

    public static int spriteColor(QuadView quad, int vertexIndex, int spriteIndex) {
        return quad instanceof CbLegacyViewSpriteColor own ? own.spriteColor(vertexIndex, spriteIndex) : quad.color(vertexIndex);
    }

    public static int superSpriteColor(QuadView quad, int vertexIndex, int spriteIndex) {
        return quad.color(vertexIndex);
    }

    public static float spriteU(QuadView quad, int vertexIndex, int spriteIndex) {
        return quad instanceof CbLegacyViewSpriteU own ? own.spriteU(vertexIndex, spriteIndex) : quad.u(vertexIndex);
    }

    public static float superSpriteU(QuadView quad, int vertexIndex, int spriteIndex) {
        return quad.u(vertexIndex);
    }

    public static float spriteV(QuadView quad, int vertexIndex, int spriteIndex) {
        return quad instanceof CbLegacyViewSpriteV own ? own.spriteV(vertexIndex, spriteIndex) : quad.v(vertexIndex);
    }

    public static float superSpriteV(QuadView quad, int vertexIndex, int spriteIndex) {
        return quad.v(vertexIndex);
    }

    public static void copyTo(QuadView quad, MutableQuadView target) {
        if (quad instanceof CbLegacyViewCopyTo own) {
            own.copyTo(target);
        } else {
            superCopyTo(quad, target);
        }
    }

    /** 1.21.3's copyTo: the target takes the quad, keeping its own material */
    public static void superCopyTo(QuadView quad, MutableQuadView target) {
        RenderMaterial material = target.material();
        target.copyFrom(quad);
        target.material(material);
    }

    public static void toVanilla(QuadView quad, int spriteIndex, int[] target, int targetIndex, boolean isItem) {
        if (quad instanceof CbLegacyViewToVanilla own) {
            own.toVanilla(spriteIndex, target, targetIndex, isItem);
        } else {
            quad.toVanilla(target, targetIndex);
        }
    }

    public static void superToVanilla(QuadView quad, int spriteIndex, int[] target, int targetIndex, boolean isItem) {
        quad.toVanilla(target, targetIndex);
    }

    public static net.minecraft.class_777 toBakedQuad(QuadView quad, int spriteIndex, net.minecraft.class_1058 sprite, boolean isItem) {
        return quad instanceof CbLegacyViewToBakedQuad own ? own.toBakedQuad(spriteIndex, sprite, isItem) : quad.toBakedQuad(sprite);
    }

    public static net.minecraft.class_777 superToBakedQuad(QuadView quad, int spriteIndex, net.minecraft.class_1058 sprite, boolean isItem) {
        return quad.toBakedQuad(sprite);
    }

    public interface CbLegacyMutableFromVanilla {
        MutableQuadView fromVanilla(int[] quadData, int startIndex, boolean isItem);
    }

    public interface CbLegacyMutableSprite {
        MutableQuadView sprite(int vertexIndex, int spriteIndex, float u, float v);
    }

    public interface CbLegacyMutableSpriteVec {
        MutableQuadView sprite(int vertexIndex, int spriteIndex, net.minecraft.class_241 uv);
    }

    public interface CbLegacyMutableSpriteBake {
        MutableQuadView spriteBake(int spriteIndex, net.minecraft.class_1058 sprite, int bakeFlags);
    }

    public interface CbLegacyMutableSpriteColor {
        MutableQuadView spriteColor(int vertexIndex, int spriteIndex, int color);
    }

    public interface CbLegacyMutableSpriteColors {
        MutableQuadView spriteColor(int spriteIndex, int c0, int c1, int c2, int c3);
    }

    /** was MutableQuadView.colorIndex(int) */
    public static MutableQuadView colorIndex(MutableQuadView quad, int colorIndex) {
        return quad.tintIndex(colorIndex);
    }

    public static MutableQuadView fromVanilla(MutableQuadView quad, int[] quadData, int startIndex, boolean isItem) {
        return quad instanceof CbLegacyMutableFromVanilla own ? own.fromVanilla(quadData, startIndex, isItem) : quad.fromVanilla(quadData, startIndex);
    }

    public static MutableQuadView superFromVanilla(MutableQuadView quad, int[] quadData, int startIndex, boolean isItem) {
        return quad.fromVanilla(quadData, startIndex);
    }

    public static MutableQuadView sprite(MutableQuadView quad, int vertexIndex, int spriteIndex, float u, float v) {
        return quad instanceof CbLegacyMutableSprite own ? own.sprite(vertexIndex, spriteIndex, u, v) : quad.uv(vertexIndex, u, v);
    }

    public static MutableQuadView superSprite(MutableQuadView quad, int vertexIndex, int spriteIndex, float u, float v) {
        return quad.uv(vertexIndex, u, v);
    }

    public static MutableQuadView sprite(MutableQuadView quad, int vertexIndex, int spriteIndex, net.minecraft.class_241 uv) {
        return quad instanceof CbLegacyMutableSpriteVec own ? own.sprite(vertexIndex, spriteIndex, uv) : quad.uv(vertexIndex, uv.field_1343, uv.field_1342);
    }

    public static MutableQuadView superSprite(MutableQuadView quad, int vertexIndex, int spriteIndex, net.minecraft.class_241 uv) {
        return quad.uv(vertexIndex, uv.field_1343, uv.field_1342);
    }

    public static MutableQuadView spriteBake(MutableQuadView quad, int spriteIndex, net.minecraft.class_1058 sprite, int bakeFlags) {
        return quad instanceof CbLegacyMutableSpriteBake own ? own.spriteBake(spriteIndex, sprite, bakeFlags) : quad.spriteBake(sprite, bakeFlags);
    }

    public static MutableQuadView superSpriteBake(MutableQuadView quad, int spriteIndex, net.minecraft.class_1058 sprite, int bakeFlags) {
        return quad.spriteBake(sprite, bakeFlags);
    }

    public static MutableQuadView spriteColor(MutableQuadView quad, int vertexIndex, int spriteIndex, int color) {
        return quad instanceof CbLegacyMutableSpriteColor own ? own.spriteColor(vertexIndex, spriteIndex, color) : quad.color(vertexIndex, color);
    }

    public static MutableQuadView superSpriteColor(MutableQuadView quad, int vertexIndex, int spriteIndex, int color) {
        return quad.color(vertexIndex, color);
    }

    public static MutableQuadView spriteColor(MutableQuadView quad, int spriteIndex, int c0, int c1, int c2, int c3) {
        return quad instanceof CbLegacyMutableSpriteColors own ? own.spriteColor(spriteIndex, c0, c1, c2, c3) : superSpriteColor(quad, spriteIndex, c0, c1, c2, c3);
    }

    public static MutableQuadView superSpriteColor(MutableQuadView quad, int spriteIndex, int c0, int c1, int c2, int c3) {
        quad.color(c0, c1, c2, c3);
        return quad;
    }

    public interface CbLegacyEmitterFromVanilla {
        QuadEmitter fromVanilla(int[] quadData, int startIndex, boolean isItem);
    }

    public interface CbLegacyEmitterSprite {
        QuadEmitter sprite(int vertexIndex, int spriteIndex, float u, float v);
    }

    public interface CbLegacyEmitterSpriteVec {
        QuadEmitter sprite(int vertexIndex, int spriteIndex, net.minecraft.class_241 uv);
    }

    public interface CbLegacyEmitterSpriteBake {
        QuadEmitter spriteBake(int spriteIndex, net.minecraft.class_1058 sprite, int bakeFlags);
    }

    public interface CbLegacyEmitterSpriteColor {
        QuadEmitter spriteColor(int vertexIndex, int spriteIndex, int color);
    }

    public interface CbLegacyEmitterSpriteColors {
        QuadEmitter spriteColor(int spriteIndex, int c0, int c1, int c2, int c3);
    }

    public interface CbLegacyEmitterSpriteUnitSquare {
        QuadEmitter spriteUnitSquare(int spriteIndex);
    }

    /** was QuadEmitter.colorIndex(int) */
    public static QuadEmitter colorIndex(QuadEmitter quad, int colorIndex) {
        return quad.tintIndex(colorIndex);
    }

    public static QuadEmitter fromVanilla(QuadEmitter quad, int[] quadData, int startIndex, boolean isItem) {
        return quad instanceof CbLegacyEmitterFromVanilla own ? own.fromVanilla(quadData, startIndex, isItem) : quad.fromVanilla(quadData, startIndex);
    }

    public static QuadEmitter superFromVanilla(QuadEmitter quad, int[] quadData, int startIndex, boolean isItem) {
        return quad.fromVanilla(quadData, startIndex);
    }

    public static QuadEmitter sprite(QuadEmitter quad, int vertexIndex, int spriteIndex, float u, float v) {
        return quad instanceof CbLegacyEmitterSprite own ? own.sprite(vertexIndex, spriteIndex, u, v) : quad.uv(vertexIndex, u, v);
    }

    public static QuadEmitter superSprite(QuadEmitter quad, int vertexIndex, int spriteIndex, float u, float v) {
        return quad.uv(vertexIndex, u, v);
    }

    public static QuadEmitter sprite(QuadEmitter quad, int vertexIndex, int spriteIndex, net.minecraft.class_241 uv) {
        return quad instanceof CbLegacyEmitterSpriteVec own ? own.sprite(vertexIndex, spriteIndex, uv) : quad.uv(vertexIndex, uv.field_1343, uv.field_1342);
    }

    public static QuadEmitter superSprite(QuadEmitter quad, int vertexIndex, int spriteIndex, net.minecraft.class_241 uv) {
        return quad.uv(vertexIndex, uv.field_1343, uv.field_1342);
    }

    public static QuadEmitter spriteBake(QuadEmitter quad, int spriteIndex, net.minecraft.class_1058 sprite, int bakeFlags) {
        return quad instanceof CbLegacyEmitterSpriteBake own ? own.spriteBake(spriteIndex, sprite, bakeFlags) : quad.spriteBake(sprite, bakeFlags);
    }

    public static QuadEmitter superSpriteBake(QuadEmitter quad, int spriteIndex, net.minecraft.class_1058 sprite, int bakeFlags) {
        return quad.spriteBake(sprite, bakeFlags);
    }

    public static QuadEmitter spriteColor(QuadEmitter quad, int vertexIndex, int spriteIndex, int color) {
        return quad instanceof CbLegacyEmitterSpriteColor own ? own.spriteColor(vertexIndex, spriteIndex, color) : quad.color(vertexIndex, color);
    }

    public static QuadEmitter superSpriteColor(QuadEmitter quad, int vertexIndex, int spriteIndex, int color) {
        return quad.color(vertexIndex, color);
    }

    public static QuadEmitter spriteColor(QuadEmitter quad, int spriteIndex, int c0, int c1, int c2, int c3) {
        return quad instanceof CbLegacyEmitterSpriteColors own ? own.spriteColor(spriteIndex, c0, c1, c2, c3) : superSpriteColor(quad, spriteIndex, c0, c1, c2, c3);
    }

    public static QuadEmitter superSpriteColor(QuadEmitter quad, int spriteIndex, int c0, int c1, int c2, int c3) {
        quad.color(c0, c1, c2, c3);
        return quad;
    }

    public static QuadEmitter spriteUnitSquare(QuadEmitter quad, int spriteIndex) {
        return quad instanceof CbLegacyEmitterSpriteUnitSquare own ? own.spriteUnitSquare(spriteIndex) : superSpriteUnitSquare(quad, spriteIndex);
    }

    public static QuadEmitter superSpriteUnitSquare(QuadEmitter quad, int spriteIndex) {
        quad.uvUnitSquare();
        return quad;
    }

    /** a 1.21.3 QuadView implementation's tintIndex(): its colorIndex() */
    public interface CbLegacyViewColorIndex {
        int colorIndex();
    }

    public static int tintIndex(CbLegacyViewColorIndex quad) {
        return quad.colorIndex();
    }

    /** a 1.21.3 QuadView implementation's faceNormal() as 1.21.4's Vector3fc */
    public interface CbLegacyViewFaceNormal {
        org.joml.Vector3f faceNormal();
    }

    public static org.joml.Vector3fc faceNormal(CbLegacyViewFaceNormal quad) {
        return quad.faceNormal();
    }

    /** a 1.21.3 MutableQuadView implementation's tintIndex(int): its colorIndex(int) */
    public interface CbLegacyMutableColorIndex {
        MutableQuadView colorIndex(int colorIndex);
    }

    public static MutableQuadView tintIndex(CbLegacyMutableColorIndex quad, int tintIndex) {
        return quad.colorIndex(tintIndex);
    }

    /** a 1.21.3 QuadEmitter implementation's tintIndex(int): its colorIndex(int) */
    public interface CbLegacyEmitterColorIndex {
        QuadEmitter colorIndex(int colorIndex);
    }

    public static QuadEmitter tintIndex(CbLegacyEmitterColorIndex quad, int tintIndex) {
        return quad.colorIndex(tintIndex);
    }

    public static MutableQuadView tintIndexMutable(CbLegacyEmitterColorIndex quad, int tintIndex) {
        return quad.colorIndex(tintIndex);
    }

    // ---- materials ----

    /** RenderMaterial.MATERIAL_STANDARD is STANDARD_ID since 1.21.4 (same id) */
    public static net.minecraft.class_2960 MATERIAL_STANDARD() {
        return RenderMaterial.STANDARD_ID;
    }

    public interface CbLegacyMaterialSpriteDepth {
        int spriteDepth();
    }

    public static int spriteDepth(RenderMaterial material) {
        return material instanceof CbLegacyMaterialSpriteDepth own ? own.spriteDepth() : 1;
    }

    /** 1.21.3's default: one sprite layer (multi-sprite materials had long been retired) */
    public static int superSpriteDepth(RenderMaterial material) {
        return 1;
    }

    /** 1.21.3's glint TriState as 1.21.4's GlintMode: the item's own, a forced standard glint, none */
    public static GlintMode glintMode(TriState glint) {
        return switch (glint) {
            case DEFAULT -> GlintMode.DEFAULT;
            case TRUE -> GlintMode.STANDARD;
            case FALSE -> GlintMode.NONE;
        };
    }

    /** 1.21.4's GlintMode as 1.21.3's TriState: the item's own, else whether there is a glint (1.21.3 had one kind) */
    public static TriState glint(GlintMode mode) {
        return switch (mode) {
            case DEFAULT -> TriState.DEFAULT;
            case NONE -> TriState.FALSE;
            case STANDARD, SPECIAL -> TriState.TRUE;
        };
    }

    /** was MaterialFinder.glint(TriState) */
    public static MaterialFinder glint(MaterialFinder finder, TriState glint) {
        return finder.glintMode(glintMode(glint));
    }

    /** was MaterialView.glint() */
    public static TriState glint(MaterialView view) {
        return glint(view.glintMode());
    }

    /** a 1.21.3 MaterialFinder implementation's glintMode(GlintMode): its glint(TriState) */
    public interface CbLegacyFinderGlint {
        MaterialFinder glint(TriState glint);
    }

    public static MaterialFinder glintMode(CbLegacyFinderGlint finder, GlintMode mode) {
        return finder.glint(glint(mode));
    }

    /** a 1.21.3 MaterialView implementation's glintMode(): its glint() */
    public interface CbLegacyViewGlint {
        TriState glint();
    }

    public static GlintMode glintMode(CbLegacyViewGlint view) {
        return glintMode(view.glint());
    }

    public interface CbLegacyFinderBlendLayer {
        MaterialFinder blendMode(int spriteIndex, net.minecraft.class_1921 renderLayer);
    }

    public interface CbLegacyFinderBlendMode {
        MaterialFinder blendMode(int spriteIndex, BlendMode blendMode);
    }

    public interface CbLegacyFinderDisableAo {
        MaterialFinder disableAo(int spriteIndex, boolean disable);
    }

    public interface CbLegacyFinderDisableDiffuse {
        MaterialFinder disableDiffuse(int spriteIndex, boolean disable);
    }

    public interface CbLegacyFinderEmissive {
        MaterialFinder emissive(int spriteIndex, boolean isEmissive);
    }

    public interface CbLegacyFinderSpriteDepth {
        MaterialFinder spriteDepth(int depth);
    }

    public static MaterialFinder blendMode(MaterialFinder finder, int spriteIndex, net.minecraft.class_1921 renderLayer) {
        return finder instanceof CbLegacyFinderBlendLayer own ? own.blendMode(spriteIndex, renderLayer) : superBlendMode(finder, spriteIndex, renderLayer);
    }

    public static MaterialFinder superBlendMode(MaterialFinder finder, int spriteIndex, net.minecraft.class_1921 renderLayer) {
        return finder.blendMode(BlendMode.fromRenderLayer(renderLayer));
    }

    public static MaterialFinder blendMode(MaterialFinder finder, int spriteIndex, BlendMode blendMode) {
        return finder instanceof CbLegacyFinderBlendMode own ? own.blendMode(spriteIndex, blendMode) : superBlendMode(finder, spriteIndex, blendMode);
    }

    public static MaterialFinder superBlendMode(MaterialFinder finder, int spriteIndex, BlendMode blendMode) {
        return finder.blendMode(blendMode == null ? BlendMode.DEFAULT : blendMode);
    }

    public static MaterialFinder disableAo(MaterialFinder finder, int spriteIndex, boolean disable) {
        return finder instanceof CbLegacyFinderDisableAo own ? own.disableAo(spriteIndex, disable) : superDisableAo(finder, spriteIndex, disable);
    }

    public static MaterialFinder superDisableAo(MaterialFinder finder, int spriteIndex, boolean disable) {
        return finder.ambientOcclusion(disable ? TriState.FALSE : TriState.DEFAULT);
    }

    public static MaterialFinder disableDiffuse(MaterialFinder finder, int spriteIndex, boolean disable) {
        return finder instanceof CbLegacyFinderDisableDiffuse own ? own.disableDiffuse(spriteIndex, disable) : finder.disableDiffuse(disable);
    }

    public static MaterialFinder superDisableDiffuse(MaterialFinder finder, int spriteIndex, boolean disable) {
        return finder.disableDiffuse(disable);
    }

    public static MaterialFinder emissive(MaterialFinder finder, int spriteIndex, boolean isEmissive) {
        return finder instanceof CbLegacyFinderEmissive own ? own.emissive(spriteIndex, isEmissive) : finder.emissive(isEmissive);
    }

    public static MaterialFinder superEmissive(MaterialFinder finder, int spriteIndex, boolean isEmissive) {
        return finder.emissive(isEmissive);
    }

    public static MaterialFinder spriteDepth(MaterialFinder finder, int depth) {
        return finder instanceof CbLegacyFinderSpriteDepth own ? own.spriteDepth(depth) : finder;
    }

    /** 1.21.3's default: a no-op, one sprite layer being all there was */
    public static MaterialFinder superSpriteDepth(MaterialFinder finder, int depth) {
        return finder;
    }
}
