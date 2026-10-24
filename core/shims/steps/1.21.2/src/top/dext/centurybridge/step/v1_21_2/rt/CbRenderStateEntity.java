package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The entity a 1.21.2 render state was last extracted from, and the partial tick it was extracted at.
 *
 * 1.21.2 renders an entity in two halves: EntityRenderer.extractRenderState(entity, state, partialTick)
 * copies what drawing needs into a state object, and render(state, ...) / model.setupAnim(state) draw from
 * the state alone. A 1.21.1 renderer, layer or model was handed the entity itself (render(entity, yaw,
 * partialTick, ...), setupAnim(entity, 5 floats), getTextureLocation(entity)). Every state the game extracts
 * is given back its entity here (CbRenderStateCaptureMixin, at EntityRenderer.extractRenderState HEAD, so the
 * capture is always the entity of the current extraction -- 1.21.2 reuses one state object per renderer for
 * every entity it draws), and the 1.21.1 bridges read it.
 *
 * Held weakly: a renderer keeps its reusable state for the whole session, and a strong reference there
 * would keep the last entity it drew (and its level) alive after it is gone. A state built without an entity
 * (ParrotOnShoulderLayer's parrot, a mod's own) answers null, and the bridges then fall back to 1.21.2's own
 * behaviour.
 */
@Environment(EnvType.CLIENT)
public interface CbRenderStateEntity {

    /** the entity of the latest extraction into this state, or null (never extracted, or collected) */
    net.minecraft.class_1297 cb$entity();

    float cb$partialTick();

    void cb$capture(net.minecraft.class_1297 entity, float partialTick);

    /** the entity's tickCount at the latest extraction */
    int cb$tick();

    /**
     * Whether this state holds an extraction of the entity as it is now: same entity, same tick, same partial
     * tick -- the inputs of everything extractRenderState copies, so extracting again would give the same state.
     */
    static boolean holds(Object state, net.minecraft.class_1297 entity, float partialTick) {
        return state instanceof CbRenderStateEntity s && entity != null && s.cb$entity() == entity
            && s.cb$tick() == entity.field_6012 && s.cb$partialTick() == partialTick;
    }

    /** the entity of a state, null for a state no extraction filled */
    static net.minecraft.class_1297 entityOf(Object state) {
        return state instanceof CbRenderStateEntity s ? s.cb$entity() : null;
    }

    static float partialTickOf(Object state) {
        return state instanceof CbRenderStateEntity s ? s.cb$partialTick() : 1.0F;
    }
}
