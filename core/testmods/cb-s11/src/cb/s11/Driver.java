package cb.s11;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.FabricGameRuleVisitor;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.class_1928;
import net.minecraft.class_2487;
import net.minecraft.server.MinecraftServer;

/**
 * Runs once the world is loaded: 1.21.10 game rule reads and writes, checked against the game rule command of the
 * running release (on 1.21.11 it names the 1.21.11 rules). "CB-S11 ok|FAIL case detail", then "CB-S11 summary ok=N fail=M".
 */
public final class Driver {
    public static volatile boolean forceKeep;
    public static volatile int boolSets;
    public static volatile int argSets;
    public static volatile boolean loudResult;
    public static volatile boolean loudSerialize;
    public static volatile Object builtBiome;
    private static int ok;
    private static int fail;

    private Driver() {
    }

    private static void check(String c, boolean cond, Object detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-S11 " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    private static int query(MinecraftServer server, String command) {
        try {
            return server.method_3734().method_9235().execute(command, server.method_3739());
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            return -1;
        }
    }

    private static void cmd(MinecraftServer server, String command) {
        server.method_3734().method_44252(server.method_3739(), command);
    }

    private static net.minecraft.class_2960 class_2960_of(String ns, String path) {
        return net.minecraft.class_2960.method_60655(ns, path);
    }

    public static void run(MinecraftServer server) {
        try {
            run0(server);
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-S11 FAIL exception " + t);
            t.printStackTrace(System.out);
        }
        System.out.println("CB-S11 summary ok=" + ok + " fail=" + fail);
    }

    private static void run0(MinecraftServer server) {
        // a 1.21.10 biome built with BiomeSpecialEffects.Builder's fog / water fog / sky color and music volume (1.21.11
        // keeps them as biome environment attributes; cb-s11n reads them back); water color stays in the effects
        net.minecraft.class_4763 effects = new net.minecraft.class_4763.class_4764().method_24395(0x111111).method_24392(0x123456)
            .method_24397(0x222222).method_30820(0x654321).method_65137(0.5F).method_24391();
        builtBiome = new net.minecraft.class_1959.class_1960().method_48164(true).method_8747(0.5F).method_8727(0.5F)
            .method_24379(effects).method_30974(net.minecraft.class_5483.field_26646).method_30973(net.minecraft.class_5485.field_26639)
            .method_30972();
        check("biome-effects-water", effects.method_24388() == 0x111111, Integer.toHexString(effects.method_24388()));
        // 1.21.10's DimensionType.effectsLocation: 1.21.11's own types by skybox / lighting, and cb_s11:fx's "effects"
        net.minecraft.class_2378<net.minecraft.class_2874> types = server.method_30611().method_30530(net.minecraft.class_7924.field_41241);
        net.minecraft.class_2960 ow = types.method_31140(net.minecraft.class_7134.field_37666).comp_655();
        net.minecraft.class_2960 ne = types.method_31140(net.minecraft.class_7134.field_37667).comp_655();
        net.minecraft.class_2960 en = types.method_31140(net.minecraft.class_7134.field_37668).comp_655();
        net.minecraft.class_2960 fx = types.method_31140(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41241,
            class_2960_of("cb_s11", "fx"))).comp_655();
        check("dimension-effects-location", ow.equals(net.minecraft.class_7134.field_37670) && ne.equals(net.minecraft.class_7134.field_37671)
            && en.equals(net.minecraft.class_7134.field_37672) && fx.equals(class_2960_of("cb_s11", "fx")), ow + " " + ne + " " + en + " " + fx);
        class_1928 rules = server.method_3767();
        // vanilla keys: read what the command set, write what the command reads
        cmd(server, "gamerule keep_inventory true");
        check("keepInventory-read", rules.method_8355(class_1928.field_19389), rules.method_8355(class_1928.field_19389));
        rules.method_20746(class_1928.field_19389).method_20758(false, server);
        check("keepInventory-write", !rules.method_8355(class_1928.field_19389) && !server.method_3767().method_8355(class_1928.field_19389), "set false");
        cmd(server, "gamerule random_tick_speed 7");
        check("randomTickSpeed", rules.method_8356(class_1928.field_19399) == 7, rules.method_8356(class_1928.field_19399));
        class_1928.class_4312 spawn = rules.method_20746(class_1928.field_19403);
        spawn.method_35236(4, server);
        check("spawnRadius-live", rules.method_20746(class_1928.field_19403).method_20763() == 4 && spawn.method_20781() == 4, spawn.method_20779());
        // inverted: disableRaids is !raids
        cmd(server, "gamerule raids false");
        check("disableRaids-read", rules.method_8355(class_1928.field_19422), rules.method_8355(class_1928.field_19422));
        rules.method_20746(class_1928.field_19422).method_20758(false, server);
        check("disableRaids-write", !rules.method_8355(class_1928.field_19422), "raids back on");
        // the fire tick pair over fire_spread_radius_around_player
        cmd(server, "gamerule fire_spread_radius_around_player 0");
        check("doFireTick-off", !rules.method_8355(class_1928.field_19387), "radius 0");
        rules.method_20746(class_1928.field_56559).method_20758(true, server);
        check("allowAway-while-off", rules.method_8355(class_1928.field_56559) && !rules.method_8355(class_1928.field_19387), "remembered, still off");
        rules.method_20746(class_1928.field_19387).method_20758(true, server);
        check("doFireTick-on-anywhere", rules.method_8355(class_1928.field_19387) && rules.method_8355(class_1928.field_56559), "radius -1");
        cmd(server, "gamerule fire_spread_radius_around_player 64");
        check("radius-64", rules.method_8355(class_1928.field_19387) && !rules.method_8355(class_1928.field_56559), "near players");
        rules.method_20746(class_1928.field_19387).method_20758(false, server);
        rules.method_20746(class_1928.field_19387).method_20758(true, server);
        check("doFireTick-restore-near", rules.method_8355(class_1928.field_19387) && !rules.method_8355(class_1928.field_56559), "near players again");

