package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.function.Consumer;
import net.minecraft.class_1297;
import net.minecraft.class_2596;
import net.minecraft.class_3218;
import net.minecraft.class_3231;

/**
 * new ServerEntity(level, entity, updateInterval, trackDelta, broadcast): 1.21.5 added a sixth argument, a broadcaster
 * that skips some players (javap 1.21.5 class_3231: it sends only ClientboundSetPassengersPacket, ignoring the
 * passengers' own players). 1.21.4 sent that packet through broadcast to everyone (javap 1.21.4 class_3231
 * sendChanges: Consumer.accept(new ClientboundSetPassengersPacket)), so the 1.21.4 call gets a broadcaster that does
 * the same.
 */
public final class CbServerEntities {
    private CbServerEntities() {
    }

    public static class_3231 newServerEntity(class_3218 level, class_1297 entity, int updateInterval, boolean trackDelta,
                                             Consumer<class_2596<?>> broadcast) {
        return new class_3231(level, entity, updateInterval, trackDelta, broadcast, (packet, ignored) -> broadcast.accept(packet));
    }
}
