package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Tags whose members 26.1 changed under the same id (tag-content drift, which links and so is invisible to the
 * API probe). Each 1.21.11 tag a mod reads is answered with a compatibility tag this layer ships,
 * centurybridge:compat/1_21_11/&lt;path&gt;: exactly 1.21.11's resolved members plus #minecraft:&lt;path&gt;, so
 * whatever a mod or data pack adds to the vanilla tag still counts, as it did when the vanilla tag was 1.21.11's.
 * Pilot: minecraft:dirt, block and item (26.1 narrowed both to dirt, coarse and rooted dirt; 1.21.11's also held grass, podzol,
 * mycelium, moss and mud). A mod's own JSON naming #minecraft:dirt is the resource translator's (not done here).
 */
public final class CbWallTagDrift {

    /** 1.21.11 BlockTags.DIRT, as its members were */
    public static final TagKey<Block> DIRT = TagKey.create(Registries.BLOCK,
        Identifier.fromNamespaceAndPath("centurybridge", "compat/1_21_11/dirt"));

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
