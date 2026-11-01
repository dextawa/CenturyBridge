package top.dext.centurybridge.step.v26_2.rt;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.SheepPredicate;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

/** 26.1.2's EntityLootSubProvider members on 26.2 (javap of both). */
public final class CbS26EntityLoot {
    private CbS26EntityLoot() {}

    /**
     * 26.1.2 createSheepDispatchPool(Map): one alternative per entry of the map, in the map's order, each the colour's
     * table when the entity has that sheep colour and its wool. 26.2 takes a ColorCollection (every colour, in
     * DyeColor.VALUES order) and builds each alternative the same way, the predicate in 26.2's terms: the exact
     * SHEEP_COLOR component and the sheep sub-predicate hasWool.
     */
    /**
     * 26.1.2 BuiltInLootTables.SHEEP_BY_DYE / SHEAR_SHEEP_BY_DYE: makeDyeKeyMap(prefix) = Util.makeEnumMap(DyeColor,
     * c -> register(prefix + "/" + c.getName())). 26.2's SHEEP / SHEAR_DYED_SHEEP are ColorCollection.NAMES (DyeColor.getName)
     * mapped to register("entities/sheep/" + name) / register("shearing/sheep/" + name): the same keys, already registered.
     * One EnumMap each, built once from them, as 26.1.2 held one map per field.
     */
    private static final class SheepMaps {
        static final Map<DyeColor, ResourceKey<LootTable>> SHEEP = of(BuiltInLootTables.SHEEP);
        static final Map<DyeColor, ResourceKey<LootTable>> SHEAR = of(BuiltInLootTables.SHEAR_DYED_SHEEP);

        private static Map<DyeColor, ResourceKey<LootTable>> of(ColorCollection<ResourceKey<LootTable>> keys) {
            Map<DyeColor, ResourceKey<LootTable>> map = new EnumMap<>(DyeColor.class);
            for (DyeColor c : DyeColor.values()) {
                map.put(c, keys.pick(c));
            }
            return map;
        }
    }

    /** 26.1.2 BuiltInLootTables.SHEEP_BY_DYE */
    public static Map<DyeColor, ResourceKey<LootTable>> sheepByDye() {
        return SheepMaps.SHEEP;
    }

    /** 26.1.2 BuiltInLootTables.SHEAR_SHEEP_BY_DYE */
    public static Map<DyeColor, ResourceKey<LootTable>> shearSheepByDye() {
        return SheepMaps.SHEAR;
    }

    public static LootPool.Builder createSheepDispatchPool(Map<DyeColor, ResourceKey<LootTable>> tables) {
        AlternativesEntry.Builder alternatives = AlternativesEntry.alternatives();
        for (Map.Entry<DyeColor, ResourceKey<LootTable>> e : tables.entrySet()) {
            alternatives = alternatives.otherwise(NestedLootTable.lootTableReference(e.getValue())
                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                            EntityPredicate.Builder.entity()
                                    .components(DataComponentExactPredicate.expect(DataComponents.SHEEP_COLOR, e.getKey()))
                                    .sheep(SheepPredicate.hasWool()))));
        }
        return LootPool.lootPool().add(alternatives);
    }
}
