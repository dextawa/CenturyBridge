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
import net.minecraft.class_1770;
import net.minecraft.class_1810;
import net.minecraft.class_1829;
import net.minecraft.class_1831;
import net.minecraft.class_1832;
import net.minecraft.class_1834;
import net.minecraft.class_1856;
import net.minecraft.class_1860;
import net.minecraft.class_1869;
import net.minecraft.class_1874;
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
import net.minecraft.class_8786;
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
 *   javac --release 21 -cp "client-1.21.1-intermediary.jar;fabric-loader.jar;fabric-events-interaction-v0.jar;&lt;1.21.1 libraries&gt;" -d out \
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
    static net.minecraft.class_5321<net.minecraft.class_7444> HORN;
    static class_1792 CHEST, HEAD, SWORD, PICK, FOOD, BUNDLE, BOOK, NAMED, COMPLEX, TEMPLATE;
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
        net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer.register(IRON_SERIALIZER);
        HORN = net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41275, class_2960.method_60655("cbs7gear", "horn"));
        class_2378.method_47984(class_7923.field_41166, HORN, new net.minecraft.class_7444(
            (class_6880<net.minecraft.class_3414>) net.minecraft.class_3417.field_39028.get(0), 100, 64.0F));
        FOOD = reg(class_7923.field_41178, "food", new class_1792(new class_1792.class_1793().method_19265(FOOD_PROPS)));
        BUNDLE = reg(class_7923.field_41178, "bundle", new net.minecraft.class_5537(new class_1792.class_1793()));
        BOOK = reg(class_7923.field_41178, "book", new net.minecraft.class_1751(new class_1792.class_1793()));
        NAMED = reg(class_7923.field_41178, "named", new net.minecraft.class_1798(class_2246.field_10293, new class_1792.class_1793()));
        COMPLEX = reg(class_7923.field_41178, "complex", new net.minecraft.class_1762(new class_1792.class_1793()));
        TEMPLATE = reg(class_7923.field_41178, "template", net.minecraft.class_8052.method_48418(net.minecraft.class_8057.field_42018));
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

    interface Section {
        void run() throws Exception;
    }

    /** each section on its own: one failing does not hide the others */
    static void section(String name, Section s) {
        try {
            s.run();
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, name + ": no exception: " + e);
        }
    }

    static void run(MinecraftServer server) {
        section("armor", () -> armor(server));
        section("tools", GearTestMod::tools);
        section("food", () -> food(server));
        section("ingredients", GearTestMod::ingredients);
        section("types", GearTestMod::types);
        section("recipes", () -> recipes(server));
        section("recipeClasses", () -> recipeClasses(server));
        section("serializers", () -> serializers(server));
        section("cookingBytes", () -> cookingBytes(server));
        section("recipeManager", () -> recipeManager(server));
        section("menus", () -> menus(server));
        section("combiners", () -> combiners(server));
        section("world", () -> world(server));
        section("fapi", () -> fapi(server));
        section("datagen", () -> datagen(server));
        section("restoredMixins", GearTestMod::restoredMixins);
        section("restoredMixinsRecipes", () -> restoredMixinsRecipes(server));
        section("tagReload", () -> tagReload(server));
        section("equipmentReads", () -> equipmentReads(server));
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

    static void ingredients() {
        class_1856 empty = class_1856.field_9017;
        check(empty.method_8093(class_1799.field_8037) && !empty.method_8093(new class_1799(class_1802.field_8477)),
            "Ingredient.EMPTY matches only the empty stack");
        check(class_1856.method_35226().method_8103() && class_1856.method_8101(class_1799.field_8037).method_8103(),
            "Ingredient.of() / of(empty stack) are empty");
        class_1856 diamond = class_1856.method_8101(new class_1799(class_1802.field_8477, 3));
        check(!diamond.method_8103() && diamond.method_8105().length == 1 && diamond.method_8100().size() == 1
            && diamond.method_8093(new class_1799(class_1802.field_8477)), "Ingredient.of(stacks): items, stacking ids, test by item");
        class_1856 planks = class_1856.method_8106(net.minecraft.class_3489.field_15537);
        check(planks.method_8093(new class_1799(class_1802.field_8118)) && planks.method_8105().length > 1, "Ingredient.of(tag)");
        check(class_1856.method_26964(java.util.stream.Stream.of(new class_1799(class_1802.field_8695))).method_8093(new class_1799(class_1802.field_8695)),
            "Ingredient.ofStacks(stream of stacks)");
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

    /**
     * type tests and casts against 1.21.1 types 1.21.2 removed (TieredItem, ElytraItem, Equipable): 1.21.1's answer
     * for vanilla objects, the cast and the calls on the cast reference
     */
    static void types() {
        Object diamondSword = class_1802.field_8802;
        check(diamondSword instanceof class_1831, "vanilla diamond sword instanceof TieredItem");
        check(class_1802.field_8377 instanceof class_1831, "vanilla diamond pickaxe instanceof TieredItem");
        check(SWORD instanceof class_1831 && PICK instanceof class_1831, "converted tools instanceof TieredItem");
        class_1831 tiered = (class_1831) diamondSword;
        check(tiered.method_8022() == class_1834.field_8930, "((TieredItem) diamond sword).getTier() = Tiers.DIAMOND");
        check(((class_1831) SWORD).method_8022() == GearTier.EMERALD, "((TieredItem) converted sword).getTier() = its tier");
        Object diamond = class_1802.field_8477;
        check(!(diamond instanceof class_1831), "a diamond is no TieredItem");
        boolean threw = false;
        try {
            class_1831 bad = (class_1831) diamond;
            check(bad == null, "unreachable");
        } catch (ClassCastException e) {
            threw = true;
        }
        check(threw, "(TieredItem) diamond throws ClassCastException");
        Object elytra = class_1802.field_8833;
        check(elytra instanceof class_1770, "vanilla elytra instanceof ElytraItem");
        check(!(class_1802.field_8523 instanceof class_1770), "iron chestplate is no ElytraItem");
        class_1770 ely = (class_1770) elytra;
        check(ely.method_7685() == class_1304.field_6174, "((ElytraItem) elytra).getEquipmentSlot() = chest");
        check(class_1770.method_7804(new class_1799(class_1802.field_8833)), "ElytraItem.isFlyEnabled(new elytra)");
        check(elytra instanceof class_5151, "vanilla elytra instanceof Equipable");
        check(((class_5151) elytra).method_31570() == class_3417.field_14966, "((Equipable) elytra).getEquipSound() = elytra's");
        check(class_2246.field_10147 instanceof class_5151, "carved pumpkin block instanceof Equipable");
        check(((class_5151) (Object) class_2246.field_10147).method_7685() == class_1304.field_6169, "carved pumpkin equips to head");
        check(class_1802.field_8523 instanceof class_5151 && CHEST instanceof class_5151, "armor instanceof Equipable");
        check(!(diamond instanceof class_5151), "a diamond is not Equipable");
        check(class_5151.method_48957(new class_1799(class_1802.field_8833)) == elytra, "Equipable.get(elytra stack) = the elytra item");
    }

    /** 1.21.1's Recipe members on loaded vanilla recipes */
    static void recipes(MinecraftServer server) {
        class_1869 shaped = null;
        class_1874 cooking = null;
        int n = 0;
        for (class_8786<?> h : server.method_3772().method_8126()) {
            class_1860<?> r = h.comp_1933();
            n++;
            if (shaped == null && r instanceof class_1869 s && s.method_8150() == 3 && s.method_8158() == 3) {
                shaped = s;
            }
            if (cooking == null && r instanceof class_1874 c) {
                cooking = c;
            }
        }
        check(shaped != null && cooking != null, "found a 3x3 shaped and a cooking recipe among " + n);
        if (shaped == null || cooking == null) {
            return;
        }
        class_1860<?> r = shaped;
        check(!r.method_8110(server.method_30611()).method_7960(), "shaped getResultItem non-empty: " + r.method_8110(server.method_30611()));
        check(r.method_8117().size() == 9, "shaped getIngredients: 9 slots: " + r.method_8117().size());
        check(r.method_8113(3, 3) && !r.method_8113(2, 2), "shaped canCraftInDimensions 3x3 yes, 2x2 no");
        check(!r.method_31584(), "shaped isIncomplete false");
        check(r.method_17447().method_31574(class_1802.field_8465), "shaped getToastSymbol = crafting table");
        class_1860<?> c = cooking;
        check(c.method_8117().size() == 1 && c.method_8113(1, 1), "cooking: one ingredient, fits 1x1");
        check(!c.method_8110(server.method_30611()).method_7960(), "cooking getResultItem non-empty");
        check(!c.method_17447().method_31574(class_1802.field_8465), "cooking getToastSymbol is its workstation: " + c.method_17447());
    }

    /** a 1.21.1 cooking recipe subclass of its own: type from the constructor, protected fields */
    public static class TestCooking extends class_1874 {
        public TestCooking() {
            super(net.minecraft.class_3956.field_17546, "cbgroup", net.minecraft.class_7709.field_40242,
                class_1856.method_8091(class_1802.field_20412), new class_1799(class_1802.field_20391), 0.25F, 123);
        }

        @Override
        public net.minecraft.class_1865<?> method_8119() {
            return net.minecraft.class_1865.field_9042;
        }

        // javac against intermediary names does not see 1.21.1's typed matches/assemble (method_59981/59982) as
        // Recipe's: the bridges 1.21.1's AbstractCookingRecipe had, written out (a loom-built mod has neither)
        public boolean method_8115(net.minecraft.class_9696 in, net.minecraft.class_1937 level) {
            return method_59981(in, level);
        }

        public class_1799 method_8116(net.minecraft.class_9696 in, net.minecraft.class_7225.class_7874 p) {
            return method_59982(in, p);
        }

        boolean fieldsAre1211() {
            return this.field_9061.method_8093(new class_1799(class_1802.field_20412)) && this.field_9059.method_31574(class_1802.field_20391)
                && this.field_9057 == 0.25F && this.field_9058 == 123 && "cbgroup".equals(this.field_9062)
                && this.field_17544 == net.minecraft.class_3956.field_17546 && this.field_40241 == net.minecraft.class_7709.field_40242;
        }
    }

    /** a 1.21.1 smithing recipe of its own: only the stack predicates */
    public static class TestSmithing implements net.minecraft.class_8059 {
        public boolean method_48453(class_1799 s) {
            return s.method_31574(class_1802.field_8477);
        }

        public boolean method_48454(class_1799 s) {
            return s.method_31574(class_1802.field_8620);
        }

        public boolean method_30029(class_1799 s) {
            return s.method_31574(class_1802.field_8695);
        }

        public boolean method_8115(net.minecraft.class_9697 in, net.minecraft.class_1937 level) {
            return method_48453(in.comp_2677()) && method_48454(in.comp_2678()) && method_30029(in.comp_2679());
        }

        public class_1799 method_8116(net.minecraft.class_9697 in, net.minecraft.class_7225.class_7874 p) {
            return new class_1799(class_1802.field_8477);
        }

        public class_1799 method_8110(net.minecraft.class_7225.class_7874 p) {
            return new class_1799(class_1802.field_8477);
        }

        public net.minecraft.class_1865<?> method_8119() {
            return net.minecraft.class_1865.field_9042;
        }
    }

    /** 1.21.1's recipe class constructors and members */
    static void recipeClasses(MinecraftServer server) throws Exception {
        TestCooking cook = new TestCooking();
        check(cook.method_17716() == net.minecraft.class_3956.field_17546, "cooking subclass getType = the constructor's (SMELTING)");
        check(cook.fieldsAre1211(), "cooking subclass reads 1.21.1's protected fields");
        check(cook.method_59981(new net.minecraft.class_9696(new class_1799(class_1802.field_20412)), server.method_30002())
            && !cook.method_59981(new net.minecraft.class_9696(new class_1799(class_1802.field_8477)), server.method_30002()),
            "cooking matches(SingleRecipeInput, level)");
        check(cook.method_59982(new net.minecraft.class_9696(new class_1799(class_1802.field_20412)), server.method_30611())
            .method_31574(class_1802.field_20391), "cooking assemble = the result");
        Object placement = cook.getClass().getMethod("method_61671").invoke(cook);
        check(placement != null, "1.21.2 asks the cooking subclass for its placement");
        TestSmithing smith = new TestSmithing();
        Object template = smith.getClass().getMethod("method_64722").invoke(smith);
        check(template instanceof java.util.Optional<?> o && o.isPresent()
            && ((class_1856) o.get()).method_8093(new class_1799(class_1802.field_8477))
            && !((class_1856) o.get()).method_8093(new class_1799(class_1802.field_8620)),
            "1.21.2's templateIngredient() of a predicate-only smithing recipe: what isTemplateIngredient accepts");
        net.minecraft.class_8059 asSmithing = smith;
        check(asSmithing.method_48454(new class_1799(class_1802.field_8620)), "SmithingRecipe.isBaseIngredient reaches the override");
        net.minecraft.class_8060 transform = new net.minecraft.class_8060(class_1856.field_9017,
            class_1856.method_8091(class_1802.field_8620), class_1856.method_8091(class_1802.field_8695), new class_1799(class_1802.field_8477));
        check(transform.method_48453(class_1799.field_8037) && !transform.method_48453(new class_1799(class_1802.field_8477)),
            "transform with an empty template: only the empty stack is a template");
        check(transform.method_48454(new class_1799(class_1802.field_8620)) && transform.method_30029(new class_1799(class_1802.field_8695)),
            "transform base / addition");
        check(transform.method_59999(new net.minecraft.class_9697(class_1799.field_8037, new class_1799(class_1802.field_8620),
            new class_1799(class_1802.field_8695)), server.method_30002()), "transform matches(empty, iron, gold)");
        net.minecraft.class_8957 pattern = new net.minecraft.class_8957(2, 1,
            net.minecraft.class_2371.method_10212(class_1856.field_9017, class_1856.method_8091(class_1802.field_8600), class_1856.field_9017),
            java.util.Optional.empty());
        check(pattern.method_59997().size() == 2 && pattern.method_59997().get(1).method_8103()
            && !pattern.method_59997().get(0).method_8103(), "ShapedRecipePattern keeps the empty ingredient in its gap");
        net.minecraft.class_1867 shapeless = new net.minecraft.class_1867("", net.minecraft.class_7710.field_40251,
            new class_1799(class_1802.field_8477), net.minecraft.class_2371.method_10212(class_1856.field_9017, class_1856.method_8091(class_1802.field_8600)));
        check(shapeless.method_8117().size() == 1 && shapeless.method_8113(1, 1), "ShapelessRecipe from a NonNullList");
        class_2960 id = class_2960.method_60655("cbs7gear", "holder");
        check(id.equals(new class_8786<>(id, shapeless).comp_1932()), "RecipeHolder(id, recipe).id() = id");
        check(net.minecraft.class_7709.field_40245 != null, "CookingBookCategory.CODEC");
    }

    /** 1.21.1's recipe classes and serializers 1.21.2 removed, CraftingInput.stackedContents */
    static void serializers(MinecraftServer server) {
        net.minecraft.class_1871 coloring = new net.minecraft.class_1871(net.minecraft.class_7710.field_40251);
        net.minecraft.class_9694 in = net.minecraft.class_9694.method_59986(2, 1,
            List.of(new class_1799(class_1802.field_8545), new class_1799(class_1802.field_8264)));
        check(coloring.method_17734(in, server.method_30002()), "ShulkerBoxColoring matches box + dye");
        check(coloring.method_17733(in, server.method_30611()).method_31574(net.minecraft.class_2246.field_10068.method_8389()),
            "ShulkerBoxColoring assembles the red box: " + coloring.method_17733(in, server.method_30611()));
        check(coloring.method_8119() == net.minecraft.class_1865.field_9041
            && class_2960.method_60656("crafting_special_shulkerboxcoloring").equals(class_7923.field_41189.method_10221(net.minecraft.class_1865.field_9041)),
            "RecipeSerializer.SHULKER_BOX_COLORING registered under 1.21.1's id");
        net.minecraft.class_1874 smelt = ((net.minecraft.class_3957<?>) net.minecraft.class_1865.field_9042).method_55104("", net.minecraft.class_7709.field_40244,
            class_1856.method_8091(class_1802.field_20412), new class_1799(class_1802.field_20391), 0.1F, 200);
        check(smelt.method_17716() == net.minecraft.class_3956.field_17546, "SimpleCookingSerializer.create on SMELTING_RECIPE: a smelting recipe");
        net.minecraft.class_3957<TestCooking> own = new net.minecraft.class_3957<>((g, c, i, r, x, t) -> new TestCooking(), 100);
        check(own.method_55104("", net.minecraft.class_7709.field_40244, class_1856.method_8091(class_1802.field_20412),
            new class_1799(class_1802.field_20391), 0.1F, 100) instanceof TestCooking, "own SimpleCookingSerializer.create uses its factory");
        net.minecraft.class_1662 contents = in.method_59988();
        check(contents == in.method_59988() && contents.field_7550.size() == 2, "CraftingInput.stackedContents: one per input, two items");
    }

    /** what 1.21.1's SimpleCookingSerializer wrote: group, category (enum ordinal), ingredient, result, experience, time */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static byte[] v1211Bytes(MinecraftServer server, class_1874 r, boolean intTime) {
        net.minecraft.class_9129 buf = new net.minecraft.class_9129(io.netty.buffer.Unpooled.buffer(), server.method_30611());
        buf.method_10814(r.method_8112());
        buf.method_10817(r.method_45438());
        ((net.minecraft.class_9139) class_1856.field_48355).encode(buf, r.method_8117().get(0));
        ((net.minecraft.class_9139) class_1799.field_48349).encode(buf, r.method_8110(server.method_30611()));
        buf.writeFloat(r.method_8171());
        if (intTime) {
            buf.writeInt(r.method_8167());
        } else {
            buf.method_10804(r.method_8167());
        }
        byte[] out = new byte[buf.readableBytes()];
        buf.readBytes(out);
        return out;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void roundTrip(MinecraftServer server, String what, net.minecraft.class_9139 codec, class_1874 r, boolean intTime) {
        byte[] expected = v1211Bytes(server, r, intTime);
        net.minecraft.class_9129 buf = new net.minecraft.class_9129(io.netty.buffer.Unpooled.buffer(), server.method_30611());
        codec.encode(buf, r);
        byte[] written = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), written);
        check(java.util.Arrays.equals(expected, written), what + (intTime ? ": the serializer writes 1.21.1's fields, the cooking time as 1.21.2's int: " : ": the serializer writes 1.21.1's bytes: ") + java.util.HexFormat.of().formatHex(written)
            + (java.util.Arrays.equals(expected, written) ? "" : " vs 1.21.1 " + java.util.HexFormat.of().formatHex(expected)));
        net.minecraft.class_9129 in = new net.minecraft.class_9129(io.netty.buffer.Unpooled.wrappedBuffer(expected), server.method_30611());
        class_1874 back = (class_1874) codec.decode(in);
        check(back.method_8112().equals(r.method_8112()) && back.method_45438() == r.method_45438()
            && back.method_8171() == r.method_8171() && back.method_8167() == r.method_8167()
            && class_1799.method_7973(back.method_8110(server.method_30611()), r.method_8110(server.method_30611()))
            && in.readableBytes() == 0, what + ": the bytes read back to the same recipe");
    }

    static void cookingBytes(MinecraftServer server) {
        net.minecraft.class_3861 vanilla = null;
        for (class_8786<?> h : server.method_3772().method_8126()) {
            if (h.comp_1933() instanceof net.minecraft.class_3861 s) {
                vanilla = s;
                break;
            }
        }
        check(vanilla != null, "found a vanilla smelting recipe");
        if (vanilla != null) {
            roundTrip(server, "vanilla smelting", net.minecraft.class_1865.field_9042.method_56104(), vanilla, true);
        }
        net.minecraft.class_3957<TestCooking> own = new net.minecraft.class_3957<>((g, c, i, r, x, t) -> new TestCooking(), 100);
        roundTrip(server, "mod cooking", own.method_56104(), new TestCooking(), false);
    }

    /** 1.21.1's RecipeManager members */
    static void recipeManager(MinecraftServer server) {
        net.minecraft.class_1863 rm = server.method_3772();
        net.minecraft.class_3218 level = server.method_30002();
        List<class_8786<net.minecraft.class_3861>> smelting = rm.method_30027(net.minecraft.class_3956.field_17546);
        check(!smelting.isEmpty(), "getAllRecipesFor(SMELTING): " + smelting.size());
        class_2960 id = smelting.get(0).comp_1932();
        check(rm.method_8130(id).isPresent(), "byKey(id) of a smelting recipe");
        check(rm.method_8127().count() == rm.method_59822().size() && rm.method_8127().anyMatch(id::equals), "getRecipeIds = getOrderedRecipes ids");
        check(!rm.method_35227(), "hasErrors false");
        net.minecraft.class_9696 cobble = new net.minecraft.class_9696(new class_1799(class_1802.field_20412));
        List<class_8786<net.minecraft.class_3861>> found = rm.method_17877(net.minecraft.class_3956.field_17546, cobble, level);
        check(found.size() == 1 && found.get(0).comp_1933().method_8110(server.method_30611()).method_31574(class_1802.field_20391),
            "getRecipesFor(SMELTING, cobblestone) = the stone recipe");
        check(rm.method_42299(net.minecraft.class_3956.field_17546, cobble, level, found.get(0).comp_1932()).isPresent(),
            "getRecipeFor(.., last id)");
        net.minecraft.class_1863.class_7266<net.minecraft.class_9696, net.minecraft.class_3861> check = net.minecraft.class_1863.method_42302(net.minecraft.class_3956.field_17546);
        check(check.method_42303(cobble, level).isPresent() && check.method_42303(cobble, level).isPresent(), "createCheck(SMELTING).getRecipeFor twice");
        check(rm.method_8128(net.minecraft.class_3956.field_17546, cobble, level).size() == 1, "getRemainingItemsFor: one slot");
    }

    static int gridCount(net.minecraft.class_1714 cm, class_1792 item) {
        int n = 0;
        for (int i = 1; i <= 9; i++) {
            class_1799 s = cm.method_7611(i).method_7677();
            if (s.method_31574(item)) {
                n += s.method_7947();
            }
        }
        return n;
    }

    /**
     * a 1.21.1 mod mixin on Item.getEnchantmentValue (cb.s7gear.mixin.EnchantValueMixin: a stick answers 30) changes
     * what vanilla enchanting reads, as on 1.21.1: EnchantmentHelper.getEnchantmentCost is 0 for an item of value 0
     */
    static void restoredMixins() {
        net.minecraft.class_5819 r = net.minecraft.class_5819.method_43049(1L);
        check(class_1802.field_8600.method_7837() == 30, "mixin on Item.getEnchantmentValue: the mod's own call sees 30 for a stick");
        int stick = net.minecraft.class_1890.method_8227(r, 2, 15, new class_1799(class_1802.field_8600));
        check(stick > 0, "mixin on Item.getEnchantmentValue: vanilla's enchanting cost of a stick is no longer 0: " + stick);
        int dirt = net.minecraft.class_1890.method_8227(r, 2, 15, new class_1799(class_1802.field_8831));
        check(dirt == 0, "an unmixed item of value 0 (dirt) still costs 0: " + dirt);
        check(class_1802.field_8802.method_7837() == 10, "diamond sword's value is its own (10), the mixin leaves it");
        // IngredientItemsMixin: an ingredient that takes sticks also takes diamonds (mixin on 1.21.1's getItems())
        class_1856 sticks = class_1856.method_8091(class_1802.field_8600);
        check(sticks.method_8105().length == 2, "mixin on Ingredient.getItems: the mod's own call sees stick + diamond: "
            + sticks.method_8105().length);
        check(sticks.method_8093(new class_1799(class_1802.field_8477)),
            "mixin on Ingredient.getItems: vanilla's Ingredient.test of a stick ingredient now takes a diamond");
        check(sticks.method_8093(new class_1799(class_1802.field_8600)), "the stick ingredient still takes a stick");
        check(!class_1856.method_8091(class_1802.field_8831).method_8093(new class_1799(class_1802.field_8477)),
            "an unmixed ingredient (dirt) does not take a diamond");
    }

    /** vanilla's own recipe matching (ShapedRecipePattern.matches -> Ingredient.test): coal over a diamond is a torch */
    static void restoredMixinsRecipes(MinecraftServer server) {
        net.minecraft.class_9694 input = net.minecraft.class_9694.method_59986(1, 2,
            java.util.List.of(new class_1799(class_1802.field_8713), new class_1799(class_1802.field_8477)));
        java.util.Optional<? extends class_8786<?>> torch = server.method_3772().method_8132(net.minecraft.class_3956.field_17545, input,
            server.method_30002());
        check(torch.isPresent() && torch.get().comp_1933().method_8110(server.method_30611()).method_31574(class_1802.field_8810),
            "mixin on Ingredient.getItems: vanilla's crafting match takes coal over a diamond as a torch: " + torch);
    }

    /** 1.21.1's RecipeBookCategories (client only): constants, tabs, icons, search sub-tabs */
    static void recipeBookCategories() {
        check(net.minecraft.class_314.values().length == 18 && net.minecraft.class_314.valueOf("CRAFTING_SEARCH") == net.minecraft.class_314.field_1809,
            "RecipeBookCategories: 18 constants, valueOf");
        check(net.minecraft.class_314.method_30285(net.minecraft.class_5421.field_25763).size() == 5
                && net.minecraft.class_314.field_1809.method_1623().get(0).method_31574(class_1802.field_8251)
                && net.minecraft.class_314.field_25783.get(net.minecraft.class_314.field_1804).size() == 3,
            "RecipeBookCategories: crafting tabs, search icon, furnace search sub-tabs");
    }

    /** 1.21.1's RecipeBookMenu members, handlePlacement, ServerPlaceRecipe and RecipeBook */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static void menus(MinecraftServer server) {
        // Fabric API's fake player: its connection drops packets (the placement updates the client's slots)
        class_3222 player = net.fabricmc.fabric.api.entity.FakePlayer.get(server.method_30002(),
            new GameProfile(UUID.randomUUID(), "cbgearmenus"));
        net.minecraft.class_1661 inv = player.method_31548();
        net.minecraft.class_1714 cm = new net.minecraft.class_1714(1, inv);
        check(cm.method_7653() == 3 && cm.method_7656() == 3, "CraftingMenu grid 3x3: " + cm.method_7653() + "x" + cm.method_7656());
        check(cm.method_7658() == 10 && cm.method_7655() == 0, "CraftingMenu size 10, result slot 0");
        check(!cm.method_32339(0) && cm.method_32339(1), "CraftingMenu shouldMoveToInventory: not the result, a grid slot");
        net.minecraft.class_1723 im = player.field_7498;
        check(im.method_7653() == 2 && im.method_7656() == 2 && im.method_7658() == 5, "InventoryMenu grid 2x2, size 5");
        net.minecraft.class_3858 fm = new net.minecraft.class_3858(2, inv);
        check(fm.method_7655() == 2 && fm.method_7658() == 3 && fm.method_7653() == 1, "FurnaceMenu result 2, size 3, width 1");
        check(!fm.method_32339(1) && fm.method_32339(0) && fm.method_32339(2), "FurnaceMenu shouldMoveToInventory: all but the fuel slot");

        net.minecraft.class_1863 rm = server.method_3772();
        class_8786 table = rm.method_8130(class_2960.method_60656("crafting_table")).orElseThrow();
        net.minecraft.class_3441 book = player.method_14253();
        check(!book.method_14878(table), "RecipeBook.contains: a new player's book lacks crafting_table");
        book.method_14876(table);
        check(book.method_14878(table) && book.method_22845(table.comp_1932()), "RecipeBook.add then contains (holder and id)");
        book.method_14885(table);
        check(book.method_14883(table), "RecipeBook.addHighlight then willHighlight");
        book.method_14886(table);
        check(!book.method_14883(table), "RecipeBook.removeHighlight");

        inv.method_5447(0, new class_1799(class_1802.field_8118, 4));
        cm.method_17697(false, table, player);
        check(gridCount(cm, class_1802.field_8118) == 4 && inv.method_5438(0).method_7960(),
            "handlePlacement moved the 4 planks into the grid: " + gridCount(cm, class_1802.field_8118));
        check(cm.method_7652(table), "recipeMatches(crafting_table) on the placed grid");
        net.minecraft.class_1662 contents = new net.minecraft.class_1662();
        cm.method_7654(contents);
        check(contents.field_7550.get(net.minecraft.class_1662.method_7408(new class_1799(class_1802.field_8118))) == 4,
            "fillCraftSlotsStackedContents counts the 4 planks");
        cm.method_7657();
        check(gridCount(cm, class_1802.field_8118) == 0, "clearCraftingContent empties the grid");

        inv.method_5447(0, new class_1799(class_1802.field_8118, 4));
        new net.minecraft.class_2955(cm).method_12826(player, table, false);
        check(gridCount(cm, class_1802.field_8118) == 4 && inv.method_5438(0).method_7960(),
            "new ServerPlaceRecipe(menu).recipeClicked places the recipe");
        cm.method_7657();

        book.method_14893(table);
        check(!book.method_14878(table), "RecipeBook.remove");
        inv.method_5447(0, new class_1799(class_1802.field_8118, 4));
        cm.method_17697(false, table, player);
        check(gridCount(cm, class_1802.field_8118) == 0 && inv.method_5438(0).method_7947() == 4,
            "handlePlacement of a recipe not in the book places nothing");
        inv.method_5447(0, class_1799.field_8037);
    }

    /** a 1.21.1 ItemCombinerMenu: one input slot, its own definitions and quick-move slot */
    public static class TestCombiner extends net.minecraft.class_4861 {
        public TestCombiner(int id, net.minecraft.class_1661 inv) {
            super(net.minecraft.class_3917.field_17329, id, inv, net.minecraft.class_3914.field_17304);
        }

        @Override
        protected net.minecraft.class_8047 method_48352() {
            return net.minecraft.class_8047.method_48364().method_48374(0, 10, 10, s -> s.method_31574(class_1802.field_8620))
                .method_48373(1, 50, 10).method_48372();
        }

        @Override
        public int method_48357(class_1799 stack) {
            return 0;
        }

        @Override
        protected boolean method_24927(net.minecraft.class_1657 player, boolean present) {
            return true;
        }

        @Override
        protected void method_24923(net.minecraft.class_1657 player, class_1799 stack) {
        }

        @Override
        protected boolean method_24925(net.minecraft.class_2680 state) {
            return true;
        }

        @Override
        public void method_24928() {
        }
    }

    /** 1.21.1's ItemCombinerMenu definitions (vanilla anvil, a mod subclass) and getSlotToQuickMoveTo */
    static void combiners(MinecraftServer server) {
        class_3222 player = player(server);
        net.minecraft.class_1661 inv = player.method_31548();
        net.minecraft.class_1706 anvil = new net.minecraft.class_1706(3, inv);
        check(anvil.field_7761.size() == 39, "anvil: 2 inputs + result + 36 inventory slots: " + anvil.field_7761.size());
        check(anvil.method_48357(new class_1799(class_1802.field_8620)) == 0, "anvil getSlotToQuickMoveTo = 0 (inputs empty)");
        TestCombiner tc = new TestCombiner(4, inv);
        check(tc.field_7761.size() == 38, "mod ItemCombinerMenu built from its own createInputSlotDefinitions: " + tc.field_7761.size());
        check(tc.method_7611(0).method_7680(new class_1799(class_1802.field_8620))
                && !tc.method_7611(0).method_7680(new class_1799(class_1802.field_8118)),
            "its input slot takes what its definition says (iron, not planks)");
        inv.method_5447(9, new class_1799(class_1802.field_8620, 3));
        tc.method_7601(player, 2);
        check(tc.method_7611(0).method_7677().method_31574(class_1802.field_8620), "quickMoveStack moves iron into the input slot");
        inv.method_5447(9, class_1799.field_8037);
    }


    /** 1.21.1's tick containers' NBT, MapItem / BannerPatternItem members, BundleItem(properties) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static void world(MinecraftServer server) {
        net.minecraft.class_2248 stone = class_2246.field_10340;
        java.util.function.Function<net.minecraft.class_2248, String> ids = b -> class_7923.field_41175.method_10221(b).toString();
        net.minecraft.class_2338 pos = new net.minecraft.class_2338(3, 64, 5);
        net.minecraft.class_4296<net.minecraft.class_2248> proto = new net.minecraft.class_4296<>();
        proto.method_39363(new net.minecraft.class_6760<>(stone, pos, 105L, net.minecraft.class_1953.field_9314, 0L));
        net.minecraft.class_2520 saved = proto.method_20463(100L, ids);
        check(saved instanceof net.minecraft.class_2499 l && l.size() == 1
                && "minecraft:stone".equals(l.method_10602(0).method_10558("i")),
            "ProtoChunkTicks.save(gameTime, idGetter): one tick named minecraft:stone: " + saved);
        net.minecraft.class_4296<net.minecraft.class_2248> loaded = net.minecraft.class_4296.method_39398((net.minecraft.class_2499) saved,
            s -> java.util.Optional.of(stone), new net.minecraft.class_1923(pos));
        check(loaded.method_20825() == 1, "ProtoChunkTicks.load(list, idParser, chunkPos): the tick back");
        java.util.List<net.minecraft.class_6759<net.minecraft.class_2248>> got = new java.util.ArrayList<>();
        net.minecraft.class_6759.method_39406((net.minecraft.class_2499) saved, s -> java.util.Optional.of(stone),
            new net.minecraft.class_1923(pos), got::add);
        check(got.size() == 1 && got.get(0).comp_249().equals(pos), "SavedTick.loadTickList(.., consumer) hands the tick on");
        net.minecraft.class_2487 one = net.minecraft.class_6759.method_39401(
            new net.minecraft.class_6760<>(stone, pos, 105L, net.minecraft.class_1953.field_9314, 0L), ids, 100L);
        check(one.method_10550("t") == 5 && "minecraft:stone".equals(one.method_10558("i")),
            "SavedTick.saveTick(tick, idGetter, gameTime): delay 5: " + one);
        net.minecraft.class_6761<net.minecraft.class_2248> lambda = (time, f) -> new net.minecraft.class_2499();
        check(lambda.method_20463(0L, ids) instanceof net.minecraft.class_2499, "a SerializableTickContainer lambda answers save");

        class_1799 map = new class_1799(class_1802.field_8204);
        check(((net.minecraft.class_1806) class_1802.field_8204).method_7757(map, server.method_30002(), player(server)) == null,
            "MapItem.getUpdatePacket of a map without data: null");
        net.minecraft.class_5250 desc = ((net.minecraft.class_1745) class_1802.field_8498).method_7703();
        check(desc.method_10851() instanceof net.minecraft.class_2588 t && t.method_11022().equals("item.minecraft.flower_banner_pattern.desc"),
            "BannerPatternItem.getDisplayName: item.minecraft.flower_banner_pattern.desc");
        check(BUNDLE instanceof net.minecraft.class_5537, "new BundleItem(properties) registered");
        check(new class_1799(BOOK).method_7923() && !new class_1799(BOOK, 2).method_7923() && BOOK.method_7837() == 1,
            "BookItem: a single book is enchantable, two are not; enchantment value 1");
        check("item.cbs7gear.named".equals(NAMED.method_7876()), "ItemNameBlockItem is named as an item: " + NAMED.method_7876());
        check(COMPLEX.method_16698() && ((net.minecraft.class_1762) COMPLEX).method_7757(new class_1799(COMPLEX), server.method_30002(), player(server)) == null,
            "ComplexItem: isComplex, no update packet of its own");
        check(TEMPLATE instanceof net.minecraft.class_8052, "SmithingTemplateItem.createArmorTrimTemplate(COAST) registered");
        // 1.21.1's RecipeBookCategories is a client class (net/minecraft/client; the 1.21.1 server jar has no class_314):
        // on a dedicated server touching it fails to load the class, as it did on 1.21.1
        if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            recipeBookCategories();
        } else {
            boolean categoriesLoaded;
            try {
                recipeBookCategories();
                categoriesLoaded = true;
            } catch (Throwable expected) {
                categoriesLoaded = false;
            }
            check(!categoriesLoaded, "RecipeBookCategories (client-only, as on 1.21.1) does not load on a dedicated server");
        }
        net.minecraft.class_1842 unnamed = new net.minecraft.class_1842(new class_1293(class_1294.field_5904, 100, 0));
        check(net.minecraft.class_1842.method_8051(java.util.Optional.of(class_6880.method_40223(unnamed)), "x.").equals("x.empty")
                && net.minecraft.class_1842.method_8051(java.util.Optional.of(net.minecraft.class_1847.field_9005), "p.").equals("p.swiftness"),
            "Potion(effects) and Potion.getName: x.empty / p.swiftness");
        check(new net.minecraft.class_1844(java.util.Optional.empty(), java.util.Optional.of(0xFF0000), List.of()).method_8064() == 0xFF0000,
            "PotionContents(potion, colour, effects): its custom colour");
        org.joml.Vector3f dust = new net.minecraft.class_2390(new org.joml.Vector3f(1.0F, 0.5F, 0.0F), 1.0F).method_59843();
        check(Math.abs(dust.x() - 1.0F) < 0.01F && Math.abs(dust.y() - 0.5F) < 0.01F && dust.z() == 0.0F
                && net.minecraft.class_2390.field_28272.x() == 1.0F && net.minecraft.class_2390.field_28272.y() == 0.0F,
            "DustParticleOptions(Vector3f, scale): the colour back; REDSTONE_PARTICLE_COLOR is red: " + dust);
        net.minecraft.class_3971 cutter = new net.minecraft.class_3971(5, player(server).method_31548());
        cutter.field_17629.method_5447(0, new class_1799(class_1802.field_20412));
        check(cutter.method_17863().size() == 3, "StonecutterMenu.getRecipes() for cobblestone: slab, stairs, wall: " + cutter.method_17863().size());
    }


    /** a 1.21.1 Fabric API custom ingredient: iron ingots only */
    public static class IronIngredient implements net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient {
        @Override
        public boolean test(class_1799 stack) {
            return stack.method_31574(class_1802.field_8620);
        }

        @Override
        public List<class_1799> getMatchingStacks() {
            return List.of(new class_1799(class_1802.field_8620));
        }

        @Override
        public boolean requiresTesting() {
            return true;
        }

        @Override
        public net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer<?> getSerializer() {
            return IRON_SERIALIZER;
        }
    }

    static final net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer<IronIngredient> IRON_SERIALIZER =
        new net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer<>() {
            @Override
            public class_2960 getIdentifier() {
                return class_2960.method_60655("cbs7gear", "iron");
            }

            @Override
            public com.mojang.serialization.MapCodec<IronIngredient> getCodec(boolean allowEmpty) {
                return com.mojang.serialization.MapCodec.unit(new IronIngredient());
            }

            @Override
            public net.minecraft.class_9139<net.minecraft.class_9129, IronIngredient> getPacketCodec() {
                return net.minecraft.class_9139.method_56431(new IronIngredient());
            }
        };

    /** Fabric API 0.116 members: a custom ingredient, getCreatorNamespace */
    static void fapi(MinecraftServer server) {
        IronIngredient iron = new IronIngredient();
        class_1856 vanilla = iron.toVanilla();
        check(vanilla.method_8093(new class_1799(class_1802.field_8620)) && !vanilla.method_8093(new class_1799(class_1802.field_8118)),
            "CustomIngredient.toVanilla(): tests iron, not planks");
        class_1799[] items = vanilla.method_8105();
        check(items.length == 1 && items[0].method_31574(class_1802.field_8620), "its getItems(): the iron its 1.21.1 getMatchingStacks gave");
        check(IRON_SERIALIZER.getCodec(false) != null, "CustomIngredientSerializer.getCodec(false)");
        class_1799 book = new class_1799(class_1802.field_8361);
        book.method_57379(class_9334.field_49613, List.of(class_2960.method_60656("crafting_table")));
        List<class_2960> recipes = book.method_57824(class_9334.field_49613);
        check(recipes != null && recipes.size() == 1 && recipes.get(0).equals(class_2960.method_60656("crafting_table")),
            "knowledge book RECIPES: set and get 1.21.1's ids: " + recipes);
        class_1799 bottle = new class_1799(class_1802.field_50140);
        bottle.method_57379(class_9334.field_50238, 3);
        Integer amplifier = bottle.method_57824(class_9334.field_50238);
        check(amplifier != null && amplifier == 3, "OMINOUS_BOTTLE_AMPLIFIER: set and get an Integer: " + amplifier);
        class_1799 netherite = new class_1799(class_1802.field_22020);
        check(netherite.method_57826(class_9334.field_50076) && netherite.method_57824(class_9334.field_50076) == net.minecraft.class_3902.field_17274
                && !new class_1799(class_1802.field_8620).method_57826(class_9334.field_50076),
            "FIRE_RESISTANT: netherite has it (Unit), iron not");
        class_2378<net.minecraft.class_7444> worldHorns = server.method_30611().method_30530(net.minecraft.class_7924.field_41275);
        check(worldHorns.method_35842(HORN) && worldHorns.method_35842(net.minecraft.class_7445.field_39126),
            "the 1.21.1 mod's instrument joined the world's instrument registry, beside vanilla's");
        net.minecraft.class_7444 standin = class_7923.field_41166.method_40290(HORN).comp_349();
        check(standin.comp_773() == 100 && class_7923.field_41166.method_40290(net.minecraft.class_7445.field_39126).comp_349().comp_773() == 140,
            "Instrument.useDuration(): 100 ticks (mod), 140 (vanilla goat horn)");
        class_1799 horn = net.minecraft.class_7430.method_43558(class_1802.field_39057, class_7923.field_41166.method_40290(HORN));
        boolean encodes = class_1799.field_24671.encodeStart(server.method_30611().method_57093(com.mojang.serialization.JsonOps.INSTANCE), horn).result().isPresent();
        check(encodes, "InstrumentItem.create with the stand-in's holder: the stack saves (the world's holder)");
        check("cbs7gear".equals(((net.fabricmc.fabric.api.item.v1.FabricItem) CHEST).getCreatorNamespace(new class_1799(CHEST))) && "minecraft".equals(((net.fabricmc.fabric.api.item.v1.FabricItemStack) (Object) new class_1799(class_1802.field_8620)).getCreatorNamespace()),
            "FabricItem / FabricItemStack.getCreatorNamespace");
    }


    /** a 1.21.1 recipe provider: two vanilla helpers and a shaped builder */
    public static class TestRecipes extends net.minecraft.class_2446 {
        public TestRecipes(net.minecraft.class_7784 output, java.util.concurrent.CompletableFuture<net.minecraft.class_7225.class_7874> registries) {
            super(output, registries);
        }

        @Override
        protected void method_10419(net.minecraft.class_8790 output) {
            method_24475(output, class_1802.field_8118, net.minecraft.class_3489.field_15545, 4);
            net.minecraft.class_2447.method_10436(net.minecraft.class_7800.field_40642, class_1802.field_8600, 4)
                .method_10434('#', class_1802.field_8118).method_10439("#").method_10439("#")
                .method_10429("has_planks", method_10426(class_1802.field_8118))
                .method_17972(output, class_2960.method_60655("cbs7gear", "sticks"));
        }
    }

    /** 1.21.1's RecipeProvider run on 1.21.2: the recipes it writes */
    static void datagen(MinecraftServer server) throws Exception {
        java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("cbs7gear-datagen");
        java.util.Map<java.nio.file.Path, byte[]> written = new java.util.concurrent.ConcurrentHashMap<>();
        net.minecraft.class_7403 cache = (path, bytes, hash) -> written.put(path, bytes);
        TestRecipes provider = new TestRecipes(new net.minecraft.class_7784(dir),
            java.util.concurrent.CompletableFuture.completedFuture(server.method_30611()));
        provider.method_10319(cache).join();
        java.nio.file.Path planks = dir.resolve("data/minecraft/recipe/oak_planks.json");
        java.nio.file.Path sticks = dir.resolve("data/cbs7gear/recipe/sticks.json");
        check(written.containsKey(planks) && new String(written.get(planks)).contains("oak_logs"),
            "RecipeProvider helper planksFromLog wrote recipe/oak_planks.json: " + written.keySet());
        check(written.containsKey(sticks) && new String(written.get(sticks)).contains("minecraft:crafting_shaped"),
            "a shaped builder saved by id wrote recipe/sticks.json");
        check(written.keySet().stream().anyMatch(p -> p.toString().replace(java.io.File.separatorChar, '/').contains("advancement/recipes/")),
            "the recipes' advancements were written");
        check("Recipes".equals(provider.method_10321()), "RecipeProvider.getName()");
        fabricDatagen(server);
    }

    /** Fabric API 0.116's FabricRecipeProvider: buildRecipes, its namespace and conditions */
    public static class TestFabricRecipes extends net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider {
        public TestFabricRecipes(net.fabricmc.fabric.api.datagen.v1.FabricDataOutput output,
                java.util.concurrent.CompletableFuture<net.minecraft.class_7225.class_7874> registries) {
            super(output, registries);
        }

        @Override
        public void method_10419(net.minecraft.class_8790 exporter) {
            net.minecraft.class_2447.method_10436(net.minecraft.class_7800.field_40642, class_1802.field_8600, 2)
                .method_10434('#', class_1802.field_8118).method_10439("#")
                .method_10429("has_planks", method_10426(class_1802.field_8118))
                .method_17972(exporter, class_2960.method_60656("cbfab_sticks"));
            net.minecraft.class_2450.method_10447(net.minecraft.class_7800.field_40642, class_1802.field_8600)
                .method_10454(class_1802.field_8118)
                .method_10442("has_planks", method_10426(class_1802.field_8118))
                .method_17972(withConditions(exporter, net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions.alwaysTrue()),
                    class_2960.method_60656("cbfab_cond"));
        }
    }

    static class_6862<class_1792> itemTag(String path) {
        return class_6862.method_40092(net.minecraft.class_7924.field_41197, class_2960.method_60655("cb_s7gear", path));
    }

    static class_1792 item(String path) {
        return class_7923.field_41178.method_10223(class_2960.method_60656(path));
    }

    /** Fabric API 0.116's FabricTagBuilder removal and FabricProvidedTagBuilder */
    public static class TestFabricTags extends net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.ItemTagProvider {
        public TestFabricTags(net.fabricmc.fabric.api.datagen.v1.FabricDataOutput output,
                java.util.concurrent.CompletableFuture<net.minecraft.class_7225.class_7874> registries) {
            super(output, registries);
        }

        @Override
        protected void method_10514(net.minecraft.class_7225.class_7874 lookup) {
            getOrCreateTagBuilder(itemTag("removing")).add(item("dirt")).remove(item("stone"))
                .removeTag(class_6862.method_40092(net.minecraft.class_7924.field_41197, class_2960.method_60656("logs")));
            net.fabricmc.fabric.api.datagen.v1.provider.FabricProvidedTagBuilder<class_1792> provided = getOrCreateTagBuilder(itemTag("provided"));
            provided.setReplace(true).removeAll(java.util.List.of(
                net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41197, class_2960.method_60656("gravel"))));
        }
    }

    static void fabricDatagen(MinecraftServer server) throws Exception {
        java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("cbs7gear-fabricdatagen");
        java.util.Map<String, String> written = new java.util.concurrent.ConcurrentHashMap<>();
        net.minecraft.class_7403 cache = (path, bytes, hash) ->
            written.put(dir.relativize(path).toString().replace(java.io.File.separatorChar, '/'), new String(bytes));
        net.fabricmc.fabric.api.datagen.v1.FabricDataOutput out = new net.fabricmc.fabric.api.datagen.v1.FabricDataOutput(
            FabricLoader.getInstance().getModContainer("cb_s7gear").orElseThrow(), dir, false);
        java.util.concurrent.CompletableFuture<net.minecraft.class_7225.class_7874> registries =
            java.util.concurrent.CompletableFuture.completedFuture(server.method_30611());
        TestFabricRecipes recipes = new TestFabricRecipes(out, registries);
        recipes.method_10319(cache).join();
        check(written.containsKey("data/cb_s7gear/recipe/cbfab_sticks.json"),
            "FabricRecipeProvider saved under the mod's namespace (getRecipeIdentifier): " + written.keySet());
        check(written.containsKey("data/cb_s7gear/advancement/recipes/misc/cbfab_sticks.json"),
            "its advancement under the mod's namespace");
        String cond = written.getOrDefault("data/cb_s7gear/recipe/cbfab_cond.json", "");
        check(cond.contains("fabric:load_conditions") && cond.contains("fabric:true"),
            "withConditions: the recipe carries its load conditions, in the mod's namespace: " + cond);
        check("Recipes".equals(recipes.method_10321()), "FabricRecipeProvider.getName()");
        written.clear();
        new TestFabricTags(out, registries).method_10319(cache).join();
        String removing = written.getOrDefault("data/cb_s7gear/tags/item/removing.json", "");
        check(removing.contains("fabric:remove") && removing.contains("minecraft:stone") && removing.contains("#minecraft:logs")
            && removing.contains("minecraft:dirt"), "FabricTagBuilder.remove / removeTag wrote fabric:remove: " + removing + " " + written.keySet());
        String provided = written.getOrDefault("data/cb_s7gear/tags/item/provided.json", "");
        check(provided.contains("\"replace\": true") && provided.contains("minecraft:gravel"),
            "FabricProvidedTagBuilder.setReplace / removeAll: " + provided);
    }

    /** what BindTagsMixin saw of each item-registry bind (tag count, planks present) */
    public static final List<String> SEEN_BINDS = java.util.Collections.synchronizedList(new ArrayList<>());

    static final class_6862<class_1792> MIXIN_BOUND = class_6862.method_40092(net.minecraft.class_7924.field_41197,
        class_2960.method_60655("cbs7gear", "mixin_bound"));

    /** every minecraft: tag of the item and block registries with its members, sorted: the bound tag contents */
    static String tagFingerprint() {
        List<String> lines = new ArrayList<>();
        for (class_2378<?> reg : List.<class_2378<?>>of(class_7923.field_41178, class_7923.field_41175)) {
            reg.method_40272().forEach(p -> {
                if (!p.getFirst().comp_327().method_12836().equals("minecraft")) {
                    return;
                }
                List<String> ids = new ArrayList<>();
                p.getSecond().method_40239().forEach(h -> ids.add(String.valueOf(h.method_40230().map(k -> k.method_29177()).orElse(null))));
                java.util.Collections.sort(ids);
                lines.add(reg.method_30517().method_29177() + " " + p.getFirst().comp_327() + "=" + String.join(",", ids));
            });
        }
        java.util.Collections.sort(lines);
        return lines.size() + " tags, hash " + Integer.toHexString(String.join("|", lines).hashCode());
    }

    /**
     * BindTagsMixin (a 1.21.1 mixin on MappedRegistry.bindTags) against vanilla's tag loading: at startup and after a
     * /reload it sees vanilla's bind of the item tags and its own tag is bound; the minecraft: tag contents are printed
     * (CB-TAGTEST fingerprint) to compare with a boot whose layer route is off (-Dcb.step1_21_2.noTagRoute=true)
     */
    static void tagReload(MinecraftServer server) {
        boolean routeOff = Boolean.getBoolean("cb.step1_21_2.noTagRoute");
        System.out.println("CB-TAGTEST startup fingerprint " + tagFingerprint() + (routeOff ? " (route off)" : ""));
        int before = SEEN_BINDS.size();
        boolean bound = class_7923.field_41178.method_47983(class_1802.field_8600).method_40220(MIXIN_BOUND);
        if (!routeOff) {
            check(before > 0 && SEEN_BINDS.get(0).endsWith(" planks"),
                "mixin on MappedRegistry.bindTags: sees vanilla's item tag bind at startup: " + SEEN_BINDS);
            check(bound, "mixin on MappedRegistry.bindTags: the tag it adds (cbs7gear:mixin_bound) is bound to the stick");
        }
        server.method_29439(server.method_3836().method_29210()).thenRun(() -> {
            System.out.println("CB-TAGTEST reload fingerprint " + tagFingerprint() + (routeOff ? " (route off)" : ""));
            if (!routeOff) {
                boolean seen = SEEN_BINDS.size() > before && SEEN_BINDS.get(SEEN_BINDS.size() - 1).endsWith(" planks");
                boolean still = class_7923.field_41178.method_47983(class_1802.field_8600).method_40220(MIXIN_BOUND);
                System.out.println("CB-TAGTEST " + (seen ? "ok   " : "FAIL ") + "after /reload the mixin saw vanilla's item tag bind again: " + SEEN_BINDS);
                System.out.println("CB-TAGTEST " + (still ? "ok   " : "FAIL ") + "after /reload cbs7gear:mixin_bound is bound to the stick");
                System.out.println("CB-TAGTEST " + (class_1802.field_8118.method_40131().method_40220(net.minecraft.class_3489.field_15537) ? "ok   " : "FAIL ")
                    + "after /reload vanilla's own tags hold (oak planks in #planks)");
            }
            System.out.println("CB-TAGTEST DONE");
        });
    }

    /** the equip sound 1.21.2's onEquipItem plays: the stack's EQUIPPABLE.equipSound (read by reflection: no 1.21.1 API) */
    static Object equipSound(class_1799 stack) throws Exception {
        Object type = Class.forName("net.minecraft.class_9334").getField("field_54196").get(null);
        Object eq = class_1799.class.getMethod("method_57824", Class.forName("net.minecraft.class_9331")).invoke(stack, type);
        return eq == null ? null : eq.getClass().getMethod("comp_3175").invoke(eq);
    }

    /**
     * ArmorEquipMixin / ShieldEquipMixin (1.21.1 mixins on ArmorItem / ShieldItem getEquipmentSlot and getEquipSound)
     * against 1.21.2's own reads: LivingEntity.getEquipmentSlotForItem and the EQUIPPABLE sound onEquipItem plays
     */
    static void equipmentReads(MinecraftServer server) throws Exception {
        class_3222 player = player(server);
        check(player.method_32326(new class_1799(class_1802.field_8267)) == class_1304.field_6174,
            "mixin on ArmorItem.getEquipmentSlot: vanilla's getEquipmentSlotForItem puts the leather helmet on the chest");
        check(player.method_32326(new class_1799(class_1802.field_8743)) == class_1304.field_6169,
            "an unmixed armor item (iron helmet) still goes to the head");
        check(player.method_32326(new class_1799(class_1802.field_8058)) == class_1304.field_6174,
            "an unmixed armor item (diamond chestplate) still goes to the chest");
        check(player.method_32326(new class_1799(class_1802.field_8255)) == class_1304.field_6169,
            "mixin on ShieldItem.getEquipmentSlot: vanilla's getEquipmentSlotForItem puts the shield on the head");
        check(equipSound(new class_1799(class_1802.field_8753)) == net.minecraft.class_3417.field_14966,
            "mixin on ArmorItem.getEquipSound: the equip sound vanilla plays for golden boots is the elytra's: "
                + equipSound(new class_1799(class_1802.field_8753)));
        check(equipSound(new class_1799(class_1802.field_8660)) == net.minecraft.class_3417.field_14862,
            "an unmixed armor item (iron boots) keeps its own equip sound: " + equipSound(new class_1799(class_1802.field_8660)));
    }

}
