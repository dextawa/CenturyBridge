package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.List;
import java.util.Optional;
import net.minecraft.class_10223;
import net.minecraft.class_10295;
import net.minecraft.class_10352;
import net.minecraft.class_10355;
import net.minecraft.class_10363;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1848;
import net.minecraft.class_1849;
import net.minecraft.class_1850;
import net.minecraft.class_1851;
import net.minecraft.class_1852;
import net.minecraft.class_1853;
import net.minecraft.class_1854;
import net.minecraft.class_1855;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1867;
import net.minecraft.class_1869;
import net.minecraft.class_1872;
import net.minecraft.class_1874;
import net.minecraft.class_1876;
import net.minecraft.class_2246;
import net.minecraft.class_2371;
import net.minecraft.class_314;
import net.minecraft.class_3859;
import net.minecraft.class_3861;
import net.minecraft.class_3862;
import net.minecraft.class_3920;
import net.minecraft.class_3955;
import net.minecraft.class_3956;
import net.minecraft.class_3972;
import net.minecraft.class_3975;
import net.minecraft.class_4317;
import net.minecraft.class_6880;
import net.minecraft.class_7225;
import net.minecraft.class_7709;
import net.minecraft.class_7924;
import net.minecraft.class_8053;
import net.minecraft.class_8054;
import net.minecraft.class_8055;
import net.minecraft.class_8056;
import net.minecraft.class_8059;
import net.minecraft.class_8060;
import net.minecraft.class_8062;
import net.minecraft.class_8164;
import net.minecraft.class_9334;
import net.minecraft.class_9694;
import net.minecraft.class_9695;
import net.minecraft.class_9887;
import top.dext.centurybridge.step.v1_21_2.mixin.CbItemShapedRecipeAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbItemShapelessRecipeAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbItemSingleItemRecipeAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbItemSmithingTransformAccess;

/**
 * 1.21.1's Recipe members on 1.21.2: getResultItem(registries), getIngredients(), canCraftInDimensions(w, h),
 * getRemainingItems(input), getToastSymbol(), isIncomplete() -- everything 1.21.1's recipe book read from a recipe.
 * 1.21.2 dropped them for placementInfo() / display() / recipeBookCategory().
 *
 * <p>Each answer is, in order:
 * <ol>
 * <li>the class's own 1.21.1 override, when it has one (the Legacy* adapters, added by adapterHooks to a converted
 *     class declaring the member);</li>
 * <li>1.21.1's body for the vanilla recipe class the object is (or extends), read off 1.21.1's bytecode and computed
 *     from 1.21.2's fields of the same recipe. 1.21.2's TransmuteRecipe is the shulker box dyeing recipe 1.21.1 made
 *     with ShulkerBoxColoring (a CustomRecipe: no result item, no ingredients, two slots or more);</li>
 * <li>1.21.1's Recipe default, for a converted class ({@link V1}, every converted class under Recipe has it);</li>
 * <li>for a recipe class written against 1.21.2 (another mod's), which has no 1.21.1 answer, the one its 1.21.2
 *     members give: the result its first display shows, the ingredients of its placement info.</li>
 * </ol>
 * The super_* statics are a subclass's super call: the same without step 1 (and for Recipe's own defaults, 1.21.1's
 * Recipe default directly).
 *
 * <p>1.21.1's empty ingredient (the gaps of a shaped pattern) is {@link CbItemIngredients#field_9017}; 1.21.2's
 * Optional.empty() turns into it and back.
 *
 * <p>The fills ({@link #placementInfo}, {@link #recipeBookCategory}, {@link #furnaceIcon}) are what 1.21.2 newly
 * demands of a converted recipe class: the placement of its 1.21.1 ingredients, the recipe book category 1.21.1's
 * client gave its type (ClientRecipeBook.getCategory), the icon of its 1.21.1 toast symbol.
 */
public final class CbItemRecipes {

    private CbItemRecipes() {
    }

    /** a recipe class converted from 1.21.1 (every one under Recipe; adapterHooks with no required shape) */
    public interface V1 {
    }

    /** a 1.21.1 class overriding getResultItem(registries) */
    public interface LegacyResult {
        class_1799 method_8110(class_7225.class_7874 registries);
    }

    /** a 1.21.1 class overriding canCraftInDimensions(width, height) */
    public interface LegacyDims {
        boolean method_8113(int width, int height);
    }

