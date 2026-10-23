package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.storage.LevelData;

/**
 * The client half of {@link CbWallLevelData}. 1.21.11's ClientLevelData kept the raining flag the weather
 * packets set (START_RAINING / STOP_RAINING; CbWallClientRainMixin records it on 26.1's, which only keeps
 * the rain level) and the day time the server sent, which is 26.1's overworld clock of the client level.
 */
@Environment(EnvType.CLIENT)
public final class CbWallLevelDataClient {

    private CbWallLevelDataClient() {
    }

    /** 1.21.11 ClientLevelData's raining flag, carried by 26.1's (CbWallClientLevelDataMixin) */
    public interface RainFlag {
        boolean cb$raining();

        void cb$setRaining(boolean raining);
    }

    static boolean isClient(LevelData data) {
        return data instanceof ClientLevel.ClientLevelData;
    }

    static boolean isRaining(LevelData data) {
        return ((RainFlag) data).cb$raining();
    }

    static void setRaining(LevelData data, boolean raining) {
        ((RainFlag) data).cb$setRaining(raining);
    }

    /** the overworld clock of the client level whose data this is (the current one: the client holds one level) */
    static long getDayTime(LevelData data) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level.getLevelData() != data) {
            throw new IllegalStateException(data + ": 1.21.11 client level data of no current client level");
        }
        return level.getOverworldClockTime();
    }
}
