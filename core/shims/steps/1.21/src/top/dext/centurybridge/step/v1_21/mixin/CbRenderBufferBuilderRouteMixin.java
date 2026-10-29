package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderRenderedBuffer;
import top.dext.centurybridge.step.v1_21.rt.CbRenderBufferBuilderV;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * The routes that go with {@link CbRenderBufferBuilderMixin}'s restored 1.20.6 batch lifecycle, each taken only when a
 * converted mod mixes the restored member or one of the restored 1.20.6 members it calls (CbMixedMembers); unmixed,
 * 1.21's path runs untouched.
 * <ul>
 * <li>begin: every 1.20.6 batch began with begin(mode, format) on the Tesselator's / RenderType's / section's builder;
 *     1.21 makes a new builder for the batch instead (Tesselator.begin, BufferSource.getBuffer, SectionCompiler), its
 *     constructor doing begin's setup. At the constructor's RETURN the builder is set idle and begun again through the
 *     restored begin with the same mode and format, which sets up the same fields to the same values. A mod handler
 *     that cancels begin cannot leave 1.21's caller without its builder: the builder is building again afterwards
 *     (cancellation not carried).</li>
 * <li>end: 1.21's build() (and buildOrThrow(), which calls it) at the sites where 1.20.6 called end() (Tesselator.end,
 *     RenderType.end, ...) answers the restored end()'s mesh; the restored end calls build() itself (cb$storing), and that
 *     inner call is 1.21's own, so a native 1.21 mod's injectors in build() run once per batch. Not at the section
 *     compile (CbRenderSectionCompileRouteMixin), where 1.20.6 called endOrDiscardIfEmpty.</li>
 * </ul>
 * Priority CbVanillaReads.ROUTE_FIRST: the end route's HEAD goes before every mod's HEAD injector in build(), so a native
 * mod's HEAD runs in the inner call only.
 */
@Mixin(value = net.minecraft.class_287.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbRenderBufferBuilderRouteMixin {

    /** building */
    @Shadow
    private boolean field_1556;

    @Inject(method = "<init>(Lnet/minecraft/class_9799;Lnet/minecraft/class_293$class_5596;Lnet/minecraft/class_293;)V", at = @At("RETURN"))
    private void cb$v1_21$begun(net.minecraft.class_9799 buffer, net.minecraft.class_293.class_5596 mode, net.minecraft.class_293 format,
                                CallbackInfo ci) {
        if (CbRenderBufferBuilderV.cbV1_21AnyMixed(CbRenderBufferBuilderV.CB_V1_21_BEGIN)) {
            this.field_1556 = false;
            try {
                ((CbRenderBufferBuilderV) this).method_1328(mode, format);
            } finally {
                this.field_1556 = true;
            }
        }
    }

    @Inject(method = "method_60794()Lnet/minecraft/class_9801;", at = @At("HEAD"), cancellable = true)
    private void cb$v1_21$build(CallbackInfoReturnable<net.minecraft.class_9801> cir) {
        CbRenderBufferBuilderV self = (CbRenderBufferBuilderV) this;
        if (self.cb$v1_21$storing() || CbRenderBufferBuilderV.CB_V1_21_NOT_END.get()[0]
                || !CbRenderBufferBuilderV.cbV1_21AnyMixed(CbRenderBufferBuilderV.CB_V1_21_END)) {
            return;
        }
        BufferBuilderRenderedBuffer rendered = self.method_1326();
        cir.setReturnValue(rendered.cbMesh());
    }
}
