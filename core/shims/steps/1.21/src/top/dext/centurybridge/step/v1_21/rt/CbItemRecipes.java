package top.dext.centurybridge.step.v1_21.rt;

import com.google.common.collect.MapMaker;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1263;
import net.minecraft.class_1662;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_3956;
import net.minecraft.class_8566;
import net.minecraft.class_9694;
import net.minecraft.class_9695;
import net.minecraft.class_9696;
import net.minecraft.class_9697;

/**
 * Recipe inputs. 1.20.6 handed a recipe the container itself (Recipe&lt;C extends Container&gt;: the
 * crafting grid as a CraftingContainer, a furnace's or stonecutter's inventory, the smithing table's three
 * slots); 1.21 hands it a RecipeInput built from the container (Recipe&lt;T extends RecipeInput&gt;): a
 * CraftingInput, a SingleRecipeInput, a SmithingRecipeInput.
 *
 * <p>The two directions undo each other. Every input this class builds from a container remembers the
 * container (an identity map with weak keys: the inputs are records and CraftingInput compares by
 * content, so equal inputs of two grids are still told apart), and the way back answers that very
 * container -- a 1.20.6 recipe a 1.20.6 caller asked keeps seeing the caller's container. 1.21's own
 * producers of a CraftingInput from a grid (the crafting and inventory menus, the result slot, the
 * crafter) go through {@link #cbCraftingInput} too (mixins CbItemRecipe*Mixin), so a 1.20.6 recipe asked
 * by 1.21 code sees the full grid it was written against: 1.21 trims the input to the occupied
 * rectangle (CraftingInput.ofPositioned), 1.20.6 always passed the whole grid, and a recipe testing
 * the grid's width or a slot index tells the two apart. An input no container stands behind (one
 * 1.21 code built from a list) comes back as a read-only container of exactly its items, which is all
 * there is to give; writes to it throw instead of vanishing.
 *
 * <p>A crafting grid's leftovers travel the other way: 1.20.6's getRemainingItems answered one stack
 * per grid slot, 1.21's answers one per slot of the trimmed input and the result slot puts them back
 * at the input's offset ({@link #cbRemainingForInput}).
 */
public final class CbItemRecipes {

    /** where an input came from: the container, and the grid offset of a trimmed crafting input */
    private record Origin(class_1263 container, int left, int top) {
    }

    private static final Map<Object, Origin> ORIGINS = new MapMaker().weakKeys().makeMap();

    private CbItemRecipes() {
    }

    // ---- V -> W ----------------------------------------------------------------------------------

    /** CraftingContainer -> CraftingInput: what 1.21's container.asCraftInput() makes, remembered */
    public static class_9694 cbCraftingInput(class_8566 container) {
        if (container == null) {
            return null;
        }
        return cbPositioned(container).comp_2795();
    }

    /** CraftingContainer -> CraftingInput.Positioned (1.21's asPositionedCraftInput), remembered */
    public static class_9694.class_9765 cbPositioned(class_8566 container) {
        class_9694.class_9765 p = class_9694.method_60505(container.method_17398(), container.method_17397(), container.method_51305());
        // the shared EMPTY input stands for every empty grid at once: no single container behind it
        if (p.comp_2795() != class_9694.field_51631) {
            ORIGINS.put(p.comp_2795(), new Origin(container, p.comp_2796(), p.comp_2797()));
        }
        return p;
    }

    /** a furnace's, campfire's or stonecutter's container -> SingleRecipeInput of its slot 0 (where 1.20.6 read it) */
    public static class_9696 cbSingleInput(class_1263 container) {
        if (container == null) {
            return null;
        }
        class_9696 in = new class_9696(container.method_5438(0));
        ORIGINS.put(in, new Origin(container, 0, 0));
        return in;
    }

    /** the smithing table's container -> SmithingRecipeInput(template, base, addition) of slots 0, 1, 2 */
    public static class_9697 cbSmithingInput(class_1263 container) {
        if (container == null) {
            return null;
        }
        class_9697 in = new class_9697(container.method_5438(0), container.method_5438(1), container.method_5438(2));
        ORIGINS.put(in, new Origin(container, 0, 0));
        return in;
    }

