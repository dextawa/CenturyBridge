package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 1.21.1 DimensionSpecialEffects.getSunriseColor(timeOfDay, partialTick): 1.21.1's body -- the sunrise / sunset
 * colour in the effects' own array, or null outside dawn and dusk. (1.21.2 answers the same colour packed, through
 * isSunriseOrSunset / getSunriseOrSunsetColor.)
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_5294.class)
public abstract class CbRenderSkyMixin {

    @Unique
    private final float[] cb$sunriseCol = new float[4];

    public float[] method_28109(float timeOfDay, float partialTick) {
        float g = net.minecraft.class_3532.method_15362(timeOfDay * 6.2831855F) - 0.0F;
        if (g >= -0.4F && g <= 0.4F) {
            float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
            float j = 1.0F - (1.0F - net.minecraft.class_3532.method_15374(i * 3.1415927F)) * 0.99F;
            j *= j;
            this.cb$sunriseCol[0] = i * 0.3F + 0.7F;
            this.cb$sunriseCol[1] = i * i * 0.7F + 0.2F;
            this.cb$sunriseCol[2] = i * i * 0.0F + 0.2F;
            this.cb$sunriseCol[3] = j;
            return this.cb$sunriseCol;
        }
        return null;
    }
}
