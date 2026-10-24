package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * 1.21.11's ServerLevelData wandering trader id: the UUID of the last wandering trader the server spawned,
 * kept in level.dat ("WanderingTraderId") and set by WanderingTraderSpawner. 26.1 dropped it (its data fixer
 * discards the key), so the layer keeps it as saved data of the server's own, under the key 1.21.11 used,
 * and records each trader 26.1's spawner spawns (mixin/CbWallTraderSpawnerMixin).
 */
public final class CbWallTraderId extends SavedData {

    private static final Codec<CbWallTraderId> CODEC = RecordCodecBuilder.create(i -> i.group(
        UUIDUtil.CODEC.optionalFieldOf("WanderingTraderId").forGetter(d -> Optional.ofNullable(d.id))
    ).apply(i, id -> new CbWallTraderId(id.orElse(null))));

    /**
     * 26.1 runs every saved data it reads through a data fix type (it takes no null); a file written at the
     * running version is left as it is, and the wandering trader data is the type this record belongs with
     */
    public static final SavedDataType<CbWallTraderId> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath("centurybridge", "wandering_trader_id"),
        () -> new CbWallTraderId(null), CODEC, DataFixTypes.SAVED_DATA_WANDERING_TRADER);

    private UUID id;

    private CbWallTraderId(UUID id) {
        this.id = id;
    }

    public static CbWallTraderId of(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public UUID id() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
        this.setDirty();
    }
}
