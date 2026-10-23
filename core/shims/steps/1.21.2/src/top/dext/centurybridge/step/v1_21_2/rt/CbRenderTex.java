package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's getTextureLocation(entity) over 1.21.2's state-driven renderers.
 *
 * 1.21.1: EntityRenderer.getTextureLocation(T entity) was abstract and every renderer answered it (RenderLayerParent
 * declared it too, RenderLayer.getTextureLocation(entity) asked the parent). LivingEntityRenderer.render drew the model
 * with getTextureLocation(entity). 1.21.2: EntityRenderer has none; LivingEntityRenderer.getTextureLocation(S state) is
 * abstract and called by its render (ArrowRenderer has its own, protected, taking ArrowRenderState); the other
 * renderers draw a texture of their own.
 *
 * The 1.21.1 methods are put back on the 1.21.2 classes (CbRenderTexMixin on EntityRenderer, CbRenderTexParentMixin,
 * CbRenderTexLayerMixin, and per renderer the generated CbRenderTex<n>Mixin, each the 1.21.1 class's own erasure and its
 * javac bridges), so a 1.21.1 mod's call, super call and override link. What they answer:
 * - a renderer whose 1.21.2 texture is state-driven ({@link #living}): its 1.21.2 getTextureLocation of a state
 *   extracted from the entity, run as vanilla's own (the guard is held, so a 1.21.1 override synthesized on a mod class
 *   falls through to the vanilla implementation instead of coming back into the mod's method);
 * - any other renderer: what its 1.21.1 body returned (the generated mixins; the textures are the same constants
 *   1.21.2 keeps).
 * The override hooks (CbRenderTexGen) send 1.21.2's call getTextureLocation(state) on a mod renderer to its 1.21.1
 * override, with the entity the state was extracted from.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTex {

    /**
     * 1.21.1's getTextureLocation(entity), as every EntityRenderer has it again (CbRenderTexMixin): calling it through
     * this interface reaches the object's own implementation -- a mod's override or the renderer's put-back one
     */
    public interface Legacy {
        net.minecraft.class_2960 method_3931(net.minecraft.class_1297 entity);
    }

    /** EntityRenderer's reusable state (CbRenderTexMixin) */
    public interface Renderer {
        net.minecraft.class_10017 cb$reusedState();
    }

    /** BoatRenderer's texture (1.21.2 has one renderer, and one texture, per boat type) */
    public interface BoatTexture {
        net.minecraft.class_2960 cb$boatTexture();
    }

    /** a renderer's protected 1.21.2 state texture, for a hook's fall-through (generated mixins) */
    public interface StateTexture {
        net.minecraft.class_2960 cb$stateTexture(net.minecraft.class_10017 state);
    }

    private CbRenderTex() {
    }

    /**
     * The partial tick 1.21.2's LevelRenderer extracts an entity at (class_761: deltaTracker.getGameTimeDeltaPartialTick
     * with true for an entity the tick rate manager does not freeze -- a frozen one takes false, which differs only
     * while the game is frozen).
     */
    public static float partialTick() {
        return net.minecraft.class_310.method_1551().method_61966().method_60637(true);
    }

    /**
     * A state of this renderer holding the entity as it is now: the renderer's own reusable state when its latest
     * extraction is of this entity at this tick and partial tick (the state being drawn), else a new one extracted the
     * way 1.21.2's createRenderState(entity, partialTick) does (createRenderState() then extractRenderState), without
     * disturbing the reusable one a render in progress may still be reading.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_10017 stateOf(net.minecraft.class_897<?, ?> renderer, net.minecraft.class_1297 entity) {
        float pt = partialTick();
        net.minecraft.class_10017 reused = ((Renderer) renderer).cb$reusedState();
        if (CbRenderStateEntity.holds(reused, entity, pt)) {
            return reused;
        }
        net.minecraft.class_897 raw = renderer;
        net.minecraft.class_10017 s = raw.method_55269();
        raw.method_62354(entity, s, pt);
        return s;
    }

    /** a LivingEntityRenderer's 1.21.1 getTextureLocation(entity): its 1.21.2 getTextureLocation(state), as vanilla's */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2960 living(Object self, net.minecraft.class_1297 entity) {
        net.minecraft.class_922 r = (net.minecraft.class_922) self;
        net.minecraft.class_10042 s = (net.minecraft.class_10042) stateOf(r, entity);
        boolean entered = Guard.cbEnter(r);
        try {
            return r.method_3885(s);
        } finally {
            if (entered) {
                Guard.cbExit(r);
            }
        }
    }

    /**
     * What a 1.21.1 renderer whose only texture is the mod's override answers for a state no extraction filled (or
     * one of an entity its override does not take): 1.21.2 has no implementation above to fall back to (the method is
     * abstract there), and the missing texture is what the game draws for a texture it cannot resolve.
     */
    public static net.minecraft.class_2960 missing(net.minecraft.class_10017 state) {
        return net.minecraft.class_1047.method_4539();
    }
}
