package cb.smx6c.mixin;

import cb.smx6c.Counters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * smixin18: 1.20.6 RebuildTask's section compile, which 1.21 moved into SectionCompiler (step wiring smixin18-sc.json
 * mixinMoves): compile (method_22785) HEAD and handleBlockEntity (method_23087) HEAD, handlers that never read this
 * (moved onto SectionCompiler.compile / handleBlockEntity); doTask's call of compile (anchor respelled to the
 * SectionCompiler.compile call); and the negative case, a compile HEAD handler reading this (another object on 1.21:
 * stays disabled, its counter stays 0).
 */
@Mixin(targets = "net.minecraft.class_846$class_851$class_4578")
public abstract class RebuildTaskMoveMixin {

    @Inject(method = "method_22785(FFFLnet/minecraft/class_750;)Lnet/minecraft/class_846$class_851$class_4578$class_7435;", at = @At("HEAD"))
    private void cb$compile(CallbackInfoReturnable<?> cir) {
        Counters.COMPILE.incrementAndGet();
    }

    @Inject(method = "method_22785(FFFLnet/minecraft/class_750;)Lnet/minecraft/class_846$class_851$class_4578$class_7435;", at = @At("HEAD"))
    private void cb$compileThis(CallbackInfoReturnable<?> cir) {
        if (this.hashCode() != 0) Counters.COMPILE_THIS.incrementAndGet();
    }

    @Inject(method = "method_23087(Lnet/minecraft/class_846$class_851$class_4578$class_7435;Lnet/minecraft/class_2586;)V", at = @At("HEAD"))
    private void cb$blockEntity(CallbackInfo ci) {
        Counters.BLOCK_ENTITY.incrementAndGet();
    }

    @Inject(method = "method_22783(Lnet/minecraft/class_750;)Ljava/util/concurrent/CompletableFuture;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_846$class_851$class_4578;method_22785(FFFLnet/minecraft/class_750;)Lnet/minecraft/class_846$class_851$class_4578$class_7435;"))
    private void cb$compileCall(CallbackInfoReturnable<?> cir) {
        Counters.COMPILE_CALL.incrementAndGet();
    }
}
