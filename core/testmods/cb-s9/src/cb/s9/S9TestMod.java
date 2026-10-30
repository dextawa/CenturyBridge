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
        LargePayloadTest.register();
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

        run("particles: 1.21.8 types that 1.21.9 gave options", () -> {
            class_3218 level = server.method_30002();
            net.minecraft.class_2400 breath = net.minecraft.class_2398.field_11216;
            check(breath != null && ((net.minecraft.class_2394) breath).method_10295() == breath, "ParticleTypes.DRAGON_BREATH reads as a SimpleParticleType that is its own type");
            net.minecraft.class_2675 pkt = new net.minecraft.class_2675(breath, false, false, 1.0, 100.0, 2.0, 0F, 0F, 0F, 0F, 3);
            check(pkt.method_11551() != (Object) breath && pkt.method_11551().method_10295() != (Object) breath,
                "ClientboundLevelParticlesPacket(DRAGON_BREATH, ..) holds 1.21.9's options, got " + pkt.method_11551().getClass().getName());
            net.minecraft.class_9129 buf = new net.minecraft.class_9129(io.netty.buffer.Unpooled.buffer(), server.method_30611());
            net.minecraft.class_2675.field_47935.encode(buf, pkt);
            net.minecraft.class_2675 back = net.minecraft.class_2675.field_47935.decode(buf);
            check(back.method_11551().method_10295() == pkt.method_11551().method_10295(),
                "the packet encodes and decodes to the same particle type (" + net.minecraft.class_7923.field_41180.method_10221(back.method_11551().method_10295()) + ")");
            int sent = level.method_65096(net.minecraft.class_2398.field_11245, 0.5, 100.0, 0.5, 4, 0.1, 0.1, 0.1, 0.0);
            check(sent == 0, "ServerLevel.sendParticles(EFFECT, ..) with no players sends to nobody, got " + sent);
            level.method_65096(net.minecraft.class_2398.field_17909, 0.5, 100.0, 0.5, 1, 0.0, 0.0, 0.0, 0.0);
            level.method_65096(net.minecraft.class_2398.field_11213, 0.5, 100.0, 0.5, 1, 0.0, 0.0, 0.0, 0.0);
            check(true, "sendParticles(FLASH / INSTANT_EFFECT, ..) ran");
            net.minecraft.class_1295 cloud = new net.minecraft.class_1295(level, 0.5, 100.0, 0.5);
            cloud.method_5608(breath);
            check(cloud.method_5600() != (Object) breath && cloud.method_5600().method_10295() == pkt.method_11551().method_10295(),
                "AreaEffectCloud.setParticle(DRAGON_BREATH) keeps 1.21.9's dragon breath options, got " + cloud.method_5600().getClass().getName());
        });

        run("explosions: no block particles", () -> {
            class_3218 level = server.method_30002();
            level.method_8454(null, null, null, 0.5, 200.0, 0.5, 1.0F, false, net.minecraft.class_1937.class_7867.field_40888,
                net.minecraft.class_2398.field_11236, net.minecraft.class_2398.field_11216, net.minecraft.class_3417.field_15152);
            check(true, "Level.explode(.., small, DRAGON_BREATH, sound) in the 1.21.8 shape ran");
            net.minecraft.class_2664 ex = new net.minecraft.class_2664(new net.minecraft.class_243(0.5, 200.0, 0.5), java.util.Optional.empty(),
                net.minecraft.class_2398.field_11216, net.minecraft.class_3417.field_15152);
            check(ex.comp_2885() != (Object) net.minecraft.class_2398.field_11216, "new ClientboundExplodePacket(.., DRAGON_BREATH, ..) holds 1.21.9's options");
            net.minecraft.class_9129 buf = new net.minecraft.class_9129(io.netty.buffer.Unpooled.buffer(), server.method_30611());
            net.minecraft.class_2664.field_47925.encode(buf, ex);
            net.minecraft.class_2664 back = net.minecraft.class_2664.field_47925.decode(buf);
            check(back.comp_2885().method_10295() == ex.comp_2885().method_10295(), "the explode packet encodes and decodes");
        });

        run("fabric api: AfterKilledOtherEntity (0.136 listener)", () -> {
            class_3218 level = server.method_30002();
            int[] seen = new int[1];
            net.minecraft.class_1309[] got = new net.minecraft.class_1309[1];
            net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killed) -> {
                seen[0]++;
                got[0] = killed;
            });
            net.minecraft.class_1308 zombie = (net.minecraft.class_1308) net.minecraft.class_1299.field_6051.method_5883(level, net.minecraft.class_3730.field_16462);
            net.minecraft.class_1309 pig = (net.minecraft.class_1309) net.minecraft.class_1299.field_6093.method_5883(level, net.minecraft.class_3730.field_16462);
            zombie.method_5814(0.5, 120.0, 0.5);
            pig.method_5814(0.5, 120.0, 0.5);
            level.method_8649(zombie);
            level.method_8649(pig);
            pig.method_64397(level, level.method_48963().method_48812(zombie), 1000.0F);
            check(seen[0] == 1 && got[0] == pig, "the 0.136-shaped listener ran once for the pig the zombie killed (" + seen[0] + ")");
            zombie.method_31472();
        });

        run("fabric api: NbtType / FakePlayer / SimpleResourceReloadListener", () -> {
            check(net.fabricmc.fabric.api.util.NbtType.NUMBER == 99, "NbtType.NUMBER == 99");
            class_3218 level = server.method_30002();
            net.fabricmc.fabric.api.entity.FakePlayer fp = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            net.minecraft.class_1297 pig = net.minecraft.class_1299.field_6093.method_5883(level, net.minecraft.class_3730.field_16462);
            boolean rode = fp.method_5873(pig, true);
            check(!rode && fp.method_5854() == null, "FakePlayer.startRiding(vehicle, force) answers false (fake players do not ride), got " + rode);
            int[] applied = new int[1];
            net.fabricmc.fabric.api.resource.SimpleResourceReloadListener<String> l = new net.fabricmc.fabric.api.resource.SimpleResourceReloadListener<String>() {
                @Override
                public net.minecraft.class_2960 getFabricId() {
                    return net.minecraft.class_2960.method_60655("cb", "s9");
                }

                @Override
                public java.util.concurrent.CompletableFuture<String> load(net.minecraft.class_3300 manager, java.util.concurrent.Executor executor) {
                    return java.util.concurrent.CompletableFuture.completedFuture("loaded");
                }

                @Override
                public java.util.concurrent.CompletableFuture<Void> apply(String data, net.minecraft.class_3300 manager, java.util.concurrent.Executor executor) {
                    if ("loaded".equals(data)) {
                        applied[0]++;
                    }
                    return java.util.concurrent.CompletableFuture.completedFuture(null);
                }
            };
            net.minecraft.class_3302.class_4045 barrier = new net.minecraft.class_3302.class_4045() {
                @Override
                public <T> java.util.concurrent.CompletableFuture<T> method_18352(T value) {
                    return java.util.concurrent.CompletableFuture.completedFuture(value);
                }
            };
            l.method_25931(barrier, server.method_34864(), Runnable::run, Runnable::run).join();
            check(applied[0] == 1, "SimpleResourceReloadListener.reload(barrier, manager, ..) loaded and applied (" + applied[0] + ")");
        });

        run("avatar: Player members 1.21.9 moved", () -> {
            class_3218 level = server.method_30002();
            net.fabricmc.fabric.api.entity.FakePlayer fp = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            net.minecraft.class_1657 p = fp;
            check(net.minecraft.class_1657.field_46174 == p.method_6068(), "Player.DEFAULT_MAIN_HAND is the main arm of a fresh player, got " + net.minecraft.class_1657.field_46174);
            check(net.minecraft.class_1657.field_18135 != null && net.minecraft.class_1657.field_18135.comp_2185() == 0.6F,
                "Player.STANDING_DIMENSIONS is 0.6 wide, got " + net.minecraft.class_1657.field_18135);
            p.method_7283(net.minecraft.class_1306.field_6182);
            check(p.method_6068() == net.minecraft.class_1306.field_6182, "setMainArm(LEFT) then getMainArm() == LEFT");
            p.method_7283(net.minecraft.class_1306.field_6183);
            check(!p.method_7348(net.minecraft.class_1664.field_7559) || p.method_7348(net.minecraft.class_1664.field_7559), "isModelPartShown(CAPE) answers");
            check(p.method_7327() == level.method_8428(), "Player.getScoreboard() is the level's scoreboard");
            check(p.method_7356() != null && p.method_7356().method_33133(), "getShoulderEntityLeft() of a player without one is an empty tag, got " + p.method_7356());
            check(p.method_7308() != null && p.method_7308().method_33133(), "getShoulderEntityRight() is an empty tag");
            p.field_7500 = 3.5;
            p.field_7483 = 0.25F;
            check(p.field_7500 == 3.5 && p.field_7483 == 0.25F, "Player.xCloak / bob keep what 1.21.8 code set on a server player");
            net.minecraft.class_1309 pig = (net.minecraft.class_1309) net.minecraft.class_1299.field_6093.method_5883(level, net.minecraft.class_3730.field_16462);
            p.method_5874(level, pig);
            check(true, "Player.killedEntity(level, killed) in the 1.21.8 shape ran");
            fp.method_64578(90.0F, 10.0F);
            check(fp.method_36454() == 90.0F && fp.method_36455() == 10.0F, "ServerPlayer.forceSetRotation(90, 10) sets absolute angles, got " + fp.method_36454() + " " + fp.method_36455());
            net.minecraft.class_10265 rot = new net.minecraft.class_10265(30.0F, -5.0F);
            check(rot.comp_3230() == 30.0F && rot.comp_3231() == -5.0F, "new ClientboundPlayerRotationPacket(30, -5) keeps the angles");
        });

        LargePayloadTest.run();
        EntityTest.run(server);
        DeflectionTest.run(server);
        ProfileCacheTest.run(server);
        ServerPropertiesTest.run(server);
        BorderSelectorTest.run(server);
        PaletteTest.run();
        CommandBlockTest.run(server);
        CustomDataTest.run(server);
        DebugPacketsTest.run(server);

        System.out.println("CB-S9TEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " failed: " + FAILS));
    }
}
