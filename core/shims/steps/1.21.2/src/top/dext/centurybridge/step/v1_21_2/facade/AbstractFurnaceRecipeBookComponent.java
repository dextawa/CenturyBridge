package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Iterator;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipesClient;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeMenus;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeView;

/**
 * AbstractFurnaceRecipeBookComponent of 1.21.1 (net/minecraft/class_517; classRenamed -- 1.21.2's
 * FurnaceRecipeBookComponent is a 1.21.2 component): the recipe book of a furnace-like screen, its ghost
 * recipe showing the result, the inputs and -- when the fuel slot is empty -- the fuels it accepts. 1.21.1's
 * body; the fuels' stacks are shown as 1.21.1 showed its fuel ingredient (each enabled fuel item in turn).
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookComponent extends RecipeBookComponent {

    private static final net.minecraft.class_8666 FILTER_SPRITES = new net.minecraft.class_8666(
        net.minecraft.class_2960.method_60656("recipe_book/furnace_filter_enabled"), net.minecraft.class_2960.method_60656("recipe_book/furnace_filter_disabled"),
        net.minecraft.class_2960.method_60656("recipe_book/furnace_filter_enabled_highlighted"),
        net.minecraft.class_2960.method_60656("recipe_book/furnace_filter_disabled_highlighted"));
    private net.minecraft.class_1799[] fuels;

    public AbstractFurnaceRecipeBookComponent() {
    }

    @Override
    public void method_2585() {
        this.field_3088.method_1962(FILTER_SPRITES);
    }

    @Override
    public void method_2600(net.minecraft.class_1735 slot) {
        super.method_2600(slot);
        if (slot != null && slot.field_7874 < CbRestRecipeMenus.method_7658(this.field_3095)) {
            this.field_3092.method_2571();
        }
    }

    @Override
    public void method_2596(net.minecraft.class_8786<?> recipe, List<net.minecraft.class_1735> slots) {
        net.minecraft.class_1799 result = CbRestRecipeView.resultItem(recipe.comp_1933(), this.field_3091.field_1687.method_30349());
        this.field_3092.method_2565(recipe);
        this.field_3092.cb$addStacks(new net.minecraft.class_1799[] {result}, slots.get(2).field_7873, slots.get(2).field_7872);
        List<net.minecraft.class_1856> ingredients = CbRestRecipeView.ingredients(recipe.comp_1933());
        net.minecraft.class_1735 fuelSlot = slots.get(1);
        if (fuelSlot.method_7677().method_7960()) {
            if (this.fuels == null) {
                net.minecraft.class_7699 features = this.field_3091.method_1562().method_45735();
                this.fuels = this.method_17065().stream().filter(item -> item.method_45382(features)).map(net.minecraft.class_1799::new)
                    .toArray(net.minecraft.class_1799[]::new);
            }
            this.field_3092.cb$addStacks(this.fuels, fuelSlot.field_7873, fuelSlot.field_7872);
        }
        Iterator<net.minecraft.class_1856> it = ingredients.iterator();
        for (int i = 0; i < 2; i++) {
            if (!it.hasNext()) {
                return;
            }
            net.minecraft.class_1856 ingredient = it.next();
            if (!CbRestRecipeView.isGap(ingredient)) {
                net.minecraft.class_1735 slot = slots.get(i);
                this.field_3092.method_2569(ingredient, slot.field_7873, slot.field_7872);
            }
        }
    }

    /** getFuelItems() */
    public abstract Set<net.minecraft.class_1792> method_17065();

    /** the fuels 1.21.1's furnace screens offered (AbstractFurnaceBlockEntity.getFuel(): 1.21.2's level fuel values) */
    protected static Set<net.minecraft.class_1792> cb$fuels() {
        return net.minecraft.class_310.method_1551().field_1687.method_61269().method_61751();
    }

    /**
     * 1.21.2's furnace component of a 1.21.2 furnace screen through the 1.21.1 API (as {@link RecipeBookComponent.Vanilla}; its filter name and fuels 1.21.2's): each call answered by the 1.21.2 member of
     * the same contract (visibility, position, ticking, drawing, input, recipes updated, slot clicks); a ghost
     * recipe of a recipe is the recipe's display (one the book knows), its ingredients set on 1.21.2's ghost slots.
     */
    public static final class Vanilla extends AbstractFurnaceRecipeBookComponent {
        private final net.minecraft.class_507<?> component;

        Vanilla(net.minecraft.class_507<?> component) {
            this.component = component;
            this.field_3091 = net.minecraft.class_310.method_1551();
            this.field_3095 = ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookComponentAccessor) component).cb$menu();
        }

        @Override
        public void method_2597(int width, int height, net.minecraft.class_310 minecraft, boolean widthTooNarrow, net.minecraft.class_1729 menu) {
            this.component.method_2597(width, height, minecraft, widthTooNarrow);
        }

        @Override
        public void method_2579() {
            ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookComponentAccessor) this.component).cb$initVisuals();
        }

        @Override
        public int method_2595(int width, int imageWidth) {
            return this.component.method_2595(width, imageWidth);
        }

        @Override
        public void method_2591() {
            this.component.method_2591();
        }

        @Override
        public boolean method_2605() {
            return this.component.method_2605();
        }

        @Override
        public void method_2593(boolean visible) {
            if (this.component.method_2605() != visible) {
                this.component.method_2591();
            }
        }

        @Override
        public void method_2600(net.minecraft.class_1735 slot) {
            this.component.method_62044(slot);
        }

        @Override
        public void method_2590() {
            this.component.method_2590();
        }

        @Override
        public void method_25394(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
            this.component.method_25394(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public void method_2601(net.minecraft.class_332 graphics, int leftPos, int topPos, int mouseX, int mouseY) {
            this.component.method_2601(graphics, mouseX, mouseY, null);
        }

        @Override
        public void method_2581(net.minecraft.class_332 graphics, int leftPos, int topPos, boolean bigResultSlot, float partialTick) {
            this.component.method_2581(graphics, bigResultSlot);
        }

        @Override
        public boolean method_25402(double mouseX, double mouseY, int button) {
            return this.component.method_25402(mouseX, mouseY, button);
        }

        @Override
        public boolean method_2598(double mouseX, double mouseY, int leftPos, int topPos, int imageWidth, int imageHeight, int button) {
            return this.component.method_2598(mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight, button);
        }

        @Override
        public boolean method_25404(int keyCode, int scanCode, int modifiers) {
            return this.component.method_25404(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean method_16803(int keyCode, int scanCode, int modifiers) {
            return this.component.method_16803(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean method_25400(char c, int modifiers) {
            return this.component.method_25400(c, modifiers);
        }

        @Override
        public void method_2592() {
            this.component.method_2592();
        }

        @Override
        public void method_2646(List<net.minecraft.class_8786<?>> recipes) {
            for (net.minecraft.class_8786<?> recipe : recipes) {
                for (int id : CbRestRecipesClient.displayIds(recipe)) {
                    this.component.method_64876(new net.minecraft.class_10298(id));
                }
            }
        }

        @Override
        public void method_2596(net.minecraft.class_8786<?> recipe, List<net.minecraft.class_1735> slots) {
            net.minecraft.class_10295 display = CbRestRecipesClient.knownDisplay(recipe);
            if (display != null) {
                this.component.method_64875(display);
            }
        }

        @Override
        public void method_60344(net.minecraft.class_1856 ingredient, int slot, int maxAmount, int x, int y) {
            if (!CbRestRecipeView.isGap(ingredient)) {
                top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookComponentAccessor access =
                    (top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookComponentAccessor) this.component;
                ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestGhostSlotsInvoker) access.cb$ghostSlots()).cb$setInput(this.field_3095.field_7761.get(slot),
                    net.minecraft.class_10363.method_65008(this.field_3091.field_1687), ingredient.method_64673());
            }
        }

        @Override
        public net.minecraft.class_2561 method_17064() {
            return ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookComponentAccessor) this.component).cb$recipeFilterName();
        }

        @Override
        public java.util.Set<net.minecraft.class_1792> method_17065() {
            return cb$fuels();
        }

        @Override
        public void method_2585() {
        }

        @Override
        public void method_2588() {
            ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeBookComponentAccessor) this.component).cb$sendUpdateSettings();
        }

        @Override
        public net.minecraft.class_6379.class_6380 method_37018() {
            return this.component.method_37018();
        }

        @Override
        public void method_37020(net.minecraft.class_6382 output) {
            this.component.method_37020(output);
        }
    }
}
