package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.trading.MerchantOffers;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades;

/**
 * 1.21.11's AbstractVillager.addOffersFromItemListings(level, offers, listings, count) (class_3988.method_19170), which
 * 26.1 replaced by data-driven trade sets. CbWallAbstractVillagerOffersMixin gives the method back on every
 * AbstractVillager under the name the wall table gives it, so a 1.21.11 subclass calls and overrides it; the layer's
 * own callers (1.21.11's Villager / WanderingTrader.updateTrades) reach it through this interface, which dispatches
 * virtually as 1.21.11's this.addOffersFromItemListings did -- a mod's override of it runs for vanilla's trades too.
 */
public interface CbWallTradeOffers {

    /** this.addOffersFromItemListings(level, offers, listings, count), dispatched virtually */
    void cb$addOffersFromItemListings(ServerLevel level, MerchantOffers offers, VillagerTrades.ItemListing[] listings, int count);
}
