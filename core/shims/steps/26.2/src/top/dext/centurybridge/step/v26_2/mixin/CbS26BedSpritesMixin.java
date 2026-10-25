package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26LegacyBedSprites;

/**
 * The atlas source type that rebuilds 26.1.2's vanilla bed sprites from 26.2's bed block textures
 * (rt/CbS26LegacyBedSprites), registered alongside 26.2's own source types; the layer's atlases/blocks.json names it.
 */
@Environment(EnvType.CLIENT)
@Mixin(SpriteSources.class)
public abstract class CbS26BedSpritesMixin {

    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SpriteSource>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void cbS26$legacyBeds(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.fromNamespaceAndPath("centurybridge", "legacy_beds"), CbS26LegacyBedSprites.MAP_CODEC);
    }
}
