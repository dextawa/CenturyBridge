package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.dext.centurybridge.compat.fapi.impl.datagen.CbFapiTagBuilders;

/**
 * A generated tag file lists its builder's removals under "fabric:remove" (1.20.1's datagen
 * TagProviderMixin). Injected where the file is written, with the locals 1.20.2's Fabric API itself
 * captures at that call to add "replace" -- so the JSON written carries both.
 */
@Mixin(net.minecraft.class_2474.class)
abstract class CbFapiTagProviderRemoval {

    @Inject(method = "method_27046", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2405;method_10320(Lnet/minecraft/class_7403;Lcom/google/gson/JsonElement;Ljava/nio/file/Path;)Ljava/util/concurrent/CompletableFuture;"),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void cb$addRemove(Predicate<net.minecraft.class_2960> predicate, Predicate<net.minecraft.class_2960> predicate2,
                              net.minecraft.class_7403 writer, Map.Entry<net.minecraft.class_2960, net.minecraft.class_3495> entry,
                              CallbackInfoReturnable<CompletableFuture<?>> cir, net.minecraft.class_2960 id, net.minecraft.class_3495 builder,
                              List<net.minecraft.class_3497> list, List<net.minecraft.class_3497> list2, JsonElement json, Path path) {
        CbFapiTagBuilders.writeRemovals(json, builder);
    }
}
