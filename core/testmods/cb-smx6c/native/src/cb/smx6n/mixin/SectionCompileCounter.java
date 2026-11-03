package cb.smx6n.mixin;

import cb.smx6n.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21 control: SectionCompiler.compile / handleBlockEntity HEAD (where 1.20.6's RebuildTask code runs on 1.21) */
@Mixin(net.minecraft.class_9810.class)
public abstract class SectionCompileCounter {
    @Inject(method = "method_60904", at = @At("HEAD"))
    private void cb$compile(CallbackInfoReturnable<?> cir) {
        Driver.SC_COMPILE.incrementAndGet();
    }

    @Inject(method = "method_60902", at = @At("HEAD"))
    private void cb$blockEntity(CallbackInfo ci) {
        Driver.SC_BLOCK_ENTITY.incrementAndGet();
    }
}
