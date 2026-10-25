package cb.s7gear.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** a 1.21.1 mod mixin redirecting Item.getDescription()'s translatable(): carrots are named "cb carrot" (GearTestMod.nameReads) */
@Mixin(class_1792.class)
public abstract class ItemDescriptionMixin {

    @Redirect(method = "method_7848()Lnet/minecraft/class_2561;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2561;method_43471(Ljava/lang/String;)Lnet/minecraft/class_5250;"))
    private class_5250 cbs7gear$carrot(String key) {
        return "item.minecraft.carrot".equals(key) ? class_2561.method_43470("cb carrot") : class_2561.method_43471(key);
    }
}
