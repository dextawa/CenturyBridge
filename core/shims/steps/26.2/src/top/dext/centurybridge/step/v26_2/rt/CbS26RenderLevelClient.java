package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

/**
 * 26.1.2 LevelRenderer.renderLevel(allocator, deltaTracker, renderOutline, camera, projection, fog, fogColor,
 * renderSky, chunkSections): 26.2's LevelRenderer.render with the first eight arguments, called from the same
 * GameRenderer.renderLevel site (26.1.2 @412, 26.2 @405). The ninth argument was always the frame's
 * levelRenderState.chunkSectionsToRender, which 26.1.2 built with prepareChunkRenders(projection) during extraction;
 * 26.2's render calls prepareChunkRenders(projection) itself (@361) and hands the result to addMainPass, so the
 * sections come from the same call on the same projection. A caller handing other sections than that one has no
 * 26.2 counterpart (render takes none); they are not drawn.
 */
@Environment(EnvType.CLIENT)
public final class CbS26RenderLevelClient {
    private CbS26RenderLevelClient() {}

    public static void renderLevel(LevelRenderer self, GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderOutline,
            CameraRenderState camera, Matrix4fc projection, GpuBufferSlice fog, Vector4f fogColor, boolean renderSky,
            ChunkSectionsToRender chunkSections) {
        self.render(allocator, deltaTracker, renderOutline, camera, projection, fog, fogColor, renderSky);
    }
}
