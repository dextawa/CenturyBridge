package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBookClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBooks;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeMenus;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeMenus;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeView;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipesClient;

/**
 * RecipeBookComponent of 1.21.1 (net/minecraft/class_507; classRenamed -- 1.21.2's is an abstract component
 * built for one menu from tab infos, over display entries): the recipe book beside a container screen. 1.21.1's
 * body, on the recipes the server sent (CbRestRecipeSync) and the 1.21.1 book state kept beside 1.21.2's
 * (CbRestRecipeBookClient); asking the menu as 1.21.1 asked it (CbItemRecipeMenus); placing a recipe with
 * 1.21.1's handlePlaceRecipe (CbRestRecipesClient: the recipe's display, or by id); drawn with 1.21.2's
 * GuiGraphics (the textured gui render type where 1.21.1's blit had none).
 *
 * 1.21.2's own screens keep 1.21.2's component; asked for theirs (RecipeUpdateListener.getRecipeBookComponent,
 * a furnace screen's field), a 1.21.1 mod gets {@link Vanilla}, the 1.21.1 API answered by that component.
 *
 * It is itself a 1.21.2 component (an empty one: no menu, no tabs, never initialised as one), so that 1.21.2's
 * screens can hold it where 1.21.1 handed a screen its component (a 1.21.1 furnace screen's constructor): each
 * 1.21.2 call such a screen makes is answered by the 1.21.1 member of the same contract -- init with the
 * player's open menu, the tooltip and ghost recipe at the screen's position, slot clicks, a ghost recipe sent
 * as a display shown as the recipe it belongs to.
 */
@Environment(EnvType.CLIENT)
public class RecipeBookComponent extends net.minecraft.class_507<net.minecraft.class_1729> implements PlaceRecipe<net.minecraft.class_1856>, RecipeShownListener {

    /** RECIPE_BUTTON_SPRITES */
    public static final net.minecraft.class_8666 field_45550 = new net.minecraft.class_8666(
        net.minecraft.class_2960.method_60656("recipe_book/button"), net.minecraft.class_2960.method_60656("recipe_book/button_highlighted"));
    private static final net.minecraft.class_8666 FILTER_BUTTON_SPRITES = new net.minecraft.class_8666(
        net.minecraft.class_2960.method_60656("recipe_book/filter_enabled"), net.minecraft.class_2960.method_60656("recipe_book/filter_disabled"),
        net.minecraft.class_2960.method_60656("recipe_book/filter_enabled_highlighted"), net.minecraft.class_2960.method_60656("recipe_book/filter_disabled_highlighted"));
    /** RECIPE_BOOK_LOCATION */
    public static final net.minecraft.class_2960 field_3097 = net.minecraft.class_2960.method_60656("textures/gui/recipe_book.png");
    private static final net.minecraft.class_2561 SEARCH_HINT = net.minecraft.class_2561.method_43471("gui.recipebook.search_hint")
        .method_27692(net.minecraft.class_124.field_1056).method_27692(net.minecraft.class_124.field_1080);
    /** IMAGE_WIDTH */
    public static final int field_32408 = 147;
    /** IMAGE_HEIGHT */
    public static final int field_32409 = 166;
    private static final net.minecraft.class_2561 ONLY_CRAFTABLES_TOOLTIP = net.minecraft.class_2561.method_43471("gui.recipebook.toggleRecipes.craftable");
    private static final net.minecraft.class_2561 ALL_RECIPES_TOOLTIP = net.minecraft.class_2561.method_43471("gui.recipebook.toggleRecipes.all");

    private int xOffset;
    private int width;
    private int height;
    /** ghostRecipe */
    public final GhostRecipe field_3092 = new GhostRecipe();
    private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
    private RecipeBookTabButton selectedTab;
    /** filterButton */
    public net.minecraft.class_361 field_3088;
    /** menu */
    public net.minecraft.class_1729 field_3095;
    /** minecraft */
    public net.minecraft.class_310 field_3091;
    private net.minecraft.class_342 searchBox;
    private String lastSearch = "";
    private net.minecraft.class_299 book;
    private final RecipeBookPage recipeBookPage = new RecipeBookPage();
    private final net.minecraft.class_9875 stackedContents = new net.minecraft.class_9875();
    private int timesInventoryChanged;
    private boolean ignoreTextInput;
    private boolean visible;
    private boolean widthTooNarrow;

