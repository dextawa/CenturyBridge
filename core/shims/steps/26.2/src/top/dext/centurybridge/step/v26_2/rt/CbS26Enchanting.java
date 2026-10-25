package top.dext.centurybridge.step.v26_2.rt;

import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.DiscardItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.FilteredFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

/**
 * 26.1.2 took enchantment options as an Optional&lt;HolderSet&lt;Enchantment&gt;&gt; (empty: any enchantment) where
 * 26.2's EnchantRandomlyFunction.Builder.withOptions and VillagerTrades.enchantedBook / enchantedItem take the
 * HolderSet itself (the builder still holds Optional options, empty by default; withOptions(set) stores
 * Optional.of(set)). 26.1.2's withOptions(Optional) stored the Optional as given, so an empty one clears the options;
 * the builder's options field is opened by the layer's access widener for that. enchantedBook / enchantedItem are
 * 26.1.2's bodies (javap: the same loot functions in the same order as 26.2's, the options passed through).
 */
public final class CbS26Enchanting {
    private CbS26Enchanting() {}

    /** 26.1.2 EnchantRandomlyFunction.Builder.withOptions(Optional): options = the Optional given */
    public static EnchantRandomlyFunction.Builder withOptions(EnchantRandomlyFunction.Builder self, Optional<HolderSet<Enchantment>> options) {
        self.options = options;
        return self;
    }

    /** 26.1.2 VillagerTrades.enchantedBook(items, options) */
    public static List<LootItemFunction> enchantedBook(HolderGetter<Item> items, Optional<HolderSet<Enchantment>> options) {
        return List.of(
            withOptions(new EnchantRandomlyFunction.Builder(), options).allowingIncompatibleEnchantments().includeAdditionalCostComponent().build(),
            FilteredFunction.filtered(new ItemPredicate.Builder().of(items, Items.ENCHANTED_BOOK)
                    .withComponents(DataComponentMatchers.Builder.components()
                        .partial(DataComponentPredicates.STORED_ENCHANTMENTS,
                            EnchantmentsPredicate.storedEnchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY))))
                        .build())
                    .build())
                .onFail(Optional.of(DiscardItem.discardItem().build()))
                .build());
    }

    /** 26.1.2 VillagerTrades.enchantedItem(items, options, item) */
    public static List<LootItemFunction> enchantedItem(HolderGetter<Item> items, Optional<HolderSet<Enchantment>> options, Item item) {
        return List.of(
            new EnchantWithLevelsFunction.Builder(UniformGenerator.between(5.0F, 19.0F)).withOptions(options).includeAdditionalCostComponent().build(),
            FilteredFunction.filtered(new ItemPredicate.Builder().of(items, item)
                    .withComponents(DataComponentMatchers.Builder.components()
                        .partial(DataComponentPredicates.ENCHANTMENTS,
                            EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY))))
                        .build())
                    .build())
                .onFail(Optional.of(DiscardItem.discardItem().build()))
                .build());
    }
}
