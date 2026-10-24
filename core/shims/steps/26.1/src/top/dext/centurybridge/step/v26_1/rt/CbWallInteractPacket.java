package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.dext.centurybridge.step.v26_1.facade.ServerboundInteractPacketHandler;

/**
 * 1.21.11's ServerboundInteractPacket -- one packet for three actions: INTERACT (a hand), INTERACT_AT (a hand and a
 * location on the entity) and ATTACK -- on 26.1's split: ServerboundInteractPacket (a hand, a location, the
 * secondary action) and ServerboundAttackPacket (the entity alone). 1.21.11 code holds such a packet as a Packet
 * (classRenames), which is either 26.1 packet; its members are these.
 * <ul>
 * <li>INTERACT_AT is 26.1's interact packet exactly. INTERACT had no location: 26.1's protocol always carries one,
 *     so a 1.21.11 INTERACT goes as an interaction at the entity's origin (26.1's server runs the at-location
 *     interaction then the plain one -- for an armor stand, at its feet).</li>
 * <li>ATTACK is 26.1's attack packet; 26.1 sends no secondary-action flag with it (its server reads sneaking from
 *     the player), so isUsingSecondaryAction() of an attack answers false.</li>
 * <li>dispatch(handler): an attack calls onAttack; an interaction calls onInteraction(hand, location) and then
 *     onInteraction(hand) -- the two packets a 1.21.11 client sent for one right click when the first did not
 *     consume it.</li>
 * </ul>
 */
public final class CbWallInteractPacket {

    private CbWallInteractPacket() {
    }

    /** 1.21.11 createAttackPacket(entity, usingSecondaryAction) */
    public static Packet<?> attack(Entity entity, boolean secondary) {
        return new ServerboundAttackPacket(entity.getId());
    }

    /** 1.21.11 createInteractionPacket(entity, usingSecondaryAction, hand): INTERACT */
    public static Packet<?> interaction(Entity entity, boolean secondary, InteractionHand hand) {
        return new ServerboundInteractPacket(entity.getId(), hand, Vec3.ZERO, secondary);
    }

    /** 1.21.11 createInteractionPacket(entity, usingSecondaryAction, hand, location): INTERACT_AT */
    public static Packet<?> interaction(Entity entity, boolean secondary, InteractionHand hand, Vec3 location) {
        return new ServerboundInteractPacket(entity.getId(), hand, location, secondary);
    }

    private static int entityId(Packet<?> packet) {
        if (packet instanceof ServerboundInteractPacket p) {
            return p.entityId();
        }
        return ((ServerboundAttackPacket) packet).entityId();
    }

    /** getTarget(level) */
    public static Entity getTarget(Packet<?> packet, ServerLevel level) {
        return level.getEntityOrPart(entityId(packet));
    }

    /** isUsingSecondaryAction() */
    public static boolean isUsingSecondaryAction(Packet<?> packet) {
        return packet instanceof ServerboundInteractPacket p && p.usingSecondaryAction();
    }

    /** isWithinRange(player, box, buffer): 1.21.11's attack range for an attack, interaction range otherwise */
    public static boolean isWithinRange(Packet<?> packet, ServerPlayer player, AABB box, double buffer) {
        if (packet instanceof ServerboundAttackPacket) {
            return player.isWithinAttackRange(player.getMainHandItem(), box, buffer);
        }
        return player.isWithinEntityInteractionRange(box, buffer);
    }

    /** dispatch(handler) */
    public static void dispatch(Packet<?> packet, ServerboundInteractPacketHandler handler) {
        if (packet instanceof ServerboundInteractPacket p) {
            handler.method_34220(p.hand(), p.location());
            handler.method_34219(p.hand());
        } else {
            entityId(packet);
            handler.method_34218();
        }
    }

    /** handle(listener): 1.21.11's handed itself to the listener's handleInteract */
    public static void handle(Packet<?> packet, ServerGamePacketListener listener) {
        handleInteract(listener, packet);
    }

    /** ServerGamePacketListener.handleInteract(packet): 26.1's handler of whichever packet it is */
    public static void handleInteract(ServerGamePacketListener listener, Packet<?> packet) {
        if (packet instanceof ServerboundAttackPacket a) {
            listener.handleAttack(a);
        } else {
            listener.handleInteract((ServerboundInteractPacket) packet);
        }
    }

    /** a cast to 1.21.11's ServerboundInteractPacket: either 26.1 packet passes, anything else fails as 1.21.11's cast would */
    public static Object cast(Object o) {
        if (o == null || o instanceof ServerboundInteractPacket || o instanceof ServerboundAttackPacket) {
            return o;
        }
        throw new ClassCastException(o.getClass().getName() + " cannot be cast to 1.21.11's ServerboundInteractPacket");
    }

    /** instanceof 1.21.11's ServerboundInteractPacket */
    public static boolean is(Object o) {
        return o instanceof ServerboundInteractPacket || o instanceof ServerboundAttackPacket;
    }
}
