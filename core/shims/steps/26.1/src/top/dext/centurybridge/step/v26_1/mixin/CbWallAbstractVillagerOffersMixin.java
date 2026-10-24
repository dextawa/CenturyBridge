package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTrades;
import top.dext.centurybridge.step.v26_1.rt.CbWallTradeOffers;

/**
 * 1.21.11's AbstractVillager.addOffersFromItemListings(level, offers, listings, count) given back (rt/CbWallTradeOffers):
 * up to count distinct listings drawn at random with the merchant's random source, a listing that offers nothing
 * (null) not counting. 26.1's private static addOffersFromItemListings(LootContext, ...) has another descriptor.
 */
@Mixin(AbstractVillager.class)
public abstract class CbWallAbstractVillagerOffersMixin implements CbWallTradeOffers {

    /** 1.21.11 AbstractVillager.addOffersFromItemListings (class_3988.method_19170), 1.21.11's body */
    protected void addOffersFromItemListings(ServerLevel level, MerchantOffers offers, VillagerTrades.ItemListing[] listings, int count) {
        CbFapiTrades.addOffersFromItemListings(level, (AbstractVillager) (Object) this, offers, listings, count);
    }

    @Override
    public void cb$addOffersFromItemListings(ServerLevel level, MerchantOffers offers, VillagerTrades.ItemListing[] listings, int count) {
        this.addOffersFromItemListings(level, offers, listings, count);
    }
}
