package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * What 26.1's plant and spreading-block constructors take where 1.21.11's blocks decided it in code, for a 1.21.11
 * subclass's super(...) (superCtorReshapes push ops). 26.1's vanilla blocks name their own support tags
 * (minecraft:supports_*), whose contents are 26.1's (broader: supports_vegetation, supports_crops). A 1.21.11 subclass
 * gets 1.21.11's rule instead, as the layer's data tags (data/centurybridge/tags/block/compat/1_21_11/), the 1.21.11
 * dirt as CbWallTagDrift.DIRT's minecraft:substrate_overworld:
 * <ul>
 * <li>StemBlock / AttachedStemBlock: placed on farmland; StemBlock's fruit grows on farmland or dirt;</li>
 * <li>FungusBlock: nylium, mycelium, soul soil, or what a bush took (dirt, farmland);</li>
 * <li>RootsBlock: nylium, soul soil, or what a bush took;</li>
 * <li>SpreadingSnowyDirtBlock: decays to dirt.</li>
 * </ul>
 */
public final class CbWallBlockSupports {

    private static final TagKey<Block> STEM_SUPPORT = tag("stem_support");
    private static final TagKey<Block> STEM_FRUIT_SUPPORT = tag("stem_fruit_support");
    private static final TagKey<Block> FUNGUS_SUPPORT = tag("fungus_support");
    private static final TagKey<Block> ROOTS_SUPPORT = tag("roots_support");

    private CbWallBlockSupports() {
    }

    private static TagKey<Block> tag(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("centurybridge", "compat/1_21_11/" + name));
    }

    public static TagKey<Block> stemSupport() {
        return STEM_SUPPORT;
    }

    public static TagKey<Block> stemFruitSupport() {
        return STEM_FRUIT_SUPPORT;
    }

    public static TagKey<Block> fungusSupport() {
        return FUNGUS_SUPPORT;
    }

    public static TagKey<Block> rootsSupport() {
        return ROOTS_SUPPORT;
    }

    public static ResourceKey<Block> dirt() {
        return BlockIds.DIRT;
    }
}
