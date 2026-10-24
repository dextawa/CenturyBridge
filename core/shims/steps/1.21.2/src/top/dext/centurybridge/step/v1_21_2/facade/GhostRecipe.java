package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeView;

/**
 * GhostRecipe of 1.21.1 (net/minecraft/class_505, gone in 1.21.2 -- its GhostSlots works on slot displays;
 * classRenamed): the recipe shown as ghost items in a crafting grid, each ingredient cycling through the
 * items it accepts. 1.21.1's body, drawing with 1.21.2's GuiGraphics (the same fill, fake item, ghost overlay
 * and decorations calls) and listing an ingredient's items as 1.21.1's Ingredient.getItems did.
 */
@Environment(EnvType.CLIENT)
public class GhostRecipe {

    private net.minecraft.class_8786<?> recipe;
    private final List<GhostIngredient> ingredients = Lists.newArrayList();
    /** time */
    public float field_3080;

    public GhostRecipe() {
    }

    /** clear() */
    public void method_2571() {
        this.recipe = null;
        this.ingredients.clear();
        this.field_3080 = 0.0F;
    }

    /** addIngredient(ingredient, x, y) */
    public void method_2569(net.minecraft.class_1856 ingredient, int x, int y) {
        this.ingredients.add(new GhostIngredient(this, ingredient, x, y));
    }

    /**
     * a ghost ingredient of exactly these stacks: 1.21.1 showed the recipe's result through Ingredient.of(result)
     * (the stack itself, count and components) and the fuels through an ingredient of their stacks; 1.21.2's
     * ingredients hold items only
     */
    void cb$addStacks(net.minecraft.class_1799[] stacks, int x, int y) {
        this.ingredients.add(new GhostIngredient(this, stacks, x, y));
    }

    /** get(index) */
    public GhostIngredient method_2570(int index) {
        return this.ingredients.get(index);
    }

    /** size() */
    public int method_2572() {
        return this.ingredients.size();
    }

    /** getRecipe() */
    public net.minecraft.class_8786<?> method_2566() {
        return this.recipe;
    }

    /** setRecipe(recipe) */
    public void method_2565(net.minecraft.class_8786<?> recipe) {
        this.recipe = recipe;
    }

    /** render(graphics, minecraft, leftPos, topPos, bigResultSlot, partialTick) */
    public void method_2567(net.minecraft.class_332 graphics, net.minecraft.class_310 minecraft, int leftPos, int topPos, boolean bigResultSlot, float partialTick) {
        if (!net.minecraft.class_437.method_25441()) {
            this.field_3080 += partialTick;
        }
        for (int i = 0; i < this.ingredients.size(); i++) {
            GhostIngredient ingredient = this.ingredients.get(i);
            int x = ingredient.method_2574() + leftPos;
            int y = ingredient.method_2575() + topPos;
            if (i == 0 && bigResultSlot) {
                graphics.method_25294(x - 4, y - 4, x + 20, y + 20, 822018048);
            } else {
                graphics.method_25294(x, y, x + 16, y + 16, 822018048);
            }
            net.minecraft.class_1799 stack = ingredient.method_2573();
            graphics.method_51445(stack, x, y);
            graphics.method_51739(net.minecraft.class_1921.method_51787(), x, y, x + 16, y + 16, 822083583);
            if (i == 0) {
                graphics.method_51431(minecraft.field_1772, stack, x, y);
            }
        }
    }

    /** GhostRecipe.GhostIngredient (class_505$class_506) */
    public static class GhostIngredient {
        private final GhostRecipe owner;
        private final net.minecraft.class_1856 ingredient;
        private final net.minecraft.class_1799[] stacks;
        private final int x;
        private final int y;

        public GhostIngredient(GhostRecipe owner, net.minecraft.class_1856 ingredient, int x, int y) {
            this.owner = owner;
            this.ingredient = ingredient;
            this.stacks = null;
            this.x = x;
            this.y = y;
        }

        GhostIngredient(GhostRecipe owner, net.minecraft.class_1799[] stacks, int x, int y) {
            this.owner = owner;
            this.ingredient = null;
            this.stacks = stacks;
            this.x = x;
            this.y = y;
        }

        /** getX() */
        public int method_2574() {
            return this.x;
        }

        /** getY() */
        public int method_2575() {
            return this.y;
        }

        /** getItem(): the accepted item shown now (each for 30 ticks in turn), EMPTY when it accepts none */
        public net.minecraft.class_1799 method_2573() {
            net.minecraft.class_1799[] items = this.stacks != null ? this.stacks : CbRestRecipeView.items(this.ingredient);
            return items.length == 0 ? net.minecraft.class_1799.field_8037
                : items[net.minecraft.class_3532.method_15375(this.owner.field_3080 / 30.0F) % items.length];
        }
    }
}
