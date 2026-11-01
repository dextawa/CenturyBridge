package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderSetup;

/**
 * 26.1.2 RenderSetup.RenderSetupBuilder.bufferSize(int) (javap: it only stored the int, which RenderType.bufferSize()
 * handed to RenderBuffers as the initial capacity of that type's fixed ByteBufferBuilder). 26.2 dropped the field from
 * RenderSetup and RenderType: its buffers grow on demand (ByteBufferBuilder.ensureCapacity / resize), and nothing reads
 * an initial size per type any more. The call keeps the builder as 26.1.2's did; the hint has no reader on 26.2.
 */
@Environment(EnvType.CLIENT)
public final class CbS26RenderSetup {
    private CbS26RenderSetup() {}

    /** 26.1.2 RenderSetupBuilder.bufferSize(size) */
    public static RenderSetup.RenderSetupBuilder bufferSize(RenderSetup.RenderSetupBuilder builder, int size) {
        return builder;
    }
}
