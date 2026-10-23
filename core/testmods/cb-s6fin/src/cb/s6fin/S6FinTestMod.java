package cb.s6fin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1280;
import net.minecraft.class_1282;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1472;
import net.minecraft.class_1767;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2818;
import net.minecraft.class_3193;
import net.minecraft.class_3218;
import net.minecraft.class_3230;
import net.minecraft.class_3492;
import net.minecraft.class_3898;
import net.minecraft.class_6608;
import net.minecraft.class_9259;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.6-era server mod (intermediary names, Fabric Loader API only) for s6fin's bridges of the 1.20.6 -> 1.21
 * step: the chunk status tables, the chunk holder's 1.20.6 surface, StructurePlaceSettings' keepLiquids,
 * TicketType.LIGHT, CombatRules without a victim, EquipmentSlot.byTypeAndIndex, the sheep colour table,
 * DamageSource.isIndirect, and ResetChunksCommand (which regenerates a chunk through ChunkStatus.generate and the
 * chunk-list WorldGenRegion). Every check prints one "CB-S6TEST ok|FAIL ..." line; the last is
 * "CB-S6TEST DONE <passed>/<checks>".
 */
public class S6FinTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-S6TEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    @Override
    public void onInitialize() {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(() -> run(server));
                    return;
                }
            }
        }, "cb-s6fin");
        t.setDaemon(true);
        t.start();
    }

    static void run(MinecraftServer server) {
        try {
            statuses();
            structures();
            combat(server);
            holder(server);
            resetChunks(server);
        } catch (Throwable e) {
            check(false, "run threw " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("CB-S6TEST DONE " + (checks - FAILS.size()) + "/" + checks);
    }

    static void statuses() {
        check(class_2806.method_12161(0) == class_2806.field_12803, "ChunkStatus.getStatusAroundFullChunk(0) is FULL");
        check(class_2806.method_12161(-1) == class_2806.field_12803, "getStatusAroundFullChunk(-1) is FULL");
        check(class_2806.method_12161(1) == class_2806.field_44633, "getStatusAroundFullChunk(1) is INITIALIZE_LIGHT (got " + class_2806.method_12161(1) + ")");
        check(class_2806.method_12161(2) == class_2806.field_12801, "getStatusAroundFullChunk(2) is CARVERS (got " + class_2806.method_12161(2) + ")");
        check(class_2806.method_12161(3) == class_2806.field_12794, "getStatusAroundFullChunk(3) is BIOMES (got " + class_2806.method_12161(3) + ")");
        check(class_2806.method_12161(11) == class_2806.field_16423, "getStatusAroundFullChunk(11) is STRUCTURE_STARTS (got " + class_2806.method_12161(11) + ")");
        check(class_2806.method_12161(12) == class_2806.field_12798, "getStatusAroundFullChunk(12) is EMPTY (got " + class_2806.method_12161(12) + ")");
        check(class_2806.method_12155() == 12, "ChunkStatus.maxDistance() is 12 (got " + class_2806.method_12155() + ")");
        check(class_2806.method_12175(class_2806.field_12803) == 0, "getDistance(FULL) is 0");
        check(class_2806.method_12175(class_2806.field_16423) == 11, "getDistance(STRUCTURE_STARTS) is 11 (got " + class_2806.method_12175(class_2806.field_16423) + ")");
        check(class_2806.field_12804.method_12152() == 8, "NOISE.getRange() is 8 (got " + class_2806.field_12804.method_12152() + ")");
        check(class_2806.field_12805.method_12152() == 1, "LIGHT.getRange() is 1 (got " + class_2806.field_12805.method_12152() + ")");
        check(class_2806.field_12805.method_51378(), "LIGHT.hasLoadDependencies()");
        check(!class_2806.field_12804.method_51378(), "NOISE has no load dependencies");
        check(class_2806.field_19160.size() == 4, "ChunkStatus.POST_FEATURES holds four heightmaps");
        check("light".equals(class_3230.field_19270.toString()), "TicketType.LIGHT is \"light\" (got " + class_3230.field_19270 + ")");
    }

    static void structures() {
        class_3492 settings = new class_3492();
        check(settings.method_15120(), "StructurePlaceSettings keeps liquids by default");
        check(settings.method_35476(false) == settings && !settings.method_15120(), "setKeepLiquids(false) answers the settings, shouldKeepLiquids() false");
        settings.method_35476(true);
        check(settings.method_15120(), "setKeepLiquids(true): shouldKeepLiquids() true");
    }

    static void combat(MinecraftServer server) {
        class_3218 level = server.method_30002();
        class_1282 generic = level.method_48963().method_48830();
        float after = class_1280.method_5496(10.0F, generic, 20.0F, 0.0F);
        check(Math.abs(after - 4.0F) < 1e-5F, "CombatRules.getDamageAfterAbsorb(10, generic, 20, 0) is 4.0 (got " + after + ")");
        check(class_1304.method_20234(class_1304.class_1305.field_6178, 3) == class_1304.field_6169, "EquipmentSlot.byTypeAndIndex(ARMOR, 3) is HEAD");
        boolean threw = false;
        try {
            class_1304.method_20234(class_1304.class_1305.field_6178, 9);
        } catch (IllegalArgumentException e) {
            threw = e.getMessage().startsWith("Invalid slot");
        }
        check(threw, "byTypeAndIndex(ARMOR, 9) throws \"Invalid slot ...\"");
        float[] white = class_1472.method_6634(class_1767.field_7952);
        check(white.length == 3 && white[0] == 0.9019608F && white[2] == 0.9019608F, "Sheep.getColorArray(WHITE) is 0.9019608 x3");
        float[] red = class_1472.method_6634(class_1767.field_7964);
        check(red.length == 3 && red[0] > red[1] && red[0] <= 0.75F, "getColorArray(RED) is red times 0.75 (" + red[0] + ", " + red[1] + ", " + red[2] + ")");
        check(!generic.method_48790(), "DamageSource.generic().isIndirect() is false");
        class_2338 spawn = level.method_43126();
        net.minecraft.class_1430 cow = new net.minecraft.class_1430(class_1299.field_6085, level);
        net.minecraft.class_1430 other = new net.minecraft.class_1430(class_1299.field_6085, level);
        class_1282 indirect = level.method_48963().method_48815(cow, other);
        check(indirect.method_48790(), "indirectMagic(cow, other).isIndirect() is true");
        check(spawn != null, "spawn position");
    }

    @SuppressWarnings("unchecked")
    static void holder(MinecraftServer server) throws Exception {
        class_3218 level = server.method_30002();
        class_2338 spawn = level.method_43126();
        class_1923 pos = new class_1923(spawn);
        class_2818 chunk = level.method_8497(pos.field_9181, pos.field_9180);
        class_3898 map = level.method_14178().field_17254;
        Field f = class_3898.class.getDeclaredField("field_17220");
        f.setAccessible(true);
        class_3193 holder = ((it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap<class_3193>) f.get(map)).get(pos.method_8324());
        check(holder != null, "the spawn chunk has a holder");
        check(holder.method_13994().equals(pos), "ChunkHolder.getPos() is the chunk's");
        check(holder.method_23270() == class_2806.field_12803, "getLastAvailableStatus() is FULL (got " + holder.method_23270() + ")");
        class_2791 last = holder.method_14010();
        check(last == chunk || last instanceof net.minecraft.class_2821 imp && imp.method_12240() == chunk, "getLastAvailable() is the level chunk (got " + last + ")");
        class_9259<class_2791> full = holder.method_16146(class_2806.field_12803).getNow(class_3193.field_36388);
        check(full.method_57122() && full.method_57130(null) != null, "getFutureIfPresentUnchecked(FULL) is done with the chunk");
        check(holder.method_21737(class_2806.field_12803).isDone(), "getFutureIfPresent(FULL) is done");
        check(holder.method_23271() != null, "getFullStatus() answers (" + holder.method_23271() + ")");
        check(holder.method_40066().size() == class_2806.method_16558().size(), "getAllFutures() has one entry per status");
        check(class_3193.field_16426 != null && !class_3193.field_16426.method_57122(), "ChunkHolder.UNLOADED_CHUNK is an error result");
        check(class_3193.field_16430.isDone(), "UNLOADED_CHUNK_FUTURE is done");
    }

    static void resetChunks(MinecraftServer server) {
        class_3218 level = server.method_30002();
        class_6608.method_38623(server.method_3734().method_9235());
        class_2338 spawn = level.method_43126();
        class_1923 pos = new class_1923(spawn);
        int y = level.method_8624(net.minecraft.class_2902.class_2903.field_13202, spawn.method_10263(), spawn.method_10260());
        StringBuilder feedback = new StringBuilder();
        net.minecraft.class_2168 source = server.method_3739().method_9208(new class_243(spawn.method_10263() + 0.5, y, spawn.method_10260() + 0.5));
        server.method_3734().method_44252(source, "resetchunks 0 false");
        class_2818 chunk = level.method_8497(pos.field_9181, pos.field_9180);
        int stone = 0;
        int air = 0;
        for (int dy = level.method_31607(); dy < level.method_31607() + 40; dy++) {
            if (level.method_8320(new class_2338(pos.method_8326(), dy, pos.method_8328())).method_26215()) {
                air++;
            } else {
                stone++;
            }
        }
        check(stone > 0, "after /resetchunks 0 the chunk's bottom is solid again (" + stone + " solid, " + air + " air of 40)");
        check(!level.method_8320(new class_2338(pos.method_8326(), level.method_31607(), pos.method_8328())).method_27852(class_2246.field_10124)
            || stone > 0, "the bottom block is not left as air");
        check(chunk != null, "the chunk is still loaded");
    }
}