    /**
     * Container -> RecipeInput, adaptive: a crafting grid becomes its CraftingInput; any other container a
     * RecipeInput view of itself (slot i is getItem(i)), which is what a recipe of a mod's own type was
     * written to read. A cooking or smithing recipe wants its own input record: callers that know the
     * recipe type use {@link #cbInputFor}.
     */
    public static class_9695 cbRecipeInput(class_1263 container) {
        if (container == null) {
            return null;
        }
        if (container instanceof class_8566 grid) {
            return cbCraftingInput(grid);
        }
        return new ContainerInput(container);
    }

    /**
     * The input a recipe of this type takes in 1.21, built from the container 1.20.6 passed: crafting a
     * CraftingInput (a non-grid container fails as it did in 1.20.6, where the crafting recipe's bridge
     * method cast it to CraftingContainer); smelting, blasting, smoking, campfire cooking and stonecutting
     * a SingleRecipeInput of slot 0; smithing a SmithingRecipeInput of slots 0-2; a mod's own type the
     * container's RecipeInput view.
     */
    public static class_9695 cbInputFor(class_3956<?> type, class_1263 container) {
        if (container == null) {
            return null;
        }
        if (type == class_3956.field_17545) {
            return cbCraftingInput((class_8566) container);
        }
        if (type == class_3956.field_17546 || type == class_3956.field_17547 || type == class_3956.field_17548
                || type == class_3956.field_17549 || type == class_3956.field_17641) {
            return cbSingleInput(container);
        }
        if (type == class_3956.field_25388) {
            return cbSmithingInput(container);
        }
        return cbRecipeInput(container);
    }

    // ---- W -> V ----------------------------------------------------------------------------------

    /** CraftingInput -> CraftingContainer: the grid it was made from, else a read-only grid of its items */
    public static class_8566 cbCraftingContainer(class_9694 input) {
        if (input == null) {
            return null;
        }
        Origin o = ORIGINS.get(input);
        if (o != null && o.container instanceof class_8566 grid) {
            return grid;
        }
        return new InputGrid(input);
    }

    /**
     * RecipeInput -> Container, adaptive: CB's view of a container answers the container; an input made
     * from a container answers that container; a 1.21 input made from items alone becomes a read-only
     * container of those items (a CraftingInput a grid of its width and height).
     */
    public static class_1263 cbContainer(class_9695 input) {
        if (input == null) {
            return null;
        }
        if (input instanceof ContainerInput view) {
            return view.container;
        }
        if (input instanceof class_9694 crafting) {
            return cbCraftingContainer(crafting);
        }
        Origin o = ORIGINS.get(input);
        if (o != null) {
            return o.container;
        }
        return new InputContainer(input);
    }

    /**
     * A 1.20.6 recipe's leftovers for the grid it was shown (one per grid slot, the grid this class
     * handed it for {@code input}) as the list 1.21 wants for the trimmed input: the result slot reads
     * entry x + y * input.width() and puts it at the input's offset in the grid. A list already the
     * input's size (the recipe was shown the input's own items) passes unchanged.
     */
    public static class_2371<class_1799> cbRemainingForInput(class_9694 input, class_2371<class_1799> remaining) {
        if (input == null || remaining == null || remaining.size() == input.method_59983()) {
            return remaining;
        }
        Origin o = ORIGINS.get(input);
        if (o == null || !(o.container instanceof class_8566 grid)) {
            throw new IllegalStateException("leftovers for " + remaining.size() + " slots, but the crafting input has "
                + input.method_59983() + " and no grid behind it");
        }
        class_2371<class_1799> out = class_2371.method_10213(input.method_59983(), class_1799.field_8037);
        for (int y = 0; y < input.method_59992(); y++) {
            for (int x = 0; x < input.method_59991(); x++) {
                out.set(x + y * input.method_59991(), remaining.get(x + o.left + (y + o.top) * grid.method_17398()));
            }
        }
        return out;
    }

    /**
     * The inverse of {@link #cbRemainingForInput}: 1.21's leftovers for a trimmed crafting input as
     * 1.20.6's list, one per slot of the grid the input was made from (the input's offset in the grid
     * placing them; every other slot empty). An input no grid stands behind answers its own list.
     */
    public static class_2371<class_1799> cbRemainingForContainer(class_9694 input, class_2371<class_1799> remaining) {
        if (input == null || remaining == null) {
            return remaining;
        }
        Origin o = ORIGINS.get(input);
        if (o == null || !(o.container instanceof class_8566 grid) || grid.method_5439() == remaining.size()) {
            return remaining;
        }
        class_2371<class_1799> out = class_2371.method_10213(grid.method_5439(), class_1799.field_8037);
        for (int y = 0; y < input.method_59992(); y++) {
            for (int x = 0; x < input.method_59991(); x++) {
                out.set(x + o.left + (y + o.top) * grid.method_17398(), remaining.get(x + y * input.method_59991()));
            }
        }
        return out;
    }