    private float lastPartialTick;

    public RecipeBookComponent() {
        super(null, List.of());
    }

    /** init(width, height, minecraft, widthTooNarrow, menu) */
    public void method_2597(int width, int height, net.minecraft.class_310 minecraft, boolean widthTooNarrow, net.minecraft.class_1729 menu) {
        this.field_3091 = minecraft;
        this.width = width;
        this.height = height;
        this.field_3095 = menu;
        this.widthTooNarrow = widthTooNarrow;
        minecraft.field_1724.field_7512 = menu;
        this.book = minecraft.field_1724.method_3130();
        this.timesInventoryChanged = minecraft.field_1724.method_31548().method_7364();
        this.visible = this.isVisibleAccordingToBookData();
        if (this.visible) {
            this.method_2579();
        }
    }

    /** initVisuals() */
    public void method_2579() {
        this.xOffset = this.widthTooNarrow ? 0 : 86;
        int x = (this.width - 147) / 2 - this.xOffset;
        int y = (this.height - 166) / 2;
        this.stackedContents.method_61534();
        this.field_3091.field_1724.method_31548().method_7387(this.stackedContents);
        CbRestRecipeMenus.fill(this.field_3095, this.stackedContents);
        String search = this.searchBox != null ? this.searchBox.method_1882() : "";
        this.searchBox = new net.minecraft.class_342(this.field_3091.field_1772, x + 25, y + 13, 81, 9 + 5, net.minecraft.class_2561.method_43471("itemGroup.search"));
        this.searchBox.method_1880(50);
        this.searchBox.method_1862(true);
        this.searchBox.method_1868(16777215);
        this.searchBox.method_1852(search);
        this.searchBox.method_47404(SEARCH_HINT);
        this.recipeBookPage.method_2636(this.field_3091, x, y);
        this.recipeBookPage.method_2630(this);
        this.field_3088 = new net.minecraft.class_361(x + 110, y + 12, 26, 16, CbRestRecipeBooks.isFiltering(this.book, this.field_3095));
        this.updateFilterButtonTooltip();
        this.method_2585();
        this.tabButtons.clear();
        for (RecipeBookCategories category : RecipeBookCategories.method_30285(this.field_3095.method_30264())) {
            this.tabButtons.add(new RecipeBookTabButton(category));
        }
        if (this.selectedTab != null) {
            RecipeBookCategories selected = this.selectedTab.method_2623();
            this.selectedTab = this.tabButtons.stream().filter(b -> b.method_2623().equals(selected)).findFirst().orElse(null);
        }
        if (this.selectedTab == null) {
            this.selectedTab = this.tabButtons.get(0);
        }
        this.selectedTab.method_1964(true);
        this.updateCollections(false);
        this.updateTabs();
    }

    private void updateFilterButtonTooltip() {
        this.field_3088.method_47400(this.field_3088.method_1965() ? net.minecraft.class_7919.method_47407(this.method_17064())
            : net.minecraft.class_7919.method_47407(ALL_RECIPES_TOOLTIP));
    }

    /** the filter name, for 1.21.2's furnace screen constructor (CbRestRecipeBookClient) */
    public net.minecraft.class_2561 cb$filterName() {
        return this.method_17064();
    }

    /** initFilterButtonTextures() */
    public void method_2585() {
        this.field_3088.method_1962(FILTER_BUTTON_SPRITES);
    }

    /**
     * The filter button's sprites, as 1.21.11 asks for them: 1.21.11 replaced initFilterButtonTextures (method_2585)
     * with the abstract getFilterButtonTextures (class_507.method_76629, read only by its filter CycleButton's sprite
     * lookup method_76634; vanilla's furnace component answers its furnace_filter_* sprites, class_517.field_45540;
     * javap of 1.21.10 / 1.21.11). Unused before 1.21.11; once this layer climbs there it implements that abstract
     * with what method_2585 hands the button, so the button no longer meets an AbstractMethodError.
     */
    public net.minecraft.class_8666 method_76629() {
        return FILTER_BUTTON_SPRITES;
    }