    /** a 1.21.1 class overriding getIngredients() */
    public interface LegacyIngredients {
        class_2371<class_1856> method_8117();
    }

    /**
     * a 1.21.1 class overriding getRemainingItems(input): the erased bridge javac wrote (a crafting recipe's typed
     * override is CraftingRecipe.getRemainingItems(CraftingInput), which 1.21.2 still calls)
     */
    public interface LegacyRemaining {
        class_2371<class_1799> method_8111(class_9695 input);
    }

    /** a 1.21.1 class overriding getToastSymbol() */
    public interface LegacyToast {
        class_1799 method_17447();
    }

    /** a 1.21.1 class overriding isIncomplete() */
    public interface LegacyIncomplete {
        boolean method_31584();
    }

    // ---- getResultItem(registries) ----

    public static class_1799 method_8110(class_1860<?> r, class_7225.class_7874 registries) {
        if (r instanceof LegacyResult l) {
            return l.method_8110(registries);
        }
        return resultOf(r, registries);
    }

    public static class_1799 method_8110(class_1851 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_1852 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_1853 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_1867 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_1869 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_1874 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_3972 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_8060 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }
    public static class_1799 method_8110(class_8062 r, class_7225.class_7874 p) { return method_8110((class_1860<?>) r, p); }

    public static class_1799 super_method_8110(class_1851 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_1852 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_1853 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_1867 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_1869 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_1874 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_3972 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_8060 r, class_7225.class_7874 p) { return resultOf(r, p); }
    public static class_1799 super_method_8110(class_8062 r, class_7225.class_7874 p) { return resultOf(r, p); }

    /** 1.21.1's bodies: the result field (the stack itself, as 1.21.1 answered it), or the fixed stack */
    static class_1799 resultOf(class_1860<?> r, class_7225.class_7874 registries) {
        if (r instanceof class_8062) {
            return trimExample(registries);
        }
        if (r instanceof class_8060 t) {
            return ((CbItemSmithingTransformAccess) t).cb$result();
        }
        if (r instanceof class_1869 s) {
            return ((CbItemShapedRecipeAccess) s).cb$result();
        }
        if (r instanceof class_1867 s) {
            return ((CbItemShapelessRecipeAccess) s).cb$result();
        }
        if (r instanceof class_3972 s) {
            return ((CbItemSingleItemRecipeAccess) s).cb$result();
        }
        if (r instanceof class_1851) {
            return new class_1799(class_1802.field_8639);
        }
        if (r instanceof class_1853) {
            return new class_1799(class_1802.field_8450);
        }
        if (r instanceof class_1852 || r instanceof class_10223) {
            return class_1799.field_8037;
        }
        if (r instanceof V1) {
            // abstract in 1.21.1: a converted concrete class declared it (LegacyResult); only an abstract one's
            // super call can land here, which javac refuses -- no 1.21.1 answer exists
            throw new AbstractMethodError("Recipe.getResultItem(HolderLookup.Provider) of " + r.getClass().getName());
        }
        return displayedResult(r, registries);
    }

    /**
     * SmithingTrimRecipe.getResultItem: an iron chestplate trimmed with the first trim pattern and redstone, when
     * both exist in the registries (1.21.1's body)
     */
    static class_1799 trimExample(class_7225.class_7874 registries) {
        class_1799 stack = new class_1799(class_1802.field_8523);
        Optional<? extends class_6880<class_8056>> pattern =
            registries.method_46762(class_7924.field_42082).method_42017().findFirst();
        Optional<? extends class_6880<class_8054>> material =
            registries.method_46762(class_7924.field_42083).method_46746(class_8055.field_42007);
        if (pattern.isPresent() && material.isPresent()) {
            stack.method_57379(class_9334.field_49607, new class_8053(material.get(), pattern.get()));
        }
        return stack;
    }

    /** a 1.21.2 recipe class: the first stack its first display's result resolves to with these registries */
    static class_1799 displayedResult(class_1860<?> r, class_7225.class_7874 registries) {
        List<class_10295> displays = r.method_64664();
        if (displays.isEmpty()) {
            return class_1799.field_8037;
        }
        class_10352 context = new class_10352.class_10353()
            .method_64970(class_10363.field_54906, registries)
            .method_64971(class_10363.field_54907);
        return displays.get(0).comp_3258().method_64742(context);
    }

    // ---- getIngredients() ----

