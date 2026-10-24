package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBooks;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeView;

/**
 * OverlayRecipeComponent of 1.21.1 (net/minecraft/class_508; classRenamed -- 1.21.2's shows display entries):
 * the pop-up listing every recipe of a collection, each as a small grid of its ingredients. 1.21.1's body with
 * its button classes; sprites drawn with 1.21.2's textured gui render type, ingredients and their items read as
 * CbRestRecipeView reads them.
 */
@Environment(EnvType.CLIENT)
public class OverlayRecipeComponent implements net.minecraft.class_4068, net.minecraft.class_364 {

    private static final net.minecraft.class_2960 OVERLAY_RECIPE_SPRITE = net.minecraft.class_2960.method_60656("recipe_book/overlay_recipe");
    public static final net.minecraft.class_2960 field_45542 = net.minecraft.class_2960.method_60656("recipe_book/furnace_overlay_highlighted");
    public static final net.minecraft.class_2960 field_45543 = net.minecraft.class_2960.method_60656("recipe_book/furnace_overlay");
    public static final net.minecraft.class_2960 field_45544 = net.minecraft.class_2960.method_60656("recipe_book/crafting_overlay_highlighted");
    public static final net.minecraft.class_2960 field_45545 = net.minecraft.class_2960.method_60656("recipe_book/crafting_overlay");
    public static final net.minecraft.class_2960 field_45546 = net.minecraft.class_2960.method_60656("recipe_book/furnace_overlay_disabled_highlighted");
    public static final net.minecraft.class_2960 field_45547 = net.minecraft.class_2960.method_60656("recipe_book/furnace_overlay_disabled");
    public static final net.minecraft.class_2960 field_45548 = net.minecraft.class_2960.method_60656("recipe_book/crafting_overlay_disabled_highlighted");
    public static final net.minecraft.class_2960 field_45549 = net.minecraft.class_2960.method_60656("recipe_book/crafting_overlay_disabled");
    /** BUTTON_SIZE */
    public static final int field_42162 = 25;

    private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    private boolean isVisible;
    private int x;
    private int y;
    private net.minecraft.class_310 minecraft;
    private RecipeCollection collection;
    private net.minecraft.class_8786<?> lastRecipeClicked;
    /** time */
    public float field_3110;
    /** isFurnaceMenu */
    public boolean field_3112;

    public OverlayRecipeComponent() {
    }

    /** init(minecraft, collection, x, y, middleX, middleY, buttonWidth) */
    public void method_2617(net.minecraft.class_310 minecraft, RecipeCollection collection, int x, int y, int middleX, int middleY, float buttonWidth) {
        this.minecraft = minecraft;
        this.collection = collection;
        if (minecraft.field_1724.field_7512 instanceof net.minecraft.class_1720) {
            this.field_3112 = true;
        }
        boolean filtering = CbRestRecipeBooks.isFiltering(minecraft.field_1724.method_3130(), (net.minecraft.class_1729) minecraft.field_1724.field_7512);
        List<net.minecraft.class_8786<?>> craftable = collection.method_2648(true);
        List<net.minecraft.class_8786<?>> uncraftable = filtering ? Collections.emptyList() : collection.method_2648(false);
        int craftableCount = craftable.size();
        int total = craftableCount + uncraftable.size();
        int perRow = total <= 16 ? 4 : 5;
        int rows = (int) Math.ceil((double) ((float) total / (float) perRow));
        this.x = x;
        this.y = y;
        float right = (float) (this.x + Math.min(total, perRow) * 25);
        float maxRight = (float) (middleX + 50);
        if (right > maxRight) {
            this.x = (int) ((float) this.x - buttonWidth * (float) ((int) ((right - maxRight) / buttonWidth)));
        }
        float bottom = (float) (this.y + rows * 25);
        float maxBottom = (float) (middleY + 50);
        if (bottom > maxBottom) {
            this.y = (int) ((float) this.y - buttonWidth * (float) net.minecraft.class_3532.method_15386((bottom - maxBottom) / buttonWidth));
        }
        float top = (float) this.y;
        float minTop = (float) (middleY - 100);
        if (top < minTop) {
            this.y = (int) ((float) this.y - buttonWidth * (float) net.minecraft.class_3532.method_15386((top - minTop) / buttonWidth));
        }
        this.isVisible = true;
        this.recipeButtons.clear();
        for (int i = 0; i < total; i++) {
            boolean isCraftable = i < craftableCount;
            net.minecraft.class_8786<?> recipe = isCraftable ? craftable.get(i) : uncraftable.get(i - craftableCount);
            int bx = this.x + 4 + 25 * (i % perRow);
            int by = this.y + 5 + 25 * (i / perRow);
            if (this.field_3112) {
                this.recipeButtons.add(new OverlaySmeltingRecipeButton(this, bx, by, recipe, isCraftable));
            } else {
                this.recipeButtons.add(new OverlayRecipeButton(this, bx, by, recipe, isCraftable));
            }
        }
        this.lastRecipeClicked = null;
    }

    /** getRecipeCollection() */
    public RecipeCollection method_2614() {
        return this.collection;
    }

    /** getLastRecipeClicked() */
    public net.minecraft.class_8786<?> method_2615() {
        return this.lastRecipeClicked;
    }