    /** cbSingleInput typed as the RecipeInput a 1.21 bridge method takes (the bridge casts it to SingleRecipeInput) */
    public static class_9695 cbSingleRecipeInput(class_1263 container) {
        return cbSingleInput(container);
    }

    /** cbSmithingInput typed as the RecipeInput a 1.21 bridge method takes */
    public static class_9695 cbSmithingRecipeInput(class_1263 container) {
        return cbSmithingInput(container);
    }

    // ---- Recipe, RecipeManager: the input form only the receiver tells ----------------------------

    /** Recipe.matches(Container, Level) (instanceRedirect): the input the recipe's type takes */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean method_8115(net.minecraft.class_1860 recipe, class_1263 container, net.minecraft.class_1937 level) {
        return recipe.method_8115(cbInputFor(recipe.method_17716(), container), level);
    }

    /** Recipe.assemble(Container, HolderLookup.Provider) (instanceRedirect) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_1799 method_8116(net.minecraft.class_1860 recipe, class_1263 container, net.minecraft.class_7225.class_7874 registries) {
        return recipe.method_8116(cbInputFor(recipe.method_17716(), container), registries);
    }

    /**
     * Recipe.getRemainingItems(Container) (instanceRedirect): one leftover per slot of the container, as
     * 1.20.6 answered. A 1.20.6 recipe that overrides it (the bridge hook gave its class the adapter)
     * answers itself, handed the container; any other gets the container's input -- a grid's
     * CraftingInput, the leftovers put back at the input's offset, else a view of the whole container,
     * which Recipe's default (the only other implementation 1.21 has) reads slot by slot as 1.20.6's did.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_2371<class_1799> method_8111(net.minecraft.class_1860 recipe, class_1263 container) {
        if (recipe instanceof CbLegacyRemaining legacy) {
            return legacy.method_8111(container);
        }
        if (container instanceof class_8566 grid) {
            if (recipe instanceof CbLegacyRemaining17704 legacy) {
                return legacy.method_17704(grid);
            }
            if (recipe instanceof CbLegacyRemaining17707 legacy) {
                return legacy.method_17707(grid);
            }
        }
        class_9695 input = cbRecipeInput(container);
        class_2371<class_1799> remaining = recipe.method_8111(input);
        return input instanceof class_9694 crafting ? cbRemainingForContainer(crafting, remaining) : remaining;
    }

    /** BannerDuplicateRecipe.getRemainingItems(CraftingContainer) (instanceRedirect) */
    public static class_2371<class_1799> method_17704(net.minecraft.class_1848 recipe, class_8566 grid) {
        if (recipe instanceof CbLegacyRemaining17704 legacy) {
            return legacy.method_17704(grid);
        }
        class_9694 input = cbCraftingInput(grid);
        return cbRemainingForContainer(input, recipe.method_17704(input));
    }

    /** BookCloningRecipe.getRemainingItems(CraftingContainer) (instanceRedirect) */
    public static class_2371<class_1799> method_17707(net.minecraft.class_1850 recipe, class_8566 grid) {
        if (recipe instanceof CbLegacyRemaining17707 legacy) {
            return legacy.method_17707(grid);
        }
        class_9694 input = cbCraftingInput(grid);
        return cbRemainingForContainer(input, recipe.method_17707(input));
    }

