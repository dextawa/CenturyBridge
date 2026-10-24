package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.List;
import java.util.OptionalInt;
import net.minecraft.class_1706;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.class_3956;
import net.minecraft.class_4861;
import net.minecraft.class_4862;
import net.minecraft.class_8047;
import net.minecraft.class_8059;
import net.minecraft.class_8786;
import net.minecraft.class_10290;

/**
 * 1.21.1's ItemCombinerMenu (class_4861) slot definitions and quick-move slot on 1.21.2.
 * <ul>
 * <li>1.21.1's constructor asked the menu's createInputSlotDefinitions() -- abstract, a virtual call on the
 *     half-built menu, after the access and player fields were set; 1.21.2's constructor takes the definition as an
 *     argument instead (each vanilla menu builds its own statically). A 1.21.1 constructor call passes
 *     {@link #DEFERRED}; CbItemCombinerMenuMixin, at the same point of 1.21.2's constructor (just after the player
 *     field is set), puts the menu's own createInputSlotDefinitions() in its place -- and does so for any menu whose
 *     class declares the 1.21.1 method (a mod SmithingMenu subclass overriding it: 1.21.1 asked the override). The
 *     definition is kept on the menu ({@link Defined}).</li>
 * <li>getSlotToQuickMoveTo(stack): where quickMoveStack starts moving a stack into the input slots. 1.21.2 always
 *     starts at slot 0; a mod menu's own 1.21.1 answer is honoured there (CbItemCombinerMenuMixin).</li>
 * </ul>
 */
public final class CbItemCombiner {
    private CbItemCombiner() {
    }

    /** the definition a 1.21.1 constructor call passes: replaced by the menu's own before 1.21.2's constructor reads it */
    public static final class_8047 DEFERRED = class_8047.method_48364().method_48373(0, 0, 0).method_48372();

    /** {@link #DEFERRED}, for the constructor reshape's push */
    public static class_8047 deferred() {
        return DEFERRED;
    }

    /** createInputSlotDefinitions(), declared by a mod menu */
    public interface Legacy_createInputSlotDefinitions {
        class_8047 method_48352();
    }

    /** getSlotToQuickMoveTo(stack), declared by a mod menu */
    public interface Legacy_getSlotToQuickMoveTo {
        int method_48357(class_1799 stack);
    }

    /** the definition the menu was built with (CbItemCombinerMenuMixin) */
    public interface Defined {
        class_8047 cb$definition();
    }

    // ---- createInputSlotDefinitions ----

    public static class_8047 createInputSlotDefinitions(class_4861 menu) {
        if (menu instanceof Legacy_createInputSlotDefinitions legacy) {
            return legacy.method_48352();
        }
        return superCreateInputSlotDefinitions(menu);
    }

    /**
     * 1.21.1's vanilla bodies: AnvilMenu two free input slots (27, 47) and (76, 47), the result at (134, 47);
     * SmithingMenu template / base / addition (8, 48), (26, 48), (44, 48) taking what a smithing recipe takes there,
     * the result at (98, 48) -- 1.21.2's SmithingMenu definition, built from the level's smithing property sets (the
     * ingredients of every smithing recipe in that place, 1.21.1's predicate). ItemCombinerMenu's was abstract.
     */
    public static class_8047 superCreateInputSlotDefinitions(class_4861 menu) {
        if (menu instanceof class_1706) {
            return class_8047.method_48364().method_48374(0, 27, 47, stack -> true).method_48374(1, 76, 47, stack -> true)
                .method_48373(2, 134, 47).method_48372();
        }
        if (menu instanceof class_4862) {
            return class_4862.method_64652(menu.field_22482.method_37908().method_8433());
        }
        throw new AbstractMethodError(menu.getClass().getName() + " does not implement 1.21.1 "
            + "ItemCombinerMenu.createInputSlotDefinitions");
    }

    public static class_8047 superCreateInputSlotDefinitions(class_1706 menu) {
        return superCreateInputSlotDefinitions((class_4861) menu);
    }

    public static class_8047 superCreateInputSlotDefinitions(class_4862 menu) {
        return superCreateInputSlotDefinitions((class_4861) menu);
    }

    // ---- getSlotToQuickMoveTo ----

    public static int getSlotToQuickMoveTo(class_4861 menu, class_1799 stack) {
        if (menu instanceof Legacy_getSlotToQuickMoveTo legacy) {
            return legacy.method_48357(stack);
        }
        return superGetSlotToQuickMoveTo(menu, stack);
    }

    /**
     * 1.21.1: ItemCombinerMenu -- 0 when the input container is empty, else the first input slot of the definition;
     * SmithingMenu -- the first empty input slot some smithing recipe takes the stack in (template, else base, else
     * addition, recipe by recipe), else 0.
     */
    public static int superGetSlotToQuickMoveTo(class_4861 menu, class_1799 stack) {
        if (menu instanceof class_4862 smithing) {
            return smithingSlot(smithing, stack).orElse(0);
        }
        if (menu.field_22480.method_5442()) {
            return 0;
        }
        return ((Defined) menu).cb$definition().method_48368().get(0).comp_1204();
    }

    public static int superGetSlotToQuickMoveTo(class_4862 menu, class_1799 stack) {
        return superGetSlotToQuickMoveTo((class_4861) menu, stack);
    }

    /**
     * 1.21.1 SmithingMenu.findSlotToQuickMoveTo over the level's smithing recipes. A 1.21.2 client holds no recipes,
     * only the smithing property sets (every recipe's template, base and addition ingredients): there the slot is the
     * first empty one whose set takes the stack -- the server, which has the recipes, answers 1.21.1's way and its
     * slot contents are what the client is sent.
     */
    private static OptionalInt smithingSlot(class_4862 menu, class_1799 stack) {
        class_1937 level = menu.field_22482.method_37908();
        if (level instanceof class_3218 server) {
            List<class_8786<class_8059>> recipes = CbItemRecipeManager.method_30027(server.method_8503().method_3772(),
                class_3956.field_25388);
            return recipes.stream().flatMapToInt(r -> slotOf(r.comp_1933(), stack).stream())
                .filter(i -> !menu.method_7611(i).method_7681()).findFirst();
        }
        class_10290[] sets = {level.method_8433().method_64678(class_10290.field_54648),
            level.method_8433().method_64678(class_10290.field_54647), level.method_8433().method_64678(class_10290.field_54649)};
        for (int i = 0; i < 3; i++) {
            if (sets[i].method_64701(stack) && !menu.method_7611(i).method_7681()) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /** 1.21.1 SmithingMenu.findSlotMatchingIngredient(recipe, stack) */
    private static OptionalInt slotOf(class_8059 recipe, class_1799 stack) {
        if (CbItemRecipeClasses.method_48453(recipe, stack)) {
            return OptionalInt.of(0);
        }
        if (CbItemRecipeClasses.method_48454(recipe, stack)) {
            return OptionalInt.of(1);
        }
        if (CbItemRecipeClasses.method_30029(recipe, stack)) {
            return OptionalInt.of(2);
        }
        return OptionalInt.empty();
    }
}
