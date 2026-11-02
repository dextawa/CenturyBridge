package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTrades;
import top.dext.centurybridge.step.v26_1.rt.CbWallTradeOffers;

/** 1.21.11's WanderingTrader.updateTrades, where a 1.21.11 mod edited the pools (CbFapiTrades.wanderingVanilla; otherwise 26.1's body runs): each pool's count of distinct listings from 1.21.11's table (rt/CbFapiTrades), drawn through this.addOffersFromItemListings as 1.21.11 did (a subclass overriding it runs: rt/CbWallTradeOffers) */
@Mixin(WanderingTrader.class)
public abstract class CbFapiWanderingTraderTradesMixin {

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTrades(ServerLevel level, CallbackInfo ci) {
        if (CbFapiTrades.wanderingVanilla()) {
            return; // the pools are vanilla's: 26.1's own body (and every native mixin in it) runs
        }
        ci.cancel();
        WanderingTrader self = (WanderingTrader) (Object) this;
        for (Pair<VillagerTrades.ItemListing[], Integer> pool : VillagerTrades.field_17724) {
            ((CbWallTradeOffers) self).cb$addOffersFromItemListings(level, self.getOffers(), pool.getLeft(), pool.getRight());
        }
    }
}
