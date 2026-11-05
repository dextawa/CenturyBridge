package top.dext.centurybridge.step.v26_1.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTrades;
import top.dext.centurybridge.step.v26_1.rt.CbWallTradeOffers;

/** 1.21.11's Villager.updateTrades, where a 1.21.11 mod edited the profession's level (CbFapiTrades.villagerVanilla; otherwise 26.1's body runs): two distinct listings of the villager's level from 1.21.11's tables (rt/CbFapiTrades), drawn through this.addOffersFromItemListings as 1.21.11 did (a subclass overriding it runs: rt/CbWallTradeOffers) */
@Mixin(Villager.class)
public abstract class CbFapiVillagerTradesMixin {
    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    private void increaseMerchantCareer(ServerLevel level) {
    }

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void cb$legacyTrades(ServerLevel level, CallbackInfo ci) {
        VillagerData data = getVillagerData();
        if (CbFapiTrades.villagerVanilla(level, data.profession(), data.level())) {
            return; // no 1.21.11 mod edited this profession's level: 26.1's own body (and every native mixin in it) runs
        }
        ci.cancel();
        Villager self = (Villager) (Object) this;
        Int2ObjectMap<VillagerTrades.ItemListing[]> levels = CbFapiTrades.villagerLevels(level, data.profession());
        if (levels == null || levels.isEmpty()) {
            return;
        }
        VillagerTrades.ItemListing[] listings = levels.get(data.level());
        if (listings == null) {
            return;
        }
        ((CbWallTradeOffers) self).cb$addOffersFromItemListings(level, self.getOffers(), listings, 2);
        if (SharedConstants.DEBUG_UNLOCK_ALL_TRADES && data.level() < levels.size()) {
            increaseMerchantCareer(level);
        }
    }
}
