package top.dext.centurybridge.step.v1_21_2.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_5455;
import net.minecraft.class_6860;
import net.minecraft.class_7225;
import net.minecraft.class_7237;
import net.minecraft.class_7712;
import net.minecraft.class_7780;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/**
 * WorldLoader.load, as it hands the world-data supplier its DataLoadContext: the frozen access 1.21.1 put in
 * that context as datapackWorldgen (the static layer and the loaded worldgen registries, the access for
 * loading dimensions) is recorded against the lookup provider 1.21.2 puts there instead (CbRestServer).
 * Locals as in 1.21.2's bytecode: 5 the pack pair, 6 the resource manager, 7 the layered access, 8 the static
 * tags, 9 the worldgen access for loading, 10 the updated lookups, 11 the loaded worldgen registries, 12 the
 * lookups for dimensions, 13 the dimensions, 14 the data configuration, 15 the provider.
 */
@Mixin(class_7237.class)
public abstract class CbRestWorldLoaderMixin {

    @Inject(method = "method_42098",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_7237$class_6907;get(Lnet/minecraft/class_7237$class_7660;)Lnet/minecraft/class_7237$class_7661;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    @SuppressWarnings("rawtypes")
    private static void cb$worldgenAccess(class_7237.class_6906 packs, class_7237.class_6907 supplier, class_7237.class_7239 factory,
                                         Executor background, Executor main, CallbackInfoReturnable<CompletableFuture> cir,
                                         Pair pair, class_6860 resources, class_7780 layers, List staticTags, class_5455.class_6890 worldgenLoading,
                                         List updated, class_5455.class_6890 worldgen, List lookups, class_5455.class_6890 dimensions,
                                         class_7712 config, class_7225.class_7874 provider) {
        CbRestServer.recordWorldgenAccess(provider, layers, worldgen);
    }
}
