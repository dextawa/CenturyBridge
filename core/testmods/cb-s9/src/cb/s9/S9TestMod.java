package cb.s9;

import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import net.minecraft.class_3324;
import net.minecraft.class_3335;
import net.minecraft.class_3336;
import net.minecraft.class_3337;
import net.minecraft.class_3340;
import net.minecraft.class_5217;
import net.minecraft.class_5269;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.8-era server mod (intermediary names, Fabric Loader API only) for s9's server-side bridges of the
 * 1.21.8 -> 1.21.9 step, run once the server is up: GameProfile -> NameAndId in ops, bans and the whitelist (calls,
 * constructors, and an old getKeyForUser override vanilla must reach), RespawnData behind the 1.21.8 spawn API.
 * Each check prints "CB-S9TEST ok|FAIL ..."; the last line is "CB-S9TEST DONE passed/checks".
 */
public class S9TestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-S9TEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    static void run(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
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
                    server.execute(() -> start(server));
                    return;
                }
            }
        }, "cb-s9");
        t.setDaemon(true);
        t.start();
    }

    /** a 1.21.8 mod's whitelist keyed its own way: vanilla's add / contains must reach getKeyForUser(GameProfile) */
    static final class KeyedWhiteList extends class_3337 {
        int keyCalls;
        GameProfile lastProfile;

        KeyedWhiteList(File file) {
            super(file);
        }

        @Override
        protected String method_14652(GameProfile profile) {
            keyCalls++;
            lastProfile = profile;
            return "cb:" + profile.getName();
        }
    }

    /** a 1.21.8 reload listener (the old reload shape) */
    static final class LegacyListener implements net.minecraft.class_3302 {
        int calls;
        Object manager;

        @Override
        public java.util.concurrent.CompletableFuture<Void> method_25931(net.minecraft.class_3302.class_4045 barrier, net.minecraft.class_3300 manager,
                java.util.concurrent.Executor background, java.util.concurrent.Executor game) {
            calls++;
            this.manager = manager;
            return barrier.method_18352(null).thenRun(() -> { });
        }
    }

    static void start(MinecraftServer server) {
        class_3324 players = server.method_3760();
        GameProfile gp = new GameProfile(UUID.fromString("0b5ed7c1-0000-4000-8000-00000000cb59"), "cbs9test");

        run("ops", () -> {
            check(!players.method_14569(gp), "isOp(profile) false before op");
            players.method_14582(gp);
            check(players.method_14569(gp), "op(profile) then isOp(profile) true");
            check(server.method_3835(gp) > 0, "server.getProfilePermissions(profile) > 0 for an op, got " + server.method_3835(gp));
            check(!players.method_14603().method_14620(gp), "ServerOpList.canBypassPlayerLimit(profile) false for a plain op (1.21.8 op() keeps the previous flag)");
            players.method_14604(gp);
            check(!players.method_14569(gp), "deop(profile) then isOp(profile) false");
        });

        run("whitelist", () -> {
            class_3337 wl = players.method_14590();
            check(!wl.method_14653(gp), "UserWhiteList.isWhiteListed(profile) false before (PlayerList.isWhiteListed is true while the whitelist is off)");
            wl.method_14633(new class_3340(gp));
            check(wl.method_14653(gp), "UserWhiteList.isWhiteListed(profile) after add(new UserWhiteListEntry(profile))");
            check(players.method_14587(gp), "PlayerList.isWhiteListed(profile) after add");
            wl.method_14635(gp);
            check(!wl.method_14653(gp), "remove(profile) then not whitelisted");
        });

        run("bans", () -> {
            class_3335 bans = players.method_14563();
            bans.method_14633(new class_3336(gp, null, "cb-s9", null, "test ban"));
            check(bans.method_14650(gp), "UserBanList.isBanned(profile) after add(new UserBanListEntry(profile, ..))");
            class_3336 e = bans.method_14640(gp);
            check(e != null && "test ban".equals(e.method_14503()), "UserBanList.get(profile) is the entry with its reason");
            bans.method_14635(gp);
            check(!bans.method_14650(gp), "remove(profile) then not banned");
        });

        run("old getKeyForUser override", () -> {
            KeyedWhiteList wl = new KeyedWhiteList(new File("cb-s9-whitelist.json"));
            wl.method_14633(new class_3340(gp));
            check(wl.keyCalls > 0, "vanilla add() reached the 1.21.8 getKeyForUser(GameProfile) override (" + wl.keyCalls + " calls)");
            check(wl.lastProfile != null && gp.getId().equals(wl.lastProfile.getId()) && "cbs9test".equals(wl.lastProfile.getName()),
                "the override got a GameProfile with the entry's id and name");
            check(wl.method_14653(gp), "isWhiteListed(profile) finds the entry under the override's key");
            new File("cb-s9-whitelist.json").delete();
        });

        run("spawn", () -> {
            class_3218 level = server.method_30002();
            class_2338 before = level.method_43126();
            check(before != null, "Level.getSharedSpawnPos() answers " + before);
            class_2338 pos = new class_2338(17, 70, -23);
            level.method_8554(pos, 45.0F);
            check(pos.equals(level.method_43126()), "setDefaultSpawnPos(pos, 45) then getSharedSpawnPos() == pos, got " + level.method_43126());
            check(level.method_43127() == 45.0F, "getSharedSpawnAngle() == 45, got " + level.method_43127());
            class_5217 data = level.method_8401();
            check(pos.equals(data.method_56126()), "LevelData.getSpawnPos() == pos, got " + data.method_56126());
            check(data.method_30656() == 45.0F, "LevelData.getSpawnAngle() == 45, got " + data.method_30656());
            if (data instanceof class_5269 w) {
                class_2338 pos2 = new class_2338(-5, 64, 9);
                w.method_187(pos2, 90.0F);
                check(pos2.equals(data.method_56126()) && data.method_30656() == 90.0F,
                    "WritableLevelData.setSpawn(pos2, 90) then getSpawnPos/Angle, got " + data.method_56126() + " " + data.method_30656());
                check(pos2.equals(level.method_43126()), "the shared spawn follows the level data, got " + level.method_43126());
            } else {
                check(false, "overworld level data is WritableLevelData");
            }
            level.method_8554(before, 0.0F);
        });


        run("riding", () -> {
            class_3218 level = server.method_30002();
            net.minecraft.class_1297 rider = net.minecraft.class_1299.field_6093.method_5883(level, net.minecraft.class_3730.field_16462);
            net.minecraft.class_1297 vehicle = net.minecraft.class_1299.field_6093.method_5883(level, net.minecraft.class_3730.field_16462);
            check(rider.method_5873(vehicle, true), "Entity.startRiding(vehicle, force) answers true");
            check(rider.method_5854() == vehicle, "the rider's vehicle is the entity it rode");
            rider.method_5848();
            check(rider.method_5854() == null, "stopRiding() leaves no vehicle");
        });

        run("reload listener", () -> {
            LegacyListener l = new LegacyListener();
            net.minecraft.class_3302.class_4045 barrier = new net.minecraft.class_3302.class_4045() {
                @Override
                public <T> java.util.concurrent.CompletableFuture<T> method_18352(T value) {
                    return java.util.concurrent.CompletableFuture.completedFuture(value);
                }
            };
            net.minecraft.class_3302 asListener = l;
            asListener.method_25931(barrier, server.method_34864(), Runnable::run, Runnable::run).join();
            check(l.calls == 1, "a 1.21.8 reload(barrier, manager, ..) call reached the 1.21.8 listener's reload (" + l.calls + " calls)");
            check(l.manager == server.method_34864(), "the listener got the resource manager it was called with");
        });

        run("packs", () -> {
            net.minecraft.class_3272 meta = new net.minecraft.class_3272(net.minecraft.class_2561.method_43470("cb"), 64, java.util.Optional.empty());
            check(meta.comp_1581() == 64, "PackMetadataSection.packFormat() == 64, got " + meta.comp_1581());
            check(meta.comp_1582().isPresent() && meta.comp_1582().get().comp_1() == 64 && meta.comp_1582().get().comp_2() == 64,
                "supportedFormats() is 64..64, got " + meta.comp_1582());
            int data = net.minecraft.class_155.method_16673().method_70592(net.minecraft.class_3264.field_14190);
            check(data > 81, "WorldVersion.packVersion(SERVER_DATA) is the 1.21.9 data pack major (> 81), got " + data);
            check(net.minecraft.class_3281.method_14436(new net.minecraft.class_6497<>(data, data), data).method_14437(),
                "PackCompatibility.forVersion(data..data, data) is compatible");
        });

        run("bee occupant", () -> {
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            tag.method_10582("id", "minecraft:bee");
            tag.method_10569("Age", 7);
            net.minecraft.class_4482.class_9309 occ = new net.minecraft.class_4482.class_9309(net.minecraft.class_9279.method_57456(tag), 0, 600);
            net.minecraft.class_2487 back = occ.comp_2431().method_57461();
            check("minecraft:bee".equals(back.method_10558("id").orElse(null)), "Occupant.entityData() keeps the id, got " + back);
            check(back.method_10550("Age").orElse(0) == 7, "Occupant.entityData() keeps the bee's own data, got " + back);
        });

        run("bounds / profile / properties", () -> {
            net.minecraft.class_2096.class_2100 b = net.minecraft.class_2096.class_2100.method_35287(1, 5);
            check(b.comp_1805().orElse(-1) == 1 && b.comp_1806().orElse(-1) == 5, "MinMaxBounds.Ints.between(1, 5) min/max, got " + b.comp_1805() + " " + b.comp_1806());
            net.minecraft.class_9296 rp = new net.minecraft.class_9296(gp);
            check(gp.getId().equals(rp.comp_2413().getId()), "new ResolvableProfile(profile).gameProfile() is that profile");
            if (server instanceof net.minecraft.class_3176 ds) {
                check(ds.method_16705().field_16811, "DedicatedServerProperties.allowNether (the allowNether game rule, on by default)");
            }
        });

        System.out.println("CB-S9TEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " failed: " + FAILS));
    }
}
