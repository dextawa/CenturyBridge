package top.dext.centurybridge.step.v26_1.rt;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;

/**
 * Villager and wandering trader members 26.1 moved off the 1.21.11 API (the trade tables themselves:
 * facade/VillagerTrades, rt/CbFapiTrades; addOffersFromItemListings: rt/CbWallTradeOffers).
 */
public final class CbWallTrades {

    private CbWallTrades() {
    }

    /** AbstractVillager.VILLAGER_SLOT_OFFSET (class_3988.field_30599): 1.21.11's constant, the offset 26.1's getSlot still uses */
    public static int villagerSlotOffset() {
        return 300;
    }

    /**
     * 1.21.11 new WanderingTraderSpawner(serverLevelData): its spawn delay and chance lived in the level data, 26.1's
     * in the server's WanderingTraderData, which 1.21.11 level data already reads and writes (CbWallLevelData). A
     * dimension's DerivedLevelData had none (0 / 0, writes dropped): 1.21.11's spawner over it started from the
     * defaults (24000 / 25) and kept them in memory only, so it gets a storage of its own that keeps them in memory.
     */
    public static SavedDataStorage spawnerStorage(ServerLevelData data) {
        if (data instanceof DerivedLevelData) {
            return new MemoryStorage();
        }
        MinecraftServer server = CbWallLevelData.SERVER;
        if (server == null) {
            throw new IllegalStateException(data + ": 1.21.11 WanderingTraderSpawner built with no server running");
        }
        return server.getDataStorage();
    }

    /** 1.21.11 new WanderingTraderSpawner(serverLevelData) ({@link #spawnerStorage}) */
    public static WanderingTraderSpawner newSpawner(ServerLevelData data) {
        return new WanderingTraderSpawner(spawnerStorage(data));
    }

    /** saved data handed out fresh and kept in memory, never read from or written to disk */
    static final class MemoryStorage extends SavedDataStorage {
        private final Map<SavedDataType<?>, SavedData> data = new HashMap<>();

        MemoryStorage() {
            super(Path.of("."), null, null);
        }

        @Override
        @SuppressWarnings("unchecked")
        public synchronized <T extends SavedData> T computeIfAbsent(SavedDataType<T> type) {
            return (T) data.computeIfAbsent(type, t -> t.constructor().get());
        }

        @Override
        @SuppressWarnings("unchecked")
        public synchronized <T extends SavedData> T get(SavedDataType<T> type) {
            return (T) data.get(type);
        }

        @Override
        public synchronized <T extends SavedData> void set(SavedDataType<T> type, T value) {
            data.put(type, value);
        }

        @Override
        public CompletableFuture<?> scheduleSave() {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void saveAndJoin() {
        }

        @Override
        public void close() {
        }
    }
}
