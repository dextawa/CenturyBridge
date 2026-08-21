package top.dext.centurybridge.shims.v26_2;

import top.dext.centurybridge.rt.v26_2.Trackers;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Rebuilds the advancement identity map on datapack (re)load, and restores the
 * legacy get-by-id shape (Advancement instead of AdvancementEntry).
 */
@Mixin(net.minecraft.server.ServerAdvancementManager.class)
public abstract class AdvancementLoaderBridge {

    @Inject(method = "apply", at = @At("TAIL"))
    private void cb$track(Map<net.minecraft.resources.Identifier, com.google.gson.JsonElement> map,
                          net.minecraft.server.packs.resources.ResourceManager rm, net.minecraft.util.profiling.ProfilerFiller profiler, CallbackInfo ci) {
        Trackers.ADVANCEMENT_ENTRIES.clear();
        for (net.minecraft.advancements.AdvancementHolder entry : ((net.minecraft.server.ServerAdvancementManager) (Object) this).getAllAdvancements()) {
            Trackers.ADVANCEMENT_ENTRIES.put(entry.value(), entry);
        }
    }

    public net.minecraft.advancements.Advancement get(net.minecraft.resources.Identifier id) {
        net.minecraft.advancements.AdvancementHolder entry = ((net.minecraft.server.ServerAdvancementManager) (Object) this).get(id);
        return entry == null ? null : entry.value();
    }
}
