package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.tuple.Pair;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades;
import top.dext.centurybridge.step.v26_1.facade.VillagerTrades.ItemListing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 1.21.11's trade tables (facade/VillagerTrades) over 26.1's trade data.
 * <ul>
 * <li>The tables start as 1.21.11 shaped them -- every profession with trade sets, levels 1 to 5 (TRADES), librarian
 *     and armorer (EXPERIMENTAL_TRADES, 1.21.11's two), the wandering trader's buying / special / common pools with
 *     1.21.11's counts 2, 2, 5 -- each level or pool holding one placeholder listing for the 26.1 trade set it stands
 *     for (the profession's trade set for that level, TradeSets.WANDERING_TRADER_*). Mods edit them before any registry
 *     exists, as they edited 1.21.11's.</li>
 * <li>When a server's registries and tags are loaded (server construction, every data pack reload) each placeholder
 *     becomes one listing per trade of its set, in the set's order, where it stands; listings expanded from a set
 *     earlier are expanded again from the current set when all of them are still there, and otherwise (a mod removed
 *     some) the remaining ones are kept. A profession registered after the tables were made gets its levels then.
 *     Everything mods put in the tables stays as they put it.</li>
 * <li>A listing of a 26.1 trade answers that trade's offer for the trader, as 26.1 makes it (its loot context, the
 *     trader's random); null when the trade's merchant predicate rejects the trader.</li>
 * <li>Villagers and wandering traders pick from the tables with 1.21.11's bodies (mixin/CbFapiVillagerTradesMixin,
 *     CbFapiWanderingTraderTradesMixin): two distinct listings of the villager's level (EXPERIMENTAL_TRADES first when
 *     trade rebalance is enabled), each pool's count of distinct listings. 26.1's per-set amount, duplicates flag and
 *     random sequence do not apply: 1.21.11's picks did not have them.</li>
 * </ul>
 */
public final class CbFapiTrades {
    private CbFapiTrades() {
    }

    // ---- the initial tables ----

    public static Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> initialTrades() {
        Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> trades = new HashMap<>();
        BuiltInRegistries.VILLAGER_PROFESSION.listElements().forEach(ref -> {
            Int2ObjectMap<ItemListing[]> levels = levels(ref.value());
            if (!levels.isEmpty()) {
                trades.put(ref.key(), levels);
            }
        });
        return trades;
    }

    public static Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> initialExperimentalTrades() {
        Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> trades = new HashMap<>();
        for (ResourceKey<VillagerProfession> key : List.of(VillagerProfession.LIBRARIAN, VillagerProfession.ARMORER)) {
            BuiltInRegistries.VILLAGER_PROFESSION.getOptional(key).ifPresent(profession -> trades.put(key, levels(profession)));
        }
        return trades;
    }

    public static List<Pair<ItemListing[], Integer>> initialWanderingTrades() {
        List<Pair<ItemListing[], Integer>> pools = new ArrayList<>();
        pools.add(Pair.of(new ItemListing[] {new SetListing(TradeSets.WANDERING_TRADER_BUYING)}, 2));
        pools.add(Pair.of(new ItemListing[] {new SetListing(TradeSets.WANDERING_TRADER_UNCOMMON)}, 2));
        pools.add(Pair.of(new ItemListing[] {new SetListing(TradeSets.WANDERING_TRADER_COMMON)}, 5));
        return pools;
    }

    private static Int2ObjectMap<ItemListing[]> levels(VillagerProfession profession) {
        Int2ObjectMap<ItemListing[]> levels = new Int2ObjectOpenHashMap<>();
        for (Int2ObjectMap.Entry<ResourceKey<TradeSet>> e : profession.tradeSetsByLevel().int2ObjectEntrySet()) {
            if (e.getValue() != null) {
                levels.put(e.getIntKey(), new ItemListing[] {new SetListing(e.getValue())});
            }
        }
        return levels;
    }

    // ---- expansion at registry time ----

    private static boolean listening;

