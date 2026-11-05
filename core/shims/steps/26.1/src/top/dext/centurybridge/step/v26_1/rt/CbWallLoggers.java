package top.dext.centurybridge.step.v26_1.rt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.21.11 private static LOGGER fields (opened by access wideners) 26.1 no longer declares on these owners. Each was
 * LogUtils.getLogger() in the owner's clinit (javap of client-1.21.11-intermediary-code.jar), the logger named after the
 * owner class; each getter answers the logger of the owner's 26.1 class (javap 26.1: all seven classes present), the
 * same slf4j logger 26.1 code of that class would get by name.
 */
public final class CbWallLoggers {
    private CbWallLoggers() {
    }

    public static Logger worldLoader() {
        return LoggerFactory.getLogger(net.minecraft.server.WorldLoader.class);
    }

    public static Logger customBossEvents() {
        return LoggerFactory.getLogger(net.minecraft.server.bossevents.CustomBossEvents.class);
    }

    public static Logger abstractPackResources() {
        return LoggerFactory.getLogger(net.minecraft.server.packs.AbstractPackResources.class);
    }

    public static Logger brain() {
        return LoggerFactory.getLogger(net.minecraft.world.entity.ai.Brain.class);
    }

    public static Logger levelSettings() {
        return LoggerFactory.getLogger(net.minecraft.world.level.LevelSettings.class);
    }

    public static Logger mapItemSavedData() {
        return LoggerFactory.getLogger(net.minecraft.world.level.saveddata.maps.MapItemSavedData.class);
    }

    public static Logger timerQueue() {
        return LoggerFactory.getLogger(net.minecraft.world.level.timers.TimerQueue.class);
    }
}
