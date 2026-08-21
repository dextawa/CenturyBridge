package top.dext.centurybridge.shims.v26_2;

import top.dext.centurybridge.rt.v26_2.Trackers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Rebuilds the recipe identity map whenever recipes (re)load, backing legacy
 * Recipe.getId() calls (redirected to Statics.method_8114). 26.2 funnels
 * loading through PreparedRecipes (net.minecraft.world.item.crafting.RecipeMap), so the one apply hook sees
 * every entry. Runtime namespace is intermediary -- no refmap.
 */
@Mixin(net.minecraft.world.item.crafting.RecipeManager.class)
public abstract class RecipeManagerBridge {

    @Inject(method = "apply", at = @At("TAIL"))
    private void cb$trackApply(net.minecraft.world.item.crafting.RecipeMap prepared, net.minecraft.server.packs.resources.ResourceManager rm, net.minecraft.util.profiling.ProfilerFiller profiler, CallbackInfo ci) {
        Trackers.RECIPE_IDS.clear();
        for (net.minecraft.world.item.crafting.RecipeHolder<?> entry : prepared.values()) {
            Trackers.RECIPE_IDS.put(entry.value(), entry.id().identifier());
        }
    }
}