    /**
     * a server was constructed (its registries and tags loaded): the tables take its trade data, and again whenever its
     * tags reload (Fabric API's TAGS_LOADED, server side)
     */
    public static synchronized void serverLoaded(net.minecraft.server.MinecraftServer server) {
        if (!listening) {
            listening = true;
            net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
                if (!client) {
                    expand(registries);
                }
            });
        }
        expand(server.registryAccess());
    }

    /** the tables against a server's loaded registries and tags */
    public static synchronized void expand(RegistryAccess registries) {
        Optional<Registry<TradeSet>> lookup = registries.lookup(Registries.TRADE_SET);
        if (lookup.isEmpty()) {
            return;
        }
        Registry<TradeSet> sets = lookup.get();
        BuiltInRegistries.VILLAGER_PROFESSION.listElements().forEach(ref -> {
            if (!VillagerTrades.field_17067.containsKey(ref.key())) {
                Int2ObjectMap<ItemListing[]> levels = levels(ref.value());
                if (!levels.isEmpty()) {
                    VillagerTrades.field_17067.put(ref.key(), levels);
                }
            }
        });
        java.util.Set<Int2ObjectMap<ItemListing[]>> seen = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        expandTable(VillagerTrades.field_17067, sets, seen);
        expandTable(VillagerTrades.field_45128, sets, seen);
        List<Pair<ItemListing[], Integer>> pools = VillagerTrades.field_17724;
        for (int i = 0; i < pools.size(); i++) {
            Pair<ItemListing[], Integer> pool = pools.get(i);
            ItemListing[] expanded = expandListings(pool.getLeft(), sets);
            if (expanded != pool.getLeft()) {
                pools.set(i, Pair.of(expanded, pool.getRight()));
            }
        }
    }

    private static void expandTable(Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> table, Registry<TradeSet> sets,
                                    java.util.Set<Int2ObjectMap<ItemListing[]>> seen) {
        for (Int2ObjectMap<ItemListing[]> levels : table.values()) {
            if (levels == null || !seen.add(levels)) {
                continue;
            }
            for (Int2ObjectMap.Entry<ItemListing[]> e : levels.int2ObjectEntrySet()) {
                ItemListing[] expanded = expandListings(e.getValue(), sets);
                if (expanded != e.getValue()) {
                    e.setValue(expanded);
                }
            }
        }
    }

    /** the listings with every placeholder (and every complete earlier expansion) expanded from the current sets */
    private static ItemListing[] expandListings(ItemListing[] listings, Registry<TradeSet> sets) {
        if (listings == null) {
            return null;
        }
        Map<Expansion, Integer> present = new IdentityHashMap<>();
        boolean any = false;
        for (ItemListing listing : listings) {
            if (listing instanceof SetListing) {
                any = true;
            } else if (listing instanceof TradeListing trade) {
                any = true;
                present.merge(trade.expansion, 1, Integer::sum);
            }
        }
        if (!any) {
            return listings;
        }
        List<ItemListing> out = new ArrayList<>(listings.length);
        java.util.Set<Expansion> replaced = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        for (ItemListing listing : listings) {
            if (listing instanceof SetListing set) {
                out.addAll(new Expansion(set.set).listings(sets));
            } else if (listing instanceof TradeListing trade) {
                Expansion old = trade.expansion;
                if (present.get(old) == old.size) {
                    if (replaced.add(old)) {
                        out.addAll(new Expansion(old.set).listings(sets));
                    }
                } else {
                    out.add(listing);
                }
            } else {
                out.add(listing);
            }
        }
        return out.toArray(new ItemListing[0]);
    }

    /** one expansion of a trade set into listings */
    private static final class Expansion {
        final ResourceKey<TradeSet> set;
        int size;

        Expansion(ResourceKey<TradeSet> set) {
            this.set = set;
        }

        List<ItemListing> listings(Registry<TradeSet> sets) {
            List<ItemListing> out = new ArrayList<>();
            Optional<TradeSet> tradeSet = sets.getOptional(this.set);
            if (tradeSet.isPresent()) {
                HolderSet<VillagerTrade> trades = tradeSet.get().getTrades();
                for (int i = 0; i < trades.size(); i++) {
                    out.add(new TradeListing(this, trades.get(i)));
                }
            }
            this.size = out.size();
            return out;
        }
    }

    /** a placeholder for a whole 26.1 trade set (before registries exist): an offer of one of its trades */
    private record SetListing(ResourceKey<TradeSet> set) implements ItemListing {
        @Override
        public MerchantOffer method_7246(ServerLevel level, Entity trader, RandomSource random) {
            Optional<TradeSet> tradeSet = level.registryAccess().lookupOrThrow(Registries.TRADE_SET).getOptional(this.set);
            if (tradeSet.isEmpty() || tradeSet.get().getTrades().size() == 0) {
                return null;
            }
            HolderSet<VillagerTrade> trades = tradeSet.get().getTrades();
            return offer(trades.get(random.nextInt(trades.size())).value(), level, trader, random);
        }
    }

    /** one 26.1 trade as a 1.21.11 listing */
    private static final class TradeListing implements ItemListing {
        final Expansion expansion;
        final Holder<VillagerTrade> trade;

        TradeListing(Expansion expansion, Holder<VillagerTrade> trade) {
            this.expansion = expansion;
            this.trade = trade;
        }

        @Override
        public MerchantOffer method_7246(ServerLevel level, Entity trader, RandomSource random) {
            return offer(this.trade.value(), level, trader, random);
        }

        @Override
        public String toString() {
            return "VillagerTrade[" + this.trade.unwrapKey().map(Object::toString).orElse("inline") + " of " + this.expansion.set + "]";
        }
    }

    /** a 26.1 trade's offer for the trader, in the loot context 26.1's AbstractVillager.addOffersFromTradeSet builds */
    private static MerchantOffer offer(VillagerTrade trade, ServerLevel level, Entity trader, RandomSource random) {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, trader.position())
                .withParameter(LootContextParams.THIS_ENTITY, trader)
                .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
                .create(LootContextParamSets.VILLAGER_TRADE);
        LootContext context = new LootContext.Builder(params).withOptionalRandomSource(random).create(Optional.empty());
        return trade.getOffer(context);
    }

    // ---- picking (1.21.11 bodies) ----

    /** 1.21.11 Villager.updateTrades' table lookup: the listings for the villager's profession and level, or null */
    public static ItemListing[] villagerListings(ServerLevel level, Holder<VillagerProfession> profession, int villagerLevel) {
        Int2ObjectMap<ItemListing[]> levels = villagerLevels(level, profession);
        return levels == null || levels.isEmpty() ? null : levels.get(villagerLevel);
    }

    /** the level table of the villager's profession (EXPERIMENTAL_TRADES first under trade rebalance), or null */
    public static Int2ObjectMap<ItemListing[]> villagerLevels(ServerLevel level, Holder<VillagerProfession> profession) {
        ResourceKey<VillagerProfession> key = profession.unwrapKey().orElse(null);
        if (key == null) {
            return null;
        }
        if (level.enabledFeatures().contains(net.minecraft.world.flag.FeatureFlags.TRADE_REBALANCE)) {
            Int2ObjectMap<ItemListing[]> experimental = VillagerTrades.field_45128.get(key);
            return experimental != null ? experimental : VillagerTrades.field_17067.get(key);
        }
        return VillagerTrades.field_17067.get(key);
    }

    /** 1.21.11 AbstractVillager.addOffersFromItemListings */
    public static void addOffersFromItemListings(ServerLevel level, Entity trader, MerchantOffers offers, ItemListing[] listings, int count) {
        ArrayList<ItemListing> remaining = new ArrayList<>(java.util.Arrays.asList(listings));
        int added = 0;
        RandomSource random = trader.getRandom();
        while (added < count && !remaining.isEmpty()) {
            MerchantOffer offer = remaining.remove(random.nextInt(remaining.size())).method_7246(level, trader, random);
            if (offer != null) {
                offers.add(offer);
                added++;
            }
        }
    }
}
