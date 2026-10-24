package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;

/**
 * 1.21.11's EndDragonFight.Data record: the dragon fight's saved state, which 1.21.11 kept in the world data
 * (level.dat's "DragonFight") and handed to and from the fight. 26.1's fight (EnderDragonFight) is saved
 * data of its own and has no such record; this is 1.21.11's -- components, accessors, record equality and
 * codec as they were, under the intermediary names 1.21.11's record had at runtime. Its component fields
 * are public (1.21.11's were private, which an access widener could open; none reaches this class).
 * rt/CbWallDragonFight converts it to and from 26.1's fight.
 */
public final class EndDragonFightData {

    public final boolean comp_1540;
    public final boolean comp_1541;
    public final boolean comp_1542;
    public final boolean comp_1543;
    public final Optional<UUID> comp_1544;
    public final Optional<BlockPos> comp_1545;
    public final Optional<List<Integer>> comp_1546;

    public EndDragonFightData(boolean needsStateScanning, boolean dragonKilled, boolean previouslyKilled, boolean isRespawning,
        Optional<UUID> dragonUUID, Optional<BlockPos> exitPortalLocation, Optional<List<Integer>> gateways) {
        this.comp_1540 = needsStateScanning;
        this.comp_1541 = dragonKilled;
        this.comp_1542 = previouslyKilled;
        this.comp_1543 = isRespawning;
        this.comp_1544 = dragonUUID;
        this.comp_1545 = exitPortalLocation;
        this.comp_1546 = gateways;
    }

    /** 1.21.11's CODEC */
    public static final Codec<EndDragonFightData> field_44945 = RecordCodecBuilder.create(i -> i.group(
        Codec.BOOL.fieldOf("NeedsStateScanning").orElse(true).forGetter(EndDragonFightData::comp_1540),
        Codec.BOOL.fieldOf("DragonKilled").orElse(false).forGetter(EndDragonFightData::comp_1541),
        Codec.BOOL.fieldOf("PreviouslyKilled").orElse(false).forGetter(EndDragonFightData::comp_1542),
        Codec.BOOL.lenientOptionalFieldOf("IsRespawning", false).forGetter(EndDragonFightData::comp_1543),
        UUIDUtil.CODEC.lenientOptionalFieldOf("Dragon").forGetter(EndDragonFightData::comp_1544),
        BlockPos.CODEC.lenientOptionalFieldOf("ExitPortalLocation").forGetter(EndDragonFightData::comp_1545),
        Codec.list(Codec.INT).lenientOptionalFieldOf("Gateways").forGetter(EndDragonFightData::comp_1546)
    ).apply(i, EndDragonFightData::new));

    /** 1.21.11's DEFAULT */
    public static final EndDragonFightData field_44946 = new EndDragonFightData(true, false, false, false, Optional.empty(), Optional.empty(),
        Optional.empty());

    /** needsStateScanning */
    public boolean comp_1540() {
        return this.comp_1540;
    }

    /** dragonKilled */
    public boolean comp_1541() {
        return this.comp_1541;
    }

    /** previouslyKilled */
    public boolean comp_1542() {
        return this.comp_1542;
    }

    /** isRespawning */
    public boolean comp_1543() {
        return this.comp_1543;
    }

    /** dragonUUID */
    public Optional<UUID> comp_1544() {
        return this.comp_1544;
    }

    /** exitPortalLocation */
    public Optional<BlockPos> comp_1545() {
        return this.comp_1545;
    }

    /** gateways */
    public Optional<List<Integer>> comp_1546() {
        return this.comp_1546;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EndDragonFightData d && this.comp_1540 == d.comp_1540 && this.comp_1541 == d.comp_1541 && this.comp_1542 == d.comp_1542
            && this.comp_1543 == d.comp_1543 && this.comp_1544.equals(d.comp_1544) && this.comp_1545.equals(d.comp_1545)
            && this.comp_1546.equals(d.comp_1546);
    }

    @Override
    public int hashCode() {
        int h = Boolean.hashCode(this.comp_1540);
        h = 31 * h + Boolean.hashCode(this.comp_1541);
        h = 31 * h + Boolean.hashCode(this.comp_1542);
        h = 31 * h + Boolean.hashCode(this.comp_1543);
        h = 31 * h + Objects.hashCode(this.comp_1544);
        h = 31 * h + Objects.hashCode(this.comp_1545);
        return 31 * h + Objects.hashCode(this.comp_1546);
    }

    /** a record's toString, under the name and component names 1.21.11's record had at runtime */
    @Override
    public String toString() {
        return "class_8576[comp_1540=" + this.comp_1540 + ", comp_1541=" + this.comp_1541 + ", comp_1542=" + this.comp_1542 + ", comp_1543="
            + this.comp_1543 + ", comp_1544=" + this.comp_1544 + ", comp_1545=" + this.comp_1545 + ", comp_1546=" + this.comp_1546 + "]";
    }
}
