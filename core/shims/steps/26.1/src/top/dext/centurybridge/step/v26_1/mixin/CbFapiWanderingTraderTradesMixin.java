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

/** 1.21.11's WanderingTrader.updateTrades: each pool's count of distinct listings from 1.21.11's table (rt/CbFapiTrades) */
@Mixin(WanderingTrader.class)
public abstract class CbFapiWanderingTraderTradesMixin {

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTrades(ServerLevel level, CallbackInfo ci) {
        ci.cancel();
        WanderingTrader self = (WanderingTrader) (Object) this;
        for (Pair<VillagerTrades.ItemListing[], Integer> pool : VillagerTrades.field_17724) {
            CbFapiTrades.addOffersFromItemListings(level, self, self.getOffers(), pool.getLeft(), pool.getRight());
        }
    }
}