    @Override
    public boolean method_25402(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (OverlayRecipeButton recipeButton : this.recipeButtons) {
            if (recipeButton.method_25402(mouseX, mouseY, button)) {
                this.lastRecipeClicked = recipeButton.field_3114;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean method_25405(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void method_25394(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isVisible) {
            this.field_3110 += partialTick;
            RenderSystem.enableBlend();
            graphics.method_51448().method_22903();
            graphics.method_51448().method_46416(0.0F, 0.0F, 1000.0F);
            int perRow = this.recipeButtons.size() <= 16 ? 4 : 5;
            int columns = Math.min(this.recipeButtons.size(), perRow);
            int rows = net.minecraft.class_3532.method_15386((float) this.recipeButtons.size() / (float) perRow);
            graphics.method_52706(net.minecraft.class_1921::method_62277, OVERLAY_RECIPE_SPRITE, this.x, this.y, columns * 25 + 8, rows * 25 + 8);
            RenderSystem.disableBlend();
            for (OverlayRecipeButton recipeButton : this.recipeButtons) {
                recipeButton.method_25394(graphics, mouseX, mouseY, partialTick);
            }
            graphics.method_51448().method_22909();
        }
    }

    /** setVisible(visible) */
    public void method_2613(boolean visible) {
        this.isVisible = visible;
    }

    /** isVisible() */
    public boolean method_2616() {
        return this.isVisible;
    }

    @Override
    public void method_25365(boolean focused) {
    }

    @Override
    public boolean method_25370() {
        return false;
    }

    /** OverlayRecipeComponent.OverlayRecipeButton (class_508$class_509) */
    public static class OverlayRecipeButton extends net.minecraft.class_339 implements PlaceRecipe<net.minecraft.class_1856> {
        final OverlayRecipeComponent owner;
        /** recipe */
        public final net.minecraft.class_8786<?> field_3114;
        private final boolean isCraftable;
        public final List<Pos> field_3116 = Lists.newArrayList();

        public OverlayRecipeButton(OverlayRecipeComponent owner, int x, int y, net.minecraft.class_8786<?> recipe, boolean isCraftable) {
            super(x, y, 200, 20, net.minecraft.class_5244.field_39003);
            this.owner = owner;
            this.field_22758 = 24;
            this.field_22759 = 24;
            this.field_3114 = recipe;
            this.isCraftable = isCraftable;
            this.method_2619(recipe);
        }

        /** calculateIngredientsPositions(recipe) */
        public void method_2619(net.minecraft.class_8786<?> recipe) {
            this.method_12816(3, 3, -1, recipe, CbRestRecipeView.ingredients(recipe.comp_1933()).iterator(), 0);
        }

        @Override
        public void method_47399(net.minecraft.class_6382 output) {
            this.method_37021(output);
        }

        @Override
        public final void method_12815(net.minecraft.class_1856 ingredient, int slot, int maxAmount, int x, int y) {
            this.method_60343(ingredient, slot, maxAmount, x, y);
        }

        /** addItemToSlot(ingredient, slot, maxAmount, x, y) */
        public void method_60343(net.minecraft.class_1856 ingredient, int slot, int maxAmount, int x, int y) {
            net.minecraft.class_1799[] items = CbRestRecipeView.items(ingredient);
            if (items.length != 0) {
                this.field_3116.add(new Pos(this, 3 + x * 7, 3 + y * 7, items));
            }
        }

        @Override
        public void method_48579(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
            net.minecraft.class_2960 sprite;
            boolean hovered = this.method_25367();
            if (this.isCraftable) {
                sprite = this.owner.field_3112 ? (hovered ? field_45542 : field_45543)
                    : (hovered ? field_45544 : field_45545);
            } else {
                sprite = this.owner.field_3112 ? (hovered ? field_45546 : field_45547)
                    : (hovered ? field_45548 : field_45549);
            }
            graphics.method_52706(net.minecraft.class_1921::method_62277, sprite, this.method_46426(), this.method_46427(), this.field_22758, this.field_22759);
            graphics.method_51448().method_22903();
            graphics.method_51448().method_22904((double) (this.method_46426() + 2), (double) (this.method_46427() + 2), 150.0);
            for (Pos pos : this.field_3116) {
                graphics.method_51448().method_22903();
                graphics.method_51448().method_22904((double) pos.field_3119, (double) pos.field_3118, 0.0);
                graphics.method_51448().method_22905(0.375F, 0.375F, 1.0F);
                graphics.method_51448().method_22904(-8.0, -8.0, 0.0);
                if (pos.field_3120.length > 0) {
                    graphics.method_51427(pos.field_3120[net.minecraft.class_3532.method_15375(this.owner.field_3110 / 30.0F) % pos.field_3120.length], 0, 0);
                }
                graphics.method_51448().method_22909();
            }
            graphics.method_51448().method_22909();
        }

        /** OverlayRecipeComponent.OverlayRecipeButton.Pos (class_508$class_509$class_510) */
        public static class Pos {
            /** ingredients */
            public final net.minecraft.class_1799[] field_3120;
            /** x */
            public final int field_3119;
            /** y */
            public final int field_3118;

            public Pos(OverlayRecipeButton button, int x, int y, net.minecraft.class_1799[] ingredients) {
                this.field_3119 = x;
                this.field_3118 = y;
                this.field_3120 = ingredients;
            }
        }
    }

    /** OverlayRecipeComponent.OverlaySmeltingRecipeButton (class_508$class_511): the one input, centred */
    public static class OverlaySmeltingRecipeButton extends OverlayRecipeButton {
        public OverlaySmeltingRecipeButton(OverlayRecipeComponent owner, int x, int y, net.minecraft.class_8786<?> recipe, boolean isCraftable) {
            super(owner, x, y, recipe, isCraftable);
        }

        @Override
        public void method_2619(net.minecraft.class_8786<?> recipe) {
            List<net.minecraft.class_1856> ingredients = CbRestRecipeView.ingredients(recipe.comp_1933());
            net.minecraft.class_1799[] items = CbRestRecipeView.items(ingredients.isEmpty() ? null : ingredients.get(0));
            this.field_3116.add(new Pos(this, 10, 10, items));
        }
    }
}
