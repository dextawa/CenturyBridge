package top.dext.centurybridge.shims.v26_2;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 1.20.1 gave every entity an id when it was constructed (one counter for all); 26.x takes
 * it from the level, and only the server level hands one out -- an entity a mod builds on
 * the client stays unassigned, and its first getId() (world.addEntity(entity.getId(),
 * entity), the 1.20.1 way to show a client-only entity) throws. The client level hands out
 * ids again, from below zero: server-assigned ids count up from one, so the two never meet,
 * and an entity spawned from a packet still takes the server's id (recreateFromPacket).
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelEntityIdBridge {

    @Unique
    private static final AtomicInteger CENTURYBRIDGE$CLIENT_IDS = new AtomicInteger();

    public int getNextEntityId() {
        return CENTURYBRIDGE$CLIENT_IDS.decrementAndGet();
    }
}
