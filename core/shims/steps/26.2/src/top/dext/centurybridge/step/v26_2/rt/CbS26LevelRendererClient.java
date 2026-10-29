package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;

/**
 * The arguments 26.2's LevelRenderer constructor takes where 26.1.2's took the Minecraft (callReshapes /
 * superCtorReshapes of LevelRenderer.&lt;init&gt;, s26g-levelrenderer.json): 26.2's Minecraft builds
 * new LevelRenderer(entityRenderDispatcher, blockEntityRenderDispatcher, modelManager, textureManager, atlasManager,
 * shaderManager, gameRenderer, window.getWidth(), window.getHeight()) (javap of Minecraft.&lt;init&gt;); each comes
 * from the Minecraft the 26.1.2 caller passed, as 26.1.2's constructor read it from there.
 */
@Environment(EnvType.CLIENT)
public final class CbS26LevelRendererClient {
    private CbS26LevelRendererClient() {
    }

    public static ModelManager modelManager(Minecraft minecraft) {
        return minecraft.getModelManager();
    }

    public static TextureManager textureManager(Minecraft minecraft) {
        return minecraft.getTextureManager();
    }

    public static AtlasManager atlasManager(Minecraft minecraft) {
        return minecraft.getAtlasManager();
    }

    public static ShaderManager shaderManager(Minecraft minecraft) {
        return minecraft.getShaderManager();
    }

    public static GameRenderer gameRenderer(Minecraft minecraft) {
        return minecraft.gameRenderer;
    }

    public static int width(Minecraft minecraft) {
        return minecraft.getWindow().getWidth();
    }

    public static int height(Minecraft minecraft) {
        return minecraft.getWindow().getHeight();
    }
}
