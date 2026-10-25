package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.facade.BedSpecialRenderer;

/**
 * 26.1.2's SpecialModelRenderers.bootstrap registered the item model type "minecraft:bed" (BedSpecialRenderer.Unbaked
 * .MAP_CODEC, javap); 26.2's has none (its bed items are composites of block models). 26.1.2's item models naming it
 * (a 26.1.2 mod's bed items) parse again once it is registered alongside 26.2's types.
 */
@Environment(EnvType.CLIENT)
@Mixin(SpecialModelRenderers.class)
public abstract class CbS26BedSpecialMixin {

    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked<?>>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void cbS26$bed(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.withDefaultNamespace("bed"), BedSpecialRenderer.Unbaked.MAP_CODEC);
    }
}
