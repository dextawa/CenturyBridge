package top.dext.centurybridge.step.v26_1.rt;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

/**
 * 1.21.11's SectionRenderDispatcher.RenderSection.CompileTask.doTask(buffers) on 26.1's. 1.21.11's answered a future of
 * the task's result: the compile ran on the calling worker and the future completed once the section's buffers were
 * uploaded (its upload tasks, run on the render thread). 26.1's compiles and stages the buffers for upload in the same
 * call and answers the result (the upload of staged buffers is the frame's, uploadGlobalGeomBuffersToGPU).
 * <ul>
 * <li>A 1.21.11 caller gets 26.1's result as a completed future (the work it waited for is done when doTask returns;
 *     an exception escapes the call, as 1.21.11's compile threw before any future existed).</li>
 * <li>26.1 asks a 1.21.11 subclass for its result: the subclass's future, joined (override hook "unwrap"). 1.21.11
 *     futures complete on the uploads the layer gives back (rt/CbWallSectionUploads answers them completed), so the
 *     join returns once the subclass's own work is done.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallCompileTasks {

    private CbWallCompileTasks() {
    }

    /** a 1.21.11 call of CompileTask.doTask(buffers) */
    public static CompletableFuture<SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult> doTask(
            SectionRenderDispatcher.RenderSection.CompileTask task, SectionBufferBuilderPack buffers) {
        return CompletableFuture.completedFuture(task.doTask(buffers));
    }

    /** a 1.21.11 subclass's doTask answer, as 26.1's doTask answers it */
    public static SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult join(
            CompletableFuture<SectionRenderDispatcher.RenderSection.CompileTask.SectionTaskResult> result) {
        return result.join();
    }
}