    /** updateScreenPosition(width, imageWidth) */
    public int method_2595(int width, int imageWidth) {
        if (this.method_2605() && !this.widthTooNarrow) {
            return 177 + (width - imageWidth - 200) / 2;
        }
        return (width - imageWidth) / 2;
    }

    /** toggleVisibility() */
    public void method_2591() {
        this.method_2593(!this.method_2605());
    }

    /** isVisible() */
    public boolean method_2605() {
        return this.visible;
    }

    private boolean isVisibleAccordingToBookData() {
        return this.book.method_14887(this.field_3095.method_30264());
    }

    /** setVisible(visible) */
    public void method_2593(boolean visible) {
        if (visible) {
            this.method_2579();
        }
        this.visible = visible;
        this.book.method_14884(this.field_3095.method_30264(), visible);
        if (!visible) {
            this.recipeBookPage.method_2638();
        }
        this.method_2588();
    }

    /** slotClicked(slot) */
    public void method_2600(net.minecraft.class_1735 slot) {
        if (slot != null && slot.field_7874 < CbItemRecipeMenus.getSize(this.field_3095)) {
            this.field_3092.method_2571();
            if (this.method_2605()) {
                this.updateStackedContents();
            }
        }
    }

    private void updateCollections(boolean resetPage) {
        List<RecipeCollection> collections = CbRestRecipeBookClient.getCollection(this.book, this.selectedTab.method_2623());
        StackedContents counts = CbRestRecipeMenus.legacyCounts(this.stackedContents);
        collections.forEach(c -> c.method_2649(counts, CbItemRecipeMenus.getGridWidth(this.field_3095), CbItemRecipeMenus.getGridHeight(this.field_3095), this.book));
        List<RecipeCollection> shown = Lists.newArrayList(collections);
        shown.removeIf(c -> !c.method_2652());
        shown.removeIf(c -> !c.method_2657());
        String search = this.searchBox.method_1882();
        if (!search.isEmpty()) {
            net.minecraft.class_634 connection = this.field_3091.method_1562();
            if (connection != null) {
                ObjectLinkedOpenHashSet<RecipeCollection> found = new ObjectLinkedOpenHashSet<>(
                    CbRestRecipeBookClient.method_60364(connection.method_60347()).method_4810(search.toLowerCase(Locale.ROOT)));
                shown.removeIf(c -> !found.contains(c));
            }
        }
        if (CbRestRecipeBooks.isFiltering(this.book, this.field_3095)) {
            shown.removeIf(c -> !c.method_2655());
        }
        this.recipeBookPage.method_2627(shown, resetPage);
    }

    private void updateTabs() {
        int x = (this.width - 147) / 2 - this.xOffset - 30;
        int y = (this.height - 166) / 2 + 3;
        int n = 0;
        for (RecipeBookTabButton tab : this.tabButtons) {
            RecipeBookCategories category = tab.method_2623();
            if (category == RecipeBookCategories.CRAFTING_SEARCH || category == RecipeBookCategories.FURNACE_SEARCH) {
                tab.field_22764 = true;
                tab.method_48229(x, y + 27 * n++);
            } else if (tab.method_2624(this.book)) {
                tab.method_48229(x, y + 27 * n++);
                tab.method_2622(this.field_3091);
            }
        }
    }

    /** tick() */
    public void method_2590() {
        boolean shouldBeVisible = this.isVisibleAccordingToBookData();
        if (this.method_2605() != shouldBeVisible) {
            this.method_2593(shouldBeVisible);
        }
        if (this.method_2605() && this.timesInventoryChanged != this.field_3091.field_1724.method_31548().method_7364()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.field_3091.field_1724.method_31548().method_7364();
        }
    }

