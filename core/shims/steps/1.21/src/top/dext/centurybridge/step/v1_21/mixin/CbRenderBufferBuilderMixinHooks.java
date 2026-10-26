package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbRenderVertex;

/**
 * The injectors into 1.21's own BufferBuilder methods that go with {@link CbRenderBufferBuilderMixin} (its restored
 * 1.20.6 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.6 injectors can anchor inside them):
 * 1.21's calls under a 1.20.6 default color (every vertex takes it, an explicit color is refused), and a mesh about to
 * be built first ends the vertices 1.21 code left open on 1.20.6 consumers.
 */
@Mixin(net.minecraft.class_287.class)
public abstract class CbRenderBufferBuilderMixinHooks {

    @Inject(method = "method_22912(FFF)Lnet/minecraft/class_4588;", at = @At("RETURN"))
    private void cb$vertexBegun(float x, float y, float z, CallbackInfoReturnable<net.minecraft.class_4588> cir) {
        CbPrioritySplit.BufferDefaultColor color = (CbPrioritySplit.BufferDefaultColor) (Object) this;
        if (color.cb$v1_21$defaultColorSet()) {
            color.cb$v1_21$fillDefaultColor();
        }
    }

    @Inject(method = "method_1336(IIII)Lnet/minecraft/class_4588;", at = @At("HEAD"))
    private void cb$colorUnderDefault(int r, int g, int b, int a, CallbackInfoReturnable<net.minecraft.class_4588> cir) {
        CbPrioritySplit.BufferDefaultColor color = (CbPrioritySplit.BufferDefaultColor) (Object) this;
        if (color.cb$v1_21$defaultColorSet() && !color.cb$v1_21$fillingDefault()) {
            throw new IllegalStateException();
        }
    }

    @Inject(method = "method_39415(I)Lnet/minecraft/class_4588;", at = @At("HEAD"))
    private void cb$packedColorUnderDefault(int color, CallbackInfoReturnable<net.minecraft.class_4588> cir) {
        if (((CbPrioritySplit.BufferDefaultColor) (Object) this).cb$v1_21$defaultColorSet()) {
            throw new IllegalStateException();
        }
    }

    @Inject(method = "method_23919(FFFIFFIIFFF)V", at = @At("HEAD"))
    private void cb$wholeVertexUnderDefault(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx,
                                            float ny, float nz, CallbackInfo ci) {
        if (((CbPrioritySplit.BufferDefaultColor) (Object) this).cb$v1_21$defaultColorSet()) {
            throw new IllegalStateException();
        }
    }

    /** a mesh is about to be built: the vertices 1.21 code left open on 1.20.6 consumers end first */
    @Inject(method = "method_60794()Lnet/minecraft/class_9801;", at = @At("HEAD"))
    private void cb$building(CallbackInfoReturnable<net.minecraft.class_9801> cir) {
        CbRenderVertex.cbEndAllPendingVertices();
    }
}
