package cb.smx1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runs once the server's world is loaded: triggers the vanilla methods the test mixins name and checks that each
 * one changed or observed what vanilla did, as on 1.20.1. Logs "CB-SMX1 ok|FAIL <case> <detail>" per case and
 * "CB-SMX1 summary ok=N fail=M" at the end.
 */
public final class Driver {
    private static final Map<String, Integer> COUNT = new ConcurrentHashMap<>();
    public static final ThreadLocal<Boolean> ON_DRIVER = ThreadLocal.withInitial(() -> false);
    public static volatile boolean forceLuck;
    private static int ok;
    private static int fail;

    private Driver() {
    }

    public static void count(String c) {
        COUNT.merge(c, 1, Integer::sum);
    }

    private static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SMX1 " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        System.out.println("CB-SMX1 start");
        // ---- a RETURN injector on LootPool's constructor: every pool the server loaded went through it
        int pools = COUNT.getOrDefault("lootpool-init", 0);
        check("lootpool-ctor-return", pools > 100, "LootPool constructor handler ran " + pools + " time(s) while loot tables loaded");
        // ---- a RETURN injector on AdvancementList.add: the advancement manager adds every loaded advancement once
        int adds = COUNT.getOrDefault("advancements-add", 0);
        check("advancements-add-return", adds >= 1, "AdvancementList.add handler ran " + adds + " time(s)");
        // ---- a cancelling HEAD injector on getValidEffectById: a beacon loaded from its own save answers Luck
        try {
            net.minecraft.class_2580 beacon = new net.minecraft.class_2580(net.minecraft.class_2338.field_10980,
                net.minecraft.class_2246.field_10327.method_9564());
            ((cb.smx1.mixin.BeaconAccess) beacon).cbsmx1$setPrimary(net.minecraft.class_1294.field_5904);
            net.minecraft.class_2487 tag = beacon.method_38244();
            net.minecraft.class_2580 loaded = new net.minecraft.class_2580(net.minecraft.class_2338.field_10980,
                net.minecraft.class_2246.field_10327.method_9564());
            forceLuck = true;
            try {
                loaded.method_11014(tag);
            } finally {
                forceLuck = false;
            }
            net.minecraft.class_1291 primary = ((cb.smx1.mixin.BeaconAccess) loaded).cbsmx1$primary();
            check("beacon-effect-head", primary == net.minecraft.class_1294.field_5926,
                "saved Speed, loaded " + primary + " (handler ran " + COUNT.getOrDefault("beacon-effect-head", 0) + " time(s))");
        } catch (Throwable t) {
            check("beacon-effect-head", false, "threw " + t);
        }
        // ---- an accessor on a field 1.20.2 made a record component
        try {
            net.minecraft.class_5341 cond = net.minecraft.class_219.method_932(0.25f).build();
            float p = ((cb.smx1.mixin.RandomChanceAccess) cond).cbsmx1$probability();
            check("record-field-accessor", p == 0.25f, "randomChance(0.25).probability -> " + p);
        } catch (Throwable t) {
            check("record-field-accessor", false, "threw " + t);
        }
        // ---- a HEAD injector on NbtIo.readUnnamedTag: one read through NbtIo.read(DataInput)
        try {
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            tag.method_10569("cb", 7);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            net.minecraft.class_2507.method_10628(tag, new DataOutputStream(bytes));
            ON_DRIVER.set(true);
            net.minecraft.class_2487 back;
            try {
                back = net.minecraft.class_2507.method_10627(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
            } finally {
                ON_DRIVER.set(false);
            }
            int reads = COUNT.getOrDefault("nbt-read-head", 0);
            check("nbt-read-head", reads == 1 && back.method_10550("cb") == 7, "handler ran " + reads + " time(s), read back cb=" + back.method_10550("cb"));
        } catch (Throwable t) {
            check("nbt-read-head", false, "threw " + t);
        }
        System.out.println("CB-SMX1 summary ok=" + ok + " fail=" + fail);
    }
}
