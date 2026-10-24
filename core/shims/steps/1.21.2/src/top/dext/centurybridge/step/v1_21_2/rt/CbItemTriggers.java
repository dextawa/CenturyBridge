package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1935;
import net.minecraft.class_175;
import net.minecraft.class_2010;
import net.minecraft.class_2119;
import net.minecraft.class_2123;
import net.minecraft.class_2135;
import net.minecraft.class_2148;
import net.minecraft.class_2248;
import net.minecraft.class_2582;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_3735;
import net.minecraft.class_5321;
import net.minecraft.class_7871;
import net.minecraft.class_7923;
import net.minecraft.class_8508;

/**
 * 1.21.1's advancement trigger and predicate helpers on 1.21.2 (advancements/criterion).
 * <ul>
 * <li>The item / block criteria builders take the registry lookup their item predicates resolve items in (1.21.2:
 *     holder sets); 1.21.1's resolved them in the built-in registries, which are the lookups passed here.</li>
 * <li>RecipeCraftedTrigger and RecipeUnlockedTrigger name the recipe by ResourceKey where 1.21.1 had its id; the id
 *     is the key's location, and a key is made from an id in the recipe registry (CbItemRecipeBook.key).</li>
 * </ul>
 */
public final class CbItemTriggers {
    private CbItemTriggers() {
    }

    /** ConsumeItemTrigger.TriggerInstance.usedItem(item) */
    public static class_175<class_2010.class_2012> usedItem(class_1935 item) {
        return class_2010.class_2012.method_8828(class_7923.field_41178, item);
    }

    /** ShotCrossbowTrigger.TriggerInstance.shotCrossbow(item) */
    public static class_175<class_2123.class_2125> shotCrossbow(class_1935 item) {
        return class_2123.class_2125.method_9120(class_7923.field_41178, item);
    }

    /** UsedTotemTrigger.TriggerInstance.usedTotem(item) */
    public static class_175<class_2148.class_2150> usedTotem(class_1935 item) {
        return class_2148.class_2150.method_9170(class_7923.field_41178, item);
    }

    /** PlayerTrigger.TriggerInstance.walkOnBlockWithEquipment(block, equipment) */
    public static class_175<class_2135.class_2137> walkOnBlockWithEquipment(class_2248 block, class_1792 equipment) {
        return class_2135.class_2137.method_43136(class_7923.field_41175, class_7923.field_41178, block, equipment);
    }

    /** EntityEquipmentPredicate.captainPredicate(patterns) */
    public static class_3735 captainPredicate(class_7871<class_2582> patterns) {
        return class_3735.method_58057(class_7923.field_41178, patterns);
    }

    /** a recipe id as the trigger's ResourceKey */
    public static class_5321<class_1860<?>> key(class_2960 id) {
        return CbItemRecipeBook.key(id);
    }

    /** RecipeCraftedTrigger.TriggerInstance.recipeId(): the key's location */
    public static class_2960 recipeId(class_8508.class_8509 instance) {
        return instance.comp_2087().method_29177();
    }

    /** RecipeUnlockedTrigger.TriggerInstance.recipe(): the key's location */
    public static class_2960 recipe(class_2119.class_2121 instance) {
        return instance.comp_2089().method_29177();
    }

    /** RecipeCraftedTrigger.TriggerInstance.craftedItem(id) */
    public static class_175<class_8508.class_8509> craftedItem(class_2960 id) {
        return class_8508.class_8509.method_51352(key(id));
    }

    /** RecipeCraftedTrigger.TriggerInstance.craftedItem(id, ingredients) */
    public static class_175<class_8508.class_8509> craftedItem(class_2960 id, List<net.minecraft.class_2073.class_2074> ingredients) {
        return class_8508.class_8509.method_51353(key(id), ingredients);
    }

    /** RecipeCraftedTrigger.TriggerInstance.crafterCraftedItem(id) */
    public static class_175<class_8508.class_8509> crafterCraftedItem(class_2960 id) {
        return class_8508.class_8509.method_58450(key(id));
    }

    /** RecipeUnlockedTrigger.unlocked(id) */
    public static class_175<class_2119.class_2121> unlocked(class_2960 id) {
        return class_2119.method_27847(key(id));
    }

    /** RecipeCraftedTrigger.trigger(player, id, items) */
    public static void trigger(class_8508 trigger, class_3222 player, class_2960 id, List<class_1799> items) {
        trigger.method_51350(player, key(id), items);
    }

    /** a 1.21.1 RecipeCraftedTrigger subclass declaring trigger(player, id, items) */
    public interface LegacyCrafted {
        void method_51350(class_3222 player, class_2960 id, List<class_1799> items);
    }

    /** 1.21.2's trigger(player, key, items) of such a subclass: its override, with the key's location */
    public static void triggerBridge(LegacyCrafted self, class_3222 player, class_5321<class_1860<?>> key, List<class_1799> items) {
        self.method_51350(player, key.method_29177(), items);
    }

    /** 1.21.2's trigger, reached past the subclass's own 1.21.2 shape (its super call) */
    public interface SuperCrafted {
        void cb$super$method_51350(class_3222 player, class_5321<class_1860<?>> key, List<class_1799> items);
    }

    /** the subclass's super.trigger(player, id, items): 1.21.2's body with the id's key */
    public static void superTriggerBridge(SuperCrafted self, class_3222 player, class_2960 id, List<class_1799> items) {
        self.cb$super$method_51350(player, key(id), items);
    }
}
