package cb.smx1g.mixin;

import cb.smx1g.Counter;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import net.minecraft.class_10866;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.5 GlRenderPass.uniforms held the caller's int[] / float[] (javap: HashMap&lt;String, Object&gt; field_57873). One put per
 * pass (a second put under the key would close the replaced buffer outside pass close), first 64 passes.
 */
@Mixin(class_10866.class)
public abstract class LegacyUniformMixin {
    private static final String KEY = "CbSmx1gLegacy";

    @Shadow
    @Final
    protected HashMap<String, Object> field_57873;

    @Inject(method = "setPipeline", at = @At("HEAD"))
    private void cbSmx1g$legacyUniform(RenderPipeline pipeline, CallbackInfo ci) {
        if (Counter.full() || field_57873.containsKey(KEY)) {
            return;
        }
        float[] value = {1f, 2f, 3f, 4f};
        field_57873.put(KEY, value);
        boolean same = field_57873.get(KEY) == value;
        value[0] = 5f;
        Counter.put(same);
    }
}
