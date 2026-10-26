package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * TicketStorage.purgeStaleTickets: 1.21.5's took no argument; 1.21.6's takes the ChunkMap of the level whose tickets it
 * holds (ServerChunkCache calls ticketStorage.purgeStaleTickets(chunkMap) with its own two fields), so that it does not age a ticket of a
 * type that waits for its chunk to load while that chunk's holder is not ready (javap of both). Each ServerChunkCache
 * builds its TicketStorage and its ChunkMap together; CbChunkTicketsMixin records the pair when the cache is built.
 */
public final class CbChunkTickets {

    private static final java.util.Map<net.minecraft.class_10592, java.lang.ref.WeakReference<net.minecraft.class_3898>> MAPS =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private CbChunkTickets() {
    }

    /** CbChunkTicketsMixin: a ServerChunkCache's ticket storage and chunk map */
    public static void owned(net.minecraft.class_10592 tickets, net.minecraft.class_3898 chunkMap) {
        MAPS.put(tickets, new java.lang.ref.WeakReference<>(chunkMap));
    }

    /**
     * frameDefaults of TicketStorage.purgeStaleTickets's ChunkMap, when a 1.21.5 override entered the old no-argument
     * shape itself: the chunk map vanilla passes for that storage -- the one its ServerChunkCache was built with. A
     * storage no chunk cache owns has no level whose chunk holders its purge could consult.
     */
    public static net.minecraft.class_3898 chunkMap(net.minecraft.class_10592 tickets) {
        java.lang.ref.WeakReference<net.minecraft.class_3898> ref = MAPS.get(tickets);
        net.minecraft.class_3898 map = ref == null ? null : ref.get();
        if (map == null) {
            throw new IllegalStateException("CenturyBridge: TicketStorage.purgeStaleTickets() on a ticket storage no "
                + "ServerChunkCache owns; 1.21.6 purges against the chunk map of the level that holds the tickets");
        }
        return map;
    }
}
