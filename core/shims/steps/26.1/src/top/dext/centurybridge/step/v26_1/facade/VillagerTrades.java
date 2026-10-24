package top.dext.centurybridge.step.v26_1.facade;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import org.apache.commons.lang3.tuple.Pair;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTrades;

import java.util.List;
import java.util.Map;

/**
 * 1.21.11's VillagerTrades (net/minecraft/class_3853) on 26.1, whose trades are data (VillagerTrade / TradeSet
 * registries). The three tables are 1.21.11's, in 1.21.11's shape: profession -> level -> listings, and the wandering
 * trader's pools with their counts. Each vanilla level (and pool) starts as one placeholder listing for the 26.1 trade
 * set the profession names for that level; once a server's registries and tags are loaded the placeholders become one
 * listing per trade of the set (rt/CbFapiTrades), with every edit mods made to the tables kept on top. Villagers and
 * wandering traders pick their offers from these tables as 1.21.11 did. Member names are 1.21.11's intermediary
 * names; the tables are not final (1.21.11's Fabric API replaced two of them with mutable copies).
 */
public class VillagerTrades {

    /** TRADES */
    public static Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> field_17067 = CbFapiTrades.initialTrades();
    /** WANDERING_TRADER_TRADES */
    public static List<Pair<ItemListing[], Integer>> field_17724 = CbFapiTrades.initialWanderingTrades();
    /** EXPERIMENTAL_TRADES */
    public static Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> field_45128 = CbFapiTrades.initialExperimentalTrades();

    public VillagerTrades() {
    }

    /** 1.21.11's VillagerTrades.ItemListing (net/minecraft/class_3853$class_1652) */
    @FunctionalInterface
    public interface ItemListing {
        /** getOffer: the offer, or null for none */
        MerchantOffer method_7246(ServerLevel level, Entity trader, RandomSource random);
    }
}
