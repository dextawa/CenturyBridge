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

        System.out.println("CB-S9TEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " failed: " + FAILS));
    }
}
