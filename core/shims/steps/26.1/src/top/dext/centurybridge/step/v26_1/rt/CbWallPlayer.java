package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * 1.21.11 Player.displayClientMessage(message, overlay), split by 26.1 into sendOverlayMessage and
 * sendSystemMessage: the server player sends the system chat packet with the overlay flag either way (26.1's
 * two methods pass true / false to the same sendSystemMessage(message, overlay)), the local player shows it
 * on the action bar or in chat, and the base Player does nothing, as 1.21.11's did.
 */
public final class CbWallPlayer {

    private CbWallPlayer() {
    }

    /** 1.21.11 Player.displayClientMessage(Component, boolean) */
    public static void displayClientMessage(Player player, Component message, boolean overlay) {
        if (overlay) {
            player.sendOverlayMessage(message);
        } else {
            player.sendSystemMessage(message);
        }
    }

    /** A 1.21.11 Player subclass overriding displayClientMessage */
    public interface LegacyMessages {
        void displayClientMessage(Component message, boolean overlay);
    }

    /** 26.1 sendOverlayMessage of a 1.21.11 override: displayClientMessage(message, true) */
    public static void sendOverlayMessage(LegacyMessages player, Component message) {
        player.displayClientMessage(message, true);
    }

    /** 26.1 sendSystemMessage of a 1.21.11 override: displayClientMessage(message, false) */
    public static void sendSystemMessage(LegacyMessages player, Component message) {
        player.displayClientMessage(message, false);
    }
}
