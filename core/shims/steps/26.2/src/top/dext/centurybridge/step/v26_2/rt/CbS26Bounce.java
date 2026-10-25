package top.dext.centurybridge.step.v26_2.rt;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * 26.1.2's Block.updateEntityMovementAfterFallOn(level, entity) on 26.2 (javap of both).
 *
 * <p>26.1.2: Entity.move zeroed the horizontal movement on an axis that collided, then on a vertical collision asked
 * the block under the entity: Block zeroed the vertical movement, SlimeBlock and BedBlock bounced (unless the entity
 * suppressed bouncing) with -y, times 0.66 on a bed, times 0.8 for an entity that is no LivingEntity. 26.2 dropped the
 * method: Entity.restituteMovementAfterCollisions computes the bounce from the block's bounce restitution.
 *
 * <p>The member is restored on Block, SlimeBlock and BedBlock with 26.1.2's bodies (mixin/CbS26BlockFallOnMixin,
 * CbS26SlimeFallOnMixin, CbS26BedFallOnMixin): a 26.1.2 call or super call links to it, a 26.1.2 class's override
 * overrides it. A block whose class overrides it below 26.2's and the layer's classes ({@link #legacy}) answers the
 * vertical movement as 26.1.2 asked it, after 26.2's Entity.restituteMovementAfterCollisions has run in full (so a
 * 26.2 mod's mixins on it still fire; mixin/CbS26EntityFallOnMixin). Every other block keeps 26.2's restitution.
 */
public final class CbS26Bounce {
    private CbS26Bounce() {}

    /** 26.1.2's shape of Block.updateEntityMovementAfterFallOn, restored on Block */
    public interface FallOn {
        void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity);
    }

    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            for (Class<?> k = c; k != null && !k.getName().startsWith("net.minecraft.")
                    && !k.getName().startsWith("top.dext.centurybridge."); k = k.getSuperclass()) {
                for (Method m : k.getDeclaredMethods()) {
                    if (m.getName().equals("updateEntityMovementAfterFallOn") && m.getReturnType() == void.class
                            && !Modifier.isStatic(m.getModifiers()) && m.getParameterCount() == 2
                            && m.getParameterTypes()[0] == BlockGetter.class && m.getParameterTypes()[1] == Entity.class) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    /** whether the block's class overrides 26.1.2's updateEntityMovementAfterFallOn below 26.2's and the layer's */
    public static boolean legacy(Block block) {
        return OVERRIDES.get(block.getClass());
    }

    /** 26.1.2 Block.updateEntityMovementAfterFallOn */
    public static void stop(Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }

    /** 26.1.2 SlimeBlock / BedBlock bounceUp: factor 1 for a slime block, 0.66 for a bed */
    public static void bounceUp(Entity entity, double factor) {
        Vec3 movement = entity.getDeltaMovement();
        if (movement.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(movement.x, -movement.y * factor * d, movement.z);
        }
    }

    /**
     * mixin/CbS26EntityFallOnMixin, at the end of 26.2's Entity.restituteMovementAfterCollisions, for a block that
     * overrides the member: on a vertical collision the vertical movement goes back to what it was before 26.2's
     * restitution ({@code before}) and the block is asked, as 26.1.2's Entity.move asked it; the horizontal movement
     * stays what 26.2 (and any mixin on it) made of it.
     */
    public static void legacyFallOn(Entity entity, Block block, Vec3 before) {
        if (entity.verticalCollision) {
            Vec3 movement = entity.getDeltaMovement();
            entity.setDeltaMovement(movement.x, before.y, movement.z);
            ((FallOn) block).updateEntityMovementAfterFallOn(entity.level(), entity);
        }
    }
}
