package cb.swfapi;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fabricmc.fabric.api.item.v1.FabricTooltipFlag;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.tag.v1.FabricTagFile;
import net.minecraft.class_12279;
import net.minecraft.class_1646;
import net.minecraft.class_1914;
import net.minecraft.class_3852;
import net.minecraft.class_3853;
import net.minecraft.class_9306;
import net.minecraft.class_1269;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1836;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7475;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.11-era server mod for the Fabric API surface 26.1 removed or reshaped (step 1.21.11 -> 26.1, swfapi):
 * custom game rule categories, loot-api-v2 events, tag removal ("fabric:remove", a tag of this mod's data),
 * EntitySleepEvents' InteractionResult answers, ServerChunkEvents.CHUNK_LOAD's two-argument listener,
 * FabricItem/FabricItemStack.getRecipeRemainder and FabricTooltipFlag. Every check prints one
 * "CB-SWFAPI ok|FAIL ..." line; the last is "CB-SWFAPI DONE &lt;passed&gt;/&lt;checks&gt;".
 */
public class SwfapiTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-SWFAPI " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static final AtomicInteger LOOT_MODIFY = new AtomicInteger();
    static final AtomicInteger LOOT_REPLACE = new AtomicInteger();
    static final AtomicInteger LOOT_LOADED = new AtomicInteger();
    static final AtomicInteger CHUNK_LOADS = new AtomicInteger();

    static class_2960 id(String path) {
        return class_2960.method_60655("cb_swfapi", path);
    }

    @Override
    public void onInitialize() {
        CustomGameRuleCategory category = new CustomGameRuleCategory(id("cat"), class_2561.method_43470("CB Category"));
        class_12279<Boolean> rule = GameRuleBuilder.forBoolean(true).category(category).buildAndRegister(id("flag"));

        LootTableEvents.MODIFY.register((key, builder, source) -> LOOT_MODIFY.incrementAndGet());
        LootTableEvents.REPLACE.register((key, original, source) -> {
            LOOT_REPLACE.incrementAndGet();
            return null;
        });
        LootTableEvents.ALL_LOADED.register((resourceManager, registry) -> LOOT_LOADED.incrementAndGet());

        EntitySleepEvents.ALLOW_NEARBY_MONSTERS.register((player, pos, vanilla) -> class_1269.field_5812);

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk) -> CHUNK_LOADS.incrementAndGet());

        registerTrades();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> run(server, rule, category));
    }

    static void run(MinecraftServer server, class_12279<Boolean> rule, CustomGameRuleCategory category) {
        try {
            check(CustomGameRuleCategory.getCategory(rule).map(c -> c.equals(category)).orElse(false), "gamerule: getCategory(rule) is the custom category");
            check(new CustomGameRuleCategory(id("cat"), class_2561.method_43470("other")).equals(category), "gamerule: categories equal by id");
            check(rule.method_76152() == net.minecraft.class_5198.field_24100, "gamerule: the rule's vanilla category is MISC, as category(custom) set it");
            check("CB Category".equals(CustomGameRuleCategory.getCategory(rule).get().getName().getString()), "gamerule: the custom category's name");

            check(LOOT_MODIFY.get() > 0, "loot v2: MODIFY called " + LOOT_MODIFY.get() + " times");
            check(LOOT_REPLACE.get() > 0, "loot v2: REPLACE called " + LOOT_REPLACE.get() + " times");
            check(LOOT_LOADED.get() > 0, "loot v2: ALL_LOADED called " + LOOT_LOADED.get() + " times");

            class_6862<net.minecraft.class_1792> tag = class_6862.method_40092(class_7924.field_41197, id("removal"));
            boolean stone = false, dirt = false;
            for (class_6880<net.minecraft.class_1792> h : class_7923.field_41178.method_40286(tag)) {
                stone |= h.comp_349() == class_1802.field_20391;
                dirt |= h.comp_349() == class_1802.field_8831;
            }
            check(stone, "tag removal: stone stays in cb_swfapi:removal");
            check(!dirt, "tag removal: dirt removed from cb_swfapi:removal by fabric:remove");
            class_7475 file = class_7475.field_39269.parse(JsonOps.INSTANCE,
                JsonParser.parseString("{\"values\":[\"minecraft:stone\"],\"fabric:remove\":[\"minecraft:dirt\",\"#minecraft:logs\"]}")).getOrThrow();
            check(((FabricTagFile) (Object) file).remove().size() == 2, "tag removal: FabricTagFile.remove() of a parsed file");
            Object encoded = class_7475.field_39269.encodeStart(JsonOps.INSTANCE, file).getOrThrow();
            check(encoded.toString().contains("fabric:remove"), "tag removal: the codec writes fabric:remove back: " + encoded);

            class_1269 sleep = EntitySleepEvents.ALLOW_NEARBY_MONSTERS.invoker().allowNearbyMonsters(null, class_2338.field_10980, false);
            check(sleep.method_23665(), "sleep: a SUCCESS listener answers a consuming result (" + sleep + ")");

            check(CHUNK_LOADS.get() > 0, "chunk load: two-argument listener called " + CHUNK_LOADS.get() + " times");

            class_1799 milk = new class_1799(class_1802.field_8103);
            class_1799 remainder = ((FabricItem) class_1802.field_8103).getRecipeRemainder(milk);
            check(remainder.method_31574(class_1802.field_8550), "item: milk bucket's recipe remainder is a bucket (" + remainder + ")");
            check(((FabricItemStack) (Object) milk).getRecipeRemainder().method_31574(class_1802.field_8550), "item: stack's recipe remainder is a bucket");
            check(((FabricItem) class_1802.field_20391).getRecipeRemainder(new class_1799(class_1802.field_20391)).method_7960(), "item: stone has an empty remainder");

            check(!((FabricTooltipFlag) (Object) class_1836.field_41070).shouldDisplayAllInformation(), "tooltip flag: vanilla NORMAL does not display all information");

            trades(server);
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "threw " + t);
        }
        System.out.println("CB-SWFAPI DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED: " + FAILS));
    }

    // ---- villager trades (VillagerTrades tables, TradeOfferHelper) ----

    /** dirt -> stone: no vanilla trade has it */
    static final class_3853.class_1652 FARMER_MINE = (level, trader, random) ->
        new class_1914(new class_9306(class_1802.field_8831, 1), new class_1799(class_1802.field_20391), 12, 1, 0.05F);
    /** bucket -> milk bucket: the fisherman's only level-1 listing (the vanilla ones replaced) */
    static final class_3853.class_1652 FISHERMAN_ONLY = (level, trader, random) ->
        new class_1914(new class_9306(class_1802.field_8550, 1), new class_1799(class_1802.field_8103), 12, 1, 0.05F);
    /** dirt -> milk bucket, added to the wandering trader's buy pool */
    static final class_3853.class_1652 WANDERING_MINE = (level, trader, random) ->
        new class_1914(new class_9306(class_1802.field_8831, 2), new class_1799(class_1802.field_8103), 12, 1, 0.05F);
    static boolean placeholderAtInit;

    static void registerTrades() {
        it.unimi.dsi.fastutil.ints.Int2ObjectMap<class_3853.class_1652[]> farmer = class_3853.field_17067.get(class_3852.field_17056);
        placeholderAtInit = farmer != null && farmer.get(1) != null && farmer.get(1).length >= 1;
        TradeOfferHelper.registerVillagerOffers(class_3852.field_17056, 1, list -> list.add(FARMER_MINE));
        class_3853.field_17067.get(class_3852.field_17057).put(1, new class_3853.class_1652[] {FISHERMAN_ONLY});
        TradeOfferHelper.registerWanderingTraderOffers(builder -> builder
            .addOffersToPool(TradeOfferHelper.WanderingTraderOffersBuilder.BUY_ITEMS_POOL, WANDERING_MINE)
            .pool(id("pool"), 1, WANDERING_MINE));
    }

    static class_1646 villager(net.minecraft.class_3218 level, net.minecraft.class_5321<class_3852> profession) {
        class_1646 villager = (class_1646) net.minecraft.class_1299.field_6077.method_5883(level, net.minecraft.class_3730.field_16462);
        class_6880<class_3852> holder = class_7923.field_41195.method_47983(class_7923.field_41195.method_31140(profession));
        villager.method_7195(villager.method_7231().method_16921(holder).method_16920(1));
        return villager;
    }

    static void trades(MinecraftServer server) {
        net.minecraft.class_3218 level = server.method_30002();
        check(placeholderAtInit, "trades: the farmer's level-1 listings exist in an initializer, before any registry");
        class_3853.class_1652[] farmer = class_3853.field_17067.get(class_3852.field_17056).get(1);
        boolean mine = false;
        int vanillaOffers = 0;
        class_1646 probe = villager(level, class_3852.field_17056);
        for (class_3853.class_1652 listing : farmer) {
            if (listing == FARMER_MINE) {
                mine = true;
            } else if (listing.method_7246(level, probe, level.method_8409()) != null) {
                vanillaOffers++;
            }
        }
        check(mine && farmer.length >= 3, "trades: farmer level 1 holds TradeOfferHelper's listing and the expanded vanilla ones (" + farmer.length + ")");
        check(vanillaOffers >= 2, "trades: the vanilla farmer listings make offers from 26.1's data (" + vanillaOffers + ")");
        boolean picked = false;
        boolean twoEach = true;
        for (int i = 0; i < 80 && !picked; i++) {
            net.minecraft.class_1916 offers = villager(level, class_3852.field_17056).method_8264();
            twoEach &= offers.size() == 2;
            for (class_1914 offer : offers) {
                picked |= offer.method_8250().method_31574(class_1802.field_20391);
            }
        }
        check(twoEach, "trades: every new farmer gets two level-1 offers");
        check(picked, "trades: a farmer picked TradeOfferHelper's listing from the table");
        net.minecraft.class_1916 fisher = villager(level, class_3852.field_17057).method_8264();
        check(fisher.size() == 1 && fisher.get(0).method_8250().method_31574(class_1802.field_8103),
            "trades: the fisherman's level 1 replaced in TRADES offers only the mod's listing (" + fisher.size() + ")");
        check(class_3853.field_17724.size() == 4 && java.util.Arrays.asList(class_3853.field_17724.get(0).getLeft()).contains(WANDERING_MINE),
            "trades: wandering pools are buy / special / common + the mod's, the buy pool holds the added listing");
        net.minecraft.class_3989 trader = (net.minecraft.class_3989) net.minecraft.class_1299.field_17713.method_5883(level, net.minecraft.class_3730.field_16462);
        int count = trader.method_8264().size();
        check(count == 10, "trades: a wandering trader gets 2 + 2 + 5 + 1 offers (" + count + ")");
    }
}
