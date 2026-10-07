package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** Facade for 1.20.1 net/minecraft/class_1798 (ItemNameBlockItem), deleted on
 *  26.2. Extends real BlockItem; legacy hooks inert defaults. */
public class class_1798 extends BlockItem {
    public class_1798(Block block, Item.Properties props) { super(block, props); }

    /** getDescriptionId (method_7876) */
    public String method_7876() { return super.getDescriptionId(); }

    /** getPlacementState (method_7707) -> null (no custom placement) */
    public net.minecraft.world.level.block.state.BlockState method_7707(
            net.minecraft.world.item.context.BlockPlaceContext ctx) {
        return null;
    }

    /** registerBlocks(map, item) (method_7713) -> no-op */
    public void method_7713(java.util.Map blockToItem, Item item) {}

    /** useOn (method_7884) -> pass */
    public net.minecraft.world.InteractionResult method_7884(net.minecraft.world.item.context.UseOnContext ctx) {
        return net.minecraft.world.InteractionResult.PASS;
    }
}