    private void updateStackedContents() {
        this.stackedContents.method_61534();
        this.field_3091.field_1724.method_31548().method_7387(this.stackedContents);
        CbRestRecipeMenus.fill(this.field_3095, this.stackedContents);
        this.updateCollections(false);
    }

    @Override
    public void method_25394(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
        this.lastPartialTick = partialTick;
        if (this.method_2605()) {
            graphics.method_51448().method_22903();
            graphics.method_51448().method_46416(0.0F, 0.0F, 100.0F);
            int x = (this.width - 147) / 2 - this.xOffset;
            int y = (this.height - 166) / 2;
            graphics.method_25290(net.minecraft.class_1921::method_62277, field_3097, x, y, 1.0F, 1.0F, 147, 166, 256, 256);
            this.searchBox.method_25394(graphics, mouseX, mouseY, partialTick);
            for (RecipeBookTabButton tab : this.tabButtons) {
                tab.method_25394(graphics, mouseX, mouseY, partialTick);
            }
            this.field_3088.method_25394(graphics, mouseX, mouseY, partialTick);
            this.recipeBookPage.method_2634(graphics, x, y, mouseX, mouseY, partialTick);
            graphics.method_51448().method_22909();
        }
    }

    /** renderTooltip(graphics, leftPos, topPos, mouseX, mouseY) */
    public void method_2601(net.minecraft.class_332 graphics, int leftPos, int topPos, int mouseX, int mouseY) {
        if (this.method_2605()) {
            this.recipeBookPage.method_2628(graphics, mouseX, mouseY);
            this.renderGhostRecipeTooltip(graphics, leftPos, topPos, mouseX, mouseY);
        }
    }

    /** getRecipeFilterName() */
    public net.minecraft.class_2561 method_17064() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    private void renderGhostRecipeTooltip(net.minecraft.class_332 graphics, int leftPos, int topPos, int mouseX, int mouseY) {
        net.minecraft.class_1799 hovered = null;
        for (int i = 0; i < this.field_3092.method_2572(); i++) {
            GhostRecipe.GhostIngredient ingredient = this.field_3092.method_2570(i);
            int x = ingredient.method_2574() + leftPos;
            int y = ingredient.method_2575() + topPos;
            if (mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16) {
                hovered = ingredient.method_2573();
            }
        }
        if (hovered != null && this.field_3091.field_1755 != null) {
            graphics.method_51434(this.field_3091.field_1772, net.minecraft.class_437.method_25408(this.field_3091, hovered), mouseX, mouseY);
        }
    }

    /** renderGhostRecipe(graphics, leftPos, topPos, bigResultSlot, partialTick) */
    public void method_2581(net.minecraft.class_332 graphics, int leftPos, int topPos, boolean bigResultSlot, float partialTick) {
        this.field_3092.method_2567(graphics, this.field_3091, leftPos, topPos, bigResultSlot, partialTick);
    }

