package top.dext.centurybridge.step.v26_2.rt;

import com.google.common.collect.MapMaker;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * 26.1.2's per-type buffer size on 26.2. 26.1.2 RenderSetup.RenderSetupBuilder.bufferSize(int) only stored the int
 * (default 1536, TRANSIENT_BUFFER_SIZE), and RenderType.bufferSize() answered setup.bufferSize, which RenderBuffers
 * handed to that type's fixed ByteBufferBuilder as its initial capacity (javap -c -p 26.1.2). 26.2 dropped the field from
 * RenderSetup and RenderType: a whole-jar search of client-26.2.jar finds no class reading a per-type buffer size (its
 * only bufferSize member is ChunkSectionLayer's; every other ByteBufferBuilder grows on demand). The size therefore has
 * no reader on 26.2 and is informational: the layer keeps what a 26.1.2-era call set (the builder's size, carried to the
 * RenderSetup it builds by CbS26RenderSetupSizeMixin) and answers it from RenderType.bufferSize(); a type built without
 * one answers 26.1.2's default for it: 786432 (SMALL_BUFFER_SIZE) for the vanilla types 26.1.2 sized explicitly and 26.2
 * still builds (RenderTypes "text", "translucent_moving_block"), otherwise the builder default 1536.
 */
@Environment(EnvType.CLIENT)
public final class CbS26RenderSetup {
    private static final Map<RenderSetup.RenderSetupBuilder, Integer> BUILDER_SIZE = new MapMaker().weakKeys().makeMap();
    private static final Map<RenderSetup, Integer> SETUP_SIZE = new MapMaker().weakKeys().makeMap();

    private CbS26RenderSetup() {}

    /** 26.1.2 RenderSetupBuilder.bufferSize(size): kept for the setup the builder builds; the builder is unchanged */
    public static RenderSetup.RenderSetupBuilder bufferSize(RenderSetup.RenderSetupBuilder builder, int size) {
        BUILDER_SIZE.put(builder, size);
        return builder;
    }

    /** RenderSetupBuilder.createRenderSetup returned setup (CbS26RenderSetupSizeMixin) */
    public static void built(RenderSetup.RenderSetupBuilder builder, RenderSetup setup) {
        Integer size = BUILDER_SIZE.get(builder);
        if (size != null && setup != null) {
            SETUP_SIZE.put(setup, size);
        }
    }

    /** 26.1.2 RenderType.bufferSize() */
    public static int bufferSize(RenderType type) {
        Integer size = SETUP_SIZE.get(type.state);
        if (size != null) {
            return size;
        }
        return switch (type.name) {
            case "text", "translucent_moving_block" -> RenderType.SMALL_BUFFER_SIZE;
            default -> RenderType.TRANSIENT_BUFFER_SIZE;
        };
    }
}
