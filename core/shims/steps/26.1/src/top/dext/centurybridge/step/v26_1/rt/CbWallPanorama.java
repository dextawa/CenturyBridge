package top.dext.centurybridge.step.v26_1.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Panorama;
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import top.dext.centurybridge.step.v26_1.mixin.CbWallGameRendererGuiAccess;
import top.dext.centurybridge.step.v26_1.mixin.CbWallGuiRendererAccess;

/**
 * 1.21.11's PanoramaRenderer(CubeMap) on 26.1's Panorama, which draws the GUI renderer's one cube map: a
 * panorama a mod makes over its own cube map (new, or a subclass's super(cubeMap)) keeps that cube map
 * here, and the frames it extracts are drawn with it (mixin/CbWallPanoramaMixin records the frame's
 * panorama state, mixin/CbWallGuiRendererMixin draws it with the recorded cube map). The game's own
 * panorama has none recorded and draws the GUI renderer's, as 1.21.11's drew the cube map it was made with.
 */
@Environment(EnvType.CLIENT)
public final class CbWallPanorama {

    private CbWallPanorama() {
    }

    private static final Map<Panorama, CubeMap> CUBE_MAPS = Collections.synchronizedMap(new WeakHashMap<>());
    /** the cube map each extracted panorama state is drawn with (taken when drawn) */
    private static final Map<PanoramaRenderState, CubeMap> FRAMES = Collections.synchronizedMap(new IdentityHashMap<>());

    /** new PanoramaRenderer(cubeMap) */
    public static Panorama newPanorama(CubeMap cubeMap) {
        Panorama p = new Panorama();
        CUBE_MAPS.put(p, cubeMap);
        return p;
    }

    /** after a 1.21.11 subclass's super(cubeMap) */
    public static void constructed(Panorama panorama, CubeMap cubeMap) {
        CUBE_MAPS.put(panorama, cubeMap);
    }

    /** the cube map a panorama draws: its own, or the GUI renderer's */
    public static CubeMap cubeMapOf(Panorama panorama) {
        CubeMap own = CUBE_MAPS.get(panorama);
        return own != null ? own : guiCubeMap(Minecraft.getInstance().gameRenderer);
    }

    /** 1.21.11 GameRenderer.cubeMap: the panorama cube map, which 26.1's GUI renderer holds */
    public static CubeMap guiCubeMap(GameRenderer renderer) {
        return ((CbWallGuiRendererAccess) ((CbWallGameRendererGuiAccess) renderer).cb$guiRenderer()).cb$cubeMap();
    }

    /** PanoramaRenderer.registerTextures(textureManager): its cube map's textures */
    public static void registerTextures(Panorama panorama, TextureManager textureManager) {
        cubeMapOf(panorama).registerTextures(textureManager);
    }

    /** CubeMap.render(minecraft, xRot, yRot): 26.1's takes the rotations only */
    public static void render(CubeMap cubeMap, Minecraft minecraft, float xRot, float yRot) {
        cubeMap.render(xRot, yRot);
    }

    /** a frame a panorama extracted (CbWallPanoramaMixin) */
    public static void extracted(Panorama panorama, PanoramaRenderState state) {
        CubeMap own = CUBE_MAPS.get(panorama);
        if (own != null && state != null) {
            FRAMES.put(state, own);
        }
    }

    /** the cube map a frame's panorama state is drawn with (CbWallGuiRendererMixin) */
    public static CubeMap drawWith(PanoramaRenderState state, CubeMap gui) {
        CubeMap own = state == null ? null : FRAMES.remove(state);
        return own != null ? own : gui;
    }
}
