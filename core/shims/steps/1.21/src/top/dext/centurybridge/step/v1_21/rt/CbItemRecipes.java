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
