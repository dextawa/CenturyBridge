package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.math.Fraction;

/**
 * Small 1.21.11 members 26.1 renamed or reshaped, 1.21.11's semantics on 26.1's:
 * <ul>
 * <li>ChunkPos(BlockPos) / ChunkPos(long): 26.1's ChunkPos.containing / unpack;</li>
 * <li>EntityType.is(tag) / is(holderSet): the type's built-in holder's;</li>
 * <li>BundleContents: 1.21.11 kept stacks, 26.1 templates -- the stacks of them (copies: a template does not
 *     change), the weight 26.1 answers as a result (1.21.11's always had one), the selected index.</li>
 * </ul>
 */
public final class CbWallMisc {

    private CbWallMisc() {
    }

    public static ChunkPos chunkPos(BlockPos pos) {
        return ChunkPos.containing(pos);
    }

    public static ChunkPos chunkPos(long packed) {
        return ChunkPos.unpack(packed);
    }

    public static boolean is(EntityType<?> type, TagKey<EntityType<?>> tag) {
        return type.builtInRegistryHolder().is(tag);
    }

    public static boolean is(EntityType<?> type, HolderSet<EntityType<?>> set) {
        return set.contains(type.builtInRegistryHolder());
    }

    /** BundleContents.getItemUnsafe(index) */
    public static ItemStack getItemUnsafe(BundleContents contents, int index) {
        return contents.items().get(index).create();
    }

    /** BundleContents.items() */
    public static Iterable<ItemStack> items(BundleContents contents) {
        return contents.items().stream().map(ItemStackTemplate::create).toList();
    }

    /** BundleContents.itemsCopy() */
    public static Iterable<ItemStack> itemsCopy(BundleContents contents) {
        List<ItemStack> copies = contents.itemCopyStream().toList();
        return copies;
    }

    /** BundleContents.weight() */
    public static Fraction weight(BundleContents contents) {
        return contents.weight().getOrThrow();
    }

    /** BundleContents.getSelectedItem(): the selected index */
    public static int getSelectedItem(BundleContents contents) {
        return contents.getSelectedItemIndex();
    }

    /** BundleContents.hasSelectedItem() */
    public static boolean hasSelectedItem(BundleContents contents) {
        return contents.getSelectedItemIndex() != BundleContents.NO_SELECTED_ITEM_INDEX;
    }
}
