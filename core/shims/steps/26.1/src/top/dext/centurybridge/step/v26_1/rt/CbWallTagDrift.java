package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Tags whose members 26.1 changed under the same id (tag-content drift, which links and so is invisible to the
 * API probe): minecraft:dirt, block and item (26.1 narrowed both to dirt, coarse and rooted dirt; 1.21.11's also held
 * grass, podzol, mycelium, moss and mud). The block tag is answered with the 26.1 tag of exactly 1.21.11's members; the
 * item tag, which 26.1 has no such tag for, with a compatibility tag this layer ships, centurybridge:compat/1_21_11/dirt:
 * 1.21.11's resolved members plus #minecraft:dirt, so whatever a mod or data pack adds to the vanilla tag still counts.
 * A mod's own JSON naming #minecraft:dirt is the resource translator's (LegacyTags, tagMap).
 */
public final class CbWallTagDrift {

    /**
     * 1.21.11 BlockTags.DIRT, as its members were: 26.1's minecraft:substrate_overworld (#dirt, #mud, #moss_blocks,
     * #grass_blocks) resolves to exactly 1.21.11's ten blocks and holds what 26.1 data adds to any of them, so a mod or
     * data pack adding to minecraft:dirt still counts, as it did in 1.21.11
     */
    public static final TagKey<Block> DIRT = BlockTags.SUBSTRATE_OVERWORLD;

    /** 1.21.11 ItemTags.DIRT, as its members were (the same ten items, the item tag narrowed the same way) */
    public static final TagKey<Item> ITEM_DIRT = TagKey.create(Registries.ITEM,
        Identifier.fromNamespaceAndPath("centurybridge", "compat/1_21_11/dirt"));

    private CbWallTagDrift() {
    }

    /** BlockTags.DIRT read by 1.21.11 code */
    public static TagKey<Block> field_29822() {
        return DIRT;
    }

    /** ItemTags.DIRT read by 1.21.11 code */
    public static TagKey<Item> field_36269() {
        return ITEM_DIRT;
    }
}
