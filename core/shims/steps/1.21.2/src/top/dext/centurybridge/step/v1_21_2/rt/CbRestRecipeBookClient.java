package top.dext.centurybridge.step.v1_21_2.rt;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1124;
import net.minecraft.class_1126;
import net.minecraft.class_1129;
import net.minecraft.class_124;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_1860;
import net.minecraft.class_1874;
import net.minecraft.class_299;
import net.minecraft.class_310;
import net.minecraft.class_3955;
import net.minecraft.class_3956;
import net.minecraft.class_5455;
import net.minecraft.class_634;
import net.minecraft.class_7709;
import net.minecraft.class_7710;
import net.minecraft.class_7923;
import net.minecraft.class_8786;
import top.dext.centurybridge.step.v1_21_2.facade.RecipeBookCategories;
import top.dext.centurybridge.step.v1_21_2.facade.RecipeCollection;

/**
 * 1.21.1's client recipe book (ClientRecipeBook's collections by 1.21.1 tab, SessionSearchTrees' recipe search)
 * on 1.21.2's. 1.21.1 grouped every recipe the server sent into collections per tab (setupCollections, when the
 * recipes arrived) and searched them by their result items' tooltips and ids; 1.21.2's book groups only the
 * display entries the player knows, per its own categories. The 1.21.1 state is kept beside each 1.21.2 book
 * and search-tree holder, built by 1.21.1's code from the recipes the layer's synchronisation delivers, at the
 * same moment 1.21.1 built it (ClientPacketListener.handleUpdateRecipes: the book's collections, then the search).
 */
@Environment(EnvType.CLIENT)
public final class CbRestRecipeBookClient {

    private CbRestRecipeBookClient() {
    }

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private record Collections1211(Map<RecipeBookCategories, List<RecipeCollection>> byTab, List<RecipeCollection> all) {
    }

    private static final Map<class_299, Collections1211> BOOKS = java.util.Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<class_1124, Search> SEARCHES = java.util.Collections.synchronizedMap(new WeakHashMap<>());

    private static final class Search {
        CompletableFuture<class_1129<RecipeCollection>> tree = CompletableFuture.completedFuture(class_1129.empty());
        Runnable reload;
    }

    static void init() {
        CbRestRecipesClient.ghostListener = (containerId, recipe) -> {
            class_310 mc = class_310.method_1551();
            if (mc.field_1724 != null && mc.field_1724.field_7512.field_7763 == containerId
                    && mc.field_1755 instanceof top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener listener) {
                setupGhost(listener, recipe);
            }
        };
        CbRestRecipesClient.onRecipesChanged(() -> {
            class_310 mc = class_310.method_1551();
            class_634 connection = mc.method_1562();
            if (mc.field_1724 == null || connection == null) {
                return;
            }
            setupCollections(mc.field_1724.method_3130(), CbRestRecipesClient.recipes(), connection.method_29091());
            updateRecipes(connection.method_60347(), mc.field_1724.method_3130(), connection.method_29091());
        });
    }

    // ------------------------------------------------------------------ screens and their components

    /**
     * a ghost recipe sent as a display, for a 1.21.1 RecipeUpdateListener: 1.21.1's client set the recipe up on its
     * component with the open menu's slots
     */
    public static void fillGhostRecipe(top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener listener, net.minecraft.class_10295 display) {
        class_8786<?> recipe = CbRestRecipesClient.byRecipeDisplay(display);
        if (recipe != null) {
            setupGhost(listener, recipe);
        }
    }

    private static void setupGhost(top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener listener, class_8786<?> recipe) {
        top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent component = listener.method_2659();
        class_310 mc = class_310.method_1551();
        if (component != null && mc.field_1724 != null) {
            component.method_2596(recipe, mc.field_1724.field_7512.field_7761);
        }
    }

    /**
     * getRecipeBookComponent() of 1.21.1's InventoryScreen / CraftingScreen / AbstractFurnaceScreen, called on one:
     * the screen's own answer (a 1.21.1 subclass's override, else its component through the 1.21.1 API)
     */
    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent method_2659(net.minecraft.class_490 screen) {
        return ((top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener) (Object) screen).method_2659();
    }

    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent method_2659(net.minecraft.class_479 screen) {
        return ((top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener) (Object) screen).method_2659();
    }

    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent method_2659(net.minecraft.class_489<?> screen) {
        return ((top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener) (Object) screen).method_2659();
    }

