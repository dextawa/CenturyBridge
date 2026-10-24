package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Set;
import net.minecraft.class_1729;
import net.minecraft.class_2960;
import net.minecraft.class_3439;
import net.minecraft.class_5411;
import net.minecraft.class_8786;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeBook;

/**
 * 1.21.1's RecipeBook fields and methods on 1.21.2's RecipeBook (CbItemRecipeBook): known / highlight (ids) and
 * add / contains / remove / willHighlight / removeHighlight / addHighlight (by holder and, protected, by id),
 * copyOverData and isFiltering(menu), under their 1.21.1 names so a converted mod's calls, overrides and super calls
 * link to them. Every body is 1.21.1's (a holder's id is 1.21.2's key's location).
 */
@Mixin(class_3439.class)
public abstract class CbItemRecipeBookMixin {

    @Shadow
    @Final
    protected class_5411 field_25734;

    /** known: the ids of the recipes the book holds */
    protected final Set<class_2960> field_15300 = CbItemRecipeBook.known((class_3439) (Object) this);

    /** highlight: the ids of the recipes the book highlights */
    protected final Set<class_2960> field_15295 = CbItemRecipeBook.highlight((class_3439) (Object) this);

    @Shadow
    public abstract boolean method_30176(net.minecraft.class_5421 type);

    /** copyOverData(other) */
    public void method_14875(class_3439 other) {
        CbItemRecipeBookMixin from = (CbItemRecipeBookMixin) (Object) other;
        this.field_15300.clear();
        this.field_15295.clear();
        this.field_25734.method_30179(from.field_25734);
        this.field_15300.addAll(from.field_15300);
        this.field_15295.addAll(from.field_15295);
    }

    /** add(recipe): a special recipe is never added */
    public void method_14876(class_8786<?> recipe) {
        if (!recipe.comp_1933().method_8118()) {
            this.method_14881(recipe.comp_1932().method_29177());
        }
    }

    /** add(id) */
    protected void method_14881(class_2960 id) {
        this.field_15300.add(id);
    }

    /** contains(recipe) */
    public boolean method_14878(class_8786<?> recipe) {
        return recipe != null && this.field_15300.contains(recipe.comp_1932().method_29177());
    }

    /** contains(id) */
    public boolean method_22845(class_2960 id) {
        return this.field_15300.contains(id);
    }

    /** remove(recipe) */
    public void method_14893(class_8786<?> recipe) {
        this.method_14879(recipe.comp_1932().method_29177());
    }

    /** remove(id): no longer known nor highlighted */
    protected void method_14879(class_2960 id) {
        this.field_15300.remove(id);
        this.field_15295.remove(id);
    }

    /** willHighlight(recipe) */
    public boolean method_14883(class_8786<?> recipe) {
        return this.field_15295.contains(recipe.comp_1932().method_29177());
    }

    /** removeHighlight(recipe) */
    public void method_14886(class_8786<?> recipe) {
        this.field_15295.remove(recipe.comp_1932().method_29177());
    }

    /** addHighlight(recipe) */
    public void method_14885(class_8786<?> recipe) {
        this.method_14877(recipe.comp_1932().method_29177());
    }

    /** addHighlight(id) */
    protected void method_14877(class_2960 id) {
        this.field_15295.add(id);
    }

    /** isFiltering(menu): the book's filter setting for the menu's recipe book type */
    public boolean method_14880(class_1729 menu) {
        return this.method_30176(menu.method_30264());
    }
}
