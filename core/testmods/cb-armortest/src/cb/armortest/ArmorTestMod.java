package cb.armortest;

import com.google.common.collect.Multimap;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1498;
import net.minecraft.class_1646;
import net.minecraft.class_1715;
import net.minecraft.class_1738;
import net.minecraft.class_1741;
import net.minecraft.class_1767;
import net.minecraft.class_1768;
import net.minecraft.class_1769;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1849;
import net.minecraft.class_1856;
import net.minecraft.class_1865;
import net.minecraft.class_1869;
import net.minecraft.class_1914;
import net.minecraft.class_1916;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2371;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_4059;
import net.minecraft.class_5134;
import net.minecraft.class_5556;
import net.minecraft.class_5620;
import net.minecraft.class_7710;
import net.minecraft.class_7923;
import net.minecraft.class_8957;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.4-era server mod (intermediary names; Fabric lifecycle events only) for the s4fin world/item
 * bridges of step 1.20.4 -> 1.20.5. Once the server has started it checks:
 * <ul>
 * <li>a mod ArmorMaterial (class implementing 1.20.4's interface) on a mod ArmorItem: the item's defense,
 *     toughness, durability (from the material, 1.20.4's defaultDurability), getMaterial() returning the
 *     mod's own object, the default attribute modifiers (1.20.4 shape: a Multimap by slot), and an armor
 *     stand wearing it getting the armor value;</li>
 * <li>a mod DyeableArmorItem: dyeing through vanilla's ArmorDyeRecipe (a crafting grid with the armor and a
 *     red dye), getColor / hasCustomColor / clearColor / setColor, DyeableLeatherItem.dyeArmor, and washing
 *     in a water cauldron (vanilla's cauldron interaction for dyed items);</li>
 * <li>a mod HorseArmorItem(protection, name): getProtection(), and a horse wearing it getting that armor;</li>
 * <li>a MerchantOffer made with 1.20.4 ItemStack costs: satisfiedBy / take, createTag -> new MerchantOffer(tag)
 *     round trip, MerchantOffers createTag -> new MerchantOffers(tag), a villager holding the offers saved and
 *     loaded;</li>
 * <li>a mod recipe serializer extending ShapedRecipe.Serializer and overriding toNetwork (writing a marker
 *     int before super.toNetwork) and fromNetwork (reading it back before super.fromNetwork): the serializer's
 *     toNetwork -> fromNetwork round trip on a plain FriendlyByteBuf, the bytes compared with 1.20.4's
 *     layout written by hand (group, category, width, height, ingredients, result, showNotification), the
 *     marker appearing exactly once (no double wrap), and vanilla's own shaped serializer's round trip.</li>
 * </ul>
 * Every check prints a "CBARMORTEST OK|FAIL ..." line; "CBARMORTEST DONE n/m" ends the run.
 */
public class ArmorTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        if (!ok) {
            FAILS.add(what);
        }
        System.out.println("CBARMORTEST " + (ok ? "OK " : "FAIL ") + what);
    }

    static class_1792 ARMOR;
    static class_1792 DYEABLE;
    static class_4059 HORSE_ARMOR;
    static class_1865<class_1869> SERIALIZER;

    /** a mod's own armor material, 1.20.4 style */
    public static final class TestMaterial implements class_1741 {
        @Override
        public int method_48402(class_1738.class_8051 type) {
            return 100 + type.ordinal();
        }

        @Override
        public int method_48403(class_1738.class_8051 type) {
            return type == class_1738.class_8051.field_41935 ? 7 : 2;
        }

        @Override
        public int method_7699() {
            return 11;
        }

        @Override
        public class_3414 method_7698() {
            return class_3417.field_14862;
        }

        @Override
        public class_1856 method_7695() {
            return class_1856.method_8091(class_1802.field_8620);
        }

        @Override
        public String method_7694() {
            return "cb_armortest:testium";
        }

        @Override
        public float method_7700() {
            return 1.5F;
        }

        @Override
        public float method_24355() {
            return 0.2F;
        }
    }

    public static final TestMaterial MATERIAL = new TestMaterial();

    /** a mod DyeableArmorItem subclass */
    public static final class TestDyeable extends net.minecraft.class_4057 {
        public TestDyeable() {
            super(MATERIAL, class_1738.class_8051.field_41934, new class_1792.class_1793());
        }
    }

    /** a mod serializer extending vanilla's shaped one, wrapping its network form with a marker */
    public static final class MarkedSerializer extends class_1869.class_1870 {
        static final int MARK = 0x5ca1ab1e;

        @Override
        public class_1869 method_8163(class_2540 buf) {
            int m = buf.readInt();
            check(m == MARK, "serializer: fromNetwork reads the mod's marker first (" + Integer.toHexString(m) + ")");
            return super.method_8163(buf);
        }

        @Override
        public void method_8165(class_2540 buf, class_1869 recipe) {
            buf.writeInt(MARK);
            super.method_8165(buf, recipe);
        }
    }

    @Override
    public void onInitialize() {
        ARMOR = class_2378.method_10230(class_7923.field_41178, new class_2960("cb_armortest", "test_chestplate"),
            new class_1738(MATERIAL, class_1738.class_8051.field_41935, new class_1792.class_1793()));
        DYEABLE = class_2378.method_10230(class_7923.field_41178, new class_2960("cb_armortest", "test_helmet"), new TestDyeable());
        HORSE_ARMOR = class_2378.method_10230(class_7923.field_41178, new class_2960("cb_armortest", "test_horse_armor"),
            new class_4059(9, "testium", new class_1792.class_1793().method_7889(1)));
        SERIALIZER = class_1865.method_17724("cb_armortest:marked_shaped", new MarkedSerializer());
        ServerLifecycleEvents.SERVER_STARTED.register(ArmorTestMod::run);
    }

    static void run(MinecraftServer server) {
        try {
            armor(server);
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "armor: " + t);
        }
        try {
            dye(server);
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "dye: " + t);
        }
        try {
            horse(server);
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "horse: " + t);
        }
        try {
            trade(server);
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "trade: " + t);
        }
        try {
            serializer(server);
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, "serializer: " + t);
        }
        System.out.println("CBARMORTEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS " + FAILS));
    }

    // ---------------------------------------------------------------- armor material

    static void armor(MinecraftServer server) {
        class_1738 item = (class_1738) ARMOR;
        check(item.method_7687() == 7, "armor: defense from the mod material (" + item.method_7687() + ")");
        check(item.method_26353() == 1.5F, "armor: toughness from the mod material (" + item.method_26353() + ")");
        class_1799 stack = new class_1799(item);
        int expect = MATERIAL.method_48402(class_1738.class_8051.field_41935);
        check(stack.method_7936() == expect, "armor: durability from the material (" + stack.method_7936() + " == " + expect + ")");
        check(item.method_7686() == MATERIAL, "armor: getMaterial() is the mod's own material object");
        Multimap<class_1320, class_1322> mods = item.method_7844(class_1304.field_6174);
        double armor = sum(mods, class_5134.field_23724);
        double tough = sum(mods, class_5134.field_23725);
        check(armor == 7.0, "armor: chest modifiers carry ARMOR 7 (" + armor + ")");
        check(tough == 1.5, "armor: chest modifiers carry ARMOR_TOUGHNESS 1.5 (" + tough + ")");
        check(item.method_7844(class_1304.field_6169).isEmpty(), "armor: no modifiers in the head slot");
        class_3218 level = server.method_30002();
        net.minecraft.class_1531 stand = new net.minecraft.class_1531(level, 0.5, 200, 0.5);
        stand.method_5673(class_1304.field_6174, stack);
        stand.method_5773();
        check(stand.method_6096() == 7, "armor: an armor stand wearing it has armor value 7 (" + stand.method_6096() + ")");
        // the vanilla enum
        check(net.minecraft.class_1740.field_7892.method_7694().equals("iron"), "armor: ArmorMaterials.IRON is named iron");
        check(net.minecraft.class_1740.field_7892.method_48403(class_1738.class_8051.field_41935) == 6,
            "armor: ArmorMaterials.IRON chest defense 6 (" + net.minecraft.class_1740.field_7892.method_48403(class_1738.class_8051.field_41935) + ")");
        check(((class_1738) class_1802.field_8523).method_7686() == net.minecraft.class_1740.field_7892,
            "armor: vanilla iron chestplate's getMaterial() is ArmorMaterials.IRON");
    }

    static double sum(Multimap<class_1320, class_1322> mods, class_1320 attribute) {
        double s = 0;
        for (class_1322 m : mods.get(attribute)) {
            s += m.method_6186();
        }
        return s;
    }

    // ---------------------------------------------------------------- dyeing

    static void dye(MinecraftServer server) {
        class_1768 dyeable = (class_1768) DYEABLE;
        class_1799 helmet = new class_1799(DYEABLE);
        check(!dyeable.method_7801(helmet), "dye: a new helmet has no custom color");
        check(dyeable.method_7800(helmet) == 10511680, "dye: default leather color (" + dyeable.method_7800(helmet) + ")");
        dyeable.method_7799(helmet, 0x123456);
        check(dyeable.method_7801(helmet) && dyeable.method_7800(helmet) == 0x123456, "dye: setColor / getColor round trip");
        dyeable.method_7798(helmet);
        check(!dyeable.method_7801(helmet), "dye: clearColor");
        class_1799 red = class_1768.method_19261(new class_1799(DYEABLE), List.of(class_1769.method_7803(class_1767.field_7964)));
        check(!red.method_7960() && dyeable.method_7801(red), "dye: DyeableLeatherItem.dyeArmor colors the helmet (" + dyeable.method_7800(red) + ")");
        int expected = dyeable.method_7800(red);
        // vanilla's dye recipe on a crafting grid
        class_1715 grid = new class_1715(new net.minecraft.class_1703(null, 0) {
            @Override
            public class_1799 method_7601(net.minecraft.class_1657 p, int i) {
                return class_1799.field_8037;
            }

            @Override
            public boolean method_7597(net.minecraft.class_1657 p) {
                return true;
            }
        }, 3, 3);
        grid.method_5447(0, new class_1799(DYEABLE));
        grid.method_5447(1, new class_1799(class_1802.field_8264));
        class_1849 recipe = new class_1849(class_7710.field_40251);
        boolean matches = recipe.method_17701(grid, server.method_30002());
        check(matches, "dye: vanilla's armor dye recipe matches the mod helmet + red dye");
        if (matches) {
            class_1799 out = recipe.method_17700(grid, server.method_30611());
            check(!out.method_7960() && dyeable.method_7801(out) && dyeable.method_7800(out) == expected,
                "dye: the recipe's result has dyeArmor's color (" + (out.method_7960() ? "empty" : dyeable.method_7800(out)) + " == " + expected + ")");
        }
        // cauldron wash
        class_3218 level = server.method_30002();
        class_2338 pos = new class_2338(0, 250, 0);
        level.method_8501(pos, class_2246.field_27097.method_9564().method_11657(class_5556.field_27206, 3));
        // 1.20.4's water cauldron map lists vanilla's leather items only (a missing item gets the map's default,
        // PASS): a mod registers its own dyeable item with DYED_ITEM, as this one does
        Map<class_1792, class_5620> water = class_5620.field_27776.comp_1982();
        water.put(DYEABLE, class_5620.field_27782);
        class_5620 wash = water.get(DYEABLE);
        check(wash == class_5620.field_27782, "dye: the water cauldron's interaction for the mod helmet is DYED_ITEM");
        if (wash != null) {
            net.fabricmc.fabric.api.entity.FakePlayer player = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            class_1799 dyed = red.method_7972();
            player.method_6122(class_1268.field_5808, dyed);
            wash.interact(level.method_8320(pos), level, pos, player, class_1268.field_5808, dyed);
            check(!dyeable.method_7801(dyed), "dye: washing in a water cauldron removes the color");
        }
    }

    // ---------------------------------------------------------------- horse armor

    static void horse(MinecraftServer server) {
        check(HORSE_ARMOR.method_18455() == 9, "horse: getProtection() is 9 (" + HORSE_ARMOR.method_18455() + ")");
        check(HORSE_ARMOR.method_18454().toString().equals("minecraft:textures/entity/horse/armor/horse_armor_testium.png"),
            "horse: texture " + HORSE_ARMOR.method_18454());
        class_3218 level = server.method_30002();
        class_1498 horse = new class_1498(net.minecraft.class_1299.field_6139, level);
        horse.method_5814(0.5, 200, 0.5);
        horse.method_6766(true);
        check(horse.method_6773(new class_1799(HORSE_ARMOR)), "horse: a horse accepts the mod horse armor");
        // the way 1.20.4 code equips horse armor: AbstractHorse.equipArmor(player, stack) (the armor inventory slot)
        net.fabricmc.fabric.api.entity.FakePlayer player = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
        horse.method_45375(player, new class_1799(HORSE_ARMOR));
        horse.method_5773();
        check(horse.method_6096() == 9, "horse: a horse wearing it has armor value 9 (" + horse.method_6096() + ")");
    }

    // ---------------------------------------------------------------- trading

    static void trade(MinecraftServer server) {
        class_1799 costA = new class_1799(class_1802.field_8687, 3);
        class_1799 costB = new class_1799(class_1802.field_8620, 2);
        class_1799 result = new class_1799(class_1802.field_8477);
        class_1914 offer = new class_1914(costA, costB, result, 0, 5, 7, 0.05F);
        check(offer.method_19272().method_7909() == class_1802.field_8687 && offer.method_19272().method_7947() == 3, "trade: costA is 3 emeralds");
        check(offer.method_8247().method_7909() == class_1802.field_8620 && offer.method_8247().method_7947() == 2, "trade: costB is 2 iron ingots");
        check(offer.method_8250().method_7909() == class_1802.field_8477, "trade: result is a diamond");
        class_1799 payA = new class_1799(class_1802.field_8687, 5);
        class_1799 payB = new class_1799(class_1802.field_8620, 2);
        check(offer.method_16952(payA, payB), "trade: 5 emeralds + 2 iron satisfy it");
        check(!offer.method_16952(new class_1799(class_1802.field_8687, 2), payB), "trade: 2 emeralds do not");
        check(offer.method_16953(payA, payB) && payA.method_7947() == 2 && payB.method_7947() == 0, "trade: take() pays 3 emeralds and 2 iron");
        class_2487 tag = offer.method_8251();
        check(tag.method_10545("buy") && tag.method_10545("buyB") && tag.method_10545("sell") && tag.method_10550("maxUses") == 5,
            "trade: createTag has buy, buyB, sell, maxUses=5 (" + tag + ")");
        class_1914 back = new class_1914(tag);
        check(back.method_8250().method_7909() == class_1802.field_8477 && back.method_19272().method_7947() == 3 && back.method_8247().method_7947() == 2,
            "trade: new MerchantOffer(createTag()) gives the same offer");
        class_1916 offers = new class_1916();
        offers.add(offer);
        class_2487 otag = offers.method_8268();
        check(otag.method_10545("Recipes"), "trade: MerchantOffers.createTag has Recipes (" + otag + ")");
        class_1916 oback = new class_1916(otag);
        check(oback.size() == 1 && oback.get(0).method_8250().method_7909() == class_1802.field_8477, "trade: new MerchantOffers(tag) round trip");
        class_3218 level = server.method_30002();
        class_1646 villager = new class_1646(net.minecraft.class_1299.field_6077, level);
        villager.method_16917(offers);
        class_2487 saved = villager.method_5647(new class_2487());
        class_1646 loaded = new class_1646(net.minecraft.class_1299.field_6077, level);
        loaded.method_5651(saved);
        class_1916 lo = loaded.method_8264();
        check(lo.size() == 1 && lo.get(0).method_8250().method_7909() == class_1802.field_8477 && lo.get(0).method_19272().method_7947() == 3,
            "trade: a villager with the offer saves and loads it");
    }

    // ---------------------------------------------------------------- recipe serializer

    static class_1869 shaped() {
        class_8957 pattern = class_8957.method_55086(Map.of('#', class_1856.method_8091(class_1802.field_8620), 'X', class_1856.method_8091(class_1802.field_8477)), "#X", "X#");
        return new class_1869("cbgroup", class_7710.field_40250, pattern, new class_1799(class_1802.field_8687, 4), false);
    }

    static byte[] bytes(class_2540 buf) {
        byte[] b = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), b);
        return b;
    }

    static void serializer(MinecraftServer server) {
        class_1869 recipe = shaped();
        // 1.20.4's layout of a shaped recipe, written by hand with 1.20.4's FriendlyByteBuf calls
        class_2540 expect = new class_2540(Unpooled.buffer());
        expect.method_10814("cbgroup");
        expect.method_10817(class_7710.field_40250);
        expect.method_10804(2);
        expect.method_10804(2);
        for (class_1856 ing : recipe.method_8117()) {
            ing.method_8088(expect);
        }
        expect.method_10793(new class_1799(class_1802.field_8687, 4));
        expect.writeBoolean(false);
        byte[] want = bytes(expect);

        // vanilla's serializer
        class_2540 v = new class_2540(Unpooled.buffer());
        class_1865.field_9035.method_8124(v, recipe);
        byte[] got = bytes(v);
        check(java.util.Arrays.equals(got, want), "serializer: vanilla toNetwork writes 1.20.4's layout (" + got.length + " bytes, want " + want.length + ")");
        class_1869 vb = (class_1869) class_1865.field_9035.method_8122(v);
        check(vb.method_8150() == 2 && vb.method_8158() == 2 && vb.method_8112().equals("cbgroup") && v.readableBytes() == 0,
            "serializer: vanilla fromNetwork reads it back, nothing left over");

        // the mod's serializer: marker + super
        class_2540 m = new class_2540(Unpooled.buffer());
        SERIALIZER.method_8124(m, recipe);
        byte[] mb = bytes(m);
        int marks = 0;
        for (int i = 0; i + 4 <= mb.length; i++) {
            if ((mb[i] & 0xff) == 0x5c && (mb[i + 1] & 0xff) == 0xa1 && (mb[i + 2] & 0xff) == 0xab && (mb[i + 3] & 0xff) == 0x1e) {
                marks++;
            }
        }
        check(marks == 1, "serializer: the mod's marker is written exactly once (" + marks + ")");
        check(mb.length == want.length + 4 && java.util.Arrays.equals(java.util.Arrays.copyOfRange(mb, 4, mb.length), want),
            "serializer: the mod's super.toNetwork writes 1.20.4's layout after the marker (" + mb.length + " bytes)");
        class_1869 mbk = (class_1869) SERIALIZER.method_8122(m);
        check(mbk.method_8150() == 2 && mbk.method_8158() == 2 && m.readableBytes() == 0, "serializer: the mod's fromNetwork reads it back, nothing left over");
        // the mod's typed methods, called directly
        class_2540 t = new class_2540(Unpooled.buffer());
        ((MarkedSerializer) SERIALIZER).method_8165(t, recipe);
        check(t.readableBytes() == want.length + 4, "serializer: the typed toNetwork call writes marker + recipe (" + t.readableBytes() + ")");
        class_1869 tb = ((MarkedSerializer) SERIALIZER).method_8163(t);
        check(tb.method_8150() == 2 && t.readableBytes() == 0, "serializer: the typed fromNetwork call reads it back");
    }
}
