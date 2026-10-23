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
import net.fabricmc.fabric.api.tag.v1.FabricTagFile;
import net.minecraft.class_12279;
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
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "threw " + t);
        }
        System.out.println("CB-SWFAPI DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED: " + FAILS));
    }
}