    /** super.getRecipeBookComponent() of a 1.21.1 subclass of those screens: the screen's component, never the override itself */
    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent superRecipeBookComponent(net.minecraft.class_10260<?> screen) {
        return top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent.of(
            ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookScreenAccessor) screen).cb$component());
    }

    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent superRecipeBookComponent(net.minecraft.class_490 screen) {
        return superRecipeBookComponent((net.minecraft.class_10260<?>) screen);
    }

    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent superRecipeBookComponent(net.minecraft.class_479 screen) {
        return superRecipeBookComponent((net.minecraft.class_10260<?>) screen);
    }

    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent superRecipeBookComponent(net.minecraft.class_489<?> screen) {
        return superRecipeBookComponent((net.minecraft.class_10260<?>) screen);
    }

    /** RecipeUpdateListener.getRecipeBookComponent() called on any listener: a 1.21.2 screen's through the 1.21.1 API */
    public static top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent method_2659(net.minecraft.class_518 listener) {
        if (listener instanceof top.dext.centurybridge.step.v1_21_2.facade.RecipeUpdateListener legacy) {
            return legacy.method_2659();
        }
        if (listener instanceof net.minecraft.class_10260<?> screen) {
            return top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent.of(
                ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookScreenAccessor) screen).cb$component());
        }
        return null;
    }

    /** AbstractFurnaceScreen.recipeBookComponent of 1.21.1: the screen's component through the 1.21.1 API */
    public static top.dext.centurybridge.step.v1_21_2.facade.AbstractFurnaceRecipeBookComponent furnaceComponent(net.minecraft.class_489<?> screen) {
        net.minecraft.class_507<?> component = ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookScreenAccessor) screen).cb$component();
        return (top.dext.centurybridge.step.v1_21_2.facade.AbstractFurnaceRecipeBookComponent) top.dext.centurybridge.step.v1_21_2.facade.RecipeBookComponent.of(component);
    }

    private static final ThreadLocal<top.dext.centurybridge.step.v1_21_2.facade.AbstractFurnaceRecipeBookComponent> PENDING_FURNACE = new ThreadLocal<>();

    /**
     * new AbstractFurnaceScreen(menu, component, inventory, title, texture, lit, burn) of 1.21.1, its component
     * argument: the component the screen is to hold (CbRestFurnaceScreenMixin hands it to AbstractRecipeBookScreen
     * in place of the one 1.21.2's constructor makes), and its filter name for 1.21.2's constructor
     */
    public static net.minecraft.class_2561 furnaceComponentArg(Object argument) {
        top.dext.centurybridge.step.v1_21_2.facade.AbstractFurnaceRecipeBookComponent component =
            (top.dext.centurybridge.step.v1_21_2.facade.AbstractFurnaceRecipeBookComponent) argument;
        PENDING_FURNACE.set(component);
        return component.cb$filterName();
    }

    /** the 1.21.1 component a 1.21.1 furnace screen's constructor handed in, once (CbRestFurnaceScreenMixin) */
    public static net.minecraft.class_507<?> takePendingFurnace(net.minecraft.class_507<?> made) {
        top.dext.centurybridge.step.v1_21_2.facade.AbstractFurnaceRecipeBookComponent pending = PENDING_FURNACE.get();
        PENDING_FURNACE.remove();
        return pending != null ? pending : made;
    }

    /** the tab list 1.21.2's furnace screen constructor takes: none, the 1.21.1 component has its own tabs */
    public static List<net.minecraft.class_507.class_10329> noTabs() {
        return List.of();
    }

    // ------------------------------------------------------------------ RecipeToast

    /**
     * RecipeToast.addOrUpdate(toasts, recipe) of 1.21.1: the recipe toast (an existing one, else a new one added)
     * shows the recipe's toast symbol and result, as 1.21.1's did
     */
    public static void method_1985(net.minecraft.class_374 toasts, class_8786<?> recipe) {
        net.minecraft.class_366 toast = toasts.method_1997(net.minecraft.class_366.class, net.minecraft.class_368.field_2208);
        if (toast == null) {
            toast = top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeToastInvoker.cb$new();
            toasts.method_1999(toast);
        }
        addRecipe(toast, recipe);
    }

    /** new RecipeToast(recipe) of 1.21.1: a recipe toast showing the recipe's toast symbol and result */
    public static net.minecraft.class_366 newRecipeToast(class_8786<?> recipe) {
        net.minecraft.class_366 toast = top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeToastInvoker.cb$new();
        addRecipe(toast, recipe);
        return toast;
    }

    private static void addRecipe(net.minecraft.class_366 toast, class_8786<?> recipe) {
        class_310 mc = class_310.method_1551();
        class_5455 access = mc.field_1687 != null ? mc.field_1687.method_30349() : null;
        ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeToastInvoker) toast).cb$addItem(
            CbRestRecipeView.toastSymbol(recipe.comp_1933()), CbRestRecipeView.resultItem(recipe.comp_1933(), access));
    }

    // ------------------------------------------------------------------ ClientRecipeBook

    /** setupCollections(recipes, registryAccess) of a 1.21.1 ClientRecipeBook subclass that overrides it */
    public interface LegacySetupCollections {
        void method_1401(Iterable<class_8786<?>> recipes, class_5455 registryAccess);
    }

    /** getCollection(category) of a 1.21.1 ClientRecipeBook subclass that overrides it */
    public interface LegacyGetCollection {
        List<RecipeCollection> method_1396(RecipeBookCategories category);
    }

    /** updateRecipes(book, registryAccess) of a 1.21.1 SessionSearchTrees subclass that overrides it */
    public interface LegacyUpdateRecipes {
        void method_60352(class_299 book, class_5455.class_6890 registryAccess);
    }

    /** ClientRecipeBook.setupCollections, called on any book: a 1.21.1 subclass's override, else 1.21.1's body */
    public static void setupCollections(class_299 book, Iterable<class_8786<?>> recipes, class_5455 registryAccess) {
        if (book instanceof LegacySetupCollections legacy) {
            legacy.method_1401(recipes, registryAccess);
        } else {
            method_1401(book, recipes, registryAccess);
        }
    }

    /** ClientRecipeBook.getCollection, called on any book: a 1.21.1 subclass's override, else 1.21.1's body */
    public static List<RecipeCollection> getCollection(class_299 book, RecipeBookCategories category) {
        if (book instanceof LegacyGetCollection legacy) {
            return legacy.method_1396(category);
        }
        return method_1396(book, category);
    }

    /** SessionSearchTrees.updateRecipes, called on any holder: a 1.21.1 subclass's override, else 1.21.1's body */
    public static void updateRecipes(class_1124 trees, class_299 book, class_5455.class_6890 registryAccess) {
        if (trees instanceof LegacyUpdateRecipes legacy) {
            legacy.method_60352(book, registryAccess);
        } else {
            method_60352(trees, book, registryAccess);
        }
    }

    /** ClientRecipeBook.setupCollections(recipes, registryAccess) of 1.21.1 */
    public static void method_1401(class_299 book, Iterable<class_8786<?>> recipes, class_5455 registryAccess) {
        Map<RecipeBookCategories, List<List<class_8786<?>>>> grouped = categorizeAndGroupRecipes(recipes);
        Map<RecipeBookCategories, List<RecipeCollection>> byTab = Maps.newHashMap();
        ImmutableList.Builder<RecipeCollection> all = ImmutableList.builder();
        grouped.forEach((category, groups) -> byTab.put(category,
            groups.stream().map(group -> new RecipeCollection(registryAccess, group)).peek(all::add).collect(ImmutableList.toImmutableList())));
        RecipeBookCategories.field_25783.forEach((search, included) -> byTab.put(search,
            included.stream().flatMap(c -> byTab.getOrDefault(c, ImmutableList.of()).stream()).collect(ImmutableList.toImmutableList())));
        BOOKS.put(book, new Collections1211(ImmutableMap.copyOf(byTab), all.build()));
    }

    private static Map<RecipeBookCategories, List<List<class_8786<?>>>> categorizeAndGroupRecipes(Iterable<class_8786<?>> recipes) {
        Map<RecipeBookCategories, List<List<class_8786<?>>>> out = Maps.newHashMap();
        HashBasedTable<RecipeBookCategories, String, List<class_8786<?>>> groups = HashBasedTable.create();
        for (class_8786<?> holder : recipes) {
            class_1860<?> recipe = holder.comp_1933();
            if (!recipe.method_8118() && !CbRestRecipeView.incomplete(recipe)) {
                RecipeBookCategories category = getCategory(holder);
                String group = recipe.method_8112();
                if (group.isEmpty()) {
                    out.computeIfAbsent(category, c -> Lists.newArrayList()).add(ImmutableList.of(holder));
                } else {
                    List<class_8786<?>> list = groups.get(category, group);
                    if (list == null) {
                        list = Lists.newArrayList();
                        groups.put(category, group, list);
                        out.computeIfAbsent(category, c -> Lists.newArrayList()).add(list);
                    }
                    list.add(holder);
                }
            }
        }
        return out;
    }

    /** 1.21.1's tab of a recipe: by crafting / cooking book category and recipe type */
    private static RecipeBookCategories getCategory(class_8786<?> holder) {
        class_1860<?> recipe = holder.comp_1933();
        if (recipe instanceof class_3955 crafting) {
            class_7710 c = crafting.method_45441();
            if (c == class_7710.field_40248) {
                return RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            }
            if (c == class_7710.field_40250) {
                return RecipeBookCategories.CRAFTING_EQUIPMENT;
            }
            if (c == class_7710.field_40249) {
                return RecipeBookCategories.CRAFTING_REDSTONE;
            }
            return RecipeBookCategories.CRAFTING_MISC;
        }
        class_3956<?> type = recipe.method_17716();
        if (recipe instanceof class_1874 cooking) {
            class_7709 c = cooking.method_45438();
            if (type == class_3956.field_17546) {
                return c == class_7709.field_40243 ? RecipeBookCategories.FURNACE_BLOCKS
                    : c == class_7709.field_40242 ? RecipeBookCategories.FURNACE_FOOD : RecipeBookCategories.FURNACE_MISC;
            }
            if (type == class_3956.field_17547) {
                return c == class_7709.field_40243 ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
            }
            if (type == class_3956.field_17548) {
                return RecipeBookCategories.SMOKER_FOOD;
            }
            if (type == class_3956.field_17549) {
                return RecipeBookCategories.CAMPFIRE;
            }
        }
        if (type == class_3956.field_17641) {
            return RecipeBookCategories.STONECUTTER;
        }
        if (type == class_3956.field_25388) {
            return RecipeBookCategories.SMITHING;
        }
        LOGGER.warn("Unknown recipe category: {}/{}", class_7923.field_41188.method_10221(recipe.method_17716()), holder.comp_1932().method_29177());
        return RecipeBookCategories.UNKNOWN;
    }

    /** ClientRecipeBook.getCollections() of 1.21.1 */
    public static List<RecipeCollection> method_1393(class_299 book) {
        Collections1211 c = BOOKS.get(book);
        return c == null ? List.of() : c.all();
    }

    /** ClientRecipeBook.getCollection(category) of 1.21.1 */
    public static List<RecipeCollection> method_1396(class_299 book, RecipeBookCategories category) {
        Collections1211 c = BOOKS.get(book);
        return c == null ? java.util.Collections.emptyList() : c.byTab().getOrDefault(category, java.util.Collections.emptyList());
    }

    // ------------------------------------------------------------------ SessionSearchTrees

    /** SessionSearchTrees.updateRecipes(book, registryAccess) of 1.21.1: the recipe search over the book's 1.21.1 collections */
    public static void method_60352(class_1124 trees, class_299 book, class_5455.class_6890 registryAccess) {
        Search search = SEARCHES.computeIfAbsent(trees, t -> new Search());
        search.reload = () -> {
            List<RecipeCollection> collections = method_1393(book);
            class_1792.class_9635 context = class_1792.class_9635.method_59530(registryAccess);
            class_1836.class_1837 flag = class_1836.field_41070;
            CompletableFuture<class_1129<RecipeCollection>> previous = search.tree;
            search.tree = CompletableFuture.supplyAsync(() -> new class_1126<RecipeCollection>(
                c -> tooltipLines(c.method_2650().stream().map(h -> CbRestRecipeView.resultItem(h.comp_1933(), registryAccess)), context, flag),
                c -> c.method_2650().stream().map(h -> class_7923.field_41178.method_10221(CbRestRecipeView.resultItem(h.comp_1933(), registryAccess).method_7909())),
                collections), net.minecraft.class_156.method_18349());
            previous.cancel(true);
        };
        search.reload.run();
    }

    /** SessionSearchTrees.recipes() of 1.21.1 */
    public static class_1129<RecipeCollection> method_60364(class_1124 trees) {
        Search search = SEARCHES.get(trees);
        return search == null ? class_1129.empty() : search.tree.join();
    }

    /** SessionSearchTrees.rebuildAfterLanguageChange: the 1.21.1 recipe search too */
    public static void rebuildAfterLanguageChange(class_1124 trees) {
        Search search = SEARCHES.get(trees);
        if (search != null && search.reload != null) {
            search.reload.run();
        }
    }

    private static Stream<String> tooltipLines(Stream<class_1799> stacks, class_1792.class_9635 context, class_1836 flag) {
        return stacks.flatMap(s -> s.method_7950(context, null, flag).stream())
            .map(c -> class_124.method_539(c.getString()).trim())
            .filter(s -> !s.isEmpty());
    }
}
