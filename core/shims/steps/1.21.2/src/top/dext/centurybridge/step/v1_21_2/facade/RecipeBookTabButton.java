package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBookClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBooks;

/**
 * RecipeBookTabButton of 1.21.1 (net/minecraft/class_512; classRenamed -- 1.21.2's takes a TabInfo): a recipe
 * book tab for a 1.21.1 RecipeBookCategories, which wobbles when a recipe in it is newly highlighted and is
 * shown when the tab has known recipes that fit. 1.21.1's body; the sprite is drawn with 1.21.2's textured gui
 * render type, as 1.21.1's blitSprite drew it.
 */
@Environment(EnvType.CLIENT)
public class RecipeBookTabButton extends net.minecraft.class_361 {

    private static final net.minecraft.class_8666 SPRITES = new net.minecraft.class_8666(
        net.minecraft.class_2960.method_60656("recipe_book/tab"), net.minecraft.class_2960.method_60656("recipe_book/tab_selected"));
    private final RecipeBookCategories category;
    private float animationTime;

    public RecipeBookTabButton(RecipeBookCategories category) {
        super(0, 0, 35, 27, false);
        this.category = category;
        this.method_1962(SPRITES);
    }

    /** startAnimation(minecraft) */
    public void method_2622(net.minecraft.class_310 minecraft) {
        net.minecraft.class_299 book = minecraft.field_1724.method_3130();
        List<RecipeCollection> collections = CbRestRecipeBookClient.getCollection(book, this.category);
        if (minecraft.field_1724.field_7512 instanceof net.minecraft.class_1729 menu) {
            for (RecipeCollection collection : collections) {
                for (net.minecraft.class_8786<?> recipe : collection.method_2651(CbRestRecipeBooks.isFiltering(book, menu))) {
                    if (CbRestRecipeBooks.willHighlight(book, recipe)) {
                        this.animationTime = 15.0F;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void method_48579(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
        if (this.field_45390 != null) {
            if (this.animationTime > 0.0F) {
                float scale = 1.0F + 0.1F * (float) Math.sin((double) (this.animationTime / 15.0F * (float) Math.PI));
                graphics.method_51448().method_22903();
                graphics.method_51448().method_46416((float) (this.method_46426() + 8), (float) (this.method_46427() + 12), 0.0F);
                graphics.method_51448().method_22905(1.0F, scale, 1.0F);
                graphics.method_51448().method_46416((float) (-(this.method_46426() + 8)), (float) (-(this.method_46427() + 12)), 0.0F);
            }
            RenderSystem.disableDepthTest();
            net.minecraft.class_2960 sprite = this.field_45390.method_52729(true, this.field_2194);
            int x = this.method_46426();
            if (this.field_2194) {
                x -= 2;
            }
            graphics.method_52706(net.minecraft.class_1921::method_62277, sprite, x, this.method_46427(), this.field_22758, this.field_22759);
            RenderSystem.enableDepthTest();
            this.renderIcon(graphics);
            if (this.animationTime > 0.0F) {
                graphics.method_51448().method_22909();
                this.animationTime -= partialTick;
            }
        }
    }

    private void renderIcon(net.minecraft.class_332 graphics) {
        List<net.minecraft.class_1799> icons = this.category.method_1623();
        int shift = this.field_2194 ? -2 : 0;
        if (icons.size() == 1) {
            graphics.method_51445(icons.get(0), this.method_46426() + 9 + shift, this.method_46427() + 5);
        } else if (icons.size() == 2) {
            graphics.method_51445(icons.get(0), this.method_46426() + 3 + shift, this.method_46427() + 5);
            graphics.method_51445(icons.get(1), this.method_46426() + 14 + shift, this.method_46427() + 5);
        }
    }

    /** getCategory() */
    public RecipeBookCategories method_2623() {
        return this.category;
    }

    /** updateVisibility(book) */
    public boolean method_2624(net.minecraft.class_299 book) {
        List<RecipeCollection> collections = CbRestRecipeBookClient.getCollection(book, this.category);
        this.field_22764 = false;
        if (collections != null) {
            for (RecipeCollection collection : collections) {
                if (collection.method_2652() && collection.method_2657()) {
                    this.field_22764 = true;
                    break;
                }
            }
        }
        return this.field_22764;
    }
}
