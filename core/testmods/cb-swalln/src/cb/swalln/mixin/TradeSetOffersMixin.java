package cb.swalln.mixin;

import cb.swalln.Driver;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 26.1's AbstractVillager.addOffersFromTradeSet (reached from 26.1's own Villager / WanderingTrader.updateTrades): counts its calls per trade set */
@Mixin(AbstractVillager.class)
public abstract class TradeSetOffersMixin {
    @Inject(method = "addOffersFromTradeSet", at = @At("HEAD"))
    private void cbswalln$count(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> set, CallbackInfo ci) {
        Driver.TRADE_SETS.add(set.identifier().toString());
    }
}
