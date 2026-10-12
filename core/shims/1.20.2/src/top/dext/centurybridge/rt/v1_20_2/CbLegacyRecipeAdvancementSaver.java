package top.dext.centurybridge.rt.v1_20_2;

/**
 * RecipeProvider.buildAdvancement(output, id, builder), 1.20.1's shape: the adapter a 1.20.1
 * override is reached through when 1.20.2 calls buildAdvancement(output, entry).
 */
public interface CbLegacyRecipeAdvancementSaver {

    java.util.concurrent.CompletableFuture<?> method_46206(net.minecraft.class_7403 output, net.minecraft.class_2960 id,
        net.minecraft.class_161.class_162 builder);
}
