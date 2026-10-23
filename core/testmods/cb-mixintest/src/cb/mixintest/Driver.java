package cb.mixintest;

import cb.mixintest.mixin.BlocksAccess;
import cb.mixintest.mixin.BoatAccess;
import cb.mixintest.mixin.LivingEntityAccess;
import cb.mixintest.mixin.ScoreboardAccess;
import cb.mixintest.mixin.UtilAccess;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Runs once the server's world is loaded (ServerDriverMixin): exercises every invoker, accessor and
 * shadow of the test mixins on real objects, triggers the vanilla methods its injectors hook, and
 * checks what each returned or saw. Written against 1.20.1, run natively there as the reference and
 * climbed to 1.20.2 / 1.20.3 as the test.
 */
public final class Driver {
    private Driver() {
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        System.out.println("CB-MIXINTEST start");
        net.minecraft.class_2995 sb = server.method_3845();
        net.minecraft.class_3218 world = server.method_30002();

        // ---- Scoreboard: invokers / accessor of members 1.20.2 removed (instance + static redirect, field getter)
        // Scoreboard.getObjective(name) (method_1165) answers the named objective, or null
        net.minecraft.class_266 auto = sb.method_1168("cbt_auto", net.minecraft.class_274.field_1468,
            net.minecraft.class_2561.method_43470("CB auto"), net.minecraft.class_274.class_275.field_1472);
        try {
            net.minecraft.class_266 got = ((ScoreboardAccess) sb).cbtest$getOrCreate("cbt_auto");
            net.minecraft.class_266 none = ((ScoreboardAccess) sb).cbtest$getOrCreate("cbt_missing");
            T.check("invoker-instance-redirect", got == auto && none == null,
                "getObjective(cbt_auto) -> " + (got == null ? null : got.method_1113()) + ", getObjective(cbt_missing) -> " + none);
        } catch (Throwable t) {
            T.fail("invoker-instance-redirect", t);
        }
        try {
            String name = ScoreboardAccess.cbtest$slotName(1);
            T.check("invoker-static-redirect", "sidebar".equals(name), "getDisplaySlotName(1) -> " + name);
        } catch (Throwable t) {
            T.fail("invoker-static-redirect", t);
        }
        try {
            int list = ScoreboardAccess.cbtest$listSlotId();
            T.check("accessor-field-getter", list == 0, "DISPLAY_SLOT_LIST -> " + list);
        } catch (Throwable t) {
            T.fail("accessor-field-getter", t);
        }
        // ---- the same members through the mixin's own @Shadow method and @Shadow field
        try {
            net.minecraft.class_266 p = ((Probes.ScoreboardProbe) sb).cbtest$probeOrCreate("cbt_auto");
            net.minecraft.class_266 none = ((Probes.ScoreboardProbe) sb).cbtest$probeOrCreate("cbt_missing");
            T.check("shadow-instance-redirect", p == auto && none == null,
                "this.getObjective(cbt_auto) -> " + (p == null ? null : p.method_1113()) + ", (cbt_missing) -> " + none);
        } catch (Throwable t) {
            T.fail("shadow-instance-redirect", t);
        }
        try {
            int list = ((Probes.ScoreboardProbe) sb).cbtest$listSlot();
            T.check("shadow-field-getter", list == 0, "DISPLAY_SLOT_LIST -> " + list);
        } catch (Throwable t) {
            T.fail("shadow-field-getter", t);
        }
        // ---- addObjective: an invoker of a method 1.20.3 reshaped, and an @Inject taking its arguments
        net.minecraft.class_266 added = null;
        try {
            added = ((ScoreboardAccess) sb).cbtest$add("cbt_add", net.minecraft.class_274.field_1468,
                net.minecraft.class_2561.method_43470("CB add"), net.minecraft.class_274.class_275.field_1472);
            T.check("invoker-reshape", added != null && "cbt_add".equals(added.method_1113()),
                "addObjective -> " + (added == null ? null : added.method_1113()));
        } catch (Throwable t) {
            T.fail("invoker-reshape", t);
        }
        String seen = T.seen("inject-capture-hook");
        T.check("inject-capture-hook", seen != null && seen.startsWith("name=cbt_add "), "handler saw " + seen);
        // ---- setDisplayObjective: an @Inject taking the slot number 1.20.2 made a DisplaySlot
        try {
            sb.method_1158(1, added);
            String s = T.seen("inject-wrap-hook");
            T.check("inject-wrap-hook", ("slot=1 obj=cbt_add").equals(s), "handler saw " + s);
        } catch (Throwable t) {
            T.fail("inject-wrap-hook", t);
        }
        // ---- removeObjective: an @Inject at its call of setDisplayObjective (an anchor 1.20.2 reshaped)
        try {
            sb.method_1194(added);
            String s = T.seen("at-hook-position");
            T.check("at-hook-position", ("obj=cbt_add").equals(s), "handler saw " + s);
        } catch (Throwable t) {
            T.fail("at-hook-position", t);
        }

        // ---- LivingEntity.lyHeadRot (renamed in 1.20.2): accessor pair and @Shadow field on one field
        net.minecraft.class_1452 pig = null;
        net.minecraft.class_1452 pig2 = null;
        try {
            pig = net.minecraft.class_1299.field_6093.method_5883(world);
            pig2 = net.minecraft.class_1299.field_6093.method_5883(world);
            ((LivingEntityAccess) pig).cbtest$setLyHeadRot(12.5);
            double a = ((LivingEntityAccess) pig).cbtest$getLyHeadRot();
            double b = ((Probes.LivingProbe) pig).cbtest$shadowLyHeadRot();
            ((Probes.LivingProbe) pig).cbtest$shadowSetLyHeadRot(7.25);
            double c = ((LivingEntityAccess) pig).cbtest$getLyHeadRot();
            T.check("accessor-rename", a == 12.5 && c == 7.25, "set 12.5 get " + a + "; shadow set 7.25, accessor get " + c);
            T.check("shadow-field-rename", b == 12.5, "accessor set 12.5, shadow get " + b);
        } catch (Throwable t) {
            T.fail("accessor-rename", t);
        }
        // ---- Entity.getTeam / ignoreExplosion (reshaped in 1.20.3) through @Shadow, and an @Inject at getTeam's call
        try {
            net.minecraft.class_268 team = sb.method_1171("cbt_team");
            sb.method_1172(pig.method_5845(), team);
            Object got = ((Probes.EntityProbe) pig).cbtest$team();
            String n = got instanceof net.minecraft.class_270 tm ? tm.method_1197() : String.valueOf(got);
            T.check("shadow-reshape-keep", "cbt_team".equals(n), "this.getTeam() -> " + n);
            boolean ig = ((Probes.EntityProbe) pig).cbtest$ignoresExplosion();
            T.check("shadow-reshape-push", !ig, "this.ignoreExplosion() -> " + ig);
            boolean allied = pig.method_5722(pig2);
            String s = T.seen("at-reshape-position");
            T.check("at-reshape-position", s != null && s.equals("allied-with=" + pig2.method_5845()),
                "isAlliedTo -> " + allied + ", handler saw " + s);
        } catch (Throwable t) {
            T.fail("shadow-reshape", t);
        }

        // ---- Boat hurt time (moved to VehicleEntity and renamed in 1.20.3): invokers, @Shadow, @Redirect in tick
        try {
            net.minecraft.class_1690 boat = new net.minecraft.class_1690(world, 0.5, 120.0, 0.5);
            ((BoatAccess) boat).cbtest$setHurtTime(5);
            int a = ((BoatAccess) boat).invokeGetHurtTime();
            T.check("invoker-rename", a == 5, "setHurtTime(5), getHurtTime -> " + a);
            ((Probes.BoatProbe) boat).cbtest$setHurt(7);
            int b = ((Probes.BoatProbe) boat).cbtest$hurt();
            T.check("shadow-rename", b == 7, "shadow setHurtTime(7), getHurtTime -> " + b);
            boat.method_5773();
            String s = T.seen("at-rename-redirect");
            int c = ((BoatAccess) boat).invokeGetHurtTime();
            T.check("at-rename-redirect", "t=6".equals(s) && c == 6, "tick redirected setHurtTime: handler saw " + s + ", after " + c);
        } catch (Throwable t) {
            T.fail("boat", t);
        }

        // ---- Blocks.PUMPKIN (renamed in 1.20.3) through a static accessor
        try {
            net.minecraft.class_2248 p = BlocksAccess.cbtest$pumpkin();
            String id = String.valueOf(net.minecraft.class_7923.field_41175.method_10221(p));
            T.check("accessor-static-rename", "minecraft:pumpkin".equals(id), "PUMPKIN -> " + id);
        } catch (Throwable t) {
            T.fail("accessor-static-rename", t);
        }
        // ---- Block.getCloneItemStack (1.20.3 takes a LevelReader): an @Inject taking its arguments
        try {
            net.minecraft.class_2248 stone = net.minecraft.class_2246.field_10340;
            net.minecraft.class_1799 st = stone.method_9574(world, new net.minecraft.class_2338(0, 64, 0), stone.method_9564());
            String s = T.seen("inject-subtype-hook");
            T.check("inject-subtype-hook", s != null && s.startsWith("pos=0,64,0 "), "getCloneItemStack -> " + st + ", handler saw " + s);
        } catch (Throwable t) {
            T.fail("inject-subtype-hook", t);
        }
        // ---- Util.safeReplaceOrMoveFile (returns a boolean in 1.20.3): static invoker and a static @Inject
        try {
            Path dir = Files.createTempDirectory(Path.of("."), "cbmixintest");
            Path cur = dir.resolve("current");
            Path latest = dir.resolve("latest");
            Path backup = dir.resolve("backup");
            Files.write(cur, "old".getBytes(StandardCharsets.UTF_8));
            Files.write(latest, "new".getBytes(StandardCharsets.UTF_8));
            UtilAccess.cbtest$replace(cur, latest, backup, false);
            String now = new String(Files.readAllBytes(cur), StandardCharsets.UTF_8);
            String old = Files.exists(backup) ? new String(Files.readAllBytes(backup), StandardCharsets.UTF_8) : null;
            T.check("invoker-static-reshape", "new".equals(now) && "old".equals(old), "current=" + now + " backup=" + old);
            String s = T.seen("inject-ci-to-cir");
            T.check("inject-ci-to-cir", ("latest=" + latest.getFileName()).equals(s), "handler saw " + s);
        } catch (Throwable t) {
            T.fail("invoker-static-reshape", t);
        }
        // ---- constructor invokers of constructors 1.20.3 reshaped / redirected to a factory
        try {
            net.minecraft.class_238 box = cb.mixintest.mixin.BoxAccess.cbtest$newBox(new net.minecraft.class_2338(1, 2, 3),
                new net.minecraft.class_2338(4, 5, 6));
            T.check("invoker-ctor-reshape", box.field_1323 == 1.0 && box.field_1322 == 2.0 && box.field_1321 == 3.0
                && box.field_1320 == 4.0 && box.field_1325 == 5.0 && box.field_1324 == 6.0, "new AABB(pos, pos) -> " + box);
        } catch (Throwable t) {
            T.fail("invoker-ctor-reshape", t);
        }
        try {
            net.minecraft.class_1667 arrow = cb.mixintest.mixin.ArrowAccess.cbtest$newArrow(world, 1.5, 100.0, 2.5);
            T.check("invoker-ctor-factory", arrow.method_23317() == 1.5 && arrow.method_23321() == 2.5,
                "new Arrow(level, 1.5, 100, 2.5) -> " + arrow.method_23317() + "," + arrow.method_23318() + "," + arrow.method_23321());
        } catch (Throwable t) {
            T.fail("invoker-ctor-factory", t);
        }
        try {
            java.util.UUID id = java.util.UUID.fromString("0c0b0a09-0807-0605-0403-020100000000");
            net.minecraft.class_1322 mod = cb.mixintest.mixin.ModifierAccess.cbtest$newModifier(id, () -> "cbt_modifier", 2.5,
                net.minecraft.class_1322.class_1323.field_6328);
            T.check("invoker-ctor-reshape-post", "cbt_modifier".equals(mod.method_6185()) && mod.method_6186() == 2.5
                && id.equals(mod.method_6189()), "new AttributeModifier(id, () -> name, 2.5, ADDITION) -> name="
                + mod.method_6185() + " amount=" + mod.method_6186());
        } catch (Throwable t) {
            T.fail("invoker-ctor-reshape-post", t);
        }
        T.summary();
    }
}
