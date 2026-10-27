package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;

/**
 * 1.21.11's ServerBossEvent(name, color, overlay) on 26.1, whose constructor takes the event's id first:
 * 1.21.11's drew one with Mth.createInsecureUUID() (javap class_3213.&lt;init&gt;), the id
 * CbWallBossEvents.newEventId() draws; a subclass's super constructor gets it pushed (superCtorReshapes).
 */
public final class CbWallServerBossEvents {
    private CbWallServerBossEvents() {
    }

    /** new ServerBossEvent(name, color, overlay) */
    public static ServerBossEvent serverBossEvent(Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        return new ServerBossEvent(CbWallBossEvents.newEventId(), name, color, overlay);
    }
}
