package top.dext.centurybridge.step.v26_2.mixin;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2 ClientChunkCache.getLoadedEmptySections(): the cache storage's set of loaded empty sections (javap of both). 26.2's
 * storage keeps only the frame's added / removed deltas (addedEmptySections / removedEmptySections, read by LevelExtractor
 * into the ChunkLoadingRenderState); the running set is SectionOcclusionGraph.emptySections, which
 * updateEmptySections(added, removed) keeps from those deltas each frame. Restored as that set for the level the level
 * renderer draws (the cache 26.1.2's renderer read it from), up to one frame behind the cache; for any other cache an empty
 * set. Priority below every mod's.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = ClientChunkCache.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26ChunkCacheRestoredMixin {

    @Shadow
    @Final
    private ClientLevel level;

    /** getLoadedEmptySections() */
    public LongOpenHashSet getLoadedEmptySections() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == this.level && minecraft.levelRenderer != null) {
            return minecraft.levelRenderer.sectionOcclusionGraph().emptySections;
        }
        return new LongOpenHashSet();
    }
}