    /** RecipeManager.getRecipeFor(RecipeType, Container, Level) (instanceRedirect) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Optional method_8132(net.minecraft.class_1863 manager, class_3956 type, class_1263 container, net.minecraft.class_1937 level) {
        return manager.method_8132(type, cbInputFor(type, container), level);
    }

    /** RecipeManager.getRecipeFor(RecipeType, Container, Level, ResourceLocation) (instanceRedirect) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Optional method_42299(net.minecraft.class_1863 manager, class_3956 type, class_1263 container, net.minecraft.class_1937 level,
            net.minecraft.class_2960 last) {
        return manager.method_42299(type, cbInputFor(type, container), level, last);
    }

    /** RecipeManager.getRecipesFor(RecipeType, Container, Level) (instanceRedirect) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List method_17877(net.minecraft.class_1863 manager, class_3956 type, class_1263 container, net.minecraft.class_1937 level) {
        return manager.method_17877(type, cbInputFor(type, container), level);
    }

    /**
     * RecipeManager.getRemainingItemsFor(RecipeType, Container, Level) (instanceRedirect): per slot of the
     * container. A 1.20.6 manager subclass that overrides it answers itself; any other manager answers
     * 1.20.6's body ({@link #legacyRemainingFor}), whose lookup and leftovers are the manager's and the
     * recipe's own (a manager class that overrides 1.21's getRemainingItemsFor without coming from 1.20.6
     * is answered by that body too).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_2371<class_1799> method_8128(net.minecraft.class_1863 manager, class_3956 type, class_1263 container, net.minecraft.class_1937 level) {
        if (manager instanceof CbLegacyRemainingFor legacy) {
            return legacy.method_8128(type, container, level);
        }
        return legacyRemainingFor(manager, type, container, level);
    }

    /**
     * 1.20.6's RecipeManager.getRemainingItemsFor: the leftovers of the recipe getRecipeFor finds (asked
     * through this class, so the manager's and the recipe's own implementations answer), else the
     * container's items as they are, one per slot.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static class_2371<class_1799> legacyRemainingFor(net.minecraft.class_1863 manager, class_3956 type, class_1263 container, net.minecraft.class_1937 level) {
        java.util.Optional<net.minecraft.class_8786> found = method_8132(manager, type, container, level);
        if (found.isPresent()) {
            return method_8111(found.get().comp_1933(), container);
        }
        class_2371<class_1799> out = class_2371.method_10213(container.method_5439(), class_1799.field_8037);
        for (int i = 0; i < out.size(); i++) {
            out.set(i, container.method_5438(i));
        }
        return out;
    }

    /**
     * RecipeManager.createCheck(RecipeType) (staticRedirect: 1.20.6's check took a container, 1.21's an
     * input): 1.21's check, remembering the recipe type, so that a 1.20.6 caller's container becomes the
     * input of that type ({@link #method_42303}); vanilla code holding it calls it with 1.21 inputs.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_1863.class_7266 method_42302(class_3956 type) {
        return new TypedCheck(type, net.minecraft.class_1863.method_42302(type));
    }

    /** RecipeManager.CachedCheck.getRecipeFor(Container, Level) (instanceRedirect) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Optional method_42303(net.minecraft.class_1863.class_7266 check, class_1263 container, net.minecraft.class_1937 level) {
        if (check instanceof TypedCheck typed) {
            return typed.check.method_42303(cbInputFor(typed.type, container), level);
        }
        if (check instanceof LegacyCheck legacy) {
            return legacy.legacy.method_42303(container, level);
        }
        return check.method_42303(cbRecipeInput(container), level);
    }

    /** a CachedCheck that knows its recipe type */
    @SuppressWarnings({"rawtypes"})
    private record TypedCheck(class_3956 type, net.minecraft.class_1863.class_7266 check) implements net.minecraft.class_1863.class_7266 {
        @SuppressWarnings("unchecked")
        @Override
        public java.util.Optional method_42303(class_9695 input, net.minecraft.class_1937 level) {
            return check.method_42303(input, level);
        }
    }

    /** a 1.20.6 mod's CachedCheck lambda: 1.20.6's single method, getRecipeFor(Container, Level) */
    @FunctionalInterface
    public interface CbLegacyCachedCheck {
        @SuppressWarnings("rawtypes")
        java.util.Optional method_42303(class_1263 container, net.minecraft.class_1937 level);
    }

    /** lambdaAdapters: a 1.20.6 CachedCheck lambda as 1.21's check, handed the container the input stands for */
    public static net.minecraft.class_1863.class_7266 cbCachedCheck(CbLegacyCachedCheck legacy) {
        return new LegacyCheck(legacy);
    }

    private record LegacyCheck(CbLegacyCachedCheck legacy) implements net.minecraft.class_1863.class_7266 {
        @SuppressWarnings("rawtypes")
        @Override
        public java.util.Optional method_42303(class_9695 input, net.minecraft.class_1937 level) {
            return legacy.method_42303(cbContainer(input), level);
        }
    }

