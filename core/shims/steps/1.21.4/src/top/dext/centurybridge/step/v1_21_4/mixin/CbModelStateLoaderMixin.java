package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelLoading;

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

    /** was loadBlockStateDefinitions(id, stateDefinition, definitions) */
    public net.minecraft.class_9824.class_10095 method_62627(net.minecraft.class_2960 id,
            net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, List<?> definitions) {
        return method_62627(id, definition, definitions, this.cb$missingModel);
    }
}
