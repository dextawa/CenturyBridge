package top.dext.centurybridge.shims.v26_2;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v26_2.CbLegacyItemModels;

/** Registers centurybridge:legacy_color beside vanilla's tint sources (see CbLegacyItemModels). */
@Mixin(ItemTintSources.class)
public abstract class ItemTintSourcesBridge {

    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ItemTintSource>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void cb$legacyColor(CallbackInfo ci) {
        ID_MAPPER.put(CbLegacyItemModels.LEGACY_COLOR, CbLegacyItemModels.LegacyColor.CODEC);
    }
}
