package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;

/**
 * 1.21.11's PlayerList.getSingleplayerData() on 26.1, which dropped it. 1.21.11's PlayerList answered null
 * (javap class_3324.method_14567); its IntegratedPlayerList answered the singleplayer owner's data as saved
 * (save(player) kept the owner's tag, class_1130.method_14577). 26.1 keeps the owner's saved data in the
 * player data storage, read by PlayerList.loadPlayerData(owner) (which takes the world's singleplayer data for
 * the owner); the integrated server's answer is that, the owner being the server's singleplayer profile.
 */
public final class CbWallPlayerLists {
    private CbWallPlayerLists() {
    }

    /** 1.21.11's getSingleplayerData(), declared by a mod's player list subclass (adapterHooks) */
    public interface LegacySingleplayerData {
        CompoundTag getSingleplayerData();
    }

    /** a mod's call of getSingleplayerData(): a 1.21.11 override's answer, else the body of the list's 1.21.11 class */
    public static CompoundTag getSingleplayerData(PlayerList list) {
        if (list instanceof LegacySingleplayerData legacy) {
            return legacy.getSingleplayerData();
        }
        return list.getServer().isDedicatedServer() ? superPlayerList(list) : superIntegrated(list);
    }

    /** 1.21.11 PlayerList.getSingleplayerData(): null */
    public static CompoundTag superPlayerList(PlayerList list) {
        return null;
    }

    /** 1.21.11 IntegratedPlayerList.getSingleplayerData(): the singleplayer owner's saved data */
    public static CompoundTag superIntegrated(PlayerList list) {
        GameProfile owner = list.getServer().getSingleplayerProfile();
        return owner == null ? null : list.loadPlayerData(new NameAndId(owner)).orElse(null);
    }
}
