package top.dext.centurybridge.step.v1_21.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderRenderedBuffer;
import top.dext.centurybridge.step.v1_21.rt.CbRenderBufferBuilderV;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * 1.20.6's section compile (RebuildTask.compile) finished each layer's builder with endOrDiscardIfEmpty(); 1.21's
 * SectionCompiler.compile (class_9810.method_60904) calls build(), null for an empty layer, at the same point. When a
 * converted mod mixes the restored endOrDiscardIfEmpty or a restored member it calls (CbMixedMembers), that call answers the restored member's mesh
 * (null when it discarded the batch); otherwise 1.21's build() runs, with the end route of
 * CbRenderBufferBuilderRouteMixin standing aside (1.20.6 never called end() here). Priority CbVanillaReads.ROUTE_FIRST:
 * the innermost wrapper, so a native 1.21 mod's @WrapOperation of the call wraps the route and runs either way.
 */
@Mixin(value = net.minecraft.class_9810.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbRenderSectionCompileRouteMixin {

    @WrapOperation(method = "method_60904", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_287;method_60794()Lnet/minecraft/class_9801;"))
    private net.minecraft.class_9801 cb$v1_21$layerBuilt(net.minecraft.class_287 builder, Operation<net.minecraft.class_9801> original) {
        if (CbRenderBufferBuilderV.cbV1_21AnyMixed(CbRenderBufferBuilderV.CB_V1_21_END_OR_DISCARD)) {
            BufferBuilderRenderedBuffer rendered = ((CbRenderBufferBuilderV) builder).method_43575();
            return rendered == null ? null : rendered.cbMesh();
        }
        boolean[] notEnd = CbRenderBufferBuilderV.CB_V1_21_NOT_END.get();
        boolean was = notEnd[0];
        notEnd[0] = true;
        try {
            return original.call(builder);
        } finally {
            notEnd[0] = was;
        }
    }
}
