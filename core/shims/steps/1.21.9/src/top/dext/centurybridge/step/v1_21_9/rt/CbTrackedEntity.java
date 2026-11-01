package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.List;
import java.util.UUID;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_3898;

/**
 * ChunkMap.TrackedEntity.broadcastIgnorePlayers(packet, uuids) (method_66006, 1.21.8) is broadcastIgnoring(packet,
 * predicate) (method_74531) on 1.21.9: javap -c, the same loop over the seen-by connections, sending to those 1.21.8's
 * list did not hold and 1.21.9's predicate accepts -- the list becomes the predicate "UUID not in the list".
 */
public final class CbTrackedEntity {
    private CbTrackedEntity() {
    }

    @SuppressWarnings("unchecked")
    public static void method_66006(class_3898.class_3208 tracked, class_2596<?> packet, List<UUID> ignored) {
        tracked.method_74531((class_2596<? super class_2602>) packet, p -> !ignored.contains(p.method_5667()));
    }
}
