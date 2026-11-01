package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_9801;
import top.dext.centurybridge.step.v1_21_5.rt.CbRenderTypes;

/**
 * What a converted direct subclass of 1.21.4's RenderType (class_1921) extends (superRenames). 1.21.4's RenderType was
 * concrete apart from its shards: constructor (name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup,
 * clear) storing format and mode (javap -c -p 1.21.4 class_1921.&lt;init&gt;), getters format() / mode() (method_23031 /
 * method_23033: getfield), draw = setupRenderState; BufferUploader.drawWithShader(buffer); clearRenderState. 1.21.5's
 * RenderType takes (name, size, crumbling, sort, setup, clear) and leaves draw, pipeline, outputTarget, format and mode
 * abstract; this class gives 1.21.4's members back and answers the 1.21.5 abstracts:
 * <ul>
 * <li>pipeline (method_68495): the program the setup runnable gave the layer's shader slot (1.21.4 drew with
 * RenderSystem.getShader() after setup), described by {@link CbRenderTypes#pipelineForShaderSlot}; GL state a setup
 * runnable set is overridden by the pipeline at the draw (known loss, release_notes_known.md).
 * <li>outputTarget (method_68494): the main render target, which 1.21.4's drawWithShader drew into unless the setup
 * runnable bound another (the bound target is not known to 1.21.5's render pass: known loss).
 * <li>draw (method_60895): setup, then 1.21.5's CompositeRenderType draw (javap -c -p class_1921$class_4687.method_60895)
 * with that pipeline, then clear. No pipeline (no program set, as 1.21.4's NO_SHADER): uploads nothing and draws nothing,
 * as 1.21.4's VertexBuffer.drawWithShader returned on a null shader.
 * </ul>
 */
@Environment(EnvType.CLIENT)
public abstract class RenderType extends net.minecraft.class_1921 {
    /** format */
    private final VertexFormat field_20972;
    /** mode */
    private final VertexFormat.class_5596 field_20973;

    public RenderType(String name, VertexFormat format, VertexFormat.class_5596 mode, int bufferSize, boolean affectsCrumbling,
            boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
        this.field_20972 = format;
        this.field_20973 = mode;
    }

    @Override
    public VertexFormat method_23031() {
        return this.field_20972;
    }

    @Override
    public VertexFormat.class_5596 method_23033() {
        return this.field_20973;
    }

    @Override
    public RenderPipeline method_68495() {
        return CbRenderTypes.pipelineForShaderSlot(this.toString(), this.field_20972, this.field_20973);
    }

    @Override
    public class_276 method_68494() {
        return class_310.method_1551().method_1522();
    }

    @Override
    public void method_60895(class_9801 buffer) {
        this.method_23516();
        try (class_9801 b = buffer) {
            RenderPipeline pipeline = this.method_68495();
            if (pipeline == null) {
                return;
            }
            GpuBuffer vertices = pipeline.getVertexFormat().uploadImmediateVertexBuffer(b.method_60818());
            GpuBuffer indices;
            VertexFormat.class_5595 indexType;
            if (b.method_60821() == null) {
                RenderSystem.class_5590 seq = RenderSystem.getSequentialBuffer(b.method_60822().comp_752());
                indices = seq.method_68274(b.method_60822().comp_751());
                indexType = seq.method_31924();
            } else {
                indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(b.method_60821());
                indexType = b.method_60822().comp_753();
            }
            class_276 target = this.method_68494();
            try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(target.method_30277(),
                    OptionalInt.empty(), target.field_1478 ? target.method_30278() : null, OptionalDouble.empty())) {
                pass.setPipeline(pipeline);
                pass.setVertexBuffer(0, vertices);
                if (RenderSystem.SCISSOR_STATE.isEnabled()) {
                    pass.enableScissor(RenderSystem.SCISSOR_STATE);
                }
                for (int i = 0; i < 12; i++) {
                    GpuTexture t = RenderSystem.getShaderTexture(i);
                    if (t != null) {
                        pass.bindSampler("Sampler" + i, t);
                    }
                }
                pass.setIndexBuffer(indices, indexType);
                pass.drawIndexed(0, b.method_60822().comp_751());
            }
        } finally {
            this.method_23518();
        }
    }
}
