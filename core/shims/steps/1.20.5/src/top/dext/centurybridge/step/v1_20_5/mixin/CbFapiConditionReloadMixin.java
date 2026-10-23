package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.resource.conditions.CbLegacyConditions;

/**
 * Fabric API 1.20.4's reload context for the 1.20.4 resource conditions this layer gives back, set and
 * cleared where 1.20.4's DataPackContentsMixin did: the reload's registries at DataPackContents.reload
 * (1.20.4's CURRENT_REGISTRIES, which its registry_contains read), and both those and the captured tags
 * dropped at refresh, the reload's end.
 */
@Mixin(net.minecraft.class_5350.class)
public abstract class CbFapiConditionReloadMixin {

    @Inject(method = "method_29466(Lnet/minecraft/class_3300;Lnet/minecraft/class_7780;Lnet/minecraft/class_7699;Lnet/minecraft/class_2170$class_5364;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"))
    private static void cb$conditionsReload(net.minecraft.class_3300 manager,
                                            net.minecraft.class_7780<net.minecraft.class_7659> registries,
                                            net.minecraft.class_7699 features, net.minecraft.class_2170.class_5364 environment,
                                            int functionPermissionLevel, Executor prepareExecutor, Executor applyExecutor,
                                            CallbackInfoReturnable<CompletableFuture<net.minecraft.class_5350>> cir) {
        CbLegacyConditions.reloadStarted(registries.method_45926());
    }

    @Inject(method = "method_40421()V", at = @At("HEAD"))
    private void cb$conditionsRefresh(CallbackInfo ci) {
        CbLegacyConditions.reloadEnded();
    }
}
