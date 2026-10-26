package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_11368;
import net.minecraft.class_1934;
import net.minecraft.class_3222;

/**
 * 1.21.8's ServerPlayer.loadGameTypes(input), which 1.21.9 folded into readAdditionalSaveData: the player's game mode
 * and previous game mode read from the saved data (none when the input is null), the game mode settled for a new
 * player as 1.21.8 settled it (the server's forced or default mode when the data names none), both set on the player's
 * game mode manager -- 1.21.8's body, with 1.21.9's own readers (the null input answered as 1.21.8's reader did).
 */
public final class CbPlayerGameTypes {

    private CbPlayerGameTypes() {
    }

    /** ServerPlayer.loadGameTypes(input) */
    public static void method_32748(class_3222 player, class_11368 input) {
        class_1934 gameType = input == null ? null : class_3222.method_32745(input, "playerGameType");
        class_1934 previous = input == null ? null : class_3222.method_32745(input, "previousPlayerGameType");
        player.field_13974.method_14261(player.method_32746(gameType), previous);
    }
}
