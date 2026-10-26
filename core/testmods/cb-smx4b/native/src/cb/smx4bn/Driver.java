package cb.smx4bn;

import java.util.UUID;
import net.minecraft.class_1268;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1430;
import net.minecraft.class_1642;
import net.minecraft.class_1665;
import net.minecraft.class_1667;
import net.minecraft.class_1677;
import net.minecraft.class_1680;
import net.minecraft.class_1744;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_1847;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_4836;
import net.minecraft.class_8791;
import net.minecraft.class_9334;

/**
 * cb-smx4bn: a 1.20.5 mod, not converted. Drives the same 1.20.5 paths the step layer routes and counts its own
 * mixins on them. Without a converted mod mixing the restored members (cb_smx4b absent) every count must be 1 and the
 * outcome 1.20.5's: the routes leave 1.20.5 untouched. With cb_smx4b loaded the routes ask the restored members; the
 * counts are reported, and the mixins outside what a route replaces must still fire. Logs "CB-SMX4BN ok|FAIL|info ..."
 * and "CB-SMX4BN summary ok=N fail=M".
 */
public final class Driver {
    public static volatile int arrowColor;
    public static volatile int hurtEquipment;
    public static volatile int angrySound;
    public static volatile int makeSound;
    public static volatile int breakSlot;
    public static volatile int fireItem;
    public static volatile int throwItem;
    public static volatile int walkOfMob;
    public static volatile int walkWithin;
    public static volatile int swimNeighbors;
    public static volatile int panicking;
    public static volatile int availableGoals;
    public static volatile int deflect;
    private static int ok;
    private static int fail;

    private Driver() {
    }