        // a mod mixin on GameRules.getBoolean changes what vanilla reads (the game rule command's query reads 1.21.11's get)
        cmd(server, "gamerule keep_inventory false");
        int plain = query(server, "gamerule keep_inventory");
        forceKeep = true;
        int forced = query(server, "gamerule keep_inventory");
        boolean modView = rules.method_8355(class_1928.field_19389);
        forceKeep = false;
        check("mixin-vanilla-read", plain == 0 && forced == 1 && modView && query(server, "gamerule keep_inventory") == 0,
            "plain " + plain + " forced " + forced);
        // a GameRules subclass's override answers converted callers; its super call reaches the restored body
        MyRules mine = new MyRules(net.minecraft.class_7701.field_40183);
        check("subclass-override", !mine.method_8355(class_1928.field_19392) && !mine.method_8355(class_1928.field_19389)
            && mine.method_8356(class_1928.field_19399) == 3, "doTileDrops off, keepInventory default");
        // assignFrom copies the other rules' values (1.21.11's setAll runs the restored assignFrom)
        mine.method_20746(class_1928.field_19389).method_20758(true, null);
        mine.method_20746(class_1928.field_19399).method_35236(9, null);
        rules.method_27322(mine, server);
        check("assignFrom", rules.method_8355(class_1928.field_19389) && rules.method_8356(class_1928.field_19399) == 9
            && query(server, "gamerule random_tick_speed") == 9, rules.method_8356(class_1928.field_19399));
        cmd(server, "gamerule keep_inventory false");
        // the game rule command writes through Value.setFromArgument, as 1.21.10's did (a mod mixin on it sees each
        // command write once; BooleanValue.set is not what 1.21.10's command called); a negated rule's command
        // goes through the 1.21.10 value's set (its argument has the other meaning)
        int sets = boolSets;
        int args = argSets;
        cmd(server, "gamerule mob_griefing false");
        cmd(server, "gamerule raids true");
        cmd(server, "gamerule random_tick_speed 4");
        check("mixin-command-write", argSets == args + 2 && boolSets == sets + 1 && !rules.method_8355(class_1928.field_19388)
            && !rules.method_8355(class_1928.field_19422) && rules.method_8356(class_1928.field_19399) == 4,
            "setFromArgument " + (argSets - args) + " set " + (boolSets - sets));
        cmd(server, "gamerule mob_griefing true");
        // a mod mixin on BooleanValue.getCommandResult changes what the game rule command returns
        loudResult = true;
        int loud = query(server, "gamerule keep_inventory");
        loudResult = false;
        check("mixin-command-result", loud == 7 && query(server, "gamerule keep_inventory") == 0, "loud " + loud);
        // mod rules
        check("hasRegistration", GameRuleRegistry.hasRegistration("cbS11Flag") && GameRuleRegistry.hasRegistration("keepInventory")
            && !GameRuleRegistry.hasRegistration("noSuchRule"), "flag/keepInventory/none");
        int calls = Init.flagCalls;
        cmd(server, "gamerule cb_s11_flag true");
        check("modFlag-command", rules.method_8355(Init.FLAG) && Init.flagCalls == calls + 1 && Boolean.TRUE.equals(Init.lastFlag),
            "calls " + (Init.flagCalls - calls) + " last " + Init.lastFlag);
        rules.method_20746(Init.FLAG).method_20758(false, server);
        check("modFlag-set", !rules.method_8355(Init.FLAG) && Init.flagCalls == calls + 2 && Boolean.FALSE.equals(Init.lastFlag),
            "calls " + (Init.flagCalls - calls));
        check("modCount-default", rules.method_8356(Init.COUNT) == 5, rules.method_8356(Init.COUNT));
        cmd(server, "gamerule cb_s11_count 11");
        check("modCount-bounds", rules.method_8356(Init.COUNT) == 5, rules.method_8356(Init.COUNT));
        cmd(server, "gamerule cb_s11_count 9");
        check("modCount-command", rules.method_8356(Init.COUNT) == 9, rules.method_8356(Init.COUNT));
        DoubleRule ratio = rules.method_20746(Init.RATIO);
        ratio.set(0.25, server);
        check("modRatio", ratio.get() == 0.25 && "0.25".equals(rules.method_20746(Init.RATIO).method_20779()), ratio.method_20779());
        boolean threw = false;
        try {
            ratio.set(2.0, server);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("modRatio-bounds", threw && ratio.get() == 0.25, ratio.get());
        EnumRule<Init.Mode> mode = rules.method_20746(Init.MODE);
        mode.cycle();
        check("modMode-cycle", mode.get() == Init.Mode.B, mode.get());
        check("modMode-category", CustomGameRuleCategory.getCategory(Init.MODE).map(c -> c.equals(Init.CATEGORY)).orElse(false)
            && CustomGameRuleCategory.getCategory(Init.FLAG).isEmpty(), "custom on mode only");
        // saved form and visitor
        class_2487 tag = rules.method_8358();
        check("createTag", tag.method_10545("keepInventory") && tag.method_10545("cbS11Flag") && tag.method_10545("doFireTick")
            && tag.method_10545("disableRaids"), tag.method_10546());
        List<String> seen = new ArrayList<>();
        int[] doubles = new int[1];
        rules.method_20744(new FabricGameRuleVisitor() {
            @Override
            public <T extends class_1928.class_4315<T>> void method_20762(class_1928.class_4313<T> key, class_1928.class_4314<T> type) {
                seen.add(key.method_20771());
            }

            @Override
            public void visitDouble(class_1928.class_4313<DoubleRule> key, class_1928.class_4314<DoubleRule> type) {
                doubles[0]++;
            }
        });
        check("visitor", seen.contains("keepInventory") && seen.contains("cbS11Mode") && doubles[0] == 1
            && seen.indexOf("allowEnteringNetherUsingPortals") < seen.indexOf("keepInventory"), seen.size() + " rules, " + doubles[0] + " double");
        check("keyId", class_1928.field_19389.method_20771().equals("keepInventory")
            && class_1928.field_19389.method_27334().equals("gamerule.keepInventory")
            && class_1928.field_19389.method_27335() == class_1928.class_5198.field_24094, class_1928.field_19389);
    }
}
