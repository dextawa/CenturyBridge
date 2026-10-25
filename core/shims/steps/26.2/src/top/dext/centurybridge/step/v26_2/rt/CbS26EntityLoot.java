package top.dext.centurybridge.step.v26_2.rt;

import java.util.Map;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.SheepPredicate;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
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
