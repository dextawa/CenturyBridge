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
    public static volatile int walkBoxAsked;
    public static volatile int walkEvaluated;
    public static volatile boolean walkBlockAll;
    public static volatile int swimAsked;
    public static volatile boolean swimNoDiagonal;
    public static volatile int goalsAsked;
    public static volatile boolean fakePanic;
    public static volatile net.minecraft.class_1314 panicMob;
    public static volatile int deflectAsked;
    public static volatile boolean cancelDeflect;
    // FriendlyByteBuf members (BufItemMixin / BufProfileMixin); cb-smx4bn reads them by reflection when it drives 1.20.5's codecs
    public static volatile int writeItemAsked;
    public static volatile int readItemAsked;
    public static volatile int writeProfileAsked;
    public static volatile int readProfileAsked;
    public static volatile int writePropertyAsked;
    public static volatile int readPropertyAsked;
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
        net.minecraft.class_2338 spawn = level.method_43126();
        int bx = spawn.method_10263();
        int bz = spawn.method_10260();
        run("walk-types", () -> {
            // a stone strip at y=200 with air above; a zombie at one end paths to the other
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
            net.minecraft.class_2338 target = new net.minecraft.class_2338(bx + 8, 201, bz + 1);
            walkBoxAsked = 0;
            walkEvaluated = 0;
            net.minecraft.class_11 path = zombie.method_5942().method_6348(target, 0);
            int box = walkBoxAsked;
            int eval = walkEvaluated;
            check("walk-types", box > 0 && eval > 0 && path != null && path.method_21655(),
                "getBlockPathTypes=" + box + " evaluateBlockPathType=" + eval + " reach=" + (path != null && path.method_21655()));
            walkBlockAll = true;
            net.minecraft.class_11 blocked = zombie.method_5942().method_6348(target, 0);
            walkBlockAll = false;
            check("walk-types-blocked", blocked == null || !blocked.method_21655(),
                "path=" + (blocked == null ? "none" : "reach=" + blocked.method_21655() + " nodes=" + blocked.method_38()));
        });
        run("swim-diagonal", () -> {
            // a 7x4x7 water pool in a stone shell at y=210..215; a dolphin paths from one corner to the opposite one
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
            net.minecraft.class_2338 target = new net.minecraft.class_2338(bx + 6, 212, bz + 6);
            swimAsked = 0;
            net.minecraft.class_11 path = dolphin.method_5942().method_6348(target, 0);
            int asked = swimAsked;
            swimNoDiagonal = true;
            net.minecraft.class_11 straight = dolphin.method_5942().method_6348(target, 0);
            swimNoDiagonal = false;
            int n1 = path == null ? -1 : path.method_38();
            int n2 = straight == null ? -1 : straight.method_38();
            check("swim-diagonal", asked > 0 && path != null && path.method_21655() && straight != null && straight.method_21655() && n2 > n1,
                "isDiagonalNodeValid=" + asked + " nodes(diagonal)=" + n1 + " nodes(no diagonal)=" + n2);
        });
        run("running-goals", () -> {
            class_1430 cow = class_1299.field_6085.method_5883(level);
            panicMob = cow;
            goalsAsked = 0;
            boolean calm = cow.method_52546();
            int asked = goalsAsked;
            fakePanic = true;
            boolean panic = cow.method_52546();
            fakePanic = false;
            check("running-goals", asked == 1 && !calm && goalsAsked == 2 && panic,
                "getRunningGoals=" + goalsAsked + " isPanicking=" + calm + " (faked running panic goal: " + panic + ")");
        });
        run("arrow-deflect", () -> {
            for (int cancel = 0; cancel < 2; cancel++) {
                net.minecraft.class_8949 breeze = new net.minecraft.class_8949(class_1299.field_47244, level);
                breeze.method_5814(bx + 0.5, 220.0, bz + 0.5);
                level.method_8649(breeze);
                class_1642 shooter = class_1299.field_6051.method_5883(level);
                class_1665 arrow = ((class_1744) class_1802.field_8107).method_7702(level, new class_1799(class_1802.field_8107), shooter);
                arrow.method_5814(bx - 1.5, 220.8, bz + 0.5);
                arrow.method_18800(2.0, 0.0, 0.0);
                deflectAsked = 0;
                cancelDeflect = cancel == 1;
                arrow.method_5773();
                cancelDeflect = false;
                double speed = arrow.method_18798().method_1033();
                breeze.method_31472();
                arrow.method_31472();
                if (cancel == 0) {
                    check("arrow-deflect", deflectAsked == 1 && speed < 1.5, "deflect=" + deflectAsked + " speed=" + speed);
                } else {
                    check("arrow-deflect-cancelled", deflectAsked == 1 && speed > 1.5, "deflect=" + deflectAsked + " speed=" + speed);
                }
            }
        });
        run("buf-plain-calls", () -> {
            // this mod's own 1.20.4 calls on a FriendlyByteBuf: the restored members, each mixin once, the values back
            net.minecraft.class_2540 buf = new net.minecraft.class_2540(io.netty.buffer.Unpooled.buffer());
            class_1799 sword = new class_1799(class_1802.field_8371);
            sword.method_7974(5);
            com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(UUID.randomUUID(), "cbsmx4b");
            profile.getProperties().put("textures", new com.mojang.authlib.properties.Property("textures", "v1", "s1"));
            writeItemAsked = 0;
            readItemAsked = 0;
            writeProfileAsked = 0;
            readProfileAsked = 0;
            writePropertyAsked = 0;
            readPropertyAsked = 0;
            buf.method_10793(sword);
            buf.method_43616(profile);
            class_1799 back = buf.method_10819();
            com.mojang.authlib.GameProfile p2 = buf.method_43623();
            com.mojang.authlib.properties.Property t = p2.getProperties().get("textures").iterator().next();
            boolean same = class_1799.method_7973(sword, back) && p2.getId().equals(profile.getId()) && p2.getName().equals("cbsmx4b")
                && t.value().equals("v1") && "s1".equals(t.signature()) && buf.readableBytes() == 0;
            check("buf-plain-calls", same && writeItemAsked == 1 && readItemAsked == 1 && writeProfileAsked == 1 && readProfileAsked == 1
                    && writePropertyAsked == 1 && readPropertyAsked == 1,
                "writeItem=" + writeItemAsked + " readItem=" + readItemAsked + " writeGameProfile=" + writeProfileAsked
                    + " readGameProfile=" + readProfileAsked + " writeProperty=" + writePropertyAsked + " readProperty=" + readPropertyAsked
                    + " roundTrip=" + same);
        });
        System.out.println("CB-SMX4B summary ok=" + ok + " fail=" + fail);
    }
}
