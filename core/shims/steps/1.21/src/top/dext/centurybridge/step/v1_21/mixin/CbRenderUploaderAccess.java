package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * BufferUploader.bindImmediateBuffer(format): the format's immediate-draw VertexBuffer, bound unless it already
 * is -- the buffer 1.20.6's BufferUploader uploaded a RenderedBuffer into (CbRenderBuffers.cbUpload).
 */
@Mixin(net.minecraft.class_286.class)
public interface CbRenderUploaderAccess {

    @Invoker("method_43435")
    static net.minecraft.class_291 cb$bindImmediateBuffer(net.minecraft.class_293 format) {
        throw new AssertionError();
    }
}
