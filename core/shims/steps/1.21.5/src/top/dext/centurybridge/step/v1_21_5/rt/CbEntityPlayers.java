package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1657;
import net.minecraft.class_1934;

/**
 * 1.21.4's Player.isCreative() / isSpectator() were abstract (AbstractClientPlayer and ServerPlayer answered them from
 * the game mode); 1.21.5 derives both from a new abstract Player.gameMode() and renamed isCreative (method_7337 ->
 * method_68878). A 1.21.4 call of isCreative takes the new name (methodRenames), a 1.21.4 override is asked under it
 * (overrideHooks); a 1.21.4 class extending Player directly, which implements isCreative / isSpectator and knows no
 * gameMode, gets gameMode() answered from them (abstractFills) -- what its own answers say it is.
 */
public final class CbEntityPlayers {

    private CbEntityPlayers() {
    }

    /** gameMode() of a 1.21.4 Player subclass: SPECTATOR / CREATIVE as its own isSpectator / isCreative say, else SURVIVAL */
    public static class_1934 gameMode(class_1657 player) {
        if (player.method_7325()) {
            return class_1934.field_9219;
        }
        return player.method_68878() ? class_1934.field_9220 : class_1934.field_9215;
    }
}
