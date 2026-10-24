package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** CustomBossEvents's event map and its codec (rt/CbWallBossEvents) */
@Mixin(CustomBossEvents.class)
public interface CbWallBossEventsAccess {

    @Accessor("events")
    Map<Identifier, CustomBossEvent> cb$events();

    @Accessor("EVENTS_CODEC")
    static Codec<Map<Identifier, CustomBossEvent.Packed>> cb$eventsCodec() {
        throw new AssertionError();
    }
}
