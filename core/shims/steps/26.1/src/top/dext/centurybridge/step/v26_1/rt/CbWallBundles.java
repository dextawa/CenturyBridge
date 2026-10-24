package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;

/** 1.21.11 BundleItem statics 26.1 dropped or changed, 1.21.11's bodies on 26.1's bundle contents */
public final class CbWallBundles {

    private CbWallBundles() {
    }

    /** hasSelectedItem(stack): the bundle has contents with a selected item */
    public static boolean hasSelectedItem(ItemStack stack) {
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        return contents != null && BundleItem.getSelectedItemIndex(stack) != -1;
    }

    /** getSelectedItem(stack): the selected item's stack, else EMPTY (26.1 answers its template) */
    public static ItemStack getSelectedItem(ItemStack stack) {
        if (!hasSelectedItem(stack)) {
            return ItemStack.EMPTY;
        }
        ItemStackTemplate selected = BundleItem.getSelectedItem(stack);
        return selected == null ? ItemStack.EMPTY : selected.create();
    }

    /** getAllBundleItemColors(): the plain bundle and the sixteen dyed ones, 1.21.11's order */
    public static List<BundleItem> getAllBundleItemColors() {
        return List.of(Items.BUNDLE, Items.WHITE_BUNDLE, Items.ORANGE_BUNDLE, Items.MAGENTA_BUNDLE, Items.LIGHT_BLUE_BUNDLE, Items.YELLOW_BUNDLE,
                Items.LIME_BUNDLE, Items.PINK_BUNDLE, Items.GRAY_BUNDLE, Items.LIGHT_GRAY_BUNDLE, Items.CYAN_BUNDLE, Items.BLACK_BUNDLE, Items.BROWN_BUNDLE,
                Items.GREEN_BUNDLE, Items.RED_BUNDLE, Items.BLUE_BUNDLE, Items.PURPLE_BUNDLE)
            .stream().map(item -> (BundleItem) item).toList();
    }

    /** getByColor(color): the bundle of that dye */
    public static Item getByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_BUNDLE;
            case ORANGE -> Items.ORANGE_BUNDLE;
            case MAGENTA -> Items.MAGENTA_BUNDLE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_BUNDLE;
            case YELLOW -> Items.YELLOW_BUNDLE;
            case LIME -> Items.LIME_BUNDLE;
            case PINK -> Items.PINK_BUNDLE;
            case GRAY -> Items.GRAY_BUNDLE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_BUNDLE;
            case CYAN -> Items.CYAN_BUNDLE;
            case PURPLE -> Items.PURPLE_BUNDLE;
            case BLUE -> Items.BLUE_BUNDLE;
            case BROWN -> Items.BROWN_BUNDLE;
            case GREEN -> Items.GREEN_BUNDLE;
            case RED -> Items.RED_BUNDLE;
            case BLACK -> Items.BLACK_BUNDLE;
        };
    }
}
