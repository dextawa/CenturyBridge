package cb.smx4c.mixin;

import cb.smx4c.Counters;
import net.minecraft.class_1043;
import net.minecraft.class_1059;
import net.minecraft.class_3300;
import net.minecraft.class_380;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.3's DynamicTexture / TextureAtlas / FontTexture .load(ResourceManager) (empty bodies): HEAD counts per class. */
@Mixin({class_1043.class, class_1059.class, class_380.class})
public abstract class TextureLoadMixin {
    @Inject(method = "method_4625", at = @At("HEAD"))
    private void cb$load(class_3300 manager, CallbackInfo ci) {
        Counters.LOADS.merge(this.getClass().getName(), 1, Integer::sum);
    }
}
