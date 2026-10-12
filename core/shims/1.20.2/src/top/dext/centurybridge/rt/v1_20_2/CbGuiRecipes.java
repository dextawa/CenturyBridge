package top.dext.centurybridge.rt.v1_20_2;

/**
 * Recipe <-> RecipeHolder for the 1.20.1 recipe book and toast API: 1.20.2 hands RecipeHolder (id +
 * recipe) where 1.20.1 handed the bare Recipe. A recipe the client knows is answered with the holder the
 * client recipe manager has for that very object (the one the recipe book and toasts are built from); one
 * it does not know (a mod's own recipe object) gets a holder under a centurybridge id -- what reads it
 * (toast icons, ghost slots) uses the recipe, and a recipe-book collection simply does not contain it,
 * as in 1.20.1.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiRecipes {

    /** conv: the RecipeHolder of a 1.20.1 Recipe argument */
    public static net.minecraft.class_8786<?> cbHolder(net.minecraft.class_1860<?> recipe) {
        if (recipe == null) {
            return null;
        }
        net.minecraft.class_638 world = net.minecraft.class_310.method_1551().field_1687;
        if (world != null) {
            for (net.minecraft.class_8786<?> h : world.method_8433().method_8126()) {
                if (h.comp_1933() == recipe) {
                    return h;
                }
            }
        }
        return new net.minecraft.class_8786<>(new net.minecraft.class_2960("centurybridge",
            "legacy_recipe/" + Integer.toHexString(System.identityHashCode(recipe))), recipe);
    }

    /** ret conv / wrap: the Recipe 1.20.1 returned or passed where 1.20.2 has its holder */
    public static net.minecraft.class_1860<?> cbRecipe(net.minecraft.class_8786<?> holder) {
        return holder == null ? null : holder.comp_1933();
    }

    /** conv: the AdvancementHolder of a 1.20.1 Advancement, as the client advancement tree has it */
    public static net.minecraft.class_8779 cbAdvancementHolder(net.minecraft.class_161 advancement) {
        if (advancement == null) {
            return null;
        }
        net.minecraft.class_634 network = net.minecraft.class_310.method_1551().method_1562();
        if (network != null) {
            for (net.minecraft.class_8781 p : network.method_2869().method_53814().method_53693()) {
                if (p.method_53647() == advancement) {
                    return p.method_53649();
                }
            }
        }
        return new net.minecraft.class_8779(new net.minecraft.class_2960("centurybridge",
            "legacy_advancement/" + Integer.toHexString(System.identityHashCode(advancement))), advancement);
    }

    /** was net/minecraft/class_366.method_1985 RecipeToast.addOrUpdate(toasts, recipe) */
    public static void method_1985(net.minecraft.class_374 toasts, net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_366.method_1985(toasts, cbHolder(recipe));
    }

    private CbGuiRecipes() {
    }
}