    public static class_2371<class_1856> method_8117(class_1860<?> r) {
        if (r instanceof LegacyIngredients l) {
            return l.method_8117();
        }
        return ingredientsOf(r);
    }

    public static class_2371<class_1856> method_8117(class_1867 r) { return method_8117((class_1860<?>) r); }
    public static class_2371<class_1856> method_8117(class_1869 r) { return method_8117((class_1860<?>) r); }
    public static class_2371<class_1856> method_8117(class_1874 r) { return method_8117((class_1860<?>) r); }
    public static class_2371<class_1856> method_8117(class_3972 r) { return method_8117((class_1860<?>) r); }

    /** Recipe.super.getIngredients(): an empty list */
    public static class_2371<class_1856> super_method_8117(class_1860<?> r) { return class_2371.method_10211(); }
    public static class_2371<class_1856> super_method_8117(class_1867 r) { return ingredientsOf(r); }
    public static class_2371<class_1856> super_method_8117(class_1869 r) { return ingredientsOf(r); }
    public static class_2371<class_1856> super_method_8117(class_1874 r) { return ingredientsOf(r); }
    public static class_2371<class_1856> super_method_8117(class_3972 r) { return ingredientsOf(r); }

    /**
     * 1.21.1's bodies: a shaped recipe's pattern ingredients (EMPTY in the gaps), a shapeless one's ingredients, a
     * cooking / stonecutting recipe's one ingredient; Recipe's default (CustomRecipe, smithing) an empty list
     */
    static class_2371<class_1856> ingredientsOf(class_1860<?> r) {
        class_2371<class_1856> out = class_2371.method_10211();
        if (r instanceof class_1869 s) {
            for (Optional<class_1856> i : s.method_61693()) {
                out.add(i.orElseGet(CbItemIngredients::field_9017));
            }
            return out;
        }
        if (r instanceof class_1867 s) {
            out.addAll(((CbItemShapelessRecipeAccess) s).cb$ingredients());
            return out;
        }
        if (r instanceof class_3972 s) {
            out.add(s.method_64720());
            return out;
        }
        if (r instanceof class_1852 || r instanceof class_10223 || r instanceof class_8060 || r instanceof class_8062
                || r instanceof V1) {
            return out;
        }
        // a 1.21.2 recipe class: the ingredients of its placement
        out.addAll(r.method_61671().method_64675());
        return out;
    }

    // ---- canCraftInDimensions(width, height) ----

    public static boolean method_8113(class_1860<?> r, int w, int h) {
        if (r instanceof LegacyDims l) {
            return l.method_8113(w, h);
        }
        return fitsIn(r, w, h);
    }

    public static boolean method_8113(class_1848 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1849 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1850 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1851 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1853 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1854 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1855 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1867 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1869 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1872 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1874 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_1876 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_3972 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_4317 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_8059 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }
    public static boolean method_8113(class_8164 r, int w, int h) { return method_8113((class_1860<?>) r, w, h); }

    public static boolean super_method_8113(class_1848 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1849 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1850 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1851 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1853 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1854 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1855 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1867 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1869 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1872 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1874 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_1876 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_3972 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_4317 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_8059 r, int w, int h) { return fitsIn(r, w, h); }
    public static boolean super_method_8113(class_8164 r, int w, int h) { return fitsIn(r, w, h); }

    /** 1.21.1's bodies, per vanilla recipe class */
    static boolean fitsIn(class_1860<?> r, int w, int h) {
        if (r instanceof class_1869 s) {
            return w >= s.method_8150() && h >= s.method_8158();
        }
        if (r instanceof class_1867 s) {
            return w * h >= ((CbItemShapelessRecipeAccess) s).cb$ingredients().size();
        }
        if (r instanceof class_3972) {
            return true;
        }
        if (r instanceof class_8059) {
            return w >= 3 && h >= 1;
        }
        if (r instanceof class_1850 || r instanceof class_1855 || r instanceof class_1876) {
            return w >= 3 && h >= 3;
        }
        if (r instanceof class_8164) {
            return w == 3 && h == 3;
        }
        if (r instanceof class_1848 || r instanceof class_1849 || r instanceof class_1851 || r instanceof class_1853
                || r instanceof class_1854 || r instanceof class_1872 || r instanceof class_4317
                || r instanceof class_10223) {
            return w * h >= 2;
        }
        if (r instanceof V1) {
            throw new AbstractMethodError("Recipe.canCraftInDimensions(int, int) of " + r.getClass().getName());
        }
        // a 1.21.2 recipe class: placeable, and no more ingredients than slots
        class_9887 placement = r.method_61671();
        return !placement.method_61687() && placement.method_64675().size() <= w * h;
    }

