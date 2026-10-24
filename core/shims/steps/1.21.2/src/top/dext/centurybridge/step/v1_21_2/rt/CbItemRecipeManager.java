package top.dext.centurybridge.step.v1_21_2.rt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_10289;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1937;
import net.minecraft.class_2371;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3695;
import net.minecraft.class_3956;
import net.minecraft.class_7225;
import net.minecraft.class_8786;
import net.minecraft.class_9695;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_21_2.mixin.CbItemRecipeManagerAccess;

/**
 * 1.21.1's RecipeManager members on 1.21.2's RecipeManager -- the server's recipeAccess() and the client's one the
 * layer's recipe sync fills (CbRestRecipesClient), the same code path. 1.21.1 kept the recipes by type and by id;
 * 1.21.2 keeps them in a RecipeMap with the holders keyed by ResourceKey. Each body is 1.21.1's, over that map:
 * getAllRecipesFor (the type's recipes), getRecipesFor (the matching ones, sorted by the description id of their
 * 1.21.1 result item), getRemainingItemsFor (the recipe's 1.21.1 remainders, else the input's stacks), byKey,
 * getOrderedRecipes / getRecipeIds, replaceRecipes (a new map of exactly those recipes), fromJson / apply (1.21.1's
 * JSON parsing). hasErrors: 1.21.1 cleared the flag on every load and replace and never set it -- always false.
 *
 * <p>A 1.21.1 subclass overriding apply(Map&lt;id, json&gt;, ..) is called by 1.21.2's apply(RecipeMap, ..) with the
 * recipes 1.21.2 parsed, each encoded back to JSON with the recipe codec ({@link #apply}, overrideHooks bridge).
 */
