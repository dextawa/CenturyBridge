package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1856;
import net.minecraft.class_1865;
import net.minecraft.class_1874;
import net.minecraft.class_2371;
import net.minecraft.class_2960;
import net.minecraft.class_3542;
import net.minecraft.class_3956;
import net.minecraft.class_3972;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7709;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_8059;
import net.minecraft.class_8060;
import net.minecraft.class_8062;
import net.minecraft.class_8786;
import net.minecraft.class_8957;
import top.dext.centurybridge.step.v1_21_2.mixin.CbItemSingleItemRecipeAccess;

/**
 * 1.21.1's recipe classes on 1.21.2: their constructors, protected fields and the members 1.21.2 replaced.
 *
 * <ul>
 * <li>ShapelessRecipe took a NonNullList, 1.21.2 a List (the same values); ShapedRecipePattern took a NonNullList
 *     with 1.21.1's empty ingredient in the gaps, 1.21.2 a list of Optionals (empty in the gaps) -- and answers
 *     ingredients() in that shape;</li>
 * <li>AbstractCookingRecipe / SingleItemRecipe took the recipe type (and serializer) and answered getType() /
 *     getSerializer() from them; 1.21.2 takes neither. The values go to the instance (CbItemSingleRecipeFieldsMixin)
 *     and the abstractFills answer them; the protected fields a subclass read are 1.21.2's accessors;</li>
 * <li>SmithingRecipe asked isTemplateIngredient / isBaseIngredient / isAdditionIngredient(stack); 1.21.2 asks for the
 *     Optional ingredients. A call is the class's own override, else 1.21.2's test of the ingredient (1.21.1's
 *     empty ingredient = Optional.empty(): matches the empty stack only); a converted class implementing only the
 *     predicates gets ingredients of every item its predicate accepts (1.21.2's smithing menu and recipe property
 *     sets read them; the class's own matches(..) still decides the recipe);</li>
 * <li>RecipeHolder was keyed by a ResourceLocation, 1.21.2's by a ResourceKey of the recipe registry.</li>
 * </ul>
 */
public final class CbItemRecipeClasses {

    private CbItemRecipeClasses() {
    }

    // ---- ShapelessRecipe / ShapedRecipePattern ----

    /** new ShapelessRecipe(.., NonNullList ingredients): 1.21.2 takes the same values as a List */
    public static List<class_1856> list(class_2371<class_1856> ingredients) {
        return ingredients;
    }

    /** new ShapedRecipePattern(w, h, ingredients, data): 1.21.1's empty ingredient is 1.21.2's empty slot */
    public static List<Optional<class_1856>> optionals(class_2371<class_1856> ingredients) {
        List<Optional<class_1856>> out = new ArrayList<>(ingredients.size());
        for (class_1856 i : ingredients) {
            out.add(optional(i));
        }
        return out;
    }

    /** ShapedRecipePattern.ingredients(): 1.21.1's NonNullList, the empty ingredient in the gaps */
    public static class_2371<class_1856> method_59997(class_8957 pattern) {
        class_2371<class_1856> out = class_2371.method_10211();
        for (Optional<class_1856> i : pattern.method_59997()) {
            out.add(i.orElseGet(CbItemIngredients::field_9017));
        }
        return out;
    }

    /** an ingredient where 1.21.2 takes an Optional one: 1.21.1's empty ingredient is Optional.empty() */
    public static Optional<class_1856> optional(class_1856 ingredient) {
        return ingredient == null || CbItemIngredients.isEmptyIngredient(ingredient) ? Optional.empty() : Optional.of(ingredient);
    }

    // ---- AbstractCookingRecipe / SingleItemRecipe ----

    /** the type / serializer a 1.21.1 subclass passed its super constructor (CbItemSingleRecipeFieldsMixin) */
    public interface LegacyFields {
        class_3956<?> cb$type();

        void cb$setType(class_3956<?> type);

        class_1865<?> cb$serializer();

        void cb$setSerializer(class_1865<?> serializer);
    }

    /** after new AbstractCookingRecipe(type, ..): the type, for getType() */
    public static void cookingCreated(class_1874 recipe, class_3956<?> type) {
        ((LegacyFields) recipe).cb$setType(type);
    }