    // ---- getRemainingItems(input) ----

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_2371<class_1799> method_8111(class_1860 r, class_9695 input) {
        if (r instanceof LegacyRemaining l) {
            return l.method_8111(input);
        }
        if (r instanceof class_3955 c && input instanceof class_9694 ci) {
            // the typed override 1.21.1 compiled as CraftingRecipe.getRemainingItems(CraftingInput), which 1.21.2
            // keeps (BannerDuplicateRecipe / BookCloningRecipe; its default is 1.21.1's Recipe default)
            return c.method_17704(ci);
        }
        return remainders(input);
    }

    /** Recipe.super.getRemainingItems(input) */
    public static class_2371<class_1799> super_method_8111(class_1860<?> r, class_9695 input) {
        return remainders(input);
    }

    /** 1.21.1's Recipe default: each slot's item's crafting remainder */
    static class_2371<class_1799> remainders(class_9695 input) {
        class_2371<class_1799> out = class_2371.method_10213(input.method_59983(), class_1799.field_8037);
        for (int i = 0; i < out.size(); i++) {
            class_1792 item = input.method_59984(i).method_7909();
            class_1799 rest = item.method_7858();
            if (!rest.method_7960()) {
                out.set(i, rest);
            }
        }
        return out;
    }

    // ---- getToastSymbol() ----

    public static class_1799 method_17447(class_1860<?> r) {
        if (r instanceof LegacyToast l) {
            return l.method_17447();
        }
        return toastOf(r);
    }

    public static class_1799 method_17447(class_3859 r) { return method_17447((class_1860<?>) r); }
    public static class_1799 method_17447(class_3861 r) { return method_17447((class_1860<?>) r); }
    public static class_1799 method_17447(class_3862 r) { return method_17447((class_1860<?>) r); }
    public static class_1799 method_17447(class_3920 r) { return method_17447((class_1860<?>) r); }
    public static class_1799 method_17447(class_3975 r) { return method_17447((class_1860<?>) r); }
    public static class_1799 method_17447(class_8059 r) { return method_17447((class_1860<?>) r); }

    /** Recipe.super.getToastSymbol(): a crafting table */
    public static class_1799 super_method_17447(class_1860<?> r) { return new class_1799(class_2246.field_9980); }
    public static class_1799 super_method_17447(class_3859 r) { return toastOf(r); }
    public static class_1799 super_method_17447(class_3861 r) { return toastOf(r); }
    public static class_1799 super_method_17447(class_3862 r) { return toastOf(r); }
    public static class_1799 super_method_17447(class_3920 r) { return toastOf(r); }
    public static class_1799 super_method_17447(class_3975 r) { return toastOf(r); }
    public static class_1799 super_method_17447(class_8059 r) { return toastOf(r); }

    /** 1.21.1's bodies: the workstation block of the recipe class, else (Recipe's default) a crafting table */
    static class_1799 toastOf(class_1860<?> r) {
        if (r instanceof class_3859) {
            return new class_1799(class_2246.field_16333);
        }
        if (r instanceof class_3861) {
            return new class_1799(class_2246.field_10181);
        }
        if (r instanceof class_3862) {
            return new class_1799(class_2246.field_16334);
        }
        if (r instanceof class_3920) {
            return new class_1799(class_2246.field_17350);
        }
        if (r instanceof class_3975) {
            return new class_1799(class_2246.field_16335);
        }
        if (r instanceof class_8059) {
            return new class_1799(class_2246.field_16329);
        }
        return new class_1799(class_2246.field_9980);
    }

    // ---- isIncomplete() ----

    public static boolean method_31584(class_1860<?> r) {
        if (r instanceof LegacyIncomplete l) {
            return l.method_31584();
        }
        return incomplete(r);
    }

    public static boolean method_31584(class_1869 r) { return method_31584((class_1860<?>) r); }
    public static boolean method_31584(class_8060 r) { return method_31584((class_1860<?>) r); }
    public static boolean method_31584(class_8062 r) { return method_31584((class_1860<?>) r); }

