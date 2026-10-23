package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * RecipeCollection.hasSingleResultItem() (method_2656) is gone in 1.21.4 (the recipe book no longer asks). 1.21.3
 * answered whether every recipe of the collection shows the same result; given back over the same entries.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_516.class)
public abstract class CbGuiRecipeCollectionMixin {

    /** entries */
    @Shadow
    @Final
    private List<net.minecraft.class_10297> field_54835;

    /** was hasSingleResultItem() (method_2656): every entry's display has the first entry's result */
    public boolean method_2656() {
        if (field_54835.isEmpty()) {
            // 1.21.3 had no empty collection (its constructor computed this from the first entry);
            // 1.21.4's EMPTY shows no result at all
            return false;
        }
        net.minecraft.class_10302 first = field_54835.getFirst().comp_3263().comp_3258();
        for (int i = 1; i < field_54835.size(); i++) {
            if (!field_54835.get(i).comp_3263().comp_3258().equals(first)) {
                return false;
            }
        }
        return true;
    }
}
