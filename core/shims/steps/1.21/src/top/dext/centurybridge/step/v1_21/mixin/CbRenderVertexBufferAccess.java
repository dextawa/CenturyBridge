package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * VertexBuffer's own upload steps (vertex data with the format's buffer state, index data or the shared
 * sequential index buffer) and the draw parameters an upload sets: 1.20.6's VertexBuffer.upload(RenderedBuffer)
 * is those steps on the batch's draw state (CbRenderBuffers.cbUploadTo), which 1.21 keeps private.
 */
@Mixin(net.minecraft.class_291.class)
public interface CbRenderVertexBufferAccess {

    /** uploadVertexBuffer(drawState, vertices) */
    @Invoker("method_43442")
    net.minecraft.class_293 cb$uploadVertexBuffer(net.minecraft.class_9801.class_4574 drawState, java.nio.ByteBuffer vertices);

    /** uploadIndexBuffer(drawState, indices): null indices bind the sequential buffer */
    @Invoker("method_43443")
    com.mojang.blaze3d.systems.RenderSystem.class_5590 cb$uploadIndexBuffer(net.minecraft.class_9801.class_4574 drawState,
                                                                            java.nio.ByteBuffer indices);

    /** format */
    @Accessor("field_29339")
    void cb$setFormat(net.minecraft.class_293 format);

    /** sequentialIndices */
    @Accessor("field_38983")
    void cb$setSequentialIndices(com.mojang.blaze3d.systems.RenderSystem.class_5590 sequential);

    /** indexCount */
    @Accessor("field_1593")
    void cb$setIndexCount(int indexCount);

    /** indexType */
    @Accessor("field_27367")
    void cb$setIndexType(net.minecraft.class_293.class_5595 indexType);

    /** mode */
    @Accessor("field_27368")
    void cb$setMode(net.minecraft.class_293.class_5596 mode);
}
