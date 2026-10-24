package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * RecipeBookPage of 1.21.1 (net/minecraft/class_513; classRenamed -- 1.21.2's is built for a 1.21.2 component):
 * twenty recipe buttons, the page arrows and the overlay, telling its listeners which recipes were shown.
 * 1.21.1's body on 1.21.2's widgets.
 */
@Environment(EnvType.CLIENT)
public class RecipeBookPage {

    /** ITEMS_PER_PAGE */
    public static final int field_32411 = 20;
    private static final net.minecraft.class_8666 PAGE_FORWARD_SPRITES = new net.minecraft.class_8666(
        net.minecraft.class_2960.method_60656("recipe_book/page_forward"), net.minecraft.class_2960.method_60656("recipe_book/page_forward_highlighted"));
    private static final net.minecraft.class_8666 PAGE_BACKWARD_SPRITES = new net.minecraft.class_8666(
        net.minecraft.class_2960.method_60656("recipe_book/page_backward"), net.minecraft.class_2960.method_60656("recipe_book/page_backward_highlighted"));
    private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity(20);
    private RecipeButton hoveredButton;
    private final OverlayRecipeComponent overlay = new OverlayRecipeComponent();
    private net.minecraft.class_310 minecraft;
    private final List<RecipeShownListener> showListeners = Lists.newArrayList();
    private List<RecipeCollection> recipeCollections = ImmutableList.of();
    private net.minecraft.class_361 forwardButton;
    private net.minecraft.class_361 backButton;
    private int totalPages;
    private int currentPage;
    private net.minecraft.class_3439 recipeBook;
    private net.minecraft.class_8786<?> lastClickedRecipe;
    private RecipeCollection lastClickedRecipeCollection;

    public RecipeBookPage() {
        for (int i = 0; i < 20; i++) {
            this.buttons.add(new RecipeButton());
        }
    }

    /** init(minecraft, x, y) */
    public void method_2636(net.minecraft.class_310 minecraft, int x, int y) {
        this.minecraft = minecraft;
        this.recipeBook = minecraft.field_1724.method_3130();
        for (int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).method_48229(x + 11 + 25 * (i % 5), y + 31 + 25 * (i / 5));
        }
        this.forwardButton = new net.minecraft.class_361(x + 93, y + 137, 12, 17, false);
        this.forwardButton.method_1962(PAGE_FORWARD_SPRITES);
        this.backButton = new net.minecraft.class_361(x + 38, y + 137, 12, 17, true);
        this.backButton.method_1962(PAGE_BACKWARD_SPRITES);
    }

    /** addListener(component) */
    public void method_2630(RecipeBookComponent component) {
        this.showListeners.remove(component);
        this.showListeners.add(component);
    }

    /** updateCollections(collections, resetPage) */
    public void method_2627(List<RecipeCollection> collections, boolean resetPage) {
        this.recipeCollections = collections;
        this.totalPages = (int) Math.ceil((double) collections.size() / 20.0);
        if (this.totalPages <= this.currentPage || resetPage) {
            this.currentPage = 0;
        }
        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int first = 20 * this.currentPage;
        for (int i = 0; i < this.buttons.size(); i++) {
            RecipeButton button = this.buttons.get(i);
            if (first + i < this.recipeCollections.size()) {
                button.method_2640(this.recipeCollections.get(first + i), this);
                button.field_22764 = true;
            } else {
                button.field_22764 = false;
            }
        }
        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardButton.field_22764 = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.field_22764 = this.totalPages > 1 && this.currentPage > 0;
    }

    /** render(graphics, x, y, mouseX, mouseY, partialTick) */
    public void method_2634(net.minecraft.class_332 graphics, int x, int y, int mouseX, int mouseY, float partialTick) {
        if (this.totalPages > 1) {
            net.minecraft.class_5250 text = net.minecraft.class_2561.method_43469("gui.recipebook.page", this.currentPage + 1, this.totalPages);
            int width = this.minecraft.field_1772.method_27525(text);
            graphics.method_51439(this.minecraft.field_1772, text, x - width / 2 + 73, y + 141, -1, false);
        }
        this.hoveredButton = null;
        for (RecipeButton button : this.buttons) {
            button.method_25394(graphics, mouseX, mouseY, partialTick);
            if (button.field_22764 && button.method_25367()) {
                this.hoveredButton = button;
            }
        }
        this.backButton.method_25394(graphics, mouseX, mouseY, partialTick);
        this.forwardButton.method_25394(graphics, mouseX, mouseY, partialTick);
        this.overlay.method_25394(graphics, mouseX, mouseY, partialTick);
    }

    /** renderTooltip(graphics, mouseX, mouseY) */
    public void method_2628(net.minecraft.class_332 graphics, int mouseX, int mouseY) {
        if (this.minecraft.field_1755 != null && this.hoveredButton != null && !this.overlay.method_2616()) {
            graphics.method_51434(this.minecraft.field_1772, this.hoveredButton.method_2644(), mouseX, mouseY);
        }
    }

    /** getLastClickedRecipe() */
    public net.minecraft.class_8786<?> method_2631() {
        return this.lastClickedRecipe;
    }

    /** getLastClickedRecipeCollection() */
    public RecipeCollection method_2635() {
        return this.lastClickedRecipeCollection;
    }

    /** setInvisible() */
    public void method_2638() {
        this.overlay.method_2613(false);
    }

    /** mouseClicked(mouseX, mouseY, button, x, y, width, height) */
    public boolean method_2632(double mouseX, double mouseY, int button, int x, int y, int width, int height) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.method_2616()) {
            if (this.overlay.method_25402(mouseX, mouseY, button)) {
                this.lastClickedRecipe = this.overlay.method_2615();
                this.lastClickedRecipeCollection = this.overlay.method_2614();
            } else {
                this.overlay.method_2613(false);
            }
            return true;
        } else if (this.forwardButton.method_25402(mouseX, mouseY, button)) {
            this.currentPage++;
            this.updateButtonsForPage();
            return true;
        } else if (this.backButton.method_25402(mouseX, mouseY, button)) {
            this.currentPage--;
            this.updateButtonsForPage();
            return true;
        } else {
            for (RecipeButton recipeButton : this.buttons) {
                if (recipeButton.method_25402(mouseX, mouseY, button)) {
                    if (button == 0) {
                        this.lastClickedRecipe = recipeButton.method_2643();
                        this.lastClickedRecipeCollection = recipeButton.method_2645();
                    } else if (button == 1 && !this.overlay.method_2616() && !recipeButton.method_2642()) {
                        this.overlay.method_2617(this.minecraft, recipeButton.method_2645(), recipeButton.method_46426(), recipeButton.method_46427(),
                            x + width / 2, y + 13 + height / 2, (float) recipeButton.method_25368());
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /** recipesShown(recipes) */
    public void method_2629(List<net.minecraft.class_8786<?>> recipes) {
        for (RecipeShownListener listener : this.showListeners) {
            listener.method_2646(recipes);
        }
    }

    /** getMinecraft() */
    public net.minecraft.class_310 method_2637() {
        return this.minecraft;
    }

    /** getRecipeBook() */
    public net.minecraft.class_3439 method_2633() {
        return this.recipeBook;
    }

    /** listButtons(consumer) */
    public void method_37083(Consumer<net.minecraft.class_339> consumer) {
        consumer.accept(this.forwardButton);
        consumer.accept(this.backButton);
        this.buttons.forEach(consumer);
    }
}
