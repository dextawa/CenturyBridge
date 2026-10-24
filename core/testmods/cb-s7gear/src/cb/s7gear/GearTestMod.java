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
            ingredients();
            types();
            recipes(server);
            recipeClasses(server);
            serializers(server);
            cookingBytes(server);
            recipeManager(server);
            menus(server);
            combiners(server);
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

}
