package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 1.20.1 SynchronizeRecipesS2CPacket.readRecipe(buf) returned the Recipe; in
 * 1.20.2 the reader is private and yields a RecipeEntry. Exposed for the
 * Statics.method_17817 static redirect, which unwraps the entry.
 */
@Mixin(net.minecraft.class_2788.class)
public interface GenClass2788Bridge {

    @Invoker("method_17817")
    static net.minecraft.class_8786<?> cbReadRecipeEntry(net.minecraft.class_2540 buf) {
        throw new AssertionError();
    }
}
