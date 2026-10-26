package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 1.21.11 new VillagerProfession(name, heldJobSite, acquirableJobSite, requestedItems, secondaryJobSites, workSound)
 * (class_3852): 26.1 adds the trade sets per level (tradeSetsByLevel). A 1.21.11 profession had no trades of its own:
 * they came from VillagerTrades.TRADES or Fabric API's TradeOfferHelper under its key, which the layer gives back
 * separately (facade/VillagerTrades over 26.1's trade data, rt/CbFapiTrades). Its trade sets are none, as 26.1's own professions without trades
 * (NONE, NITWIT: Int2ObjectMap.ofEntries()).
 */
public final class CbWallProfessions {

    private CbWallProfessions() {
    }

    public static VillagerProfession newProfession(Component name, Predicate<Holder<PoiType>> heldJobSite,
                                                   Predicate<Holder<PoiType>> acquirableJobSite, ImmutableSet<Item> requestedItems,
                                                   ImmutableSet<Block> secondaryJobSites, SoundEvent workSound) {
        return new VillagerProfession(name, heldJobSite, acquirableJobSite, requestedItems, secondaryJobSites, workSound,
            Int2ObjectMap.ofEntries());
    }
}
