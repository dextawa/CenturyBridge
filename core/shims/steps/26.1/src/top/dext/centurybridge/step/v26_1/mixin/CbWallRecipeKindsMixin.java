package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipeKinds;

/** 1.21.11's removed special recipe serializers registered right after 26.1's own (rt/CbWallRecipeKinds) */
@Mixin(RecipeSerializers.class)
public abstract class CbWallRecipeKindsMixin {

    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void cb$registerLegacyKinds(Registry<RecipeSerializer<?>> registry, CallbackInfoReturnable<Object> cir) {
        CbWallRecipeKinds.register(registry);
    }
}
