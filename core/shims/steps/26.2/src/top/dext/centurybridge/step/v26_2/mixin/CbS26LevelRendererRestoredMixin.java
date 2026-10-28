package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2 LevelRenderer members 26.2 dropped whose work 26.2's LevelRenderer still has the state for, restored with
 * 26.1.2's bodies (javap -c of both):
 * <ul>
 * <li>initOutline(): 26.1.2 destroyed the entity outline target and made a new TextureTarget("Entity Outline") of the
 *     window render state's size. 26.2 makes that target once, final, in the constructor and resizes it with the window
 *     (LevelRenderer.resize: entityOutlineTarget.resize(w, h), which rebuilds its buffers); the same target of the same
 *     size is that resize to the window render state's size.</li>
 * <li>shouldShowEntityOutlines(): not in panoramic mode, an outline target, a player. 26.2 asks the same in
 *     LevelExtractor.shouldShowEntityOutlines(Camera) (camera.isPanoramicMode, minecraft.player; its target is never
 *     null); here the camera's flag is the level render state's, as 26.1.2 read it.</li>
 * <li>onSectionBecomingNonEmpty(long): 26.1.2 scheduled the section's propagation on the occlusion graph and marked it
 *     previously empty. 26.2 feeds the graph the same event as a set (ClientChunkCache's removedEmptySections, read by
 *     LevelExtractor into the frame's ChunkLoadingRenderState): SectionOcclusionGraph.updateEmptySections(added, removed)
 *     does, for each removed section, 26.1.2's two calls plus dropping it from the graph's own empty set. A one-element
 *     removal is that event.</li>
 * <li>onChunkReadyToRender(ChunkPos): 26.1.2 queued the chunk's neighbour events on the graph. 26.2 keeps the loaded
 *     chunks as a set the graph derives those from (updateLoadedChunks(added, removed), fed from ClientChunkCache's
 *     addedLoadedChunks); a one-element addition is that event.</li>
 * <li>update(Camera): 26.1.2 culled the terrain for the camera (repositioned the view area on the camera's section,
 *     updated the occlusion graph, applied the frustum) and scheduled section compiles, before rendering. 26.2 does the
 *     same work at the end of render(...) on the frame's CameraRenderState: repositionCamera(state), compileSections(state),
 *     SectionOcclusionGraph.update(state, options fov, chunk-loading state); the frustum is applied to the visible
 *     sections by LevelExtractor.extract at the next frame. The camera's state is the frame's own when it is the main
 *     camera, else extracted from that camera as GameRenderer extracts the main one.</li>
 * </ul>
 * 26.2's vanilla never calls these (its paths run on render-state snapshots); they serve converted mods' calls and the
 * super calls of their overrides. Priority below every mod's.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = LevelRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26LevelRendererRestoredMixin {

    @Shadow
    @Final
    private GameRenderer gameRenderer;

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Shadow
    @Final
    private SectionOcclusionGraph sectionOcclusionGraph;

    @Shadow
    @Final
    private RenderTarget entityOutlineTarget;

    @Shadow
    @Final
    private OptionsRenderState optionsRenderState;

    @Shadow
    private void repositionCamera(CameraRenderState state) {
        throw new AssertionError();
    }

    @Shadow
    private void compileSections(CameraRenderState state) {
        throw new AssertionError();
    }

    /** update(Camera) */
    public void update(Camera camera) {
        CameraRenderState state;
        if (camera == this.gameRenderer.mainCamera()) {
            state = this.levelRenderState.cameraRenderState;
        } else {
            state = new CameraRenderState();
            camera.extractRenderState(state, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
        }
        ProfilerFiller profiler = Profiler.get();
        profiler.push("cullTerrain");
        this.repositionCamera(state);
        this.sectionOcclusionGraph.update(state, this.optionsRenderState.fov, this.levelRenderState.chunkLoadingRenderState);
        profiler.popPush("compileSections");
        this.compileSections(state);
        profiler.pop();
    }

    /** initOutline() */
    public void initOutline() {
        WindowRenderState window = this.gameRenderer.gameRenderState().windowRenderState;
        this.entityOutlineTarget.resize(window.width, window.height);
    }

    /** shouldShowEntityOutlines() */
    protected boolean shouldShowEntityOutlines() {
        return !this.levelRenderState.cameraRenderState.isPanoramicMode && this.entityOutlineTarget != null
            && Minecraft.getInstance().player != null;
    }

    /** onSectionBecomingNonEmpty(long) */
    public void onSectionBecomingNonEmpty(long sectionNode) {
        LongOpenHashSet removed = new LongOpenHashSet();
        removed.add(sectionNode);
        this.sectionOcclusionGraph.updateEmptySections(new LongOpenHashSet(), removed);
    }

    /** onChunkReadyToRender(ChunkPos) */
    public void onChunkReadyToRender(ChunkPos pos) {
        LongOpenHashSet added = new LongOpenHashSet();
        added.add(ChunkPos.pack(pos.x(), pos.z()));
        this.sectionOcclusionGraph.updateLoadedChunks(added, new LongOpenHashSet());
    }
}