    /** Recipe.super.isIncomplete(): 1.21.1's default over the class's getIngredients() */
    public static boolean super_method_31584(class_1860<?> r) { return defaultIncomplete(method_8117(r)); }
    public static boolean super_method_31584(class_1869 r) { return incomplete(r); }
    public static boolean super_method_31584(class_8060 r) { return incomplete(r); }
    public static boolean super_method_31584(class_8062 r) { return incomplete(r); }

    /**
     * 1.21.1's bodies. ShapedRecipe: no ingredients, or a non-empty one without items; smithing: a missing
     * template / base / addition (1.21.1's empty ingredient, 1.21.2's Optional.empty()); Recipe's default (over the
     * class's own getIngredients()): no ingredients, or one without items
     */
    static boolean incomplete(class_1860<?> r) {
        if (r instanceof class_1869 s) {
            class_2371<class_1856> ings = method_8117(s);
            return ings.isEmpty() || ings.stream()
                .filter(i -> !CbItemIngredients.method_8103(i))
                .anyMatch(i -> CbItemIngredients.method_8105(i).length == 0);
        }
        if (r instanceof class_8060 || r instanceof class_8062) {
            class_8059 s = (class_8059) r;
            return s.method_64722().isEmpty() || s.method_64723().isEmpty() || s.method_64724().isEmpty();
        }
        return defaultIncomplete(method_8117(r));
    }

    static boolean defaultIncomplete(class_2371<class_1856> ings) {
        return ings.isEmpty() || ings.stream().anyMatch(i -> CbItemIngredients.method_8105(i).length == 0);
    }

    // ---- what 1.21.2 demands of a converted recipe class (abstractFills) ----

    /**
     * placementInfo(): the placement of the class's 1.21.1 ingredients -- none (1.21.1: incomplete) is not
     * placeable; 1.21.1's empty ingredient is a gap
     */
    public static class_9887 placementInfo(class_1860<?> r) {
        class_2371<class_1856> ings = method_8117(r);
        if (ings.isEmpty()) {
            return class_9887.field_52597;
        }
        boolean gaps = false;
        for (class_1856 i : ings) {
            gaps |= CbItemIngredients.isEmptyIngredient(i);
        }
        if (!gaps) {
            return class_9887.method_61686(ings);
        }
        List<Optional<class_1856>> slots = new java.util.ArrayList<>(ings.size());
        for (class_1856 i : ings) {
            slots.add(CbItemIngredients.isEmptyIngredient(i) ? Optional.empty() : Optional.of(i));
        }
        return class_9887.method_61683(slots);
    }

    /**
     * recipeBookCategory(): the category 1.21.1's client gave the recipe's type (ClientRecipeBook.getCategory):
     * crafting by its crafting category, smelting by its cooking category, blasting blocks or misc, smoking,
     * campfire, stonecutting, smithing. 1.21.1 filed any other type under UNKNOWN, which 1.21.2 has no category for;
     * 1.21.2 asks only for a recipe with a display, which a converted class has only when it extends a vanilla
     * cooking recipe: such a recipe goes by its cooking category as a smelting one would.
     */
    public static class_10355 recipeBookCategory(class_1860<?> r) {
        if (r instanceof class_3955 c) {
            return switch (c.method_45441()) {
                case field_40248 -> class_314.field_1806;
                case field_40249 -> class_314.field_1803;
                case field_40250 -> class_314.field_1813;
                case field_40251 -> class_314.field_1810;
            };
        }
        class_3956<?> type = r.method_17716();
        if (type == class_3956.field_17547) {
            return r instanceof class_1874 c && c.method_45438() == class_7709.field_40243
                ? class_314.field_17111 : class_314.field_17112;
        }
        if (type == class_3956.field_17548) {
            return class_314.field_17114;
        }
        if (type == class_3956.field_17549) {
            return class_314.field_17765;
        }
        if (type == class_3956.field_17641) {
            return class_314.field_17764;
        }
        if (type == class_3956.field_25388) {
            return class_314.field_25624;
        }
        if (r instanceof class_1874 c) {
            return switch (c.method_45438()) {
                case field_40242 -> class_314.field_1808;
                case field_40243 -> class_314.field_1811;
                case field_40244 -> class_314.field_1812;
            };
        }
        // SMELTING without a cooking recipe, or another type: 1.21.1's UNKNOWN; the misc crafting shelf
        return class_314.field_1810;
    }

    /** AbstractCookingRecipe.furnaceIcon(): the item of the class's 1.21.1 toast symbol */
    public static class_1792 furnaceIcon(class_1874 r) {
        return method_17447(r).method_7909();
    }
}
