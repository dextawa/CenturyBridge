package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelLoading;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * BlockStateModelLoader's 1.21.3 instance API: it was built with the missing model and loaded a block's definitions with
 * it; 1.21.4 does so in a private static taking the missing model. The constructor's model is kept (superCtorReshapes post).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_9824.class)
public abstract class CbModelStateLoaderMixin implements CbModelLoading.CbLegacyStateLoader {

    @Shadow
    private static net.minecraft.class_9824.class_10095 method_62627(net.minecraft.class_2960 id,
            net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, List<?> definitions,
            net.minecraft.class_1100 missingModel) {
        throw new AssertionError();
    }

    private net.minecraft.class_1100 cb$missingModel;

    @Override
    public void cb$setMissingModel(net.minecraft.class_1100 missingModel) {
        this.cb$missingModel = missingModel;
    }

    /**
     * was loadBlockStateDefinitions(id, stateDefinition, definitions). 1.21.4's loader enters it for every block's
     * definitions ({@link #cb$loadThroughRestored}) and takes its answer; its body is 1.21.4's (the static it became), not
     * 1.21.3's: 1.21.3 gave every variant no definition names the missing model (with a warning), 1.21.4 leaves such
     * variants out and its BlockStateModel holder type takes no plain UnbakedModel, so 1.21.3's body cannot run here
     * without changing what 1.21.4 loads. A 1.21.3 mod's injector at its HEAD / RETURN changes vanilla's definitions as it
     * did; one anchored inside 1.21.3's body does not (MixinJudge: "vanilla path partial").
     */
    @CbVanillaReads(value = "CbModelStateLoaderMixin: 1.21.4's loader (method_65720) loads every block's definitions through it",
        carries = {"Inject@HEAD", "Inject@RETURN"})
    public net.minecraft.class_9824.class_10095 method_62627(net.minecraft.class_2960 id,
            net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, List<?> definitions) {
        return method_62627(id, definition, definitions, this.cb$missingModel);
    }

    /**
     * 1.21.4's loader, per block: load the definitions through the restored 1.21.3 member, on a loader that holds the
     * missing model 1.21.4 passes (1.21.3 built its loader with it). With no mod mixin on it the answer is the static's,
     * with the same arguments: vanilla is unchanged. A fresh loader per call: 1.21.4's loading is static and parallel.
     */
    @Redirect(method = "method_65720", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9824;method_62627(Lnet/minecraft/class_2960;Lnet/minecraft/class_2689;Ljava/util/List;Lnet/minecraft/class_1100;)Lnet/minecraft/class_9824$class_10095;"))
    private static net.minecraft.class_9824.class_10095 cb$loadThroughRestored(net.minecraft.class_2960 id,
            net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, List<?> definitions,
            net.minecraft.class_1100 missingModel) {
        CbModelLoading.CbLegacyStateLoader loader = (CbModelLoading.CbLegacyStateLoader) (Object) new net.minecraft.class_9824();
        loader.cb$setMissingModel(missingModel);
        return loader.method_62627(id, definition, definitions);
    }
}
