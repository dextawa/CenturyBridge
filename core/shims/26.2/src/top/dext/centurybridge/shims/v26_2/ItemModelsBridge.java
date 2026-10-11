package top.dext.centurybridge.shims.v26_2;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v26_2.CbLegacyItemModels;

/** Registers centurybridge:legacy_overrides beside vanilla's item model types (see CbLegacyItemModels). */
@Mixin(ItemModels.class)
public abstract class ItemModelsBridge {

    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ItemModel.Unbaked>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void cb$legacyOverrides(CallbackInfo ci) {
        ID_MAPPER.put(CbLegacyItemModels.OVERRIDES, CbLegacyItemModels.Unbaked.CODEC);
    }
}