    /** after new SingleItemRecipe(type, serializer, ..): both, for getType() / getSerializer() */
    public static void singleCreated(class_3972 recipe, class_3956<?> type, class_1865<?> serializer) {
        ((LegacyFields) recipe).cb$setType(type);
        ((LegacyFields) recipe).cb$setSerializer(serializer);
    }

    /** AbstractCookingRecipe.getType() (1.21.1: the type field) */
    public static class_3956<?> cookingType(class_1874 recipe) {
        return ((LegacyFields) recipe).cb$type();
    }

    /** SingleItemRecipe.getType() (1.21.1: the type field) */
    public static class_3956<?> singleType(class_3972 recipe) {
        return ((LegacyFields) recipe).cb$type();
    }

    /** SingleItemRecipe.getSerializer() (1.21.1: the serializer field) */
    public static class_1865<?> singleSerializer(class_3972 recipe) {
        return ((LegacyFields) recipe).cb$serializer();
    }

    /** AbstractCookingRecipe.type */
    public static class_3956<?> field_17544(class_1874 r) {
        class_3956<?> t = ((LegacyFields) r).cb$type();
        return t != null ? t : r.method_17716();
    }

    /** AbstractCookingRecipe.group */
    public static String field_9062(class_1874 r) {
        return r.method_8112();
    }

    /** AbstractCookingRecipe.category */
    public static class_7709 field_40241(class_1874 r) {
        return r.method_45438();
    }

    /** AbstractCookingRecipe.ingredient */
    public static class_1856 field_9061(class_1874 r) {
        return r.method_64720();
    }

    /** AbstractCookingRecipe.result */
    public static class_1799 field_9059(class_1874 r) {
        return ((CbItemSingleItemRecipeAccess) r).cb$result();
    }

    /** AbstractCookingRecipe.experience */
    public static float field_9057(class_1874 r) {
        return r.method_8171();
    }

    /** AbstractCookingRecipe.cookingTime */
    public static int field_9058(class_1874 r) {
        return r.method_8167();
    }

    /** SingleItemRecipe.ingredient */
    public static class_1856 field_17642(class_3972 r) {
        return r.method_64720();
    }

    /** SingleItemRecipe.result */
    public static class_1799 field_17643(class_3972 r) {
        return ((CbItemSingleItemRecipeAccess) r).cb$result();
    }

    /** SingleItemRecipe.group */
    public static String field_17645(class_3972 r) {
        return r.method_8112();
    }

    private static volatile class_3542.class_7292<class_7709> cookingCodec;

    /** CookingBookCategory.CODEC: 1.21.1's was the StringRepresentable enum codec (1.21.2's field is a plain Codec) */
    public static class_3542.class_7292<class_7709> field_40245() {
        class_3542.class_7292<class_7709> c = cookingCodec;
        if (c == null) {
            c = class_3542.method_28140(class_7709::values);
            cookingCodec = c;
        }
        return c;
    }

    // ---- SmithingRecipe ----

    public interface LegacyTemplate {
        boolean method_48453(class_1799 stack);
    }

    public interface LegacyBase {
        boolean method_48454(class_1799 stack);
    }

    public interface LegacyAddition {
        boolean method_30029(class_1799 stack);
    }

    /** isTemplateIngredient(stack): the class's own, else its template ingredient's test */
    public static boolean method_48453(class_8059 r, class_1799 stack) {
        return r instanceof LegacyTemplate l ? l.method_48453(stack) : class_1856.method_61676(r.method_64722(), stack);
    }

    /** isBaseIngredient(stack) */
    public static boolean method_48454(class_8059 r, class_1799 stack) {
        return r instanceof LegacyBase l ? l.method_48454(stack) : class_1856.method_61676(r.method_64723(), stack);
    }

    /** isAdditionIngredient(stack) */
    public static boolean method_30029(class_8059 r, class_1799 stack) {
        return r instanceof LegacyAddition l ? l.method_30029(stack) : class_1856.method_61676(r.method_64724(), stack);
    }

    public static boolean method_48453(class_8060 r, class_1799 s) { return method_48453((class_8059) r, s); }
    public static boolean method_48454(class_8060 r, class_1799 s) { return method_48454((class_8059) r, s); }
    public static boolean method_30029(class_8060 r, class_1799 s) { return method_30029((class_8059) r, s); }
    public static boolean method_48453(class_8062 r, class_1799 s) { return method_48453((class_8059) r, s); }
    public static boolean method_48454(class_8062 r, class_1799 s) { return method_48454((class_8059) r, s); }
    public static boolean method_30029(class_8062 r, class_1799 s) { return method_30029((class_8059) r, s); }

