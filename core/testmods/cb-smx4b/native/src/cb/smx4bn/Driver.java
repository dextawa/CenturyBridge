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
        System.out.println("CB-SMX4BN summary ok=" + ok + " fail=" + fail);
    }
}