    private static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-SMX4BN " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static void info(String c, String detail) {
        System.out.println("CB-SMX4BN info " + c + " " + detail);
    }

    private static void run(String c, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-SMX4BN FAIL " + c + " threw " + t);
            t.printStackTrace(System.out);
        }
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        boolean mixed = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("cb_smx4b");
        String mode = mixed ? "(cb_smx4b loaded: routes active)" : "(no converted mod: routes idle)";
        class_3218 level = server.method_30002();
        run("arrow", () -> {
            class_1642 shooter = class_1299.field_6051.method_5883(level);
            class_1799 ammo = new class_1799(class_1802.field_8087);
            ammo.method_57379(class_9334.field_49651, new class_1844(class_1847.field_8982));
            arrowColor = 0;
            class_1665 a = ((class_1744) class_1802.field_8087).method_7702(level, ammo, shooter);
            int color = ((class_1667) a).method_7460();
            check("arrow", arrowColor >= 1 && color != -1, "updateColor=" + arrowColor + " color=" + color + " " + mode);
        });
        run("hurt-armor", () -> {
            class_3222 p = new class_3222(server, level, new com.mojang.authlib.GameProfile(UUID.randomUUID(), "cbsmx4bn"), class_8791.method_53821());
            p.method_5673(class_1304.field_6174, new class_1799(class_1802.field_8523));
            hurtEquipment = 0;
            ((cb.smx4bn.mixin.PlayerInvoker) p).cbsmx4bn$hurtArmor(level.method_48963().method_48830(), 8.0F);
            int damage = p.method_6118(class_1304.field_6174).method_7919();
            if (mixed) {
                check("hurt-armor", damage == 2, "doHurtEquipment=" + hurtEquipment + " (routed: 1.20.4's hurtArmor ran instead) damage=" + damage + " " + mode);
            } else {
                check("hurt-armor", hurtEquipment == 1 && damage == 2, "doHurtEquipment=" + hurtEquipment + " damage=" + damage + " " + mode);
            }
        });
        run("piglin-sound", () -> {
            class_4836 piglin = class_1299.field_22281.method_5883(level);
            angrySound = 0;
            makeSound = 0;
            ((cb.smx4bn.mixin.PiglinInvoker) piglin).cbsmx4bn$playAngrySound();
            if (mixed) {
                check("piglin-sound", angrySound == 1, "playAngrySound=" + angrySound + " makeSound=" + makeSound + " " + mode);
            } else {
                check("piglin-sound", angrySound == 1 && makeSound == 1, "playAngrySound=" + angrySound + " makeSound=" + makeSound + " " + mode);
            }
        });
        run("break-slot", () -> {
            class_1642 zombie = class_1299.field_6051.method_5883(level);
            class_1430 cow = class_1299.field_6085.method_5883(level);
            class_1799 sword = new class_1799(class_1802.field_8371);
            sword.method_7974(sword.method_7936() - 1);
            zombie.method_6122(class_1268.field_5808, sword);
            breakSlot = 0;
            sword.method_7909().method_7873(sword, cow, zombie);
            check("break-slot", breakSlot == 1 && sword.method_7960(), "broadcastBreakEvent(slot)=" + breakSlot + " broken=" + sword.method_7960() + " " + mode);
        });
        run("fireball-item", () -> {
            class_1677 f = new class_1677(level, 0.0, 100.0, 0.0, 0.0, 0.0, 0.0);
            fireItem = 0;
            class_1799 item = f.method_7495();
            if (mixed) {
                check("fireball-item", item.method_31574(class_1802.field_8814), "getItem=" + fireItem + " item=" + item + " " + mode);
            } else {
                check("fireball-item", fireItem == 1 && item.method_31574(class_1802.field_8814), "getItem=" + fireItem + " item=" + item + " " + mode);
            }
        });
        run("snowball-item", () -> {
            class_1680 s = new class_1680(level, 0.0, 100.0, 0.0);
            throwItem = 0;
            class_1799 item = s.method_7495();
            if (mixed) {
                check("snowball-item", item.method_31574(class_1802.field_8543), "getItem=" + throwItem + " item=" + item + " " + mode);
            } else {
                check("snowball-item", throwItem == 1 && item.method_31574(class_1802.field_8543), "getItem=" + throwItem + " item=" + item + " " + mode);
            }
        });
        net.minecraft.class_2338 spawn = level.method_43126();
        int bx = spawn.method_10263() + 20;
        int bz = spawn.method_10260();
        run("walk", () -> {
            for (int x = -1; x <= 9; x++) {
                for (int z = -1; z <= 3; z++) {
                    level.method_8501(new net.minecraft.class_2338(bx + x, 200, bz + z), net.minecraft.class_2246.field_10340.method_9564());
                    for (int y = 201; y <= 203; y++) {
                        level.method_8501(new net.minecraft.class_2338(bx + x, y, bz + z), net.minecraft.class_2246.field_10124.method_9564());
                    }
                }
            }
            class_1642 zombie = class_1299.field_6051.method_5883(level);
            zombie.method_5814(bx + 0.5, 201.0, bz + 1.5);
            zombie.method_24830(true);
            walkOfMob = 0;
            walkWithin = 0;
            net.minecraft.class_11 path = zombie.method_5942().method_6348(new net.minecraft.class_2338(bx + 8, 201, bz + 1), 0);
            boolean reach = path != null && path.method_21655();
            if (mixed) {
                check("walk", walkOfMob > 0 && reach, "getPathTypeOfMob=" + walkOfMob + " getPathTypeWithinMobBB=" + walkWithin + " (routed) reach=" + reach + " " + mode);
            } else {
                check("walk", walkOfMob > 0 && walkWithin > 0 && reach, "getPathTypeOfMob=" + walkOfMob + " getPathTypeWithinMobBB=" + walkWithin + " reach=" + reach + " " + mode);
            }
        });
        run("swim", () -> {
            for (int x = -1; x <= 7; x++) {
                for (int z = -1; z <= 7; z++) {
                    for (int y = 210; y <= 215; y++) {
                        boolean inside = x >= 0 && x <= 6 && z >= 0 && z <= 6 && y >= 211 && y <= 214;
                        level.method_8501(new net.minecraft.class_2338(bx + x, y, bz + z),
                            (inside ? net.minecraft.class_2246.field_10382 : net.minecraft.class_2246.field_10340).method_9564());
                    }
                }
            }
            net.minecraft.class_1308 dolphin = (net.minecraft.class_1308) class_1299.field_6087.method_5883(level);
            dolphin.method_5814(bx + 0.5, 212.0, bz + 0.5);
            swimNeighbors = 0;
            net.minecraft.class_11 path = dolphin.method_5942().method_6348(new net.minecraft.class_2338(bx + 6, 212, bz + 6), 0);
            boolean reach = path != null && path.method_21655();
            int nodes = path == null ? -1 : path.method_38();
            if (mixed) {
                check("swim", reach, "getNeighbors=" + swimNeighbors + " (routed) nodes=" + nodes + " " + mode);
            } else {
                check("swim", swimNeighbors > 0 && reach, "getNeighbors=" + swimNeighbors + " nodes=" + nodes + " " + mode);
            }
        });
        run("panicking", () -> {
            class_1430 cow = class_1299.field_6085.method_5883(level);
            panicking = 0;
            availableGoals = 0;
            boolean panic = cow.method_52546();
            if (mixed) {
                check("panicking", panicking == 1 && !panic, "isPanicking=" + panicking + " getAvailableGoals=" + availableGoals + " (routed) result=" + panic + " " + mode);
            } else {
                check("panicking", panicking == 1 && availableGoals == 1 && !panic, "isPanicking=" + panicking + " getAvailableGoals=" + availableGoals + " result=" + panic + " " + mode);
            }
        });
        run("deflect", () -> {
            net.minecraft.class_8949 breeze = new net.minecraft.class_8949(class_1299.field_47244, level);
            breeze.method_5814(bx + 0.5, 220.0, bz + 0.5);
            level.method_8649(breeze);
            class_1642 shooter = class_1299.field_6051.method_5883(level);
            class_1665 arrow = ((class_1744) class_1802.field_8107).method_7702(level, new class_1799(class_1802.field_8107), shooter);
            arrow.method_5814(bx - 1.5, 220.8, bz + 0.5);
            arrow.method_18800(2.0, 0.0, 0.0);
            deflect = 0;
            arrow.method_5773();
            double speed = arrow.method_18798().method_1033();
            breeze.method_31472();
            arrow.method_31472();
            if (mixed) {
                check("deflect", speed < 1.5, "Projectile.deflect=" + deflect + " (routed) speed=" + speed + " " + mode);
            } else {
                check("deflect", deflect == 1 && speed < 1.5, "Projectile.deflect=" + deflect + " speed=" + speed + " " + mode);
            }
        });
        System.out.println("CB-SMX4BN summary ok=" + ok + " fail=" + fail);
    }
}