    /** a subclass's super call: 1.21.1's body, the ingredient's test */
    public static boolean super_method_48453(class_8060 r, class_1799 s) { return class_1856.method_61676(r.method_64722(), s); }
    public static boolean super_method_48454(class_8060 r, class_1799 s) { return class_1856.method_61676(r.method_64723(), s); }
    public static boolean super_method_30029(class_8060 r, class_1799 s) { return class_1856.method_61676(r.method_64724(), s); }
    public static boolean super_method_48453(class_8062 r, class_1799 s) { return class_1856.method_61676(r.method_64722(), s); }
    public static boolean super_method_48454(class_8062 r, class_1799 s) { return class_1856.method_61676(r.method_64723(), s); }
    public static boolean super_method_30029(class_8062 r, class_1799 s) { return class_1856.method_61676(r.method_64724(), s); }

    private static final Map<Object, Optional<class_1856>[]> SMITHING = Collections.synchronizedMap(new WeakHashMap<>());

    @SuppressWarnings("unchecked")
    private static Optional<class_1856>[] smithing(class_8059 r) {
        return SMITHING.computeIfAbsent(r, k -> new Optional[] {
            accepted(s -> method_48453(r, s)), accepted(s -> method_48454(r, s)), accepted(s -> method_30029(r, s))});
    }

    /**
     * the items a 1.21.1 predicate accepts (asked with a default stack of each), as an ingredient; none is 1.21.2's
     * empty slot (1.21.1's empty ingredient, which matched the empty stack only)
     */
    static Optional<class_1856> accepted(Predicate<class_1799> predicate) {
        List<class_6880<class_1792>> items = new ArrayList<>();
        for (class_1792 item : class_7923.field_41178) {
            if (item != class_1802.field_8162 && predicate.test(new class_1799(item))) {
                items.add(item.method_40131());
            }
        }
        return items.isEmpty() ? Optional.empty() : Optional.of(class_1856.method_8106(class_6885.method_40242(items)));
    }

    /** templateIngredient() of a converted SmithingRecipe implementing only isTemplateIngredient */
    public static Optional<class_1856> templateIngredient(class_8059 r) {
        return smithing(r)[0];
    }

    /** baseIngredient() */
    public static Optional<class_1856> baseIngredient(class_8059 r) {
        return smithing(r)[1];
    }

    /** additionIngredient() */
    public static Optional<class_1856> additionIngredient(class_8059 r) {
        return smithing(r)[2];
    }

    // ---- CraftingInput ----

    /** the 1.21.1 StackedContents an input keeps (CbItemCraftingInputMixin) */
    public interface LegacyStacked {
        top.dext.centurybridge.step.v1_21_2.facade.StackedContents cb$stacked();

        void cb$setStacked(top.dext.centurybridge.step.v1_21_2.facade.StackedContents stacked);
    }

    /**
     * CraftingInput.stackedContents(): 1.21.1's (StackedContents is s7entity's facade), built as 1.21.1's constructor
     * built it -- one of each non-empty stack -- once per input
     */
    public static top.dext.centurybridge.step.v1_21_2.facade.StackedContents method_59988(net.minecraft.class_9694 input) {
        LegacyStacked l = (LegacyStacked) (Object) input;
        synchronized (l) {
            top.dext.centurybridge.step.v1_21_2.facade.StackedContents s = l.cb$stacked();
            if (s == null) {
                s = new top.dext.centurybridge.step.v1_21_2.facade.StackedContents();
                for (class_1799 stack : input.method_59989()) {
                    if (!stack.method_7960()) {
                        s.method_20478(stack, 1);
                    }
                }
                l.cb$setStacked(s);
            }
            return s;
        }
    }

    // ---- RecipeHolder ----

    /** new RecipeHolder(id, recipe): 1.21.2 keys it by the recipe registry's key of that id */
    public static class_5321<net.minecraft.class_1860<?>> key(class_2960 id) {
        return class_5321.method_29179(class_7924.field_52178, id);
    }

    /** RecipeHolder.id(): the key's location */
    public static class_2960 comp_1932(class_8786<?> holder) {
        return holder.comp_1932().method_29177();
    }
}
