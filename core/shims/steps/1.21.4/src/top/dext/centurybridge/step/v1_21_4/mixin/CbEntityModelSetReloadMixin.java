package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 1.21.3's EntityModelSet was a resource reload listener: built empty, it took LayerDefinitions.createRoots() in its
 * onResourceManagerReload. 1.21.4's ModelManager reload makes it with EntityModelSet.vanilla() (new set of
 * ImmutableMap.copyOf(createRoots())); here it is made as 1.21.3 made it, an empty set given the reload's resource manager
 * through the restored onResourceManagerReload (CbRenderEntityModelSetMixin), on the executor and at the point 1.21.4
 * makes it. With no mod mixin the set holds the same roots: vanilla is unchanged.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1092.class)
public abstract class CbEntityModelSetReloadMixin {

    @Redirect(method = "method_25931", at = @At(value = "INVOKE", ordinal = 0,
            target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<?> cb$entityModelSetThroughReload(Supplier<?> vanilla, Executor executor, net.minecraft.class_3302.class_4045 barrier,
            net.minecraft.class_3300 resourceManager, Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            net.minecraft.class_5599 set = new net.minecraft.class_5599(com.google.common.collect.ImmutableMap.of());
            ((net.minecraft.class_4013) (Object) set).method_14491(resourceManager);
            return set;
        }, executor);
    }
}
