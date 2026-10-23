package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.loot.CbLootEvents;

/**
 * Fabric API 1.20.4's loot table events on 1.20.5's loot loading (compat/fapi/impl/loot/CbLootEvents):
 * the reload's resource manager at ReloadableRegistries.reload, and the events at apply(registries, list),
 * where every loot registry of the reload has been read and none is frozen yet -- the list apply goes on with
 * holds the loot table registry rebuilt from the events' results.
 */
@Mixin(net.minecraft.class_9383.class)
public abstract class CbFapiLootEventsMixin {

    @Inject(method = "method_58284(Lnet/minecraft/class_7780;Lnet/minecraft/class_3300;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"))
    private static void cb$lootReload(net.minecraft.class_7780<?> registries, net.minecraft.class_3300 resources, Executor executor,
                                      CallbackInfoReturnable<CompletableFuture<?>> cir) {
        CbLootEvents.reloadStarted(resources);
    }

    @ModifyVariable(method = "method_58285(Lnet/minecraft/class_7780;Ljava/util/List;)Lnet/minecraft/class_7780;",
                    at = @At("HEAD"), argsOnly = true)
    @SuppressWarnings("rawtypes")
    private static List cb$legacyLootEvents(List registries) {
        return CbLootEvents.apply(registries);
    }
}
