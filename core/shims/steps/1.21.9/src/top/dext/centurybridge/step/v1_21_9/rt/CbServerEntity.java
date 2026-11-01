package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3231;

/**
 * 1.21.8's ServerEntity(level, entity, updateInterval, trackDelta, Consumer broadcast, BiConsumer broadcastIgnoring)
 * on 1.21.9's ServerEntity(level, entity, updateInterval, trackDelta, ServerEntity.Synchronizer) (class_3231$class_12004;
 * javap -c of both class_3231 and ChunkMap.TrackedEntity class_3898$class_3208, which implements it):
 * <ul>
 * <li>sendToTrackingPlayers (method_18730) is 1.21.8's broadcast.</li>
 * <li>sendToTrackingPlayersAndSelf (method_18734): 1.21.8's ServerEntity did broadcast.accept(p) and then sent p to
 * the entity's own connection when it is a ServerPlayer (method_18758); 1.21.9 moved that into the synchronizer.</li>
 * <li>sendToTrackingPlayersFiltered(p, test) (method_74531): 1.21.8 passed the players to skip as a UUID list (the
 * players whose passenger state changed); 1.21.9 passes the players to send to. The skip list is the level's players
 * the test refuses (TrackedEntity sent to its tracking players only, a subset of the level's).</li>
 * </ul>
 */
public final class CbServerEntity {
    private CbServerEntity() {
    }

    /** the 1.21.8 callbacks as 1.21.9's synchronizer */
    public static class_3231.class_12004 synchronizer(class_3218 level, class_1297 entity, Consumer<class_2596<?>> broadcast,
                                                     BiConsumer<class_2596<?>, List<UUID>> broadcastIgnoring) {
        return new class_3231.class_12004() {
            @Override
            public void method_18730(class_2596<? super class_2602> packet) {
                broadcast.accept(packet);
            }

            @Override
            public void method_18734(class_2596<? super class_2602> packet) {
                broadcast.accept(packet);
                if (entity instanceof class_3222 player) {
                    player.field_13987.method_14364(packet);
                }
            }

            @Override
            public void method_74531(class_2596<? super class_2602> packet, Predicate<class_3222> test) {
                List<UUID> skip = level.method_18766(test.negate()).stream().map(class_3222::method_5667).toList();
                broadcastIgnoring.accept(packet, skip);
            }
        };
    }
}
