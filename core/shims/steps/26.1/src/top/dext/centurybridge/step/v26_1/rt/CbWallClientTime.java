package top.dext.centurybridge.step.v26_1.rt;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientClockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;

/**
 * 1.21.11's client day time on 26.1. 1.21.11's ClientLevel kept a day time in its level data, ticked when the
 * server said so: setTimeFromServer(gameTime, dayTime, tickDayTime) set both times and the flag
 * (javap class_638.method_29089), ClientLevelData.setDayTime(dayTime) the day time (class_638$class_5271.method_165).
 * 26.1 keeps the day time as the dimension's default clock (DimensionType.defaultClock) in the level's
 * ClientClockManager: its total ticks, advanced at its rate each tick (ClientPacketListener.handleSetTime hands
 * the server's clock states to it). The day time goes to that clock's total ticks, the flag to its rate (1 or
 * 0); a dimension without a default clock has no day time in 26.1. The clock instance is opened by the layer's
 * access widener.
 */
@Environment(EnvType.CLIENT)
public final class CbWallClientTime {
    private CbWallClientTime() {
    }

    /** 1.21.11's setTimeFromServer(gameTime, dayTime, tickDayTime), declared by a mod's level subclass (adapterHooks) */
    public interface LegacySetTimeFromServer {
        void setTimeFromServer(long gameTime, long dayTime, boolean tickDayTime);
    }

    /** 1.21.11's ClientLevelData.setDayTime(dayTime), declared by a mod's level data subclass (adapterHooks) */
    public interface LegacySetDayTime {
        void setDayTime(long dayTime);
    }

    /** a mod's call of level.setTimeFromServer(gameTime, dayTime, tickDayTime) */
    public static void setTimeFromServer(ClientLevel level, long gameTime, long dayTime, boolean tickDayTime) {
        if (level instanceof LegacySetTimeFromServer legacy) {
            legacy.setTimeFromServer(gameTime, dayTime, tickDayTime);
            return;
        }
        superSetTimeFromServer(level, gameTime, dayTime, tickDayTime);
    }

    /** 1.21.11's body: the game time, then the day time and whether it ticks */
    public static void superSetTimeFromServer(ClientLevel level, long gameTime, long dayTime, boolean tickDayTime) {
        level.setTimeFromServer(gameTime);
        ClientClockManager.ClockInstance clock = dayClock(level);
        if (clock != null) {
            clock.totalTicks = dayTime;
            clock.rate = tickDayTime ? 1.0F : 0.0F;
        }
    }

    /** a mod's call of levelData.setDayTime(dayTime) */
    public static void setDayTime(ClientLevel.ClientLevelData data, long dayTime) {
        if (data instanceof LegacySetDayTime legacy) {
            legacy.setDayTime(dayTime);
            return;
        }
        superSetDayTime(data, dayTime);
    }

    /** 1.21.11's body: the day time of the level the data belongs to */
    public static void superSetDayTime(ClientLevel.ClientLevelData data, long dayTime) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getLevelData() == data) {
            ClientClockManager.ClockInstance clock = dayClock(level);
            if (clock != null) {
                clock.totalTicks = dayTime;
            }
        }
    }

    private static ClientClockManager.ClockInstance dayClock(ClientLevel level) {
        Optional<Holder<WorldClock>> clock = level.dimensionType().defaultClock();
        return clock.map(level.clockManager()::getInstance).orElse(null);
    }
}
