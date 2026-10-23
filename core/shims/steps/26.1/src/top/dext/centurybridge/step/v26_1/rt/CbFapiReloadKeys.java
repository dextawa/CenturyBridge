package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.resources.Identifier;

/**
 * 1.21.11's ResourceReloaderKeys.Client.BLOCK_RENDER_MANAGER (minecraft:block_render_manager), which 26.1 no
 * longer has: 1.21.11's BlockRenderDispatcher reloaded as a listener of its own (it set up the liquid
 * renderer's sprites); 26.1 bakes fluid models with the other models (FluidStateModelSet, in the model
 * manager's reload), so a reloader ordered after the block render manager is ordered after the models.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiReloadKeys {
    private CbFapiReloadKeys() {
    }

    public static final Identifier BLOCK_RENDER_MANAGER = ResourceReloaderKeys.Client.MODELS;
}