    // ---- PlaceRecipe ------------------------------------------------------------------------------

    /**
     * PlaceRecipe.addItemToSlot's first argument (callReshapes): 1.20.6 handed the implementation the
     * ingredient iterator and the implementation took one ingredient (ServerPlaceRecipe:
     * ingredients.next()); 1.21 takes the ingredient itself, taken by the caller.
     */
    public static Object cbNextIngredient(java.util.Iterator<?> ingredients) {
        return ingredients.next();
    }

    /** the override side: the ingredient 1.21 passes as the one-element iterator a 1.20.6 implementation takes it from */
    public static java.util.Iterator<?> cbIngredientIterator(Object ingredient) {
        return java.util.List.of(ingredient).iterator();
    }

    /** a 1.20.6 mod's PlaceRecipe lambda: addItemToSlot(Iterator ingredients, slot, maxAmount, y, x) */
    @FunctionalInterface
    public interface CbLegacyPlaceRecipe {
        @SuppressWarnings("rawtypes")
        void method_12815(java.util.Iterator ingredients, int slot, int maxAmount, int y, int x);
    }

    /** lambdaAdapters: a 1.20.6 PlaceRecipe lambda as 1.21's, handed the ingredient in a one-element iterator and x, y in 1.20.6's order */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_2952 cbPlaceRecipe(CbLegacyPlaceRecipe legacy) {
        return (item, slot, maxAmount, x, y) -> legacy.method_12815(cbIngredientIterator(item), slot, maxAmount, y, x);
    }

    // ---- getRemainingItems overrides (bridge hooks) --------------------------------------------

    /** the 1.20.6 Recipe.getRemainingItems(Container) a mod's recipe declares (adapter of the bridge hook) */
    public interface CbLegacyRemaining {
        class_2371<class_1799> method_8111(class_1263 container);
    }

    /** the 1.20.6 BannerDuplicateRecipe.getRemainingItems(CraftingContainer) a mod's subclass overrides */
    public interface CbLegacyRemaining17704 {
        class_2371<class_1799> method_17704(class_8566 grid);
    }

    /** the 1.20.6 BookCloningRecipe.getRemainingItems(CraftingContainer) a mod's subclass overrides */
    public interface CbLegacyRemaining17707 {
        class_2371<class_1799> method_17707(class_8566 grid);
    }

    /** 1.21 asks a 1.20.6 recipe for its leftovers: the mod's answer per slot of the container, as 1.21's list for the input */
    public static class_2371<class_1799> remaining(CbLegacyRemaining recipe, class_9695 input) {
        return cbRemainingForInputOf(input, recipe.method_8111(cbContainer(input)));
    }

    /**
     * A 1.20.6 answer per slot of the container an input stands for, as 1.21's per slot of the input: a
     * crafting input's at its offset in the grid ({@link #cbRemainingForInput}); any other input this class
     * makes reads the container's first slots in order (a furnace's slot 0, the smithing table's 0-2; a
     * view, all of them), so its entries are the answer's first ones.
     */
    static class_2371<class_1799> cbRemainingForInputOf(class_9695 input, class_2371<class_1799> remaining) {
        if (input instanceof class_9694 crafting) {
            return cbRemainingForInput(crafting, remaining);
        }
        if (input == null || remaining == null || remaining.size() <= input.method_59983()) {
            return remaining;
        }
        class_2371<class_1799> out = class_2371.method_10213(input.method_59983(), class_1799.field_8037);
        for (int i = 0; i < out.size(); i++) {
            out.set(i, remaining.get(i));
        }
        return out;
    }

    /** BannerDuplicateRecipe's leftovers, as {@link #remaining} */
    public static class_2371<class_1799> remaining17704(CbLegacyRemaining17704 recipe, class_9694 input) {
        return cbRemainingForInput(input, recipe.method_17704(cbCraftingContainer(input)));
    }

    /** BookCloningRecipe's leftovers, as {@link #remaining} */
    public static class_2371<class_1799> remaining17707(CbLegacyRemaining17707 recipe, class_9694 input) {
        return cbRemainingForInput(input, recipe.method_17707(cbCraftingContainer(input)));
    }

    /** the 1.20.6 RecipeManager.getRemainingItemsFor(RecipeType, Container, Level) a mod's manager overrides */
    public interface CbLegacyRemainingFor {
        @SuppressWarnings("rawtypes")
        class_2371<class_1799> method_8128(class_3956 type, class_1263 container, net.minecraft.class_1937 level);
    }

