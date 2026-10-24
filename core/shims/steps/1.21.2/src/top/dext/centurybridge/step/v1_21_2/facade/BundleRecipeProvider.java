package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1802;
import net.minecraft.class_2447;
import net.minecraft.class_7225;
import net.minecraft.class_7784;
import net.minecraft.class_7800;
import net.minecraft.class_7923;
import net.minecraft.class_8790;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagenRecipes;

/**
 * 1.21.1's BundleRecipeProvider (net/minecraft/class_7801, classRenames; dev-time datagen), gone in 1.21.2 (the bundle
 * recipe joined the vanilla recipes): the experimental pack's provider of the bundle recipe. buildRecipes is 1.21.1's:
 * a bundle shaped from rabbit hide ('#') and string ('-') ("-#-", "# #", "###"), unlocked by having string.
 */
public class BundleRecipeProvider extends RecipeProvider {

    public BundleRecipeProvider(class_7784 output, CompletableFuture<class_7225.class_7874> registries) {
        super(output, registries);
    }

    /** buildRecipes(output) */
    @Override
    public void method_10419(class_8790 output) {
        class_2447.method_10437(class_7923.field_41178, class_7800.field_40638, class_1802.field_27023)
            .method_10434('#', class_1802.field_8245)
            .method_10434('-', class_1802.field_8276)
            .method_10439("-#-").method_10439("# #").method_10439("###")
            .method_10429("has_string", CbItemDatagenRecipes.legacy_method_10426(class_1802.field_8276))
            .method_10431(output);
    }
}
