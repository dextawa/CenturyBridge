package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.nbt.CompoundTag;

/** CbWallPlayerLists for calls through 1.21.11's IntegratedPlayerList (a client class) */
@Environment(EnvType.CLIENT)
public final class CbWallPlayerListsClient {
    private CbWallPlayerListsClient() {
    }

    /** a mod's call of IntegratedPlayerList.getSingleplayerData() */
    public static CompoundTag getSingleplayerData(IntegratedPlayerList list) {
        return CbWallPlayerLists.getSingleplayerData(list);
    }

    /** a mod's super.getSingleplayerData() in an IntegratedPlayerList subclass */
    public static CompoundTag superGetSingleplayerData(IntegratedPlayerList list) {
        return CbWallPlayerLists.superIntegrated(list);
    }
}
