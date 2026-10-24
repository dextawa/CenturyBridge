package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * The client half of rt/CbWallInteract: the location of the last 1.21.11 interactAt whose local answer did
 * not consume the action, sent with the interact that follows it (mixin/CbWallInteractGameModeMixin).
 * Client thread only.
 */
@Environment(EnvType.CLIENT)
public final class CbWallInteractClient {

    private CbWallInteractClient() {
    }

    private static Entity pendingEntity;
    private static InteractionHand pendingHand;
    private static Vec3 pendingLocation;

    public static void pend(Entity entity, InteractionHand hand, Vec3 location) {
        pendingEntity = entity;
        pendingHand = hand;
        pendingLocation = location;
    }

    public static void clear() {
        pendingEntity = null;
        pendingHand = null;
        pendingLocation = null;
    }

    /** the kept location when the interact is for the same entity and hand, else null; nothing stays kept */
    public static Vec3 take(Entity entity, InteractionHand hand) {
        Vec3 location = pendingEntity == entity && pendingHand == hand ? pendingLocation : null;
        clear();
        return location;
    }

    /**
     * frameDefaults of the old MultiPlayerGameMode.interact hook: the hit 26.1's interact takes -- at the kept
     * interactAt location for this entity and hand, else at CbWallInteract.location (both absolute).
     */
    public static EntityHitResult hit(MultiPlayerGameMode gameMode, Player player, Entity entity, InteractionHand hand) {
        Vec3 location = pendingEntity == entity && pendingHand == hand ? pendingLocation : CbWallInteract.location(entity, player, hand);
        return new EntityHitResult(entity, location.add(entity.getX(), entity.getY(), entity.getZ()));
    }
}
