package cb.smx6n.mixin;

import cb.smx6n.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21's BufferBuilder lifecycle as a native mod sees it: constructions, build() HEAD and RETURN */
@Mixin(net.minecraft.class_287.class)
public abstract class BufferBuildCounter {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$made(CallbackInfo ci) {
        Driver.BUILDERS.incrementAndGet();
    }

    @Inject(method = "method_60794", at = @At("HEAD"))
    private void cb$buildHead(CallbackInfoReturnable<?> cir) {
        Driver.BUILD_HEAD.incrementAndGet();
    }

    @Inject(method = "method_60794", at = @At("RETURN"))
    private void cb$buildReturn(CallbackInfoReturnable<?> cir) {
        Driver.BUILD_RETURN.incrementAndGet();
    }
}
