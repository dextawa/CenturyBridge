package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 renderer and layer methods that took the entity, over 1.21.2's that take the render state.
 *
 * 1.21.1 renderers drew from the entity: EntityRenderer.render(entity, yaw, partialTick, ...), LivingEntityRenderer's
 * setupRotations / scale / isShaking / getRenderType ... (entity, ...), RenderLayer.render(..., entity, limbSwing,
 * limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch). 1.21.2's take the state the renderer extracted.
 * The generated mixins (gen_s7render.py family 4, CbRenderState<n>Mixin) put every 1.21.1 erasure back on its class,
 * running the 1.21.2 method on a state of the entity; the generated hooks (CbRenderStateGen) send 1.21.2's calls on
 * a mod class to its 1.21.1 override, with the entity the state came from and the other values 1.21.1's caller
 * computed. This class holds what both need.
 *
 * getBob and getAttackAnim (LivingEntityRenderer, gone in 1.21.2) had no state counterpart: 1.21.1's render fed them
 * to the model (ageInTicks, attackTime), to setupRotations (bob) and to the layers (ageInTicks). They are put back with
 * their 1.21.1 bodies (CbRenderLivingMixin and, for the renderers that overrode getBob, CbRenderLivingBob*Mixin), and
 * the bridges ask the renderer for them ({@link #bob}, {@link #attackAnim}), so a mod's override is used as 1.21.1
 * used it.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderLiving {

    /** LivingEntityRenderer's 1.21.1 getBob / getAttackAnim, public (CbRenderLivingMixin) */
    public interface Legacy {
        float cb$bob(net.minecraft.class_1309 entity, float partialTick);

        float cb$attackAnim(net.minecraft.class_1309 entity, float partialTick);
    }

    /** RenderLayer's parent (CbRenderLivingLayerMixin) */
    public interface Layer {
        net.minecraft.class_3883<?, ?> cb$parent();
    }

    private CbRenderLiving() {
    }

    /** the renderer an entity is drawn with */
    public static net.minecraft.class_897<?, ?> rendererOf(net.minecraft.class_1297 entity) {
        return net.minecraft.class_310.method_1551().method_1561().method_3953(entity);
    }

    /** the renderer a layer draws for (its parent), else the entity's */
    public static net.minecraft.class_897<?, ?> rendererOf(Object layer, net.minecraft.class_1297 entity) {
        if (layer instanceof Layer l && l.cb$parent() instanceof net.minecraft.class_897<?, ?> r) {
            return r;
        }
        return rendererOf(entity);
    }

    /** a renderer, a layer's renderer, or (anything else, a model) the entity's */
    private static Object rendererFor(Object who, net.minecraft.class_1297 entity) {
        if (who instanceof net.minecraft.class_897<?, ?>) {
            return who;
        }
        return rendererOf(who, entity);
    }

    /**
     * A state of the renderer holding the entity as at this partial tick: the renderer's own reusable state when its
     * latest extraction is exactly that (the state being drawn), else a new one extracted as 1.21.2's
     * createRenderState(entity, partialTick) does (createRenderState() then extractRenderState), leaving the reusable
     * one, which a render in progress may still be reading, alone.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_10017 stateOf(net.minecraft.class_897<?, ?> renderer, net.minecraft.class_1297 entity, float partialTick) {
        net.minecraft.class_10017 reused = ((CbRenderTex.Renderer) renderer).cb$reusedState();
        if (CbRenderStateEntity.holds(reused, entity, partialTick)) {
            return reused;
        }
        net.minecraft.class_897 raw = renderer;
        net.minecraft.class_10017 s = raw.method_55269();
        raw.method_62354(entity, s, partialTick);
        return s;
    }

    /** 1.21.1 LivingEntityRenderer.render's bob (ageInTicks): the renderer's getBob(entity, partialTick) */
    public static float bob(Object who, net.minecraft.class_1297 entity, float partialTick) {
        if (entity instanceof net.minecraft.class_1309 living && rendererFor(who, entity) instanceof Legacy r) {
            return r.cb$bob(living, partialTick);
        }
        // 1.21.1's EntityRenderer has no getBob: the renderers of other entities posed with tickCount + partialTick
        return (float) entity.field_6012 + partialTick;
    }

    /** 1.21.1 LivingEntityRenderer.render's model.attackTime: the renderer's getAttackAnim(entity, partialTick) */
    public static float attackAnim(Object who, net.minecraft.class_1309 entity, float partialTick) {
        if (rendererFor(who, entity) instanceof Legacy r) {
            return r.cb$attackAnim(entity, partialTick);
        }
        return entity.method_6055(partialTick);
    }

    /** the limb swing / bob numbers of 1.21.1's LivingEntityRenderer.render, for any entity (zero for a non-living one) */
    public static CbRenderModels.Anim anim(Object who, net.minecraft.class_1297 entity, float partialTick, net.minecraft.class_10017 state) {
        if (entity instanceof net.minecraft.class_1309 living) {
            CbRenderModels.Anim a = CbRenderModels.living(living, partialTick, state);
            return new CbRenderModels.Anim(a.walkPos(), a.walkSpeed(), bob(who, entity, partialTick), a.headYaw(), a.headPitch(), partialTick);
        }
        return new CbRenderModels.Anim(0.0F, 0.0F, bob(who, entity, partialTick), 0.0F, 0.0F, partialTick);
    }
}
