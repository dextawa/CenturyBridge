package top.dext.centurybridge.step.v26_2.rt;

import java.util.OptionalInt;
import net.minecraft.network.protocol.game.ServerboundSpectatorActionPacket;

/**
 * 26.1.2's ServerboundSpectateEntityPacket(entityId), which 26.2 folded into ServerboundSpectatorActionPacket
 * (OptionalInt spectateEntityId: present = spectate that entity, as 26.1.2's packet always asked; the handler
 * ServerGamePacketListener.handleSpectatorAction takes the place of handleSpectateEntity, javap of both).
 */
public final class CbS26Packets {
    private CbS26Packets() {}

    /** 26.1.2 new ServerboundSpectateEntityPacket(entityId) */
    public static ServerboundSpectatorActionPacket cbNewSpectate(int entityId) {
        return new ServerboundSpectatorActionPacket(OptionalInt.of(entityId));
    }

    /**
     * 26.1.2 ServerboundSpectateEntityPacket.entityId(): the id asked for; -1 (no entity) for a 26.2 packet without one,
     * which 26.1.2's handler would have looked up and not found
     */
    public static int spectateEntityId(ServerboundSpectatorActionPacket p) {
        return p.spectateEntityId().orElse(-1);
    }
}
