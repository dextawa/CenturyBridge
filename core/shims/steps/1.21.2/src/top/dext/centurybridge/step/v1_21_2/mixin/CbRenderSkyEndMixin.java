package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 DimensionSpecialEffects.EndEffects.getSunriseColor: null (the End has no sunrise) */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_5294.class_5295.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderSkyEndMixin {

    public float[] method_28109(float timeOfDay, float partialTick) {
        return null;
    }
}
