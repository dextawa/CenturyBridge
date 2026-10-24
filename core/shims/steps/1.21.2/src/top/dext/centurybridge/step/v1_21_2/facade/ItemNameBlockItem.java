package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_2248;

/**
 * 1.21.1's ItemNameBlockItem (net/minecraft/class_1798, classRenames), gone in 1.21.2: a BlockItem named as an item
 * (getDescriptionId() = getOrCreateDescriptionId(), "item.&lt;ns&gt;.&lt;path&gt;", not its block's) -- 1.21.2's
 * Properties.useItemDescriptionPrefix(), which the layer's description id backfill keeps for this class
 * (CbPendingIdsItems).
 */
public class ItemNameBlockItem extends class_1747 {

    public ItemNameBlockItem(class_2248 block, class_1792.class_1793 properties) {
        super(block, properties.method_63687());
    }
}
