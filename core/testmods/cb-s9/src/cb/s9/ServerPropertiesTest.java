package cb.s9;

import net.minecraft.class_3176;
import net.minecraft.class_3806;
import net.minecraft.server.MinecraftServer;

/** 1.21.8 DedicatedServerProperties fields read and written by 1.21.8 code (s9c) */
final class ServerPropertiesTest {

    private ServerPropertiesTest() {
    }

    static void run(MinecraftServer server) {
        if (!(server instanceof class_3176 dedicated)) {
            return;
        }
        S9TestMod.run("server properties: plain-value fields, removed pvp, acceptsTransfers write", () -> {
            class_3806 p = dedicated.method_16705();
            S9TestMod.check(p.field_16825 != null && p.field_16825.equals(server.method_3818()),
                "DedicatedServerProperties.motd reads the server's motd, got " + p.field_16825 + " / " + server.method_3818());
            S9TestMod.check(p.field_16844 > 0 && p.field_16814 == server.method_3802(),
                "view-distance / max-players read as ints (max-players = the server's), got " + p.field_16844 + " " + p.field_16814 + " / " + server.method_3802());
            S9TestMod.check(p.field_16840 != null && p.field_16841 != null, "difficulty / gamemode read, got " + p.field_16840 + " " + p.field_16841);
            S9TestMod.check(p.field_16833 == server.method_3852(), "pvp reads what the server enforces, got " + p.field_16833 + " / " + server.method_3852());
            boolean before = p.field_48270;
            S9TestMod.check(before == dedicated.method_56040(), "acceptsTransfers reads what the server answers, got " + before);
            p.field_48270 = !before;
            S9TestMod.check(dedicated.method_56040() == !before, "writing acceptsTransfers changes what the server answers");
            p.field_48270 = before;
            S9TestMod.check(dedicated.method_56040() == before && p.field_48270 == before, "writing it back restores it");
        });
        S9TestMod.run("MinecraftServer: 1.21.8 members 1.21.9 dropped or renamed", () -> {
            class_3806 p = dedicated.method_16705();
            S9TestMod.check(server.method_3841() == p.field_16816, "getSpawnProtectionRadius() is the spawn-protection property, got "
                + server.method_3841() + " / " + p.field_16816);
            net.minecraft.class_3218 overworld = server.method_30002();
            S9TestMod.check(server.method_3829(overworld) == overworld.method_64395().method_8356(net.minecraft.class_1928.field_19403)
                && server.method_3829(null) == 10, "getSpawnRadius(level) is the spawnRadius rule, 10 without a level");
            S9TestMod.check(server.method_3719() != null && server.method_3844() != null, "getProfileRepository() / getSessionService() answer");
            net.minecraft.class_3218 nether = server.method_3847(net.minecraft.class_1937.field_25180);
            S9TestMod.check(nether == null || server.method_60671(nether), "isLevelEnabled(nether) with nether allowed");
            S9TestMod.check(server.method_60671(overworld), "isLevelEnabled(overworld)");
            boolean monsters = server.method_3783();
            S9TestMod.check(!monsters || overworld.method_8407() != net.minecraft.class_1267.field_5801,
                "isSpawningMonsters() is false in peaceful, got " + monsters + " at " + overworld.method_8407());
            boolean pvp = server.method_3852();
            server.method_3815(!pvp);
            S9TestMod.check(server.method_3852() == !pvp, "setPvpAllowed changes what isPvpAllowed answers");
            server.method_3815(pvp);
            S9TestMod.check(server.method_3852() == pvp, "setPvpAllowed back restores it");
            boolean flight = server.method_3718();
            server.method_3745(!flight);
            S9TestMod.check(server.method_3718() == !flight && p.field_16807 == !flight, "setFlightAllowed changes what isFlightAllowed and allowFlight answer");
            server.method_3745(flight);
            S9TestMod.check(server.method_3718() == flight, "setFlightAllowed back restores it");
            server.method_3728(server.method_3739());
            S9TestMod.check(true, "kickUnlistedPlayers(source) ran");
        });
    }
}
