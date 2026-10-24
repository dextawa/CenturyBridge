package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbFapiBlockLayers;

import java.util.List;

/** a block item of a block 1.21.11's BlockRenderLayerMap mapped renders its quads in the mapped layer (rt/CbFapiBlockLayers.itemQuads) */
@Environment(EnvType.CLIENT)
@Mixin(CuboidItemModelWrapper.class)
public abstract class CbFapiCuboidItemModelMixin {

    @Redirect(method = "update", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/geometry/QuadCollection;getAll()Ljava/util/List;", ordinal = 0))
    private List<BakedQuad> cb$mappedLayerQuads(QuadCollection quads, ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver,
                                                ItemDisplayContext context, ClientLevel level, ItemOwner owner, int seed) {
        return CbFapiBlockLayers.itemQuads(quads, stack);
    }
}
