package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import com.mojang.blaze3d.buffers.GpuBuffer;
import net.minecraft.class_10865;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native control: 1.21.11 GlDevice.createBuffer(Supplier, int, long) HEAD, counted on the render thread */
@Mixin(class_10865.class)
public class NativeCreateBufferMixin {
    @Inject(method = "createBuffer(Ljava/util/function/Supplier;IJ)Lcom/mojang/blaze3d/buffers/GpuBuffer;", at = @At("HEAD"))
    private void cbSmx1dn$create(java.util.function.Supplier<String> label, int usage, long size, CallbackInfoReturnable<GpuBuffer> cir) {
        if (label != null && "cb_smx1d_buf".equals(label.get())) {
            Probe.bumpRender("createBuffer");
            System.setProperty("cbsmx1d.lastBufferSize", String.valueOf(size));
        }
    }
}
