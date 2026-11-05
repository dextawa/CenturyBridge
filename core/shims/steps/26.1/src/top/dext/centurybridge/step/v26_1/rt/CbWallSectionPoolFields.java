package top.dext.centurybridge.step.v26_1.rt;

import java.util.Queue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SectionBufferBuilderPool;

/**
 * 1.21.11 SectionBufferBuilderPool.freeBuffers / freeBufferCount (class_8901.field_46905 / field_46906, opened by
 * access wideners). 1.21.11 kept the free packs in a Queue and set freeBufferCount = freeBuffers.size() after every
 * acquire and release. 26.1 (javap) keeps the same packs in an ArrayBlockingQueue (a Queue) and answers
 * getFreeBufferCount() as freeBuffers.size(). Reads only: the count was a mirror of the queue (a write lasted until
 * the next acquire/release), so its aw-put row stays open.
 */
@Environment(EnvType.CLIENT)
public final class CbWallSectionPoolFields {
    private CbWallSectionPoolFields() {
    }

    @SuppressWarnings("rawtypes")
    public static Queue freeBuffers(SectionBufferBuilderPool p) {
        return p.freeBuffers;
    }

    public static int freeBufferCount(SectionBufferBuilderPool p) {
        return p.getFreeBufferCount();
    }
}
