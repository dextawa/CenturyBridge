package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** SimpleCraftingRecipeSerializer's own stream codec (the vanilla body a 1.20.4 subclass's super.fromNetwork / super.toNetwork reached; rt/CbFinItemRecipes) */
@Mixin(net.minecraft.class_1866.class)
public interface CbFinItemRecipeSimpleAccess {

    @Accessor("field_48362")
    net.minecraft.class_9139<net.minecraft.class_9129, ?> cbFin$streamCodec();
}