    @Override
    public boolean method_25402(double mouseX, double mouseY, int button) {
        if (!this.method_2605() || this.field_3091.field_1724.method_7325()) {
            return false;
        }
        if (this.recipeBookPage.method_2632(mouseX, mouseY, button, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            net.minecraft.class_8786<?> recipe = this.recipeBookPage.method_2631();
            RecipeCollection collection = this.recipeBookPage.method_2635();
            if (recipe != null && collection != null) {
                if (!collection.method_2653(recipe) && this.field_3092.method_2566() == recipe) {
                    return false;
                }
                this.field_3092.method_2571();
                CbRestRecipesClient.handlePlaceRecipe(this.field_3091.field_1761, this.field_3091.field_1724.field_7512.field_7763, recipe,
                    net.minecraft.class_437.method_25442());
                if (!this.isOffsetNextToMainGUI()) {
                    this.method_2593(false);
                }
            }
            return true;
        } else if (this.searchBox.method_25402(mouseX, mouseY, button)) {
            this.searchBox.method_25365(true);
            return true;
        } else {
            this.searchBox.method_25365(false);
            if (this.field_3088.method_25402(mouseX, mouseY, button)) {
                boolean filtering = this.toggleFiltering();
                this.field_3088.method_1964(filtering);
                this.updateFilterButtonTooltip();
                this.method_2588();
                this.updateCollections(false);
                return true;
            }
            for (RecipeBookTabButton tab : this.tabButtons) {
                if (tab.method_25402(mouseX, mouseY, button)) {
                    if (this.selectedTab != tab) {
                        if (this.selectedTab != null) {
                            this.selectedTab.method_1964(false);
                        }
                        this.selectedTab = tab;
                        this.selectedTab.method_1964(true);
                        this.updateCollections(true);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private boolean toggleFiltering() {
        net.minecraft.class_5421 type = this.field_3095.method_30264();
        boolean filtering = !this.book.method_30176(type);
        this.book.method_30177(type, filtering);
        return filtering;
    }

    /** hasClickedOutside(mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight, button) */
    public boolean method_2598(double mouseX, double mouseY, int leftPos, int topPos, int imageWidth, int imageHeight, int button) {
        if (!this.method_2605()) {
            return true;
        }
        boolean outside = mouseX < (double) leftPos || mouseY < (double) topPos || mouseX >= (double) (leftPos + imageWidth) || mouseY >= (double) (topPos + imageHeight);
        boolean onBook = (double) (leftPos - 147) < mouseX && mouseX < (double) leftPos && (double) topPos < mouseY && mouseY < (double) (topPos + imageHeight);
        return outside && !onBook && !this.selectedTab.method_25367();
    }

    @Override
    public boolean method_25404(int keyCode, int scanCode, int modifiers) {
        this.ignoreTextInput = false;
        if (!this.method_2605() || this.field_3091.field_1724.method_7325()) {
            return false;
        } else if (keyCode == 256 && !this.isOffsetNextToMainGUI()) {
            this.method_2593(false);
            return true;
        } else if (this.searchBox.method_25404(keyCode, scanCode, modifiers)) {
            this.checkSearchStringUpdate();
            return true;
        } else if (this.searchBox.method_25370() && this.searchBox.method_1885() && keyCode != 256) {
            return true;
        } else if (this.field_3091.field_1690.field_1890.method_1417(keyCode, scanCode) && !this.searchBox.method_25370()) {
            this.ignoreTextInput = true;
            this.searchBox.method_25365(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean method_16803(int keyCode, int scanCode, int modifiers) {
        this.ignoreTextInput = false;
        return false;
    }

    @Override
    public boolean method_25400(char c, int modifiers) {
        if (this.ignoreTextInput) {
            return false;
        } else if (!this.method_2605() || this.field_3091.field_1724.method_7325()) {
            return false;
        } else if (this.searchBox.method_25400(c, modifiers)) {
            this.checkSearchStringUpdate();
            return true;
        }
        return false;
    }

    @Override
    public boolean method_25405(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void method_25365(boolean focused) {
    }

    @Override
    public boolean method_25370() {
        return false;
    }

    private void checkSearchStringUpdate() {
        String search = this.searchBox.method_1882().toLowerCase(Locale.ROOT);
        this.pirateSpeechForThePeople(search);
        if (!search.equals(this.lastSearch)) {
            this.updateCollections(false);
            this.lastSearch = search;
        }
    }

    private void pirateSpeechForThePeople(String search) {
        if ("excitedze".equals(search)) {
            net.minecraft.class_1076 languages = this.field_3091.method_1526();
            net.minecraft.class_1077 language = languages.method_4668("en_pt");
            if (language == null || languages.method_4669().equals("en_pt")) {
                return;
            }
            languages.method_4667("en_pt");
            this.field_3091.field_1690.field_1883 = "en_pt";
            this.field_3091.method_1521();
            this.field_3091.field_1690.method_1640();
        }
    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    /** recipesUpdated() */
    public void method_2592() {
        this.updateTabs();
        if (this.method_2605()) {
            this.updateCollections(false);
        }
    }

    /** recipesShown(recipes) */
    @Override
    public void method_2646(List<net.minecraft.class_8786<?>> recipes) {
        for (net.minecraft.class_8786<?> recipe : recipes) {
            CbRestRecipesClient.removeRecipeHighlight(this.field_3091.field_1724, recipe);
        }
    }

    /** setupGhostRecipe(recipe, slots) */
    public void method_2596(net.minecraft.class_8786<?> recipe, List<net.minecraft.class_1735> slots) {
        net.minecraft.class_1799 result = CbRestRecipeView.resultItem(recipe.comp_1933(), this.field_3091.field_1687.method_30349());
        this.field_3092.method_2565(recipe);
        this.field_3092.cb$addStacks(new net.minecraft.class_1799[] {result}, slots.get(0).field_7873, slots.get(0).field_7872);
        this.method_12816(CbItemRecipeMenus.getGridWidth(this.field_3095), CbItemRecipeMenus.getGridHeight(this.field_3095), CbItemRecipeMenus.getResultSlotIndex(this.field_3095),
            recipe, CbRestRecipeView.ingredients(recipe.comp_1933()).iterator(), 0);
    }

    /** addItemToSlot(ingredient, slot, maxAmount, x, y) */
    public void method_60344(net.minecraft.class_1856 ingredient, int slot, int maxAmount, int x, int y) {
        if (!CbRestRecipeView.isGap(ingredient)) {
            net.minecraft.class_1735 target = this.field_3095.field_7761.get(slot);
            this.field_3092.method_2569(ingredient, target.field_7873, target.field_7872);
        }
    }

    @Override
    public final void method_12815(net.minecraft.class_1856 ingredient, int slot, int maxAmount, int x, int y) {
        this.method_60344(ingredient, slot, maxAmount, x, y);
    }

    /** sendUpdateSettings() */
    public void method_2588() {
        if (this.field_3091.method_1562() != null) {
            net.minecraft.class_5421 type = this.field_3095.method_30264();
            boolean open = this.book.method_30173().method_30180(type);
            boolean filtering = this.book.method_30173().method_30187(type);
            this.field_3091.method_1562().method_52787(new net.minecraft.class_5427(type, open, filtering));
        }
    }

    @Override
    public net.minecraft.class_6379.class_6380 method_37018() {
        return this.visible ? net.minecraft.class_6379.class_6380.field_33785 : net.minecraft.class_6379.class_6380.field_33784;
    }

    @Override
    public void method_37020(net.minecraft.class_6382 output) {
        List<net.minecraft.class_6379> entries = new ArrayList<>();
        this.recipeBookPage.method_37083(widget -> {
            if (widget.method_37303()) {
                entries.add(widget);
            }
        });
        entries.add(this.searchBox);
        entries.add(this.field_3088);
        entries.addAll(this.tabButtons);
        net.minecraft.class_437.class_6390 result = net.minecraft.class_437.method_37061(entries, null);
        if (result != null) {
            result.field_33825.method_37020(output.method_37031());
        }
    }

    // ------------------------------------------------------------------ 1.21.2's calls, when a 1.21.2 screen holds this component

    /** 1.21.2's init(width, height, minecraft, widthTooNarrow): 1.21.1's, with the player's open menu */
    @Override
    public void method_2597(int width, int height, net.minecraft.class_310 minecraft, boolean widthTooNarrow) {
        this.method_2597(width, height, minecraft, widthTooNarrow, (net.minecraft.class_1729) minecraft.field_1724.field_7512);
    }

    private net.minecraft.class_465<?> containerScreen() {
        return this.field_3091 != null && this.field_3091.field_1755 instanceof net.minecraft.class_465<?> screen ? screen : null;
    }

    /** 1.21.2's renderTooltip(graphics, mouseX, mouseY, hoveredSlot): 1.21.1's at the screen's position */
    @Override
    public void method_2601(net.minecraft.class_332 graphics, int mouseX, int mouseY, net.minecraft.class_1735 hoveredSlot) {
        net.minecraft.class_465<?> screen = containerScreen();
        top.dext.centurybridge.step.v1_21_2.mixin.CbRestContainerScreenAccessor pos = (top.dext.centurybridge.step.v1_21_2.mixin.CbRestContainerScreenAccessor) screen;
        this.method_2601(graphics, screen == null ? 0 : pos.cb$leftPos(), screen == null ? 0 : pos.cb$topPos(), mouseX, mouseY);
    }

    /** 1.21.2's renderGhostRecipe(graphics, bigResultSlot): 1.21.1's at the screen's position, with the frame's partial tick */
    @Override
    public void method_2581(net.minecraft.class_332 graphics, boolean bigResultSlot) {
        net.minecraft.class_465<?> screen = containerScreen();
        top.dext.centurybridge.step.v1_21_2.mixin.CbRestContainerScreenAccessor pos = (top.dext.centurybridge.step.v1_21_2.mixin.CbRestContainerScreenAccessor) screen;
        this.method_2581(graphics, screen == null ? 0 : pos.cb$leftPos(), screen == null ? 0 : pos.cb$topPos(), bigResultSlot, this.lastPartialTick);
    }

    /** 1.21.2's slotClicked(slot): 1.21.1's */
    @Override
    public void method_62044(net.minecraft.class_1735 slot) {
        this.method_2600(slot);
    }

    /** 1.21.2's recipeShown(displayId): 1.21.1's recipesShown of the recipe it belongs to */
    @Override
    public void method_64876(net.minecraft.class_10298 display) {
        net.minecraft.class_8786<?> recipe = CbRestRecipesClient.byDisplay(display);
        if (recipe != null) {
            this.method_2646(List.of(recipe));
        }
    }

    /** 1.21.2's fillGhostRecipe(display): 1.21.1's setupGhostRecipe of the recipe it belongs to, on the menu's slots */
    @Override
    public void method_64875(net.minecraft.class_10295 display) {
        net.minecraft.class_8786<?> recipe = CbRestRecipesClient.byRecipeDisplay(display);
        if (recipe != null && this.field_3095 != null) {
            this.field_3092.method_2571();
            this.method_2596(recipe, this.field_3095.field_7761);
        }
    }

    /** 1.21.2's isCraftingSlot: a slot below the menu's grid size, as 1.21.1's slotClicked judged */
    @Override
    protected boolean method_62023(net.minecraft.class_1735 slot) {
        return this.field_3095 != null && slot.field_7874 < CbItemRecipeMenus.getSize(this.field_3095);
    }

    /** 1.21.2's selectMatchingRecipes: not asked (this component's 1.21.1 collections do the selecting) */
    @Override
    protected void method_62024(net.minecraft.class_516 collection, net.minecraft.class_9875 stacked) {
        collection.method_64884(stacked, display -> true);
    }

    /** 1.21.2's ghost-slot filling: not asked (this component draws its 1.21.1 ghost recipe) */
    @Override
    protected void method_64868(net.minecraft.class_9934 slots, net.minecraft.class_10295 display, net.minecraft.class_10352 context) {
    }

    // ------------------------------------------------------------------ 1.21.2's components, through the 1.21.1 API

    private static final Map<net.minecraft.class_507<?>, RecipeBookComponent> VANILLA = java.util.Collections.synchronizedMap(new WeakHashMap<>());

    /** a 1.21.2 component as 1.21.1's (cached, one per component) */
    public static RecipeBookComponent of(net.minecraft.class_507<?> component) {
        if (component == null) {
            return null;
        }
        if (component instanceof RecipeBookComponent facade) {
            return facade;
        }
        return VANILLA.computeIfAbsent(component, c -> c instanceof net.minecraft.class_517 ? new AbstractFurnaceRecipeBookComponent.Vanilla(c) : new Vanilla(c));
    }

    /**
     * 1.21.2's component of a 1.21.2 screen through the 1.21.1 API: each call answered by the 1.21.2 member of
     * the same contract (visibility, position, ticking, drawing, input, recipes updated, slot clicks); a ghost
     * recipe of a recipe is the recipe's display (one the book knows), its ingredients set on 1.21.2's ghost slots.
     */
    public static final class Vanilla extends RecipeBookComponent {
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
