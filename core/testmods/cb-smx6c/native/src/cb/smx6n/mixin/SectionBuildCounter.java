package cb.smx6n.mixin;

import cb.smx6n.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21's section compile: each layer builder's build() call in SectionCompiler.compile */
@Mixin(net.minecraft.class_9810.class)
public abstract class SectionBuildCounter {
    @Inject(method = "method_60904", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_287;method_60794()Lnet/minecraft/class_9801;"))
    private void cb$layerBuild(CallbackInfoReturnable<?> cir) {
        Driver.SECTION_BUILDS.incrementAndGet();
    }
}
