package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbRenderBufferBuilderV;
import top.dext.centurybridge.step.v1_21.rt.CbRenderBuffers;

/**
 * Tesselator's 1.20.6 builder: 1.20.6's Tesselator owned one BufferBuilder as large as its buffer, handed out by
 * getBuilder() and drawn by end() (BufferUploader.drawWithShader(builder.end())). 1.21's begins a new builder
 * over its ByteBufferBuilder for every batch. The 1.20.6 builder is a builder of its own (CbRenderBuffers.cbNewBuilder,
 * idle until begun), made on first use with the Tesselator's capacity, so 1.20.6 and 1.21 batches of the one
 * Tesselator never write into each other's memory.
 */
@Mixin(net.minecraft.class_289.class)
public abstract class CbRenderTesselatorMixin {

    @Shadow
    @Final
    private net.minecraft.class_9799 field_52098;

    @Unique
    private net.minecraft.class_287 cb$builder;

    /** getBuilder() */
    public net.minecraft.class_287 method_1349() {
        if (this.cb$builder == null) {
            this.cb$builder = CbRenderBuffers.cbNewBuilder(((CbRenderByteBufferBuilderAccess) (Object) this.field_52098).cb$capacity());
        }
        return this.cb$builder;
    }

    /** end(): the builder's batch, drawn with the current shader */
    public void method_1350() {
        CbRenderBuffers.cbDrawWithShader(((CbRenderBufferBuilderV) (Object) this.method_1349()).method_1326());
    }
}