    /** 1.21 asks a 1.20.6 recipe manager for leftovers: its answer per slot of the container, as 1.21's list for the input */
    @SuppressWarnings("rawtypes")
    public static class_2371<class_1799> remainingFor(CbLegacyRemainingFor manager, class_3956 type, class_9695 input, net.minecraft.class_1937 level) {
        return cbRemainingForInputOf(input, manager.method_8128(type, cbContainer(input), level));
    }

    // ---- super calls of 1.20.6 overrides ("superBridge"): super's 1.21 method, through the class's accessors ----

    /** the accessor a recipe class that super-calls Recipe.getRemainingItems(Container) gets: super's getRemainingItems(RecipeInput) */
    public interface RemainingSuper {
        class_2371<class_1799> cb$super$method_8111(class_9695 input);
    }

    /**
     * A mod recipe's super.getRemainingItems(container): super's 1.21 answer (Recipe's default, the only
     * implementation of that shape 1.21 has besides the two below) for the container's input -- a grid's
     * CraftingInput, the leftovers put back at its offset; else a view of the whole container, read slot
     * by slot as 1.20.6's default read the container.
     */
    public static class_2371<class_1799> superRemaining(RemainingSuper self, class_1263 container) {
        class_9695 input = cbRecipeInput(container);
        class_2371<class_1799> remaining = self.cb$super$method_8111(input);
        return input instanceof class_9694 crafting ? cbRemainingForContainer(crafting, remaining) : remaining;
    }

    /** the accessor a BannerDuplicateRecipe subclass that super-calls getRemainingItems gets */
    public interface BannerDuplicateSuper {
        class_2371<class_1799> cb$super$method_17704(class_9694 input);
    }

    /** a BannerDuplicateRecipe subclass's super.getRemainingItems(grid): super's for the grid's input, per grid slot */
    public static class_2371<class_1799> superRemaining17704(BannerDuplicateSuper self, class_8566 grid) {
        class_9694 input = cbCraftingInput(grid);
        return cbRemainingForContainer(input, self.cb$super$method_17704(input));
    }

    /** the accessor a BookCloningRecipe subclass that super-calls getRemainingItems gets */
    public interface BookCloningSuper {
        class_2371<class_1799> cb$super$method_17707(class_9694 input);
    }

    /** a BookCloningRecipe subclass's super.getRemainingItems(grid): super's for the grid's input, per grid slot */
    public static class_2371<class_1799> superRemaining17707(BookCloningSuper self, class_8566 grid) {
        class_9694 input = cbCraftingInput(grid);
        return cbRemainingForContainer(input, self.cb$super$method_17707(input));
    }

    /** the accessors a RecipeManager subclass that super-calls its lookups gets */
    @SuppressWarnings("rawtypes")
    public interface RecipeManagerSuper {
        java.util.Optional cb$super$method_8132(class_3956 type, class_9695 input, net.minecraft.class_1937 level);

        java.util.Optional cb$super$method_42299(class_3956 type, class_9695 input, net.minecraft.class_1937 level, net.minecraft.class_2960 last);

        List cb$super$method_17877(class_3956 type, class_9695 input, net.minecraft.class_1937 level);
    }

    /** a RecipeManager subclass's super.getRecipeFor(type, container, level): super's for the input of that type */
    @SuppressWarnings("rawtypes")
    public static java.util.Optional superRecipeFor(RecipeManagerSuper self, class_3956 type, class_1263 container, net.minecraft.class_1937 level) {
        return self.cb$super$method_8132(type, cbInputFor(type, container), level);
    }

    /** a RecipeManager subclass's super.getRecipeFor(type, container, level, last) */
    @SuppressWarnings("rawtypes")
    public static java.util.Optional superRecipeForLast(RecipeManagerSuper self, class_3956 type, class_1263 container, net.minecraft.class_1937 level,
            net.minecraft.class_2960 last) {
        return self.cb$super$method_42299(type, cbInputFor(type, container), level, last);
    }

    /** a RecipeManager subclass's super.getRecipesFor(type, container, level) */
    @SuppressWarnings("rawtypes")
    public static List superRecipesFor(RecipeManagerSuper self, class_3956 type, class_1263 container, net.minecraft.class_1937 level) {
        return self.cb$super$method_17877(type, cbInputFor(type, container), level);
    }

