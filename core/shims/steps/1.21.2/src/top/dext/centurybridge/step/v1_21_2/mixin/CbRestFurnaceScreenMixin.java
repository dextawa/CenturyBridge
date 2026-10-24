package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_489;
import net.minecraft.class_507;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBookClient;

/**
 * AbstractFurnaceScreen's constructor hands AbstractRecipeBookScreen the component it makes; when a 1.21.1
 * furnace screen's constructor brought its own (1.21.1's AbstractFurnaceScreen took the component), that one is
 * handed instead, as 1.21.1's screen held the component it was given.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_489.class)
public abstract class CbRestFurnaceScreenMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/class_10260;<init>(Lnet/minecraft/class_1729;Lnet/minecraft/class_507;Lnet/minecraft/class_1661;Lnet/minecraft/class_2561;)V"), index = 1)
    private static class_507<?> cb$component(class_507<?> made) {
        return CbRestRecipeBookClient.takePendingFurnace(made);
    }
}
