package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.ChatAbilities;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Input;

/**
 * 1.21.11's LocalPlayer(minecraft, level, connection, stats, recipeBook, lastSentInput, wasSprinting) on 26.1,
 * whose constructor also takes the player's chat abilities: 26.1's MultiPlayerGameMode.createPlayer passes
 * minecraft.computeChatAbilities() (javap), as here.
 */
@Environment(EnvType.CLIENT)
public final class CbWallLocalPlayers {
    private CbWallLocalPlayers() {
    }

    /** new LocalPlayer(minecraft, level, connection, stats, recipeBook, lastSentInput, wasSprinting) */
    public static LocalPlayer localPlayer(Minecraft minecraft, ClientLevel level, ClientPacketListener connection, StatsCounter stats,
        ClientRecipeBook recipeBook, Input lastSentInput, boolean wasSprinting) {
        return new LocalPlayer(minecraft, level, connection, stats, recipeBook, lastSentInput, wasSprinting, minecraft.computeChatAbilities());
    }

    /** the chat abilities 26.1's createPlayer gives a player (a subclass's super constructor) */
    public static ChatAbilities chatAbilities() {
        return Minecraft.getInstance().computeChatAbilities();
    }
}
