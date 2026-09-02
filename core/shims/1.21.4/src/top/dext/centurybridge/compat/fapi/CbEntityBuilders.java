package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1317;
import net.minecraft.class_2902;
import top.dext.centurybridge.facade.v1_21_4.class_1317_class_1319;
import top.dext.centurybridge.rt.v1_21_4.CbPending;

/**
 * fabric-object-builder-api-v1's FabricEntityTypeBuilder members whose 1.20.1 descriptor died
 * (instanceRedirects: receiver first). {@code build()} lost its no-arg form in 1.21.2 -- the
 * EntityType now wants its registry key at construction, but the legacy corpus (30 mods) builds
 * first and registers later. Same two-phase repair as blocks and items: build under a placeholder
 * key, remember the type, and let the registry add hook (CbPending.backfill) re-derive the two
 * fields the key materialized (translation key, default loot table).
 */
public final class CbEntityBuilders {

    public static <T extends class_1297> class_1299<T> build(FabricEntityTypeBuilder<T> self) {
        class_1299<T> type = self.build(CbPending.pendingEntityKey());
        CbPending.rememberEntityType(type);
        return type;
    }

    public static <T extends class_1309> class_1299<T> build(FabricEntityTypeBuilder.Living<T> self) {
        class_1299<T> type = self.build(CbPending.pendingEntityKey());
        CbPending.rememberEntityType(type);
        return type;
    }

    public static <T extends class_1308> class_1299<T> build(FabricEntityTypeBuilder.Mob<T> self) {
        class_1299<T> type = self.build(CbPending.pendingEntityKey());
        CbPending.rememberEntityType(type);
        return type;
    }

    /** was Mob.spawnRestriction(SpawnRestriction.Location, Heightmap.Type, SpawnPredicate): the enum became SpawnLocation */
    public static <T extends class_1308> FabricEntityTypeBuilder.Mob<T> spawnRestriction(FabricEntityTypeBuilder.Mob<T> self,
            class_1317_class_1319 location, class_2902.class_2903 heightmap, class_1317.class_4306<T> predicate) {
        return self.spawnRestriction(location.cbVanilla(), heightmap, predicate);
    }

    private CbEntityBuilders() {
    }
}