    /**
     * A RecipeManager subclass's super.getRemainingItemsFor(type, container, level): 1.20.6's body, which
     * asks the manager itself for the recipe (the subclass's own getRecipeFor, if it has one) and the
     * recipe for its leftovers ({@link #legacyRemainingFor}).
     */
    @SuppressWarnings("rawtypes")
    public static class_2371<class_1799> superRemainingFor(RecipeManagerSuper self, class_3956 type, class_1263 container, net.minecraft.class_1937 level) {
        return legacyRemainingFor((net.minecraft.class_1863) self, type, container, level);
    }

    // ---- ServerPlaceRecipe.moveItemToGrid --------------------------------------------------------

    /**
     * 1.20.6's moveItemToGrid(Slot, ItemStack) moved one item into the grid slot; 1.21's
     * moveItemToGrid(Slot, ItemStack, int) moves up to that many and answers how many are left (-1: none
     * to take). A 1.20.6 call moves one (callReshapes: amount 1, the answer dropped).
     */
    public static int cbOne() {
        return 1;
    }

    /** the 1.20.6 ServerPlaceRecipe.moveItemToGrid(Slot, ItemStack) a mod's subclass overrides */
    public interface CbLegacyMoveItem {
        void method_12824(net.minecraft.class_1735 slot, class_1799 stack);
    }

    /**
     * 1.21's addItemToSlot asks a 1.20.6 placer to move up to {@code amount}: 1.20.6's addItemToSlot called
     * its moveItemToGrid once per item of the amount, whatever each call found, and so does this; nothing is
     * left to move after that (0 ends 1.21's loop).
     */
    public static int moveItems(CbLegacyMoveItem placer, net.minecraft.class_1735 slot, class_1799 stack, int amount) {
        for (int i = 0; i < amount; i++) {
            placer.method_12824(slot, stack);
        }
        return 0;
    }

    // ---- views -----------------------------------------------------------------------------------

    /** a container seen as a RecipeInput: slot i of the container is item i */
    public static final class ContainerInput implements class_9695 {
        final class_1263 container;

        ContainerInput(class_1263 container) {
            this.container = container;
        }

        public class_1263 container() {
            return container;
        }

        @Override
        public class_1799 method_59984(int slot) {
            return container.method_5438(slot);
        }

        @Override
        public int method_59983() {
            return container.method_5439();
        }
    }

    /** a 1.21 input's items as a read-only container; writes throw, there is nothing behind it to write to */
    private static class InputContainer implements class_1263 {
        final class_9695 input;

        InputContainer(class_9695 input) {
            this.input = input;
        }

        @Override
        public int method_5439() {
            return input.method_59983();
        }

        @Override
        public boolean method_5442() {
            return input.method_59987();
        }

        @Override
        public class_1799 method_5438(int slot) {
            return slot >= 0 && slot < input.method_59983() ? input.method_59984(slot) : class_1799.field_8037;
        }

        @Override
        public class_1799 method_5434(int slot, int amount) {
            throw readOnly();
        }

        @Override
        public class_1799 method_5441(int slot) {
            throw readOnly();
        }

        @Override
        public void method_5447(int slot, class_1799 stack) {
            throw readOnly();
        }

        @Override
        public void method_5431() {
        }

        @Override
        public boolean method_5443(net.minecraft.class_1657 player) {
            return true;
        }

        @Override
        public void method_5448() {
            throw readOnly();
        }

        static UnsupportedOperationException readOnly() {
            return new UnsupportedOperationException("a 1.21 recipe input seen as a 1.20.6 container is read-only:"
                + " no container stands behind it");
        }
    }

    /** a CraftingInput as a read-only crafting grid of its own width and height */
    private static final class InputGrid extends InputContainer implements class_8566 {
        InputGrid(class_9694 input) {
            super(input);
        }

        @Override
        public int method_17398() {
            return ((class_9694) input).method_59991();
        }

        @Override
        public int method_17397() {
            return ((class_9694) input).method_59992();
        }

        @Override
        public List<class_1799> method_51305() {
            return ((class_9694) input).method_59989();
        }

        @Override
        public void method_7683(class_1662 contents) {
            for (class_1799 stack : ((class_9694) input).method_59989()) {
                contents.method_7404(stack);
            }
        }
    }
}
