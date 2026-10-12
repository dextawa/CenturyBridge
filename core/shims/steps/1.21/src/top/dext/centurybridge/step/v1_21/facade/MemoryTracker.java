package top.dext.centurybridge.step.v1_21.facade;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;

/**
 * 1.20.6's MemoryTracker (class_311, gone in 1.21, whose ByteBufferBuilder allocates for itself): native buffers
 * from LWJGL's default allocator, as 1.20.6 made them -- create, resize (realloc), free. Its allocator is public
 * here: a mod's access widener may name it, and wideners never reach a CenturyBridge class.
 */
@Environment(EnvType.CLIENT)
public class MemoryTracker {

    /** ALLOCATOR */
    public static final MemoryUtil.MemoryAllocator field_34054 = MemoryUtil.getAllocator(false);

    public MemoryTracker() {
    }

    /** create(size) */
    public static ByteBuffer method_1596(int size) {
        long pointer = field_34054.malloc(size);
        if (pointer == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
        }
        return MemoryUtil.memByteBuffer(pointer, size);
    }

    /** resize(buffer, size) */
    public static ByteBuffer method_37465(ByteBuffer buffer, int size) {
        long pointer = field_34054.realloc(MemoryUtil.memAddress0(buffer), size);
        if (pointer == 0L) {
            throw new OutOfMemoryError("Failed to resize buffer from " + buffer.capacity() + " bytes to " + size + " bytes");
        }
        return MemoryUtil.memByteBuffer(pointer, size);
    }

    /** free(buffer) */
    public static void method_54557(ByteBuffer buffer) {
        field_34054.free(MemoryUtil.memAddress0(buffer));
    }
}
