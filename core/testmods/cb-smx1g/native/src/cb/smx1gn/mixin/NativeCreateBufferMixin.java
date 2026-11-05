package cb.smx1gn.mixin;

import cb.smx1gn.Probe;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 26.2 GpuDevice.createBuffer RETURN: the buffers the 1.21.6 layer makes for legacy uniform arrays (its labels) */
@Mixin(GpuDevice.class)
public class NativeCreateBufferMixin {
    private static boolean cbSmx1gn$legacy(Supplier<String> label) {
        String s = label == null ? null : label.get();
        return s != null && s.startsWith("CenturyBridge 1.21.5");
    }

    @Inject(method = "createBuffer(Ljava/util/function/Supplier;ILjava/nio/ByteBuffer;)Lcom/mojang/blaze3d/buffers/GpuBuffer;", at = @At("RETURN"))
    private void cbSmx1gn$data(Supplier<String> label, int usage, ByteBuffer data, CallbackInfoReturnable<GpuBuffer> cir) {
        if (cbSmx1gn$legacy(label)) {
            Probe.created(cir.getReturnValue());
        }
    }

    @Inject(method = "createBuffer(Ljava/util/function/Supplier;IJ)Lcom/mojang/blaze3d/buffers/GpuBuffer;", at = @At("RETURN"))
    private void cbSmx1gn$size(Supplier<String> label, int usage, long size, CallbackInfoReturnable<GpuBuffer> cir) {
        if (cbSmx1gn$legacy(label)) {
            Probe.created(cir.getReturnValue());
        }
    }
}
