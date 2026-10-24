package top.dext.centurybridge.step.v26_1.compat.fapi.object.builder.v1.trade;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTradeOffers;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 1.21.11's net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper, which 26.1's Fabric API removed (26.1
 * trades are data). Its bodies are 1.21.11's, over 1.21.11's trade tables (facade/VillagerTrades), which villagers and
 * wandering traders pick from (rt/CbFapiTrades).
 */
public final class TradeOfferHelper {
    private TradeOfferHelper() {
    }

    public static void registerVillagerOffers(ResourceKey<VillagerProfession> profession, int level,
                                              Consumer<List<VillagerTrades.ItemListing>> factories) {
        registerVillagerOffers(profession, level, (list, rebalanced) -> factories.accept(list));
    }

    public static void registerVillagerOffers(ResourceKey<VillagerProfession> profession, int level, VillagerOffersAdder factories) {
        CbFapiTradeOffers.registerVillagerOffers(profession, level, factories);
    }

    public static synchronized void registerWanderingTraderOffers(Consumer<WanderingTraderOffersBuilder> factory) {
        factory.accept(new CbFapiTradeOffers.WanderingTraderOffersBuilderImpl());
    }

    @FunctionalInterface
    public interface VillagerOffersAdder {
        void onRegister(List<VillagerTrades.ItemListing> factories, boolean rebalanced);
    }

    public interface WanderingTraderOffersBuilder {
        Identifier BUY_ITEMS_POOL = Identifier.withDefaultNamespace("buy_items");
        Identifier SELL_SPECIAL_ITEMS_POOL = Identifier.withDefaultNamespace("sell_special_items");
        Identifier SELL_COMMON_ITEMS_POOL = Identifier.withDefaultNamespace("sell_common_items");

        WanderingTraderOffersBuilder pool(Identifier id, int count, VillagerTrades.ItemListing... factories);

        default WanderingTraderOffersBuilder pool(Identifier id, int count, Collection<? extends VillagerTrades.ItemListing> factories) {
            return pool(id, count, factories.toArray(VillagerTrades.ItemListing[]::new));
        }

        default WanderingTraderOffersBuilder addAll(Identifier id, Collection<? extends VillagerTrades.ItemListing> factories) {
            return pool(id, factories.size(), factories);
        }

        default WanderingTraderOffersBuilder addAll(Identifier id, VillagerTrades.ItemListing... factories) {
            return pool(id, factories.length, factories);
        }

        WanderingTraderOffersBuilder addOffersToPool(Identifier pool, VillagerTrades.ItemListing... factories);

        default WanderingTraderOffersBuilder addOffersToPool(Identifier pool, Collection<VillagerTrades.ItemListing> factories) {
            return addOffersToPool(pool, factories.toArray(VillagerTrades.ItemListing[]::new));
        }
    }
}
