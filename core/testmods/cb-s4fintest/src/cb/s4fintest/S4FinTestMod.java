package cb.s4fintest;

import io.netty.buffer.Unpooled;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1263;
import net.minecraft.class_1280;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1295;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_1439;
import net.minecraft.class_1496;
import net.minecraft.class_1604;
import net.minecraft.class_1642;
import net.minecraft.class_1676;
import net.minecraft.class_1680;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1847;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2315;
import net.minecraft.class_2338;
import net.minecraft.class_2342;
import net.minecraft.class_2350;
import net.minecraft.class_2371;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2540;
import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_2601;
import net.minecraft.class_2664;
import net.minecraft.class_2680;
import net.minecraft.class_2783;
import net.minecraft.class_2965;
import net.minecraft.class_3218;
import net.minecraft.class_3417;
import net.minecraft.class_3745;
import net.minecraft.class_5134;
import net.minecraft.class_5631;
import net.minecraft.class_7450;
import net.minecraft.class_7472;
import net.minecraft.class_7635;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.4-era server mod (intermediary names; Fabric lifecycle events, FakePlayer) for the s4fin network and
 * world/entity bridges of step 1.20.4 -> 1.20.5. Once the server has started it checks, each printing
 * "CBS4FIN OK|FAIL ...", "CBS4FIN DONE n/m" at the end:
 * <ul>
 * <li>a mod ProjectileDispenseBehavior (1.20.4's abstract class) dispensing out of a real dispenser: one
 *     projectile per item, shot by its own getPower(), one item used;</li>
 * <li>AreaEffectCloud: setPotion / getPotion, setFixedColor / getColor;</li>
 * <li>AttributeInstance.getModifiers(operation) seeing a modifier added through the instance;</li>
 * <li>IronGolem.Crackiness.byFraction, GoalSelector.getRunningGoals;</li>
 * <li>ClientboundUpdateMobEffectPacket(id, instance): getEffect (the effect), getEffectAmplifier, getFactorData
 *     (none for speed); ClientboundExplodePacket with a SoundEvent: getExplosionSound;</li>
 * <li>ChatType.BoundNetwork write / read round trip;</li>
 * <li>ServerboundChatCommandPacket with no signatures: the command, empty signatures, salt 0;</li>
 * <li>CombatRules.getDamageAfterAbsorb (1.20.4's formula), Container.stillValidBlockEntity (in reach / out of reach),
 *     Inventory.hurtArmor (a quarter of the damage to the chest piece);</li>
 * <li>BundleTooltip(items, weight): getItems / getWeight; CrossbowAttackMob.getProjectileShotVector (1.20.4's default);
 *     AbstractHorse.getCustomJump (the jump strength attribute).</li>
 * </ul>
 */
public class S4FinTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        if (!ok) {
            FAILS.add(what);
        }
        System.out.println("CBS4FIN " + (ok ? "OK " : "FAIL ") + what);
    }

    /** a 1.20.4 mod's projectile dispense behaviour */
    static final class SnowDispense extends class_2965 {
        int made;

        @Override
        protected class_1676 method_12844(class_1937 level, class_2374 pos, class_1799 stack) {
            made++;
            return new class_1680(level, pos.method_10216(), pos.method_10214(), pos.method_10215());
        }

        @Override
        protected float method_12846() {
            return 2.0F;
        }

        @Override
        protected float method_12845() {
            return 0.0F;
        }
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(S4FinTestMod::run);
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();
        try {
            dispense(level);
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "dispense threw " + t);
        }
        guard("cloud", () -> {
            class_1295 cloud = new class_1295(level, 0.5, 100, 0.5);
            cloud.method_5612(class_1847.field_8963);
            check(cloud.method_35048() == class_1847.field_8963, "cloud getPotion is the potion set");
            cloud.method_5602(0x123456);
            check(cloud.method_5606() == 0x123456, "cloud getColor is the fixed color (" + Integer.toHexString(cloud.method_5606()) + ")");
        });
        guard("attributes", () -> {
            net.minecraft.class_1642 zombie = class_1299.field_6051.method_5883(level);
            class_1324 health = zombie.method_5996(class_5134.field_23716);
            class_1322 mod = new class_1322("cb test", 4.0, class_1322.class_1323.field_6328);
            health.method_26835(mod);
            check(health.method_6193(class_1322.class_1323.field_6328).contains(mod), "getModifiers(ADDITION) sees the added modifier");
            check(new net.minecraft.class_1355(level::method_16107).method_19048().count() == 0, "getRunningGoals of a fresh selector is empty");
        });
        guard("golem", () -> check(class_1439.class_4621.method_23693(0.3F) == class_1439.class_4621.field_21083, "Crackiness.byFraction(0.3) is MEDIUM"));
        guard("effect packet", () -> {
            class_2783 p = new class_2783(7, new class_1293(class_1294.field_5904, 200, 1));
            check(p.method_11946() == class_1294.field_5904, "mob effect packet getEffect is the effect");
            check(p.method_11945() == 1, "mob effect packet getEffectAmplifier");
            check(p.method_42625() == null, "mob effect packet getFactorData none for speed");
        });
        guard("explode packet", () -> {
            class_2664 p = new class_2664(1, 2, 3, 4.0F, List.of(), null, net.minecraft.class_1927.class_4179.field_18687,
                net.minecraft.class_2398.field_11236, net.minecraft.class_2398.field_11221, class_3417.field_15152);
            check(p.method_55331() == class_3417.field_15152, "explode packet getExplosionSound");
        });
        guard("bound network", () -> {
            class_2556.class_7603 b = new class_2556.class_7603(3, class_2561.method_43470("a"), class_2561.method_43470("b"));
            class_2540 buf = new class_2540(Unpooled.buffer());
            b.method_44841(buf);
            class_2556.class_7603 r = new class_2556.class_7603(buf);
            check(r.equals(b) && buf.readableBytes() == 0, "ChatType.BoundNetwork round trip");
        });
        guard("chat command", () -> {
            class_7472 p = new class_7472("say hi", Instant.now(), 0L, class_7450.field_39807, new class_7635.class_7636(0, new BitSet()));
            check("say hi".equals(p.comp_808()) && p.comp_810().comp_912().isEmpty() && p.comp_944() == 0L, "unsigned chat command packet");
        });
        guard("combat", () -> {
            float expect = 10.0F * (1.0F - net.minecraft.class_3532.method_15363(10.0F - 10.0F / 2.5F, 2.0F, 20.0F) / 25.0F);
            check(Math.abs(class_1280.method_5496(10.0F, 10.0F, 2.0F) - expect) < 1.0E-5F, "getDamageAfterAbsorb is 1.20.4's formula");
        });
        guard("reach and armor", () -> {
            class_2338 pos = new class_2338(0, 120, 0);
            level.method_8501(pos, class_2246.field_10200.method_9564());
            class_2601 be = (class_2601) level.method_8321(pos);
            net.fabricmc.fabric.api.entity.FakePlayer player = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            player.method_5814(0.5, 121, 2.5);
            check(class_1263.method_49106(be, player, 8), "stillValidBlockEntity within 8");
            player.method_5814(0.5, 121, 20.5);
            check(!class_1263.method_49106(be, player, 8), "stillValidBlockEntity not at 20");
            class_1799 chest = new class_1799(class_1802.field_8523);
            player.method_5673(class_1304.field_6174, chest);
            int before = chest.method_7919();
            player.method_31548().method_7375(level.method_48963().method_48830(), 8.0F, new int[] {2});
            check(chest.method_7919() == before + 2, "Inventory.hurtArmor quarter damage (" + (chest.method_7919() - before) + ")");
        });
        guard("bundle", () -> {
            class_2371<class_1799> items = class_2371.method_10211();
            items.add(new class_1799(class_1802.field_8600, 16));
            class_5631 t = new class_5631(items, 16);
            check(t.method_32340().size() == 1 && t.method_32341() == 16, "BundleTooltip items / weight (" + t.method_32341() + ")");
        });
        guard("crossbow vector", () -> {
            class_1604 pillager = class_1299.field_6105.method_5883(level);
            org.joml.Vector3f v = ((class_3745) pillager).method_24653(pillager, new class_243(3, 0, 4), 0.0F);
            check(Math.abs(v.x() - 0.6F) < 1.0E-4F && Math.abs(v.z() - 0.8F) < 1.0E-4F, "getProjectileShotVector (" + v + ")");
        });
        guard("horse jump", () -> {
            class_1496 horse = (class_1496) class_1299.field_6139.method_5883(level);
            check(horse.method_6771() == horse.method_26825(class_5134.field_23728), "getCustomJump is the jump strength");
        });
        guard("item parser", () -> {
            net.minecraft.class_2291.class_7215 r;
            try {
                r = net.minecraft.class_2291.method_41972(net.minecraft.class_7923.field_41178.method_46771(),
                    new com.mojang.brigadier.StringReader("minecraft:diamond_sword{Damage:5}"));
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            check(r.comp_628().comp_349() == class_1802.field_8802 && r.comp_629() != null && r.comp_629().method_10550("Damage") == 5,
                "ItemParser.parseForItem reads 1.20.4's id{nbt} (" + r.comp_629() + ")");
            net.minecraft.class_2290 input = new net.minecraft.class_2290(r.comp_628(), r.comp_629());
            class_1799 damaged = new class_1799(class_1802.field_8802);
            damaged.method_7974(5);
            check(input.method_9783(damaged) && !input.method_9783(new class_1799(class_1802.field_8802)), "ItemInput.test by item and NBT");
        });
        guard("item predicate", () -> {
            net.minecraft.class_2073 p = net.minecraft.class_2073.class_2074.method_8973().method_35232(class_1847.field_8963).method_8976();
            class_1799 healing = net.minecraft.class_1844.method_8061(new class_1799(class_1802.field_8574), class_1847.field_8963);
            class_1799 water = net.minecraft.class_1844.method_8061(new class_1799(class_1802.field_8574), class_1847.field_8991);
            check(p.method_8970(healing) && !p.method_8970(water), "ItemPredicate isPotion");
            check(p.comp_1789().isPresent() && p.comp_1789().get().comp_349() == class_1847.field_8963, "ItemPredicate.potion()");
        });
        guard("sub predicate types", () -> check(net.minecraft.class_7376.class_7378.field_38731.get("slime") == net.minecraft.class_7376.class_7378.field_38728
            && net.minecraft.class_7376.class_7378.field_38731.inverse().get(net.minecraft.class_7376.class_7378.field_38729.method_43114()).equals("cat"),
            "EntitySubPredicate.Types by 1.20.4 name"));
        guard("block predicate", () -> {
            class_2338 pos = new class_2338(8, 120, 8);
            level.method_8501(pos, class_2246.field_10431.method_9564());
            net.minecraft.class_4550 p = new net.minecraft.class_4550(java.util.Optional.of(net.minecraft.class_3481.field_15475),
                java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty());
            check(p.method_22454(level, pos) && p.comp_1731().isPresent(), "BlockPredicate by tag");
        });
        guard("registry lifecycle", () -> check(net.minecraft.class_7923.field_41178.method_31139(class_1802.field_8600) != null,
            "Registry.lifecycle(value)"));
        System.out.println("CBS4FIN DONE " + (checks - FAILS.size()) + "/" + checks);
    }

    static void guard(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, what + " threw " + t);
        }
    }

    static void dispense(class_3218 level) {
        class_2338 pos = new class_2338(4, 120, 4);
        class_2680 state = class_2246.field_10200.method_9564().method_11657(class_2315.field_10918, class_2350.field_11036);
        level.method_8501(pos, state);
        class_2601 be = (class_2601) level.method_8321(pos);
        SnowDispense behavior = new SnowDispense();
        class_1799 stack = new class_1799(class_1802.field_8543, 3);
        net.minecraft.class_238 box = new net.minecraft.class_238(pos).method_1014(3.0);
        int before = level.method_18467(class_1680.class, box).size();
        class_1799 left = behavior.dispense(new class_2342(level, pos, state, be), stack);
        List<class_1680> after = level.method_18467(class_1680.class, box);
        check(behavior.made == 1 && after.size() == before + 1, "dispense made one projectile (" + behavior.made + ", " + (after.size() - before) + ")");
        check(left.method_7947() == 2, "dispense used one item (" + left.method_7947() + ")");
        if (!after.isEmpty()) {
            class_243 v = after.get(after.size() - 1).method_18798();
            check(v.field_1351 > 1.9 && Math.abs(v.field_1352) < 1.0E-6 && Math.abs(v.field_1350) < 1.0E-6,
                "dispensed upward at the behaviour's power (" + v + ")");
        }
    }
}
