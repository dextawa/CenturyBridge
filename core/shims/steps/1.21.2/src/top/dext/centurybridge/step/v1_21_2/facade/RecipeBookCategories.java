package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_5421;

/**
 * 1.21.1's RecipeBookCategories (net/minecraft/class_314, classRenames): the client's recipe book tabs, an enum with
 * the icons each tab showed, the tabs of each recipe book type and the search tab's sub-tabs. 1.21.2 reused the name
 * for the holder of its registered RecipeBookCategory values (the server assigns them to recipes) and split the
 * search tabs off on the client; the 1.21.1 enum is this one, constants, icons, lists and map as 1.21.1 built them.
 */
public enum RecipeBookCategories {
    CRAFTING_SEARCH(new class_1799(class_1802.field_8251)),
    CRAFTING_BUILDING_BLOCKS(new class_1799(class_2246.field_10104)),
    CRAFTING_REDSTONE(new class_1799(class_1802.field_8725)),
    CRAFTING_EQUIPMENT(new class_1799(class_1802.field_8475), new class_1799(class_1802.field_8845)),
    CRAFTING_MISC(new class_1799(class_1802.field_8187), new class_1799(class_1802.field_8279)),
    FURNACE_SEARCH(new class_1799(class_1802.field_8251)),
    FURNACE_FOOD(new class_1799(class_1802.field_8389)),
    FURNACE_BLOCKS(new class_1799(class_2246.field_10340)),
    FURNACE_MISC(new class_1799(class_1802.field_8187), new class_1799(class_1802.field_8687)),
    BLAST_FURNACE_SEARCH(new class_1799(class_1802.field_8251)),
    BLAST_FURNACE_BLOCKS(new class_1799(class_2246.field_10080)),
    BLAST_FURNACE_MISC(new class_1799(class_1802.field_8699), new class_1799(class_1802.field_8416)),
    SMOKER_SEARCH(new class_1799(class_1802.field_8251)),
    SMOKER_FOOD(new class_1799(class_1802.field_8389)),
    STONECUTTER(new class_1799(class_1802.field_8525)),
    SMITHING(new class_1799(class_1802.field_22028)),
    CAMPFIRE(new class_1799(class_1802.field_8389)),
    UNKNOWN(new class_1799(class_1802.field_8077));

    /** SMOKER_CATEGORIES */
    public static final List<RecipeBookCategories> field_25779 = ImmutableList.of(SMOKER_SEARCH, SMOKER_FOOD);
    /** BLAST_FURNACE_CATEGORIES */
    public static final List<RecipeBookCategories> field_25780 =
        ImmutableList.of(BLAST_FURNACE_SEARCH, BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC);
    /** FURNACE_CATEGORIES */
    public static final List<RecipeBookCategories> field_25781 =
        ImmutableList.of(FURNACE_SEARCH, FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC);
    /** CRAFTING_CATEGORIES */
    public static final List<RecipeBookCategories> field_25782 =
        ImmutableList.of(CRAFTING_SEARCH, CRAFTING_EQUIPMENT, CRAFTING_BUILDING_BLOCKS, CRAFTING_MISC, CRAFTING_REDSTONE);
    /** AGGREGATE_CATEGORIES: each search tab's sub-tabs */
    public static final Map<RecipeBookCategories, List<RecipeBookCategories>> field_25783 = ImmutableMap.of(
        CRAFTING_SEARCH, ImmutableList.of(CRAFTING_EQUIPMENT, CRAFTING_BUILDING_BLOCKS, CRAFTING_MISC, CRAFTING_REDSTONE),
        FURNACE_SEARCH, ImmutableList.of(FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC),
        BLAST_FURNACE_SEARCH, ImmutableList.of(BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC),
        SMOKER_SEARCH, ImmutableList.of(SMOKER_FOOD));

    /** iconItems (private in 1.21.1; public here: an access widener never reaches a CenturyBridge class) */
    public final List<class_1799> field_1807;

    RecipeBookCategories(class_1799... icons) {
        this.field_1807 = ImmutableList.copyOf(icons);
    }

    // the per-constant fields under their 1.21.1 intermediary names
    public static final RecipeBookCategories field_1809 = CRAFTING_SEARCH;
    public static final RecipeBookCategories field_1806 = CRAFTING_BUILDING_BLOCKS;
    public static final RecipeBookCategories field_1803 = CRAFTING_REDSTONE;
    public static final RecipeBookCategories field_1813 = CRAFTING_EQUIPMENT;
    public static final RecipeBookCategories field_1810 = CRAFTING_MISC;
    public static final RecipeBookCategories field_1804 = FURNACE_SEARCH;
    public static final RecipeBookCategories field_1808 = FURNACE_FOOD;
    public static final RecipeBookCategories field_1811 = FURNACE_BLOCKS;
    public static final RecipeBookCategories field_1812 = FURNACE_MISC;
    public static final RecipeBookCategories field_17110 = BLAST_FURNACE_SEARCH;
    public static final RecipeBookCategories field_17111 = BLAST_FURNACE_BLOCKS;
    public static final RecipeBookCategories field_17112 = BLAST_FURNACE_MISC;
    public static final RecipeBookCategories field_17113 = SMOKER_SEARCH;
    public static final RecipeBookCategories field_17114 = SMOKER_FOOD;
    public static final RecipeBookCategories field_17764 = STONECUTTER;
    public static final RecipeBookCategories field_25624 = SMITHING;
    public static final RecipeBookCategories field_17765 = CAMPFIRE;
    public static final RecipeBookCategories field_25625 = UNKNOWN;

    /** $VALUES */
    public static final RecipeBookCategories[] field_1805 = values();

    /** $values() */
    public static RecipeBookCategories[] method_36866() {
        return values();
    }

    /** getCategories(type): the tabs of a recipe book type */
    public static List<RecipeBookCategories> method_30285(class_5421 type) {
        return switch (type) {
            case field_25763 -> field_25782;
            case field_25764 -> field_25781;
            case field_25765 -> field_25780;
            case field_25766 -> field_25779;
        };
    }

    /** getIconItems() */
    public List<class_1799> method_1623() {
        return this.field_1807;
    }
}
