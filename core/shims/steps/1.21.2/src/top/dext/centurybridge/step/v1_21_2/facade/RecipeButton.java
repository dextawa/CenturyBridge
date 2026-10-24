package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBooks;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeView;

/**
 * RecipeButton of 1.21.1 (net/minecraft/class_514; classRenamed -- 1.21.2's shows display entries): one recipe
 * collection on a recipe book page, cycling through its recipes' results. 1.21.1's body; sprites drawn with
 * 1.21.2's textured gui render type, a recipe's result as CbRestRecipeView reads it.
 */
@Environment(EnvType.CLIENT)
public class RecipeButton extends net.minecraft.class_339 {

    private static final net.minecraft.class_2960 SLOT_MANY_CRAFTABLE_SPRITE = net.minecraft.class_2960.method_60656("recipe_book/slot_many_craftable");
    private static final net.minecraft.class_2960 SLOT_CRAFTABLE_SPRITE = net.minecraft.class_2960.method_60656("recipe_book/slot_craftable");
    private static final net.minecraft.class_2960 SLOT_MANY_UNCRAFTABLE_SPRITE = net.minecraft.class_2960.method_60656("recipe_book/slot_many_uncraftable");
    private static final net.minecraft.class_2960 SLOT_UNCRAFTABLE_SPRITE = net.minecraft.class_2960.method_60656("recipe_book/slot_uncraftable");
    private static final net.minecraft.class_2561 MORE_RECIPES_TOOLTIP = net.minecraft.class_2561.method_43471("gui.recipebook.moreRecipes");
    /** TICKS_TO_SWAP */
    public static final int field_32413 = 30;
    private net.minecraft.class_1729 menu;
    private net.minecraft.class_3439 book;
    private RecipeCollection collection;
    private float time;
    private float animationTime;
    private int currentIndex;

    public RecipeButton() {
        super(0, 0, 25, 25, net.minecraft.class_5244.field_39003);
    }

    /** init(collection, page) */
    public void method_2640(RecipeCollection collection, RecipeBookPage page) {
        this.collection = collection;
        this.menu = (net.minecraft.class_1729) page.method_2637().field_1724.field_7512;
        this.book = page.method_2633();
        List<net.minecraft.class_8786<?>> recipes = collection.method_2651(CbRestRecipeBooks.isFiltering(this.book, this.menu));
        for (net.minecraft.class_8786<?> recipe : recipes) {
            if (CbRestRecipeBooks.willHighlight(this.book, recipe)) {
                page.method_2629(recipes);
                this.animationTime = 15.0F;
                break;
            }
        }
    }

    /** getCollection() */
    public RecipeCollection method_2645() {
        return this.collection;
    }

    private boolean filtering() {
        return CbRestRecipeBooks.isFiltering(this.book, this.menu);
    }

    @Override
    public void method_48579(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
        if (!net.minecraft.class_437.method_25441()) {
            this.time += partialTick;
        }
        net.minecraft.class_2960 sprite;
        if (this.collection.method_2655()) {
            sprite = this.collection.method_2651(filtering()).size() > 1 ? SLOT_MANY_CRAFTABLE_SPRITE : SLOT_CRAFTABLE_SPRITE;
        } else {
            sprite = this.collection.method_2651(filtering()).size() > 1 ? SLOT_MANY_UNCRAFTABLE_SPRITE : SLOT_UNCRAFTABLE_SPRITE;
        }
        boolean animating = this.animationTime > 0.0F;
        if (animating) {
            float scale = 1.0F + 0.1F * (float) Math.sin((double) (this.animationTime / 15.0F * (float) Math.PI));
            graphics.method_51448().method_22903();
            graphics.method_51448().method_46416((float) (this.method_46426() + 8), (float) (this.method_46427() + 12), 0.0F);
            graphics.method_51448().method_22905(scale, scale, 1.0F);
            graphics.method_51448().method_46416((float) (-(this.method_46426() + 8)), (float) (-(this.method_46427() + 12)), 0.0F);
            this.animationTime -= partialTick;
        }
        graphics.method_52706(net.minecraft.class_1921::method_62277, sprite, this.method_46426(), this.method_46427(), this.field_22758, this.field_22759);
        List<net.minecraft.class_8786<?>> recipes = this.method_2639();
        this.currentIndex = net.minecraft.class_3532.method_15375(this.time / 30.0F) % recipes.size();
        net.minecraft.class_1799 result = CbRestRecipeView.resultItem(recipes.get(this.currentIndex).comp_1933(), this.collection.method_48479());
        int offset = 4;
        if (this.collection.method_2656() && this.method_2639().size() > 1) {
            graphics.method_51429(result, this.method_46426() + offset + 1, this.method_46427() + offset + 1, 0, 10);
            offset--;
        }
        graphics.method_51445(result, this.method_46426() + offset, this.method_46427() + offset);
        if (animating) {
            graphics.method_51448().method_22909();
        }
    }

    /** getOrderedRecipes(): the craftable display recipes, then -- unless filtering -- the others */
    public List<net.minecraft.class_8786<?>> method_2639() {
        List<net.minecraft.class_8786<?>> recipes = this.collection.method_2648(true);
        if (!filtering()) {
            recipes.addAll(this.collection.method_2648(false));
        }
        return recipes;
    }

    /** isOnlyOption() */
    public boolean method_2642() {
        return this.method_2639().size() == 1;
    }

    /** getRecipe() */
    public net.minecraft.class_8786<?> method_2643() {
        return this.method_2639().get(this.currentIndex);
    }

    /** getTooltipText() */
    public List<net.minecraft.class_2561> method_2644() {
        net.minecraft.class_1799 result = CbRestRecipeView.resultItem(this.method_2639().get(this.currentIndex).comp_1933(), this.collection.method_48479());
        List<net.minecraft.class_2561> lines = Lists.newArrayList(net.minecraft.class_437.method_25408(net.minecraft.class_310.method_1551(), result));
        if (this.collection.method_2651(filtering()).size() > 1) {
            lines.add(MORE_RECIPES_TOOLTIP);
        }
        return lines;
    }

    @Override
    public void method_47399(net.minecraft.class_6382 output) {
        net.minecraft.class_1799 result = CbRestRecipeView.resultItem(this.method_2639().get(this.currentIndex).comp_1933(), this.collection.method_48479());
        output.method_37034(net.minecraft.class_6381.field_33788, net.minecraft.class_2561.method_43469("narration.recipe", result.method_7964()));
        if (this.collection.method_2651(filtering()).size() > 1) {
            output.method_37035(net.minecraft.class_6381.field_33791, new net.minecraft.class_2561[] {
                net.minecraft.class_2561.method_43471("narration.button.usage.hovered"), net.minecraft.class_2561.method_43471("narration.recipe.usage.more")});
        } else {
            output.method_37034(net.minecraft.class_6381.field_33791, net.minecraft.class_2561.method_43471("narration.button.usage.hovered"));
        }
    }

    @Override
    public int method_25368() {
        return 25;
    }

    @Override
    public boolean method_25351(int button) {
        return button == 0 || button == 1;
    }
}
