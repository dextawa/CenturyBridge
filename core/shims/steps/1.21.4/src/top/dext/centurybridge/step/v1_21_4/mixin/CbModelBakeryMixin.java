package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelLoading;
import top.dext.centurybridge.step.v1_21_4.rt.CbVanillaReads;

/**
 * ModelBakery's 1.21.3 bakeModels(textureGetter) (void) and getBakedTopLevelModels(): 1.21.4's bakeModels answers a
 * BakingResult; its baked block state models are what 1.21.3 kept as the baked top models.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_1088.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelBakeryMixin implements CbModelLoading.CbLegacyBakery {

    private Map<net.minecraft.class_1091, net.minecraft.class_1087> cb$bakedTop = new java.util.HashMap<>();

    /** was bakeModels(textureGetter) */
    @Override
    public void cb$bakeModels(net.minecraft.class_1088.class_9826 textures) {
        this.cb$bakedTop = CbModelLoading.bakeTop((net.minecraft.class_1088) (Object) this, textures);
    }

    /** was getBakedTopLevelModels() */
    @Override
    public Map<net.minecraft.class_1091, net.minecraft.class_1087> method_4734() {
        return this.cb$bakedTop;
    }
}
