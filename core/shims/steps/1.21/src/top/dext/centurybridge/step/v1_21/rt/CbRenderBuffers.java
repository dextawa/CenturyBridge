package top.dext.centurybridge.step.v1_21.rt;

import java.util.Map;
import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderDrawState;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderRenderedBuffer;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderUploaderAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderVertexBufferAccess;

/**
 * 1.20.6's long-lived BufferBuilders and RenderedBuffers among 1.21's ByteBufferBuilders and MeshData: a 1.20.6
 * builder is a 1.21 BufferBuilder over a ByteBufferBuilder of its own, idle until begun (CbRenderBufferBuilderMixin);
 * where 1.21 takes the memory itself (MultiBufferSource, SectionBufferBuilderPack) a 1.20.6 builder stands for
 * its ByteBufferBuilder and back; a RenderedBuffer is drawn and uploaded with 1.20.6's steps (BufferUploader,
 * VertexBuffer.upload), which 1.21 still has, private, for MeshData.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBuffers {

    private CbRenderBuffers() {
    }

    // ------------------------------------------------------------------ builders

    /** new BufferBuilder(capacity) of 1.20.6: a builder of its own, as large, idle until begun */
    public static net.minecraft.class_287 cbNewBuilder(int capacity) {
        return cbIdleBuilder(new net.minecraft.class_9799(capacity));
    }

    /** an idle builder over the ByteBufferBuilder (1.21's constructor wants a mode and a format; begin() sets both) */
    private static net.minecraft.class_287 cbIdleBuilder(net.minecraft.class_9799 bytes) {
        net.minecraft.class_287 builder = new net.minecraft.class_287(bytes, net.minecraft.class_293.class_5596.field_27382,
            net.minecraft.class_290.field_1592);
        ((CbRenderBufferBuilderV) (Object) builder).cb$idle();
        return builder;
    }

    /** the super(capacity) of a 1.20.6 BufferBuilder subclass: its own ByteBufferBuilder */
    public static net.minecraft.class_9799 cbNewBytes(int capacity) {
        return new net.minecraft.class_9799(capacity);
    }

    /** the mode a 1.20.6 BufferBuilder subclass is constructed with (begin() sets the real one) */
    public static net.minecraft.class_293.class_5596 cbIdleMode() {
        return net.minecraft.class_293.class_5596.field_27382;
    }

    /** the format a 1.20.6 BufferBuilder subclass is constructed with (begin() sets the real one) */
    public static net.minecraft.class_293 cbIdleFormat() {
        return net.minecraft.class_290.field_1592;
    }

    /** a 1.20.6 BufferBuilder subclass, constructed: idle until begun, as 1.20.6's constructor left it */
    public static void cbBuilt(net.minecraft.class_287 builder) {
        ((CbRenderBufferBuilderV) (Object) builder).cb$idle();
    }

    /** the ByteBufferBuilder a builder writes into (what 1.21 takes where 1.20.6 took the builder) */
    public static net.minecraft.class_9799 cbBytesOf(net.minecraft.class_287 builder) {
        return ((CbRenderBufferBuilderV) (Object) builder).cb$byteBuffer();
    }

    private static final Map<net.minecraft.class_9799, net.minecraft.class_287> VIEWS = new MapMaker().weakKeys().makeMap();

    /**
     * The 1.20.6 builder that stands for a ByteBufferBuilder 1.21 hands out where 1.20.6 handed out a builder
     * (SectionBufferBuilderPack.builder(layer)): the same one every time, idle until begun, writing into it.
     */
    public static net.minecraft.class_287 cbBuilderOf(net.minecraft.class_9799 bytes) {
        return bytes == null ? null : VIEWS.computeIfAbsent(bytes, CbRenderBuffers::cbIdleBuilder);
    }

    // ------------------------------------------------------------------ drawing and uploading a RenderedBuffer

    /** BufferUploader.drawWithShader(rendered) of 1.20.6: on the render thread, or recorded for it */
    public static void cbDrawWithShader(BufferBuilderRenderedBuffer rendered) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> cbDrawWithShaderNow(rendered));
        } else {
            cbDrawWithShaderNow(rendered);
        }
    }

    private static void cbDrawWithShaderNow(BufferBuilderRenderedBuffer rendered) {
        net.minecraft.class_291 buffer = cbUpload(rendered);
        if (buffer != null) {
            buffer.method_34427(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }
    }

    /** BufferUploader.draw(rendered) of 1.20.6: uploaded into the format's immediate buffer, drawn */
    public static void cbDraw(BufferBuilderRenderedBuffer rendered) {
        net.minecraft.class_291 buffer = cbUpload(rendered);
        if (buffer != null) {
            buffer.method_35665();
        }
    }

    /** BufferUploader.upload of 1.20.6: an empty batch is released, any other goes into its format's immediate buffer */
    private static net.minecraft.class_291 cbUpload(BufferBuilderRenderedBuffer rendered) {
        RenderSystem.assertOnRenderThread();
        if (rendered.method_43584()) {
            rendered.method_43585();
            return null;
        }
        net.minecraft.class_291 buffer = CbRenderUploaderAccess.cb$bindImmediateBuffer(rendered.method_43583().comp_749);
        cbUploadTo(buffer, rendered);
        return buffer;
    }

    /** VertexBuffer.upload(RenderedBuffer) of 1.20.6, as a VertexBuffer subclass of 1.20.6 declares it */
    public interface CbLegacyUpload {
        void method_1352(BufferBuilderRenderedBuffer rendered);
    }

    /** vertexBuffer.upload(rendered), called 1.20.6's way: a 1.20.6 subclass's own upload, else 1.20.6's */
    public static void cbUploadTo(net.minecraft.class_291 buffer, BufferBuilderRenderedBuffer rendered) {
        if (buffer instanceof CbLegacyUpload upload) {
            upload.method_1352(rendered);
            return;
        }
        cbUploadToBody(buffer, rendered);
    }

    /**
     * VertexBuffer.upload(rendered) of 1.20.6 (a subclass's super.upload): (an invalid buffer only releases the
     * batch) the vertices unless the batch is index-only, with the format's buffer state; the indices, or the
     * shared sequential buffer when there are none; the draw parameters; and the batch released however that went.
     */
    public static void cbUploadToBody(net.minecraft.class_291 buffer, BufferBuilderRenderedBuffer rendered) {
        if (buffer.method_43444()) {
            rendered.method_43585();
            return;
        }
        RenderSystem.assertOnRenderThread();
        try {
            BufferBuilderDrawState state = rendered.method_43583();
            net.minecraft.class_9801.class_4574 drawn = new net.minecraft.class_9801.class_4574(state.comp_749, state.comp_750,
                state.comp_751, state.comp_752, state.comp_753);
            CbRenderVertexBufferAccess access = (CbRenderVertexBufferAccess) (Object) buffer;
            access.cb$setFormat(access.cb$uploadVertexBuffer(drawn, rendered.method_43581()));
            access.cb$setSequentialIndices(access.cb$uploadIndexBuffer(drawn, rendered.method_43582()));
            access.cb$setIndexCount(state.comp_751);
            access.cb$setIndexType(state.comp_753);
            access.cb$setMode(state.comp_752);
        } finally {
            rendered.method_43585();
        }
    }

    /** a mesh 1.21 uploads into a 1.20.6 VertexBuffer subclass, as the RenderedBuffer it stands for */
    public static BufferBuilderRenderedBuffer cbAsRendered(net.minecraft.class_9801 mesh) {
        net.minecraft.class_9801.class_4574 drawn = mesh.method_60822();
        return BufferBuilderRenderedBuffer.cbOf(null, new BufferBuilderDrawState(drawn.comp_749(), drawn.comp_750(), drawn.comp_751(),
            drawn.comp_752(), drawn.comp_753(), false, mesh.method_60821() == null), mesh, null);
    }

    // ------------------------------------------------------------------ MultiBufferSource.immediate / immediateWithBuffers

    /** MultiBufferSource.immediate(builder): 1.21's over the builder's memory (1.20.6's began the builder per batch) */
    public static net.minecraft.class_4597.class_4598 cbImmediate(net.minecraft.class_287 builder) {
        return net.minecraft.class_4597.method_22991(cbBytesOf(builder));
    }

    /** MultiBufferSource.immediateWithBuffers(fixed, builder): the same, each fixed type over its builder's memory, in order */
    public static net.minecraft.class_4597.class_4598 cbImmediateWithBuffers(Map<net.minecraft.class_1921, net.minecraft.class_287> fixed,
                                                                           net.minecraft.class_287 builder) {
        return net.minecraft.class_4597.method_22992(cbFixedBytes(fixed), cbBytesOf(builder));
    }

    // ------------------------------------------------------------------ MultiBufferSource.BufferSource's 1.20.6 fields, for a 1.20.6 subclass

    /*
     * 1.20.6's BufferSource kept one shared builder (begun per render type), its fixed builders, the last state and
     * the set of builders started; 1.21's keeps the memory (a shared ByteBufferBuilder and one per fixed type), the
     * builders started per type, and the last shared type. A 1.20.6 subclass reading the old fields sees them as
     * they stand: the builder of the batch in progress where there is one, else the 1.20.6 builder over that memory
     * (cbBuilderOf, idle until begun).
     */

    private static top.dext.centurybridge.step.v1_21.mixin.CbRenderBufferSourceAccess cbSource(net.minecraft.class_4597.class_4598 source) {
        return (top.dext.centurybridge.step.v1_21.mixin.CbRenderBufferSourceAccess) (Object) source;
    }

    /** builder: the shared builder -- the one of the shared batch in progress, else the one over the shared memory */
    public static net.minecraft.class_287 cbSharedBuilder(net.minecraft.class_4597.class_4598 source) {
        top.dext.centurybridge.step.v1_21.mixin.CbRenderBufferSourceAccess s = cbSource(source);
        net.minecraft.class_1921 last = s.cb$lastSharedType();
        net.minecraft.class_287 started = last != null ? s.cb$startedBuilders().get(last) : null;
        return started != null ? started : cbBuilderOf(s.cb$sharedBuffer());
    }

    /** fixedBuffers: each fixed type's builder -- its batch in progress, else the one over its memory -- in order */
    public static Map<net.minecraft.class_1921, net.minecraft.class_287> cbFixedBuilders(net.minecraft.class_4597.class_4598 source) {
        top.dext.centurybridge.step.v1_21.mixin.CbRenderBufferSourceAccess s = cbSource(source);
        java.util.LinkedHashMap<net.minecraft.class_1921, net.minecraft.class_287> builders = new java.util.LinkedHashMap<>();
        for (Map.Entry<net.minecraft.class_1921, net.minecraft.class_9799> e : s.cb$fixedBuffers().entrySet()) {
            net.minecraft.class_287 started = s.cb$startedBuilders().get(e.getKey());
            builders.put(e.getKey(), started != null ? started : cbBuilderOf(e.getValue()));
        }
        return java.util.Collections.unmodifiableMap(builders);
    }

    /** lastState: the last shared type, as 1.20.6's Optional */
    public static java.util.Optional<net.minecraft.class_1921> cbLastState(net.minecraft.class_4597.class_4598 source) {
        return java.util.Optional.ofNullable(cbSource(source).cb$lastSharedType());
    }

    /** lastState = state */
    public static void cbSetLastState(net.minecraft.class_4597.class_4598 source, java.util.Optional<net.minecraft.class_1921> state) {
        cbSource(source).cb$setLastSharedType(state.orElse(null));
    }

    /** startedBuffers: the builders of the batches in progress */
    public static java.util.Set<net.minecraft.class_287> cbStartedBuffers(net.minecraft.class_4597.class_4598 source) {
        java.util.Set<net.minecraft.class_287> started = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        started.addAll(cbSource(source).cb$startedBuilders().values());
        return java.util.Collections.unmodifiableSet(started);
    }

    /** a 1.20.6 map of fixed builders as 1.21's ordered map of their memory (iteration order kept) */
    public static java.util.SequencedMap<net.minecraft.class_1921, net.minecraft.class_9799> cbFixedBytes(
            Map<net.minecraft.class_1921, net.minecraft.class_287> fixed) {
        java.util.LinkedHashMap<net.minecraft.class_1921, net.minecraft.class_9799> bytes = new java.util.LinkedHashMap<>();
        for (Map.Entry<net.minecraft.class_1921, net.minecraft.class_287> e : fixed.entrySet()) {
            bytes.put(e.getKey(), cbBytesOf(e.getValue()));
        }
        return bytes;
    }
}
