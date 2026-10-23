package cb.s7gear;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1304;
import net.minecraft.class_1738;
import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1810;
import net.minecraft.class_1829;
import net.minecraft.class_1832;
import net.minecraft.class_1834;
import net.minecraft.class_1856;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3417;
import net.minecraft.class_3481;
import net.minecraft.class_4174;
import net.minecraft.class_5134;
import net.minecraft.class_5151;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_8791;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import net.minecraft.class_9424;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.1-era server mod (intermediary names, Fabric Loader API only) for the s7item gear bridges of the
 * 1.21.1 -> 1.21.2 step: a registered 1.21.1 ArmorMaterial and an ArmorItem built from it (durability from its
 * Properties, defense / toughness / slot / material / enchantment value / repair / attribute modifiers, Equipable,
 * an ArmorItem subclass overriding getEquipmentSlot seen by the game's slot choice), a 1.21.1 Tier enum and the
 * tools built from it and from Tiers (durability, getTier, Tool, the caller's attribute modifiers, correct tool for
 * drops), and a food with 1.21.1's fast / effect (eat time, the effect applied when a mob eats it).
 * Every check prints one "CB-GEARTEST ok|FAIL ..." line; the last is "CB-GEARTEST DONE &lt;passed&gt;/&lt;checks&gt;".
 *
 * <p>Build against 1.21.1:
 * <pre>
 *   javac --release 21 -cp "client-1.21.1-intermediary.jar;fabric-loader.jar;&lt;1.21.1 libraries&gt;" -d out \
 *         core/testmods/cb-s7gear/src/cb/s7gear/*.java
 *   cp core/testmods/cb-s7gear/res/fabric.mod.json out/ &amp;&amp; (cd out &amp;&amp; jar cf cb-s7gear.jar .)
 * </pre>
 */
public class GearTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-GEARTEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    /** a 1.21.1 tier, as mods wrote them */
    public enum GearTier implements class_1832 {
        EMERALD;

        public int method_8025() {
            return 777;
        }

        public float method_8027() {
            return 7.0F;
        }

        public float method_8028() {
            return 2.5F;
        }

        public class_6862<class_2248> method_58419() {
            return class_3481.field_49927;
        }

        public int method_8026() {
            return 12;
        }

        public class_1856 method_8023() {
            return class_1856.method_8091(class_1802.field_8687);
        }
    }

    /** an ArmorItem subclass putting itself on the head */
    public static class HeadArmor extends class_1738 {
        public HeadArmor(class_6880<class_1741> material, class_1738.class_8051 type, class_1792.class_1793 p) {
            super(material, type, p);
        }

        @Override
        public class_1304 method_7685() {
            return class_1304.field_6169;
        }
    }

    static class_6880<class_1741> MATERIAL;
    static class_1792 CHEST, HEAD, SWORD, PICK, FOOD;
    static class_4174 FOOD_PROPS;

    static <T> T reg(class_2378<? super T> r, String path, T v) {
        class_2378.method_10230(r, class_2960.method_60655("cbs7gear", path), v);
        return v;
    }

    @Override
    public void onInitialize() {
        EnumMap<class_1738.class_8051, Integer> defense = new EnumMap<>(class_1738.class_8051.class);
        defense.put(class_1738.class_8051.field_41934, 2);
        defense.put(class_1738.class_8051.field_41935, 7);
        defense.put(class_1738.class_8051.field_41936, 5);
        defense.put(class_1738.class_8051.field_41937, 2);
        defense.put(class_1738.class_8051.field_48838, 9);
        MATERIAL = class_2378.method_47985(class_7923.field_48976, class_2960.method_60655("cbs7gear", "emerald"),
            new class_1741(defense, 7, class_3417.field_14883, () -> class_1856.method_8091(class_1802.field_8687),
                List.of(new class_1741.class_9196(class_2960.method_60655("cbs7gear", "emerald"))), 1.5F, 0.05F));
        CHEST = reg(class_7923.field_41178, "chest", new class_1738(MATERIAL, class_1738.class_8051.field_41935,
            new class_1792.class_1793().method_7895(123)));
        HEAD = reg(class_7923.field_41178, "head", new HeadArmor(MATERIAL, class_1738.class_8051.field_41935,
            new class_1792.class_1793().method_7895(50)));
        SWORD = reg(class_7923.field_41178, "sword", new class_1829(GearTier.EMERALD,
            new class_1792.class_1793().method_57348(class_1829.method_57394(GearTier.EMERALD, 3, -2.4F))));
        PICK = reg(class_7923.field_41178, "pick", new class_1810(class_1834.field_8923, new class_1792.class_1793()));
        FOOD_PROPS = new class_4174.class_4175().method_19238(3).method_19237(0.5F).method_19241()
            .method_19239(new class_1293(class_1294.field_5904, 100, 0), 1.0F).method_19240().method_19242();
        FOOD = reg(class_7923.field_41178, "food", new class_1792(new class_1792.class_1793().method_19265(FOOD_PROPS)));
        Thread t = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(() -> run(server));
                    return;
                }
            }
        }, "cb-s7gear");
        t.setDaemon(true);
        t.start();
    }

    static void run(MinecraftServer server) {
        try {
            armor(server);
            tools();
            food(server);
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, "no exception: " + e);
        }
        System.out.println("CB-GEARTEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED " + FAILS));
    }

    static double amount(class_9285 m, class_6880<?> attribute) {
        double sum = 0;
        for (class_9285.class_9287 e : m.comp_2393()) {
            if (e.comp_2395() == attribute) {
                sum += e.comp_2396().comp_2449();
            }
        }
        return sum;
    }

    static class_3222 player(MinecraftServer server) {
        class_3218 level = server.method_30002();
        return new class_3222(server, level, new GameProfile(UUID.randomUUID(), "cbgeartest"), class_8791.method_53821());
    }

    static void armor(MinecraftServer server) {
        class_1738 chest = (class_1738) CHEST;
        class_1799 stack = new class_1799(CHEST);
        check(stack.method_7936() == 123, "armor durability is its Properties' (1.21.1 ArmorItem left it): " + stack.method_7936());
        check(chest.method_7687() == 7, "getDefense = the material's chestplate defense: " + chest.method_7687());
        check(chest.method_26353() == 1.5F, "getToughness");
        check(chest.method_7685() == class_1304.field_6174, "getEquipmentSlot = chest");
        check(chest.method_48398() == class_1738.class_8051.field_41935, "getType = chestplate");
        check(chest.method_7686() == MATERIAL, "getMaterial = the registered holder");
        check(class_7923.field_48976.method_10221(MATERIAL.comp_349()) != null, "the material is in ARMOR_MATERIAL");
        check(chest.method_7837() == 7, "getEnchantmentValue = the material's: " + chest.method_7837());
        check(chest.method_7878(stack, new class_1799(class_1802.field_8687)), "repairs with the material's ingredient (emerald)");
        check(!chest.method_7878(stack, new class_1799(class_1802.field_8477)), "does not repair with diamond");
        class_9285 mods = stack.method_57824(class_9334.field_49636);
        check(mods != null && amount(mods, class_5134.field_23724) == 7.0 && amount(mods, class_5134.field_23725) == 1.5,
            "attribute modifiers: armor 7, toughness 1.5");
        class_5151 eq = class_5151.method_48957(stack);
        check(eq != null && eq.method_7685() == class_1304.field_6174, "Equipable.get(stack) answers the chest slot");
        class_3222 player = player(server);
        check(player.method_32326(stack) == class_1304.field_6174, "the game equips the armor to the chest");
        check(player.method_32326(new class_1799(HEAD)) == class_1304.field_6169,
            "the game equips a subclass overriding getEquipmentSlot to its slot (head)");
        check(player.method_32326(new class_1799(class_1802.field_8833)) == class_1304.field_6174
                && class_5151.method_48957(new class_1799(class_1802.field_8833)).method_7685() == class_1304.field_6174,
            "vanilla elytra: Equipable.get answers the chest");
    }

    static void tools() {
        class_1799 sword = new class_1799(SWORD);
        check(sword.method_7936() == 777, "tool durability = the tier's uses: " + sword.method_7936());
        check(((class_1829) SWORD).method_8022() == GearTier.EMERALD, "SwordItem.getTier = the tier");
        check(SWORD.method_7837() == 12, "sword enchantment value = the tier's");
        class_9285 mods = sword.method_57824(class_9334.field_49636);
        check(mods != null && amount(mods, class_5134.field_23721) == 5.5, "sword attack damage = the caller's createAttributes (3 + 2.5): "
            + (mods == null ? null : amount(mods, class_5134.field_23721)));
        class_9424 tool = sword.method_57824(class_9334.field_50077);
        check(tool != null && tool.comp_2498().size() == 2 && tool.comp_2500() == 2, "sword Tool = 1.21.1's (2 rules, 2 damage per block)");
        class_1799 pick = new class_1799(PICK);
        check(pick.method_7936() == 250 && ((class_1810) PICK).method_8022() == class_1834.field_8923, "iron pickaxe: 250 uses, Tiers.IRON");
        check(pick.method_7951(class_2246.field_10340.method_9564()) && !pick.method_7951(class_2246.field_10540.method_9564()),
            "iron pickaxe: correct for stone, not for obsidian");
        check(SWORD.method_7878(sword, new class_1799(class_1802.field_8687)), "sword repairs with the tier's ingredient");
    }

    static void food(MinecraftServer server) {
        check(FOOD_PROPS.comp_2494() == 0.8F && FOOD_PROPS.method_58399() == 16, "fast food: 0.8 s, 16 ticks");
        check(FOOD_PROPS.comp_2495().size() == 1 && FOOD_PROPS.comp_2495().get(0).comp_2497() == 1.0F, "food effects: one, certain");
        class_3222 player = player(server);
        class_1799 stack = new class_1799(FOOD);
        check(FOOD.method_7881(stack, player) == 16, "the item eats in 16 ticks: " + FOOD.method_7881(stack, player));
        // a mob eats it (a test player has no connection to send the eating to)
        net.minecraft.class_1642 zombie = new net.minecraft.class_1642(server.method_30002());
        class_1799 eaten = new class_1799(FOOD, 2);
        class_1799 left = eaten.method_7910(server.method_30002(), zombie);
        check(zombie.method_6059(class_1294.field_5904), "eating gives the food's effect (speed)");
        check(left.method_7947() == 1, "eating uses one: " + left.method_7947());
    }
}
