package top.dext.centurybridge.step.v1_21.rt;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.facade.BufferBuilderRenderedBuffer;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderBeaconAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderLevelRendererAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderSectionDispatcherAccess;

/**
 * 1.20.6's level-rendering calls 1.21 reshaped: the section dispatcher's construction and layer upload, the region
 * cache's box regions, LevelRenderer.playStreamingMusic, BeaconRenderer.renderBeaconBeam with float colors. Each is
 * 1.20.6's body on 1.21's pieces; a 1.20.6 subclass's own override is reached where 1.20.6 reached it (adapters for
 * 1.20.6-style calls, override hooks for 1.21's), its super call lands on the body.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderLevel {

    private CbRenderLevel() {
    }

    // ------------------------------------------------------------------ SectionRenderDispatcher

    /** the block renderer 1.20.6's section dispatcher read from the game (1.21 takes it at construction) */
    public static net.minecraft.class_776 cbBlockRenderer() {
        return net.minecraft.class_310.method_1551().method_1541();
    }

    /** the block entity render dispatcher 1.20.6's section dispatcher read from the game (1.21 takes it at construction) */
    public static net.minecraft.class_824 cbBlockEntityRenderDispatcher() {
        return net.minecraft.class_310.method_1551().method_31975();
    }

    /** SectionRenderDispatcher.uploadSectionLayer(RenderedBuffer, VertexBuffer), as a 1.20.6 subclass declares it */
    public interface CbLegacySectionUpload {
        CompletableFuture<Void> method_3635(BufferBuilderRenderedBuffer rendered, net.minecraft.class_291 buffer);
    }

    /** dispatcher.uploadSectionLayer(rendered, buffer), called 1.20.6's way */
    public static CompletableFuture<Void> cbUploadSectionLayer(net.minecraft.class_846 dispatcher, BufferBuilderRenderedBuffer rendered,
                                                             net.minecraft.class_291 buffer) {
        if (dispatcher instanceof CbLegacySectionUpload own) {
            return own.method_3635(rendered, buffer);
        }
        return cbUploadSectionLayerBody(dispatcher, rendered, buffer);
    }

    /**
     * uploadSectionLayer(rendered, buffer) of 1.20.6: nothing once closed; else on the upload queue -- an invalid
     * buffer only releases the batch, any other is bound, uploaded into (VertexBuffer.upload, 1.20.6's way) and
     * unbound.
     */
    public static CompletableFuture<Void> cbUploadSectionLayerBody(net.minecraft.class_846 dispatcher, BufferBuilderRenderedBuffer rendered,
                                                                 net.minecraft.class_291 buffer) {
        CbRenderSectionDispatcherAccess access = (CbRenderSectionDispatcherAccess) (Object) dispatcher;
        if (access.cb$closed()) {
            return CompletableFuture.completedFuture(null);
        }
        java.util.Queue<Runnable> queue = access.cb$toUpload();
        return CompletableFuture.runAsync(() -> {
            if (buffer.method_43444()) {
                rendered.method_43585();
                return;
            }
            buffer.method_1353();
            CbRenderBuffers.cbUploadTo(buffer, rendered);
            net.minecraft.class_291.method_1354();
        }, queue::add);
    }

    // ------------------------------------------------------------------ RenderRegionCache

    private static final ThreadLocal<Boolean> FORCED = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /** a 1.20.6 box region is being built: the centre section's emptiness does not decide (CbRenderRegionCacheMixin) */
    public static boolean cbRegionForced() {
        return FORCED.get();
    }

    /** RenderRegionCache.createRegion(level, from, to, padding), as a 1.20.6 subclass declares it */
    public interface CbLegacyCreateRegion {
        net.minecraft.class_853 method_39969(net.minecraft.class_1937 level, net.minecraft.class_2338 from, net.minecraft.class_2338 to,
                                             int padding);
    }

    /** cache.createRegion(level, from, to, padding), called 1.20.6's way */
    public static net.minecraft.class_853 cbCreateRegion(net.minecraft.class_6850 cache, net.minecraft.class_1937 level,
                                                       net.minecraft.class_2338 from, net.minecraft.class_2338 to, int padding) {
        if (cache instanceof CbLegacyCreateRegion own) {
            return own.method_39969(level, from, to, padding);
        }
        return cbCreateRegionBody(cache, level, from, to, padding);
    }

    /**
     * createRegion(level, from, to, padding) of 1.20.6: null when every chunk the box [from, to] reaches is empty
     * over its heights; else the region of the chunks the padded box reaches. 1.21's region is the 3 x 3 chunks
     * around a section, so a box whose padded chunks fit in 3 x 3 is served by that region (it answers every
     * position the 1.20.6 one did); a wider box has no 1.21 region.
     */
    public static net.minecraft.class_853 cbCreateRegionBody(net.minecraft.class_6850 cache, net.minecraft.class_1937 level,
                                                           net.minecraft.class_2338 from, net.minecraft.class_2338 to, int padding) {
        int minX = net.minecraft.class_4076.method_18675(from.method_10263() - padding);
        int minZ = net.minecraft.class_4076.method_18675(from.method_10260() - padding);
        int maxX = net.minecraft.class_4076.method_18675(to.method_10263() + padding);
        int maxZ = net.minecraft.class_4076.method_18675(to.method_10260() + padding);
        boolean empty = true;
        for (int x = net.minecraft.class_4076.method_18675(from.method_10263()); empty && x <= net.minecraft.class_4076.method_18675(to.method_10263()); x++) {
            for (int z = net.minecraft.class_4076.method_18675(from.method_10260()); z <= net.minecraft.class_4076.method_18675(to.method_10260()); z++) {
                if (!level.method_8497(x, z).method_12228(from.method_10264(), to.method_10264())) {
                    empty = false;
                    break;
                }
            }
        }
        if (empty) {
            return null;
        }
        if (maxX - minX > 2 || maxZ - minZ > 2) {
            throw new UnsupportedOperationException("1.21's RenderChunkRegion covers the 3 x 3 chunks around a section; a 1.20.6 region of "
                + (maxX - minX + 1) + " x " + (maxZ - minZ + 1) + " chunks has no 1.21 counterpart");
        }
        FORCED.set(Boolean.TRUE);
        try {
            return cache.method_39969(level, net.minecraft.class_4076.method_18676(minX + 1,
                net.minecraft.class_4076.method_18675(from.method_10264()), minZ + 1));
        } finally {
            FORCED.set(Boolean.FALSE);
        }
    }

    /** 1.21 asks a 1.20.6 cache subclass for a section's region: its own createRegion, called as 1.20.6's dispatcher called it */
    public static net.minecraft.class_853 cbRegionForSection(CbLegacyCreateRegion own, net.minecraft.class_1937 level,
                                                           net.minecraft.class_4076 section) {
        net.minecraft.class_2338 origin = section.method_19767();
        return own.method_39969(level, origin.method_10069(-1, -1, -1), origin.method_10069(16, 16, 16), 1);
    }

    // ------------------------------------------------------------------ LevelRenderer.playStreamingMusic

    /** LevelRenderer.playStreamingMusic(sound, pos), as a 1.20.6 subclass declares it */
    public interface CbLegacyStreamingMusic {
        void method_8562(net.minecraft.class_3414 sound, net.minecraft.class_2338 pos);
    }

    /** renderer.playStreamingMusic(sound, pos), called 1.20.6's way */
    public static void cbPlayStreamingMusic(net.minecraft.class_761 renderer, net.minecraft.class_3414 sound, net.minecraft.class_2338 pos) {
        if (renderer instanceof CbLegacyStreamingMusic own) {
            own.method_8562(sound, pos);
            return;
        }
        cbPlayStreamingMusicBody(renderer, sound, pos);
    }

    /**
     * playStreamingMusic(sound, pos) of 1.20.6: the jukebox sound at pos stops; a sound (if any) plays there as a
     * jukebox song, its name shown as now playing when it is a song's (1.20.6 looked its record up; 1.21's songs
     * carry the description); nearby entities hear whether music plays.
     */
    public static void cbPlayStreamingMusicBody(net.minecraft.class_761 renderer, net.minecraft.class_3414 sound, net.minecraft.class_2338 pos) {
        CbRenderLevelRendererAccess access = (CbRenderLevelRendererAccess) (Object) renderer;
        Map<net.minecraft.class_2338, net.minecraft.class_1113> playing = access.cb$playingJukeboxSongs();
        net.minecraft.class_310 minecraft = access.cb$minecraft();
        net.minecraft.class_1113 current = playing.get(pos);
        if (current != null) {
            minecraft.method_1483().method_4870(current);
            playing.remove(pos);
        }
        if (sound != null) {
            net.minecraft.class_2561 name = cbSongName(access.cb$level(), sound);
            if (name != null) {
                minecraft.field_1705.method_1732(name);
            }
            net.minecraft.class_1113 instance = net.minecraft.class_1109.method_4760(sound, net.minecraft.class_243.method_24953(pos));
            playing.put(pos, instance);
            minecraft.method_1483().method_4873(instance);
        }
        access.cb$notifyNearbyEntities(access.cb$level(), pos, sound != null);
    }

    /** the jukebox song's description whose sound this is, or null */
    private static net.minecraft.class_2561 cbSongName(net.minecraft.class_1937 level, net.minecraft.class_3414 sound) {
        if (level == null) {
            return null;
        }
        for (net.minecraft.class_9793 song : level.method_30349().method_30530(net.minecraft.class_7924.field_52176)) {
            if (song.comp_2835().comp_349() == sound) {
                return song.comp_2836();
            }
        }
        return null;
    }

    /** 1.21 plays a jukebox song: a 1.20.6 renderer subclass's playStreamingMusic with the song's sound */
    public static void cbJukeboxPlayed(CbLegacyStreamingMusic own, net.minecraft.class_6880<net.minecraft.class_9793> song,
                                       net.minecraft.class_2338 pos) {
        own.method_8562(song.comp_349().comp_2835().comp_349(), pos);
    }

    /** 1.21 stops the jukebox song at pos: a 1.20.6 renderer subclass's playStreamingMusic with no sound */
    public static void cbJukeboxStopped(CbLegacyStreamingMusic own, net.minecraft.class_2338 pos) {
        own.method_8562(null, pos);
    }

    // ------------------------------------------------------------------ BeaconRenderer.renderBeaconBeam

    /**
     * BeaconRenderer.renderBeaconBeam(pose, source, texture, partialTick, textureScale, gameTime, yOffset, height,
     * colors, beamRadius, glowRadius) of 1.20.6: 1.21's geometry (renderPart) with 1.20.6's colors -- the beam the
     * three floats opaque, the glow the same at alpha 0.125, each channel the byte 1.20.6's consumer wrote.
     */
    public static void method_3545(net.minecraft.class_4587 pose, net.minecraft.class_4597 source, net.minecraft.class_2960 texture,
                                   float partialTick, float textureScale, long gameTime, int yOffset, int height, float[] colors,
                                   float beamRadius, float glowRadius) {
        int top = yOffset + height;
        pose.method_22903();
        pose.method_22904(0.5, 0.0, 0.5);
        float time = (float) Math.floorMod(gameTime, 40) + partialTick;
        float scroll = height < 0 ? time : -time;
        float vOffset = net.minecraft.class_3532.method_22450(scroll * 0.2F - (float) net.minecraft.class_3532.method_15375(scroll * 0.1F));
        float r = colors[0];
        float g = colors[1];
        float b = colors[2];
        pose.method_22903();
        pose.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(time * 2.25F - 45.0F));
        float maxV = -1.0F + vOffset;
        float minV = (float) height * textureScale * (0.5F / beamRadius) + maxV;
        CbRenderBeaconAccess.cb$renderPart(pose, source.getBuffer(net.minecraft.class_1921.method_23592(texture, false)),
            CbClientModels.cbColor(r, g, b, 1.0F), yOffset, top, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius,
            0.0F, 1.0F, minV, maxV);
        pose.method_22909();
        maxV = -1.0F + vOffset;
        minV = (float) height * textureScale + maxV;
        CbRenderBeaconAccess.cb$renderPart(pose, source.getBuffer(net.minecraft.class_1921.method_23592(texture, true)),
            CbClientModels.cbColor(r, g, b, 0.125F), yOffset, top, -glowRadius, -glowRadius, glowRadius, -glowRadius, -glowRadius,
            glowRadius, glowRadius, glowRadius, 0.0F, 1.0F, minV, maxV);
        pose.method_22909();
    }
}
