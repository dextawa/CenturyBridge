package top.dext.centurybridge.step.v1_21_6.rt;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import top.dext.centurybridge.step.v1_21_6.facade.blaze3d.BufferType;
import top.dext.centurybridge.step.v1_21_6.facade.blaze3d.BufferUsage;

/**
 * 1.21.5's GPU buffer kinds (BufferType + BufferUsage) on 1.21.6's usage bits (GpuBuffer.USAGE_*). 1.21.5 (javap):
 * the type picked the GL binding target, the usage the glBufferData hint and two checks -- readBuffer needed a readable
 * usage, writeToBuffer a writable one. 1.21.6 states roles as bits and checks them: a vertex / index / uniform buffer
 * needs VERTEX / INDEX / UNIFORM, writeToBuffer and copyTextureToBuffer's target need COPY_DST, a texture upload's
 * source COPY_SRC, mapping for reading MAP_READ (javap of GlCommandEncoder). Each 1.21.5 kind maps to the bits that
 * allow what 1.21.5 allowed:
 * <ul>
 * <li>VERTICES -&gt; VERTEX, INDICES -&gt; INDEX, UNIFORM -&gt; UNIFORM; PIXEL_PACK (the GPU packs pixels into it) and
 *     COPY_WRITE -&gt; COPY_DST; PIXEL_UNPACK and COPY_READ (sources) -&gt; COPY_SRC;</li>
 * <li>a readable usage adds MAP_READ, a writable one COPY_DST (1.21.5 wrote through glBufferSubData, 1.21.6's
 *     writeToBuffer is a copy into the buffer).</li>
 * </ul>
 * 1.21.6's own replacements agree site by site (javap of both): the screenshot / texture download buffers
 * (PIXEL_PACK + STATIC_READ / STREAM_READ) are 9 = MAP_READ | COPY_DST, VertexFormat's immediate vertex / index
 * buffers (VERTICES / INDICES + DYNAMIC_WRITE) 40 = VERTEX | COPY_DST and 72 = INDEX | COPY_DST.
 * Reading a buffer's kind back answers what it was created with when a 1.21.5 call or constructor created it; for one
 * 1.21.6 created, the kind its bits stand for (the static / dynamic / stream hint 1.21.6 does not keep reads DYNAMIC).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbBuffers {

    private static final java.util.Map<GpuBuffer, Object[]> KINDS =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private CbBuffers() {
    }

    /** the 1.21.6 usage bits that allow what a 1.21.5 buffer of this type and usage allowed */
    public static int usage(BufferType type, BufferUsage usage) {
        int bits = switch (type) {
            case VERTICES -> GpuBuffer.USAGE_VERTEX;
            case INDICES -> GpuBuffer.USAGE_INDEX;
            case UNIFORM -> GpuBuffer.USAGE_UNIFORM;
            case PIXEL_PACK, COPY_WRITE -> GpuBuffer.USAGE_COPY_DST;
            case PIXEL_UNPACK, COPY_READ -> GpuBuffer.USAGE_COPY_SRC;
        };
        if (usage.isReadable()) {
            bits |= GpuBuffer.USAGE_MAP_READ;
        }
        if (usage.isWritable()) {
            bits |= GpuBuffer.USAGE_COPY_DST;
        }
        return bits;
    }

    /** GpuDevice.createBuffer(Supplier, BufferType, BufferUsage, int size) */
    public static GpuBuffer createBuffer(GpuDevice device, java.util.function.Supplier<String> label, BufferType type,
            BufferUsage usage, int size) {
        return remember(device.createBuffer(label, usage(type, usage), size), type, usage);
    }

    /** GpuDevice.createBuffer(Supplier, BufferType, BufferUsage, ByteBuffer data) */
    public static GpuBuffer createBuffer(GpuDevice device, java.util.function.Supplier<String> label, BufferType type,
            BufferUsage usage, java.nio.ByteBuffer data) {
        return remember(device.createBuffer(label, usage(type, usage), data), type, usage);
    }

    /** GlDevice.createBuffer(Supplier, BufferType, BufferUsage, int size), called on the GL device itself */
    public static GpuBuffer createBuffer(net.minecraft.class_10865 device, java.util.function.Supplier<String> label,
            BufferType type, BufferUsage usage, int size) {
        return createBuffer((GpuDevice) device, label, type, usage, size);
    }

    /** GlDevice.createBuffer(Supplier, BufferType, BufferUsage, ByteBuffer data), called on the GL device itself */
    public static GpuBuffer createBuffer(net.minecraft.class_10865 device, java.util.function.Supplier<String> label,
            BufferType type, BufferUsage usage, java.nio.ByteBuffer data) {
        return createBuffer((GpuDevice) device, label, type, usage, data);
    }

    /** GpuBuffer(BufferType, BufferUsage, int)'s super call, reshaped: the subclass instance keeps its 1.21.5 kind */
    public static void created(GpuBuffer buffer, BufferType type, BufferUsage usage) {
        KINDS.put(buffer, new Object[] {type, usage});
    }

    private static GpuBuffer remember(GpuBuffer buffer, BufferType type, BufferUsage usage) {
        created(buffer, type, usage);
        return buffer;
    }

    /** GpuBuffer.type() */
    public static BufferType type(GpuBuffer buffer) {
        Object[] kind = KINDS.get(buffer);
        if (kind != null) {
            return (BufferType) kind[0];
        }
        int bits = buffer.usage();
        if ((bits & GpuBuffer.USAGE_VERTEX) != 0) {
            return BufferType.VERTICES;
        }
        if ((bits & GpuBuffer.USAGE_INDEX) != 0) {
            return BufferType.INDICES;
        }
        if ((bits & GpuBuffer.USAGE_UNIFORM) != 0) {
            return BufferType.UNIFORM;
        }
        if ((bits & GpuBuffer.USAGE_MAP_READ) != 0 && (bits & GpuBuffer.USAGE_COPY_DST) != 0) {
            return BufferType.PIXEL_PACK;
        }
        if ((bits & GpuBuffer.USAGE_COPY_SRC) != 0) {
            return BufferType.COPY_READ;
        }
        return BufferType.COPY_WRITE;
    }

    /** GpuBuffer.usage() (1.21.5's answered the BufferUsage) */
    public static BufferUsage usage(GpuBuffer buffer) {
        Object[] kind = KINDS.get(buffer);
        if (kind != null) {
            return (BufferUsage) kind[1];
        }
        int bits = buffer.usage();
        if ((bits & GpuBuffer.USAGE_MAP_READ) != 0) {
            return BufferUsage.DYNAMIC_READ;
        }
        if ((bits & (GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_MAP_WRITE)) != 0) {
            return BufferUsage.DYNAMIC_WRITE;
        }
        return BufferUsage.DYNAMIC_COPY;
    }

    /** GlConst.toGl(BufferType): 1.21.5's GL binding targets (javap) */
    public static int toGl(BufferType type) {
        return switch (type) {
            case VERTICES -> 34962;
            case INDICES -> 34963;
            case PIXEL_PACK -> 35051;
            case COPY_READ -> 36662;
            case COPY_WRITE -> 36663;
            case PIXEL_UNPACK -> 35052;
            case UNIFORM -> 35345;
        };
    }

    /** GlConst.toGl(BufferUsage): 1.21.5's glBufferData hints (javap) */
    public static int toGl(BufferUsage usage) {
        return switch (usage) {
            case DYNAMIC_WRITE -> 35048;
            case STATIC_WRITE -> 35044;
            case STREAM_WRITE -> 35040;
            case STATIC_READ -> 35045;
            case DYNAMIC_READ -> 35049;
            case STREAM_READ -> 35041;
            case DYNAMIC_COPY -> 35050;
            case STATIC_COPY -> 35046;
            case STREAM_COPY -> 35042;
        };
    }
}
