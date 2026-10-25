package cb.s5;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1642;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1738;
import net.minecraft.class_1740;
import net.minecraft.class_1766;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1810;
import net.minecraft.class_1829;
import net.minecraft.class_1836;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3468;
import net.minecraft.class_4059;
import net.minecraft.class_5134;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_8051;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import net.minecraft.class_9886;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.4-era server mod (intermediary names, Fabric Loader + Fabric API's FakePlayer) for s5's bridges of the
 * 1.21.4 -> 1.21.5 step: its own SwordItem / PickaxeItem / ArmorItem / AnimalArmorItem subclasses, the Item members
 * 1.21.5 reshaped (hurtEnemy, appendHoverText, inventoryTick, getBreakingSound), type tests against the removed
 * classes, CompoundTag / ListTag getter fallbacks, Entity.fallDistance and causeFallDamage, and a Player subclass.
 * Runs once the server has started. Prints "CB-S5 FAIL ..." per failure and "CB-S5 DONE passed/checks".
 */
public class S5TestMod implements ModInitializer {
    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        if (!ok) {
            FAILS.add(what);
            System.out.println("CB-S5 FAIL " + what);
        }
    }

    // ------------------------------------------------------------------ the mod's items (1.21.4 classes)

    static final List<Object[]> TICKS = new ArrayList<>();
    static final List<String> HURT = new ArrayList<>();
    static boolean breakingSoundAsked;

    /** a 1.21.4 sword: overrides hurtEnemy (boolean), appendHoverText (List), inventoryTick (slot, selected), getBreakingSound */
    public static class TestSword extends class_1829 {
        public TestSword(class_1792.class_1793 props) {
            super(class_9886.field_52588, 3.0F, -2.4F, props);
        }

        @Override
        public boolean method_7873(class_1799 stack, class_1309 target, class_1309 attacker) {
            HURT.add("sword");
            return super.method_7873(stack, target, attacker);
        }

        @Override
        public void method_7851(class_1799 stack, class_1792.class_9635 ctx, List<class_2561> lines, class_1836 flag) {
            super.method_7851(stack, ctx, lines, flag);
            lines.add(class_2561.method_43470("cb-s5 tooltip line"));
        }

        @Override
        public void method_7888(class_1799 stack, class_1937 level, class_1297 entity, int slot, boolean selected) {
            TICKS.add(new Object[] {stack, slot, selected, level.field_9236});
            super.method_7888(stack, level, entity, slot, selected);
        }

        @Override
        public class_3414 method_57336() {
            breakingSoundAsked = true;
            return super.method_57336();
        }
    }

    /** a 1.21.4 pickaxe with no overrides: DiggerItem's bodies */
    public static class TestPick extends class_1810 {
        public TestPick(class_1792.class_1793 props) {
            super(class_9886.field_52588, 1.0F, -2.8F, props);
        }
    }

    public static class TestHelmet extends class_1738 {
        public TestHelmet(class_1792.class_1793 props) {
            super(class_1740.field_7892, class_8051.field_41934, props);
        }
    }

    public static class TestWolfArmor extends class_4059 {
        public TestWolfArmor(class_1792.class_1793 props) {
            super(class_1740.field_7892, class_4059.class_9076.field_47826, props);
        }
    }

    static boolean fallAsked;

    /** a 1.21.4 zombie overriding causeFallDamage(float, float, source) */
    public static class TestZombie extends class_1642 {
        public TestZombie(class_1937 level) {
            super(class_1299.field_6051, level);
        }

        @Override
        public boolean method_5747(float distance, float multiplier, class_1282 source) {
            fallAsked = distance == 5.0F && multiplier == 1.0F;
            return false;
        }
    }

    /** a 1.21.4 class extending Player directly: isCreative / isSpectator are abstract there */
    public static class TestPlayer extends class_1657 {
        public TestPlayer(class_1937 level) {
            super(level, class_2338.field_10980, 0.0F, new GameProfile(UUID.randomUUID(), "cbs5player"));
        }

        @Override
        public boolean method_7325() {
            return false;
        }

        @Override
        public boolean method_7337() {
            return true;
        }
    }

    static class_1792 SWORD, PICK, HELMET, WOLF;

    static <T extends class_1792> T item(String path, java.util.function.Function<class_1792.class_1793, T> make) {
        class_2960 id = class_2960.method_60655("cbs5", path);
        T item = make.apply(new class_1792.class_1793().method_63686(class_5321.method_29179(class_7924.field_41197, id)));
        class_2378.method_10230(class_7923.field_41178, id, item);
        return item;
    }

    @Override
    public void onInitialize() {
        SWORD = item("sword", TestSword::new);
        PICK = item("pick", TestPick::new);
        HELMET = item("helmet", TestHelmet::new);
        WOLF = item("wolf_armor", TestWolfArmor::new);
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
        }, "cb-s5");
        t.setDaemon(true);
        t.start();
    }

    interface Section {
        void run() throws Exception;
    }

    static void section(String name, Section s) {
        try {
            s.run();
        } catch (Throwable e) {
            e.printStackTrace();
            check(false, name + ": no exception: " + e);
        }
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();
        section("types", S5TestMod::types);
        section("tools", S5TestMod::tools);
        section("hits", () -> hits(level));
        section("tooltip", S5TestMod::tooltip);
        section("inventoryTick", () -> inventoryTick(level));
        section("breakSound", () -> breakSound(level));
        section("armor", S5TestMod::armor);
        section("nbt", S5TestMod::nbt);
        section("fall", () -> fall(level));
        section("player", () -> player(level));
        System.out.println("CB-S5 DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED " + FAILS));
    }

    static void types() {
        Object diamondSword = class_1802.field_8802;
        Object stick = class_1802.field_8600;
        Object ironPick = class_1802.field_8403;
        Object ironAxe = class_1802.field_8475;
        check(diamondSword instanceof class_1829, "vanilla diamond sword instanceof SwordItem");
        check(!(stick instanceof class_1829), "stick is no SwordItem");
        check(SWORD instanceof class_1829, "mod sword instanceof SwordItem");
        check(ironPick instanceof class_1810 && ironPick instanceof class_1766, "vanilla iron pickaxe instanceof PickaxeItem and DiggerItem");
        check(ironAxe instanceof class_1766 && !(ironAxe instanceof class_1810), "vanilla iron axe is a DiggerItem, not a PickaxeItem");
        check(PICK instanceof class_1766, "mod pickaxe instanceof DiggerItem");
        check(class_1802.field_8743 instanceof class_1738 && !(diamondSword instanceof class_1738), "iron helmet instanceof ArmorItem, sword not");
        check(class_1802.field_47831 instanceof class_4059 && WOLF instanceof class_4059, "wolf armor (vanilla and mod) instanceof AnimalArmorItem");
        class_1829 cast = (class_1829) diamondSword;
        check(cast == diamondSword, "cast of a vanilla sword to SwordItem");
        boolean threw = false;
        try {
            class_1829 bad = (class_1829) stick;
            check(bad == null, "unreachable");
        } catch (ClassCastException e) {
            threw = true;
        }
        check(threw, "cast of a stick to SwordItem throws ClassCastException");
    }

    static double amount(class_9285 m, Object attribute) {
        double sum = 0;
        for (class_9285.class_9287 e : m.comp_2393()) {
            if (e.comp_2395() == attribute) {
                sum += e.comp_2396().comp_2449();
            }
        }
        return sum;
    }

    static void tools() {
        class_1799 sword = new class_1799(SWORD);
        class_1799 pick = new class_1799(PICK);
        check(sword.method_7936() == 1561, "mod sword durability = diamond's 1561: " + sword.method_7936());
        double dmg = amount(sword.method_57824(class_9334.field_49636), class_5134.field_23721);
        check(dmg == 6.0, "mod sword attack damage modifier 3 + diamond bonus 3: " + dmg);
        float speed = pick.method_7924(class_2246.field_10340.method_9564());
        check(speed == 8.0F, "mod pickaxe mines stone at diamond speed 8: " + speed);
        check(sword.method_7924(class_2246.field_10340.method_9564()) == 1.0F, "mod sword mines stone at 1");
    }

    static void hits(class_3218 level) {
        // a real ServerPlayer: Fabric API's fake player drops stats
        class_3222 player = new class_3222(level.method_8503(), level, new GameProfile(UUID.randomUUID(), "cbs5hits"),
            net.minecraft.class_8791.method_53821());
        class_1642 target = new class_1642(level);
        int used0 = player.method_14248().method_15025(class_3468.field_15372.method_14956(SWORD));
        class_1799 sword = new class_1799(SWORD);
        HURT.clear();
        boolean w = sword.method_7979(target, player);
        check(w && HURT.equals(List.of("sword")), "mod sword: hurtEnemy override asked once and answers true: " + w + " " + HURT);
        sword.method_59979(target, player);
        check(sword.method_7919() == 1, "mod sword wears exactly 1 per hit: " + sword.method_7919());
        int used1 = player.method_14248().method_15025(class_3468.field_15372.method_14956(SWORD));
        check(used1 == used0 + 1, "mod sword hit counts as a use once: " + used0 + " -> " + used1);
        class_1799 pick = new class_1799(PICK);
        check(pick.method_7979(target, player), "mod pickaxe hurtEnemy answers true (DiggerItem)");
        pick.method_59979(target, player);
        check(pick.method_7919() == 2, "mod pickaxe wears exactly 2 per hit: " + pick.method_7919());
        class_1799 vanilla = new class_1799(class_1802.field_8802);
        check(vanilla.method_7979(target, player), "vanilla diamond sword hurtEnemy answers true");
        vanilla.method_59979(target, player);
        check(vanilla.method_7919() == 1, "vanilla diamond sword wears exactly 1 per hit: " + vanilla.method_7919());
        class_1799 stick = new class_1799(class_1802.field_8600);
        check(!stick.method_7979(target, player), "stick hurtEnemy answers false");
    }

    static void tooltip() {
        List<class_2561> lines = new class_1799(SWORD).method_7950(class_1792.class_9635.field_51353, null, class_1836.field_41070);
        boolean found = false;
        for (class_2561 l : lines) {
            found |= l.getString().equals("cb-s5 tooltip line");
        }
        check(found, "mod sword's appendHoverText line is in the tooltip: " + lines.size() + " lines");
    }

    static void inventoryTick(class_3218 level) {
        class_3222 player = net.fabricmc.fabric.api.entity.FakePlayer.get(level, new GameProfile(UUID.randomUUID(), "cbs5tick"));
        class_1661 inv = player.method_31548();
        class_1799 a = new class_1799(SWORD);
        class_1799 b = new class_1799(SWORD);
        inv.method_5447(0, a);
        inv.method_5447(7, b);
        TICKS.clear();
        inv.method_7381();
        Map<class_1799, Object[]> got = new IdentityHashMap<>();
        for (Object[] r : TICKS) {
            got.put((class_1799) r[0], r);
        }
        Object[] ra = got.get(a);
        Object[] rb = got.get(b);
        check(ra != null && (int) ra[1] == 0 && (boolean) ra[2] && !(boolean) ra[3],
            "inventoryTick of the selected slot 0 on the server: " + (ra == null ? "not asked" : ra[1] + "/" + ra[2]));
        check(rb != null && (int) rb[1] == 7 && !(boolean) rb[2], "inventoryTick of slot 7, not selected: " + (rb == null ? "not asked" : rb[1] + "/" + rb[2]));
        check(TICKS.size() == 2, "each stack ticked once: " + TICKS.size());
        class_1642 zombie = new class_1642(level);
        TICKS.clear();
        new class_1799(SWORD).method_7917(level, zombie, 0, true);
        check(TICKS.isEmpty() || TICKS.size() == 1, "a 1.21.4 stack tick call links: " + TICKS.size());
    }

    static void breakSound(class_3218 level) {
        class_1642 zombie = new class_1642(level);
        class_1799 sword = new class_1799(SWORD);
        zombie.method_5673(class_1304.field_6173, sword);
        breakingSoundAsked = false;
        // the break effect (sound, particles) is the entity event 47 (main hand) that breaking sends to clients; handled
        // here on the server entity, it runs LivingEntity.breakItem as a client does
        zombie.method_5711((byte) 47);
        check(breakingSoundAsked, "the main-hand break event asks the mod sword's getBreakingSound override");
        sword.method_7970(sword.method_7936() + 1, zombie, class_1304.field_6173);
        check(sword.method_7960(), "mod sword breaks");
        class_3414 wolf = WOLF.method_57336();
        check(wolf.comp_3319().method_12832().contains("wolf"), "mod wolf armor's breaking sound is the wolf armor's: " + wolf.comp_3319());
        class_3414 plain = class_1802.field_8600.method_57336();
        check(plain.comp_3319().method_12832().equals("entity.item.break"), "a plain item's breaking sound is item break: " + plain.comp_3319());
        check(new class_1799(class_1802.field_8600).method_57351() == plain, "ItemStack.getBreakingSound asks the item");
    }

    static void armor() {
        class_1799 helmet = new class_1799(HELMET);
        check(helmet.method_7936() == 165, "mod iron helmet durability 11 * 15: " + helmet.method_7936());
        check(helmet.method_57824(class_9334.field_54196).comp_3174() == class_1304.field_6169, "mod helmet equips on the head");
        class_1799 wolf = new class_1799(WOLF);
        check(wolf.method_57824(class_9334.field_54196).comp_3174() == class_1304.field_48824, "mod wolf armor equips on the body");
    }

    static void nbt() {
        class_2487 tag = new class_2487();
        check(tag.method_10550("missing") == 0, "getInt of a missing key: 0");
        check(tag.method_10558("missing").equals(""), "getString of a missing key: \"\"");
        check(tag.method_10547("missing").length == 0, "getByteArray of a missing key: empty");
        check(tag.method_10562("missing") != null && tag.method_10562("missing").method_33133(), "getCompound of a missing key: a new empty compound");
        check(tag.method_10554("missing", 10).isEmpty(), "getList of a missing key: empty");
        check(!tag.method_10573("missing", 99), "contains(missing, any number): false");
        tag.method_10569("n", 42);
        check(tag.method_10550("n") == 42 && tag.method_10573("n", 99) && tag.method_10540("n") == 3, "putInt / getInt / contains / getTagType");
        check(tag.method_10558("n").equals(""), "getString of an int: \"\"");
        UUID u = UUID.randomUUID();
        tag.method_25927("u", u);
        check(tag.method_25928("u") && u.equals(tag.method_25926("u")), "putUUID / hasUUID / getUUID");
        class_2499 list = new class_2499();
        list.add(class_2497.method_23247(5));
        tag.method_10566("l", list);
        check(tag.method_10554("l", 3).method_10600(0) == 5, "getList(key, int type).getInt(0)");
        check(tag.method_10554("l", 10).isEmpty(), "getList of ints asked for compounds: empty");
        check(list.method_10601() == 3, "ListTag.getElementType of an int list: 3");
        check(list.method_10602(0).method_33133(), "ListTag.getCompound of an int: a new empty compound");
    }

    static void fall(class_3218 level) {
        TestZombie z = new TestZombie(level);
        z.field_6017 = 3.5F;
        check(z.field_6017 == 3.5F, "fallDistance written and read as a float: " + z.field_6017);
        fallAsked = false;
        z.method_5747(5.0F, 1.0F, z.method_48923().method_48827());
        check(fallAsked, "causeFallDamage override asked with 1.21.4's float distance");
    }

    static void player(class_3218 level) throws Exception {
        TestPlayer p = new TestPlayer(level);
        check(p.method_7337(), "Player subclass isCreative answers its override");
        Object mode = p.getClass().getMethod("method_68876").invoke(p);
        check(mode != null && ((Enum<?>) mode).name().equals("CREATIVE"), "Player subclass gameMode() from its isCreative: " + mode);
    }
}
