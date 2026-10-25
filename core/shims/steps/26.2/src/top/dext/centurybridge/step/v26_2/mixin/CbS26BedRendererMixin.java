package top.dext.centurybridge.step.v26_2.mixin;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.facade.BedRenderer;
import top.dext.centurybridge.step.v26_2.rt.CbS26Beds;

/**
 * 26.1.2's BlockEntityRenderers registered BedRenderer for BlockEntityType.BED (javap: register(BED, BedRenderer::new)
 * in its initializer); 26.2's registers no bed renderer. The 26.1.2 pair -- the layer's BED type (rt/CbS26Beds) and
 * 26.1.2's renderer (facade/BedRenderer) -- is put into 26.2's provider map at the end of the same initializer.
 */
@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderers.class)
public abstract class CbS26BedRendererMixin {

    @Shadow
    @Final
    private static Map<BlockEntityType<?>, BlockEntityRendererProvider<?, ?>> PROVIDERS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cbS26$bedRenderer(CallbackInfo ci) {
        BlockEntityRendererProvider<?, ?> provider = (BlockEntityRendererProvider<top.dext.centurybridge.step.v26_2.facade.BedBlockEntity,
            net.minecraft.client.renderer.blockentity.state.BedRenderState>) BedRenderer::new;
        PROVIDERS.put(CbS26Beds.BED(), provider);
    }
}
