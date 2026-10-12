package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * SmithingTrimRecipe's constructor is package-private in 1.20.2 (public in 1.20.1, with the id):
 * the NEW-site factory (CbRecipeCtors) builds through this invoker. A mod subclass's super(..)
 * reaches it through the access widener's entry for the same constructor.
 */
@Mixin(net.minecraft.class_8062.class)
public interface SmithingTrimRecipeCtor {

    @Invoker("<init>")
    static net.minecraft.class_8062 cb$new(net.minecraft.class_1856 template, net.minecraft.class_1856 base,
            net.minecraft.class_1856 addition) {
        throw new AssertionError();
    }
}
