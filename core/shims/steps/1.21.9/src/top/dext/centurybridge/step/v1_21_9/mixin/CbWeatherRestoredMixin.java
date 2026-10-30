package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_12077;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_4597;
import net.minecraft.class_9976;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * WeatherEffectRenderer.render(level, buffers, ticks, partialTick, cameraPos), which 1.21.9 split into extract
 * (method_62315: rain level, column radius, the rain and snow columns, into a WeatherRenderState) and render
 * (method_62320: draws the non-empty column lists). 1.21.8's body did the same two halves on local lists (returning
 * when the rain level is not above 0, where extract leaves both lists empty and render draws nothing). 1.21.9 never
 * calls it.
 */
@Mixin(value = class_9976.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWeatherRestoredMixin {

    /** WeatherEffectRenderer.render(level, buffers, ticks, partialTick, cameraPos) */
    public void method_62316(class_1937 level, class_4597 buffers, int ticks, float partialTick, class_243 cameraPos) {
        class_12077 state = new class_12077();
        class_9976 self = (class_9976) (Object) this;
        self.method_62315(level, ticks, partialTick, cameraPos, state);
        self.method_62320(buffers, cameraPos, state);
    }
}