public final class CbItemRecipeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/RecipeManager");

    private CbItemRecipeManager() {
    }

    private static class_10289 map(class_1863 rm) {
        return ((CbItemRecipeManagerAccess) rm).cb$recipeMap();
    }

    /** hasErrors() */
    public static boolean super_method_35227(class_1863 rm) {
        return false;
    }

    /** getRecipeFor(type, input, level, lastRecipe id) */
    public static <I extends class_9695, T extends class_1860<I>> Optional<class_8786<T>> super_method_42299(class_1863 rm,
            class_3956<T> type, I input, class_1937 level, class_2960 last) {
        return rm.method_42299(type, input, level, last == null ? null : CbItemRecipeClasses.key(last));
    }

    /** getAllRecipesFor(type): a copy of the type's recipes */
    public static <I extends class_9695, T extends class_1860<I>> List<class_8786<T>> super_method_30027(class_1863 rm, class_3956<T> type) {
        return List.copyOf(map(rm).method_64698(type));
    }

    /** getRecipesFor(type, input, level): the matching recipes, by their result's description id */
    public static <I extends class_9695, T extends class_1860<I>> List<class_8786<T>> super_method_17877(class_1863 rm,
            class_3956<T> type, I input, class_1937 level) {
        return map(rm).method_64698(type).stream()
            .filter(h -> h.comp_1933().method_8115(input, level))
            .sorted(Comparator.comparing(h -> CbItemMembers.method_7922(CbItemRecipes.method_8110(h.comp_1933(), level.method_30349()))))
            .collect(Collectors.toList());
    }

    /** getRemainingItemsFor(type, input, level): the recipe's remainders, else the input's stacks */
    public static <I extends class_9695, T extends class_1860<I>> class_2371<class_1799> super_method_8128(class_1863 rm,
            class_3956<T> type, I input, class_1937 level) {
        Optional<class_8786<T>> found = rm.method_8132(type, input, level);
        if (found.isPresent()) {
            return CbItemRecipes.method_8111(found.get().comp_1933(), input);
        }
        class_2371<class_1799> out = class_2371.method_10213(input.method_59983(), class_1799.field_8037);
        for (int i = 0; i < out.size(); i++) {
            out.set(i, input.method_59984(i));
        }
        return out;
    }

    /** byKey(id) */
    public static Optional<class_8786<?>> super_method_8130(class_1863 rm, class_2960 id) {
        return rm.method_8130(CbItemRecipeClasses.key(id));
    }

    /** getOrderedRecipes(): every recipe */
    public static Collection<class_8786<?>> super_method_59822(class_1863 rm) {
        return map(rm).method_64695();
    }

    /** getRecipeIds() */
    public static Stream<class_2960> super_method_8127(class_1863 rm) {
        return map(rm).method_64695().stream().map(h -> h.comp_1932().method_29177());
    }

    /** replaceRecipes(recipes): the manager holds exactly these */
    public static void super_method_20702(class_1863 rm, Iterable<class_8786<?>> recipes) {
        ((CbItemRecipeManagerAccess) rm).cb$setRecipeMap(class_10289.method_64700(recipes));
    }

    /** fromJson(id, json, registries): the recipe codec's reading of the JSON, held under the id */
    public static class_8786<?> method_17720(class_2960 id, JsonObject json, class_7225.class_7874 registries) {
        class_1860<?> recipe = class_1860.field_47319.parse(registries.method_57093(JsonOps.INSTANCE), json)
            .getOrThrow(JsonParseException::new);
        return new class_8786<>(CbItemRecipeClasses.key(id), recipe);
    }

    /** a 1.21.1 RecipeManager subclass's own apply(Map, ResourceManager, Profiler) */
    public interface LegacyApply {
        void method_20705(Map<class_2960, JsonElement> recipes, class_3300 resources, class_3695 profiler);
    }

    /**
     * apply(map, resources, profiler) called on a manager (a subclass's own call, or its super call): the class's own
     * when it overrides it, else 1.21.1's body -- each JSON read by the recipe codec (a failure logged and skipped),
     * the manager then holding exactly those recipes
     */
    public static void method_20705(class_1863 rm, Map<class_2960, JsonElement> recipes, class_3300 resources, class_3695 profiler) {
        if (rm instanceof LegacyApply l) {
            l.method_20705(recipes, resources, profiler);
            return;
        }
        super_method_20705(rm, recipes, resources, profiler);
    }

    /** super.apply(map, resources, profiler): 1.21.1's body */
    public static void super_method_20705(class_1863 rm, Map<class_2960, JsonElement> recipes, class_3300 resources,
            class_3695 profiler) {
        class_7225.class_7874 registries = ((CbItemRecipeManagerAccess) rm).cb$registries();
        List<class_8786<?>> holders = new ArrayList<>();
        for (Map.Entry<class_2960, JsonElement> e : recipes.entrySet()) {
            try {
                class_1860<?> recipe = class_1860.field_47319.parse(registries.method_57093(JsonOps.INSTANCE), e.getValue())
                    .getOrThrow(JsonParseException::new);
                holders.add(new class_8786<>(CbItemRecipeClasses.key(e.getKey()), recipe));
            } catch (IllegalArgumentException | JsonParseException ex) {
                LOGGER.error("Parsing error loading recipe {}", e.getKey(), ex);
            }
        }
        super_method_20702(rm, holders);
        LOGGER.info("Loaded {} recipes", holders.size());
    }

    /**
     * 1.21.2's apply(RecipeMap, resources, profiler) on a 1.21.1 subclass overriding apply(Map, ..): the recipes
     * 1.21.2 parsed, each encoded back to its JSON with the recipe codec, handed to the override
     */
    public static void apply(LegacyApply self, class_10289 parsed, class_3300 resources, class_3695 profiler) {
        class_7225.class_7874 registries = ((CbItemRecipeManagerAccess) self).cb$registries();
        Map<class_2960, JsonElement> json = new LinkedHashMap<>();
        for (class_8786<?> h : parsed.method_64695()) {
            class_1860.field_47319.encodeStart(registries.method_57093(JsonOps.INSTANCE), h.comp_1933())
                .ifSuccess(j -> json.put(h.comp_1932().method_29177(), j))
                .ifError(err -> LOGGER.error("Could not encode recipe {} for a 1.21.1 RecipeManager.apply: {}",
                    h.comp_1932().method_29177(), err.message()));
        }
        self.method_20705(json, resources, profiler);
    }


    // ---- calls: the class's own 1.21.1 override (adapterHooks), else 1.21.1's body (super_*) ----

    public interface LegacyHasErrors {
        boolean method_35227();
    }

    public interface LegacyRecipeForLast {
        <I extends class_9695, T extends class_1860<I>> Optional<class_8786<T>> method_42299(class_3956<T> type, I input,
            class_1937 level, class_2960 last);
    }

    public interface LegacyAllRecipesFor {
        <I extends class_9695, T extends class_1860<I>> List<class_8786<T>> method_30027(class_3956<T> type);
    }

    public interface LegacyRecipesFor {
        <I extends class_9695, T extends class_1860<I>> List<class_8786<T>> method_17877(class_3956<T> type, I input, class_1937 level);
    }

    public interface LegacyRemainingFor {
        <I extends class_9695, T extends class_1860<I>> class_2371<class_1799> method_8128(class_3956<T> type, I input, class_1937 level);
    }

    public interface LegacyByKey {
        Optional<class_8786<?>> method_8130(class_2960 id);
    }

    public interface LegacyOrdered {
        Collection<class_8786<?>> method_59822();
    }

    public interface LegacyIds {
        Stream<class_2960> method_8127();
    }

    public interface LegacyReplace {
        void method_20702(Iterable<class_8786<?>> recipes);
    }

    public static boolean method_35227(class_1863 rm) {
        return rm instanceof LegacyHasErrors l ? l.method_35227() : super_method_35227(rm);
    }

    public static <I extends class_9695, T extends class_1860<I>> Optional<class_8786<T>> method_42299(class_1863 rm,
            class_3956<T> type, I input, class_1937 level, class_2960 last) {
        return rm instanceof LegacyRecipeForLast l ? l.method_42299(type, input, level, last)
            : super_method_42299(rm, type, input, level, last);
    }

    public static <I extends class_9695, T extends class_1860<I>> List<class_8786<T>> method_30027(class_1863 rm, class_3956<T> type) {
        return rm instanceof LegacyAllRecipesFor l ? l.method_30027(type) : super_method_30027(rm, type);
    }

    public static <I extends class_9695, T extends class_1860<I>> List<class_8786<T>> method_17877(class_1863 rm,
            class_3956<T> type, I input, class_1937 level) {
        return rm instanceof LegacyRecipesFor l ? l.method_17877(type, input, level) : super_method_17877(rm, type, input, level);
    }

    public static <I extends class_9695, T extends class_1860<I>> class_2371<class_1799> method_8128(class_1863 rm,
            class_3956<T> type, I input, class_1937 level) {
        return rm instanceof LegacyRemainingFor l ? l.method_8128(type, input, level) : super_method_8128(rm, type, input, level);
    }

    public static Optional<class_8786<?>> method_8130(class_1863 rm, class_2960 id) {
        return rm instanceof LegacyByKey l ? l.method_8130(id) : super_method_8130(rm, id);
    }

    public static Collection<class_8786<?>> method_59822(class_1863 rm) {
        return rm instanceof LegacyOrdered l ? l.method_59822() : super_method_59822(rm);
    }

    public static Stream<class_2960> method_8127(class_1863 rm) {
        return rm instanceof LegacyIds l ? l.method_8127() : super_method_8127(rm);
    }

    public static void method_20702(class_1863 rm, Iterable<class_8786<?>> recipes) {
        if (rm instanceof LegacyReplace l) {
            l.method_20702(recipes);
        } else {
            super_method_20702(rm, recipes);
        }
    }

    // ---- a 1.21.1 CachedCheck lambda (getRecipeFor(input, Level)) ----

    /** the 1.21.1 shape of CachedCheck, which a mod's lambda implements */
    @FunctionalInterface
    public interface LegacyCachedCheck<I extends class_9695, T extends class_1860<I>> {
        Optional<class_8786<T>> method_42303(I input, class_1937 level);
    }

    /** a 1.21.1 CachedCheck lambda as 1.21.2's (asked with a server level, a Level as 1.21.1 was) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_1863.class_7266 fromLambda(LegacyCachedCheck check) {
        return new LambdaCheck(check);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    record LambdaCheck(LegacyCachedCheck check) implements class_1863.class_7266 {
        @Override
        public Optional method_42303(class_9695 input, net.minecraft.class_3218 level) {
            return check.method_42303(input, level);
        }
    }

    // ---- RecipeManager.CachedCheck ----

    /**
     * createCheck(type): 1.21.1's quick check -- the type and the id of the last recipe it found; it asks the level's
     * RecipeManager (CbLevelLevels) for the first match, the last one first. 1.21.2's interface asks with a server
     * level, 1.21.1's with any level ({@link #method_42303}).
     */
    public static <I extends class_9695, T extends class_1860<I>> class_1863.class_7266<I, T> method_42302(class_3956<T> type) {
        return new LegacyCheck<>(type);
    }

    static final class LegacyCheck<I extends class_9695, T extends class_1860<I>> implements class_1863.class_7266<I, T> {
        private final class_3956<T> type;
        private class_2960 lastRecipe;

        LegacyCheck(class_3956<T> type) {
            this.type = type;
        }

        Optional<class_8786<T>> find(I input, class_1937 level) {
            class_1863 rm = CbLevelLevels.method_8433(level);
            Optional<class_8786<T>> found = method_42299(rm, type, input, level, lastRecipe);
            if (found.isPresent()) {
                lastRecipe = found.get().comp_1932().method_29177();
                return found;
            }
            return Optional.empty();
        }

        @Override
        public Optional<class_8786<T>> method_42303(I input, net.minecraft.class_3218 level) {
            return find(input, level);
        }
    }

    /** CachedCheck.getRecipeFor(input, level): 1.21.1's check with any level; 1.21.2's own with a server level */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional method_42303(class_1863.class_7266 check, class_9695 input, class_1937 level) {
        if (check instanceof LegacyCheck l) {
            return l.find(input, level);
        }
        if (check instanceof LambdaCheck l) {
            return l.check().method_42303(input, level);
        }
        if (level instanceof net.minecraft.class_3218 server) {
            return check.method_42303(input, server);
        }
        throw new UnsupportedOperationException("1.21.2's RecipeManager.CachedCheck " + check.getClass().getName()
            + " answers only on a server level (1.21.1's asked any level's RecipeManager)");
    }
}
