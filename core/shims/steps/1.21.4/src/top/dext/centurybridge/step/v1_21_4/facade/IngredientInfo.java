package top.dext.centurybridge.step.v1_21_4.facade;

import java.util.List;
import java.util.Objects;
import net.minecraft.class_1662;

/**
 * 1.21.3's StackedContents.IngredientInfo (net/minecraft/class_1662$class_9873, classRenames): a record of the
 * items an ingredient allows. 1.21.4's StackedContents.IngredientInfo is an interface (acceptsItem) that
 * Ingredient implements; this class implements it as 1.21.3's matching did (an item fits when the list holds
 * it), so 1.21.3 values go straight into 1.21.4's StackedContents. A record's members with 1.21.3's names
 * (allowedItems: comp_2920), all public (a mod's access widener cannot reach a CenturyBridge class).
 */
public final class IngredientInfo<T> implements class_1662.class_10528<T> {
    public final List<T> comp_2920;

    public IngredientInfo(List<T> allowedItems) {
        this.comp_2920 = allowedItems;
    }

    public List<T> comp_2920() {
        return this.comp_2920;
    }

    @Override
    public boolean acceptsItem(T item) {
        return this.comp_2920.contains(item);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IngredientInfo<?> other && Objects.equals(this.comp_2920, other.comp_2920);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.comp_2920);
    }

    @Override
    public String toString() {
        return "IngredientInfo[allowedItems=" + this.comp_2920 + "]";
    }
}
