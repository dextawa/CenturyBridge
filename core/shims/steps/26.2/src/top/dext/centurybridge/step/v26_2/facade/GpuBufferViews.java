package top.dext.centurybridge.step.v26_2.facade;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Holds 26.1.2's com.mojang.blaze3d.buffers.GpuBuffer$MappedView, an interface (data(), close()) that 26.2 replaced by
 * the record GpuBufferSlice.MappedView (the same data() and close(), plus its slice). classRenames send a 26.1.2
 * mod's references to {@link MappedView}; rt/CbS26CommandEncoderClient hands 26.2's mapped views out through it.
 */
@Environment(EnvType.CLIENT)
public final class GpuBufferViews {
    private GpuBufferViews() {}

    @Environment(EnvType.CLIENT)
    public interface MappedView extends AutoCloseable {
        ByteBuffer data();

        @Override
        void close();
    }

    /** 26.2's mapped view as 26.1.2's */
    public static MappedView of(com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView view) {
        return new MappedView() {
            @Override
            public ByteBuffer data() {
                return view.data();
            }

            @Override
            public void close() {
                view.close();
            }
        };
    }
}
