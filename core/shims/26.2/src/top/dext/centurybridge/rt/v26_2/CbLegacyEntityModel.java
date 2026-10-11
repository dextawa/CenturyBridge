package top.dext.centurybridge.rt.v26_2;

import net.minecraft.world.entity.Entity;

/**
 * A 1.20.1 entity model's animation hooks, as the namespace wall names them: setAngles
 * (the token became "setupAnim", still with the entity and the five floats) and
 * animateModel (gone in 1.21.2; the token stays, and a model that never overrode it
 * keeps 1.20.1's empty default).
 */
public interface CbLegacyEntityModel {

    void setupAnim(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch);

    default void method_2816(Entity entity, float limbAngle, float limbDistance, float tickDelta) {
    }
}
