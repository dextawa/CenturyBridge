package top.dext.centurybridge.step.v26_1.rt;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

/**
 * 1.21.11's SectionRenderDispatcher.getToUpload() on 26.1's upload path. 1.21.11 queued one upload task per compiled
 * section (toUpload, a queue of runnables run on the render thread; getToUpload() answered its size, the "pU" of its
 * stats). 26.1 stages each compiled section's layer buffers into the per-layer uber buffers (RenderSection
 * .addSectionBuffersToUberBuffer, UberGpuBuffer.addAllocation, under the dispatcher's copy lock; rt/CbWallSectionUploads
 * hands 1.21.11's uploads the same way) and uploads every staged allocation once per frame
 * (uploadGlobalGeomBuffersToGPU): the sections waiting are the meshes staged in any layer's vertex or index buffer,
 * each counted once, read under the same lock.
 */
@Environment(EnvType.CLIENT)
public final class CbWallSectionUploadCount {

    private CbWallSectionUploadCount() {
    }

    /** 1.21.11's getToUpload(), declared by a mod's dispatcher subclass (adapterHooks) */
    public interface LegacyToUpload {
        int getToUpload();
    }

    /** a mod's call of 1.21.11's getToUpload(): a 1.21.11 override's answer, else 1.21.11's count */
    public static int getToUpload(SectionRenderDispatcher dispatcher) {
        if (dispatcher instanceof LegacyToUpload legacy) {
            return legacy.getToUpload();
        }
        return superGetToUpload(dispatcher);
    }

    /** the sections whose compiled buffers wait for upload */
    public static int superGetToUpload(SectionRenderDispatcher dispatcher) {
        Set<SectionMesh> waiting = new HashSet<>();
        dispatcher.lock();
        try {
            for (SectionRenderDispatcher.SectionUberBuffers buffers : dispatcher.chunkUberBuffers.values()) {
                waiting.addAll(buffers.vertexBuffer().stagedAllocations.keySet());
                if (buffers.indexBuffer() != null) {
                    waiting.addAll(buffers.indexBuffer().stagedAllocations.keySet());
                }
            }
        } finally {
            dispatcher.unlock();
        }
        return waiting.size();
    }
}
