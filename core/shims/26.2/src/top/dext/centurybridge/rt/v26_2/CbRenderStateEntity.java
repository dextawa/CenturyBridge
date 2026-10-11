package top.dext.centurybridge.rt.v26_2;

import net.minecraft.world.entity.Entity;

/**
 * The entity a render state was extracted from, and the partial tick it was extracted at.
 * 26.x render states are plain value bags with no way back to the entity; every 1.20.1
 * renderer, model and feature took the entity itself. Captured for every state the
 * dispatcher builds (EntityRendererCaptureBridge) and read by the legacy bridges.
 */
public interface CbRenderStateEntity {

    Entity centurybridge$entity();

    float centurybridge$partialTick();

    void centurybridge$capture(Entity entity, float partialTick);
}
