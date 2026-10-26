package cb.smx4b;

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
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_4836;
import net.minecraft.class_8791;

/**
 * Runs once the server's world is loaded, on 1.20.4's API (converted 1.20.4 -> 1.20.5). Each case does what 1.20.4's
 * vanilla did through a member 1.20.5 dropped, through the vanilla 1.20.5 path the step layer routes, and checks that
 * this mod's 1.20.4 mixin on the member saw it and that the outcome is 1.20.4's. Logs "CB-SMX4B ok|FAIL <case> <detail>"
 * and "CB-SMX4B summary ok=N fail=M".
 */
public final class Driver {
    public static volatile int arrowAsked;
    public static volatile boolean cancelArrow;
    public static volatile int hurtArmorAsked;
    public static volatile int hurtArmorSlots;
    public static volatile int piglinAsked;
    public static volatile class_3414 piglinSound;
    public static volatile int breakAsked;
    public static volatile class_1268 breakHand;
    public static volatile int fireAsked;
    public static volatile int throwAsked;
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
        System.out.println("CB-SMX4B " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static void run(String c, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-SMX4B FAIL " + c + " threw " + t);
            t.printStackTrace(System.out);
        }
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        class_3218 level = server.method_30002();
        run("arrow-effects", () -> {
            class_1642 shooter = class_1299.field_6051.method_5883(level);
            class_1799 ammo = class_1844.method_8061(new class_1799(class_1802.field_8087), class_1847.field_8982);
            arrowAsked = 0;
            class_1665 a = ((class_1744) class_1802.field_8087).method_7702(level, ammo, shooter);
            int color = ((class_1667) a).method_7460();
            check("arrow-effects", arrowAsked == 1 && color != -1, "asked=" + arrowAsked + " color=" + color);
            cancelArrow = true;
            class_1665 b = ((class_1744) class_1802.field_8087).method_7702(level, ammo, shooter);
            cancelArrow = false;
            int color2 = ((class_1667) b).method_7460();
            check("arrow-effects-cancelled", arrowAsked == 2 && color2 == -1, "asked=" + arrowAsked + " color=" + color2);
        });
        run("hurt-armor", () -> {
            class_3222 p = new class_3222(server, level, new com.mojang.authlib.GameProfile(UUID.randomUUID(), "cbsmx4b"), class_8791.method_53821());
            class_1799 chest = new class_1799(class_1802.field_8523);
            p.method_5673(class_1304.field_6174, chest);
            hurtArmorAsked = 0;
            ((cb.smx4b.mixin.PlayerInvoker) p).cbsmx4b$hurtArmor(level.method_48963().method_48830(), 8.0F);
            class_1799 worn = p.method_6118(class_1304.field_6174);
            check("hurt-armor", hurtArmorAsked == 1 && hurtArmorSlots == 4 && worn.method_7919() == 2,
                "asked=" + hurtArmorAsked + " slots=" + hurtArmorSlots + " damage=" + worn.method_7919());
        });
        run("piglin-sound", () -> {
            class_4836 piglin = class_1299.field_22281.method_5883(level);
            piglinAsked = 0;
            ((cb.smx4b.mixin.PiglinInvoker) piglin).cbsmx4b$playAngrySound();
            check("piglin-sound", piglinAsked == 1 && piglinSound == class_3417.field_22272, "asked=" + piglinAsked + " sound=" + piglinSound);
        });
        run("break-hand", () -> {
            class_1642 zombie = class_1299.field_6051.method_5883(level);
            class_1430 cow = class_1299.field_6085.method_5883(level);
            class_1799 sword = new class_1799(class_1802.field_8371);
            sword.method_7974(sword.method_7936() - 1);
            zombie.method_6122(class_1268.field_5808, sword);
            breakAsked = 0;
            sword.method_7909().method_7873(sword, cow, zombie);
            check("break-hand", breakAsked == 1 && breakHand == class_1268.field_5808 && sword.method_7960(),
                "asked=" + breakAsked + " hand=" + breakHand + " broken=" + sword.method_7960());
        });
        run("fireball-item", () -> {
            class_1677 f = new class_1677(level, 0.0, 100.0, 0.0, 0.0, 0.0, 0.0);
            fireAsked = 0;
            class_1799 item = f.method_7495();
            check("fireball-item", fireAsked == 1 && item.method_31574(class_1802.field_8814), "asked=" + fireAsked + " item=" + item);
        });
        run("snowball-item", () -> {
            class_1680 s = new class_1680(level, 0.0, 100.0, 0.0);
            throwAsked = 0;
            class_1799 item = s.method_7495();
            check("snowball-item", throwAsked == 1 && item.method_31574(class_1802.field_8543), "asked=" + throwAsked + " item=" + item);
        });
        System.out.println("CB-SMX4B summary ok=" + ok + " fail=" + fail);
    }
}
