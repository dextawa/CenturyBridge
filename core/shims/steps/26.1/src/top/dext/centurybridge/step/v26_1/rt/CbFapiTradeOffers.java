package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import top.dext.centurybridge.step.v26_1.compat.fapi.object.builder.v1.trade.TradeOfferHelper;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades.ItemListing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/** 1.21.11 Fabric API's TradeOfferInternals (its bodies) over 1.21.11's trade tables (facade/VillagerTrades) */
public final class CbFapiTradeOffers {
    private CbFapiTradeOffers() {
    }

    /** 1.21.11: EXPERIMENTAL_TRADES made a mutable copy holding every TRADES profession it lacks */
    private static void initVillagerTrades() {
        Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> experimental = new HashMap<>(VillagerTrades.field_45128);
        for (Map.Entry<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> e : VillagerTrades.field_17067.entrySet()) {
            if (!experimental.containsKey(e.getKey())) {
                experimental.put(e.getKey(), e.getValue());
            }
        }
        VillagerTrades.field_45128 = experimental;
    }

    public static synchronized void registerVillagerOffers(ResourceKey<VillagerProfession> profession, int level,
                                                           TradeOfferHelper.VillagerOffersAdder factory) {
        Objects.requireNonNull(profession, "VillagerProfession may not be null.");
        initVillagerTrades();
        registerOffers(VillagerTrades.field_17067.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>()), level,
                trades -> factory.onRegister(trades, false));
        registerOffers(VillagerTrades.field_45128.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>()), level,
                trades -> factory.onRegister(trades, true));
    }

    private static void registerOffers(Int2ObjectMap<ItemListing[]> leveledTradeMap, int level, Consumer<List<ItemListing>> factory) {
        List<ItemListing> list = new ArrayList<>();
        factory.accept(list);
        ItemListing[] originalEntries = leveledTradeMap.computeIfAbsent(level, key -> new ItemListing[0]);
        ItemListing[] addedEntries = list.toArray(new ItemListing[0]);
        leveledTradeMap.put(level, ArrayUtils.addAll(originalEntries, addedEntries));
    }

    /** 1.21.11 Fabric API's WanderingTraderOffersBuilderImpl */
    public static final class WanderingTraderOffersBuilderImpl implements TradeOfferHelper.WanderingTraderOffersBuilder {
        private static final Object2IntMap<Identifier> ID_TO_INDEX = new Object2IntOpenHashMap<>();
        private static final Map<Identifier, ItemListing[]> DELAYED_MODIFICATIONS = new HashMap<>();

        static {
            ID_TO_INDEX.put(BUY_ITEMS_POOL, 0);
            ID_TO_INDEX.put(SELL_SPECIAL_ITEMS_POOL, 1);
            ID_TO_INDEX.put(SELL_COMMON_ITEMS_POOL, 2);
        }

        /** 1.21.11: WANDERING_TRADER_TRADES made a mutable copy */
        static void initWanderingTraderTrades() {
            VillagerTrades.field_17724 = new ArrayList<>(VillagerTrades.field_17724);
        }

        @Override
        public TradeOfferHelper.WanderingTraderOffersBuilder pool(Identifier id, int count, ItemListing... factories) {
            if (factories.length == 0) {
                throw new IllegalArgumentException("cannot add empty pool");
            }
            if (count <= 0) {
                throw new IllegalArgumentException("count must be positive");
            }
            Objects.requireNonNull(id, "id cannot be null");
            if (ID_TO_INDEX.containsKey(id)) {
                throw new IllegalArgumentException("pool id %s is already registered".formatted(id));
            }
            Pair<ItemListing[], Integer> pool = Pair.of(factories, count);
            initWanderingTraderTrades();
            ID_TO_INDEX.put(id, VillagerTrades.field_17724.size());
            VillagerTrades.field_17724.add(pool);
            ItemListing[] delayedModifications = DELAYED_MODIFICATIONS.remove(id);
            if (delayedModifications != null) {
                addOffersToPool(id, delayedModifications);
            }
            return this;
        }

        @Override
        public TradeOfferHelper.WanderingTraderOffersBuilder addOffersToPool(Identifier pool, ItemListing... factories) {
            if (!ID_TO_INDEX.containsKey(pool)) {
                DELAYED_MODIFICATIONS.compute(pool, (id, current) -> current == null ? factories : ArrayUtils.addAll(current, factories));
                return this;
            }
            int index = ID_TO_INDEX.getInt(pool);
            initWanderingTraderTrades();
            Pair<ItemListing[], Integer> poolPair = VillagerTrades.field_17724.get(index);
            ItemListing[] modified = ArrayUtils.addAll(poolPair.getLeft(), factories);
            VillagerTrades.field_17724.set(index, Pair.of(modified, poolPair.getRight()));
            return this;
        }
    }
}
