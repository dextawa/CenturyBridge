package cb.itemuse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1282;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1657;
import net.minecraft.class_1665;
import net.minecraft.class_1744;
import net.minecraft.class_1753;
import net.minecraft.class_1756;
import net.minecraft.class_1761;
import net.minecraft.class_1764;
import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1813;
import net.minecraft.class_1893;
import net.minecraft.class_1937;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3417;
import net.minecraft.class_3505;
import net.minecraft.class_4081;
import net.minecraft.class_4174;
import net.minecraft.class_4590;
import net.minecraft.class_4609;
import net.minecraft.class_5134;
import net.minecraft.class_5321;
import net.minecraft.class_7706;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9279;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.6-era server mod (intermediary names, Fabric Loader API only) for the item-use bridges of the
 * 1.20.6 -> 1.21 step: getUseDuration / getChargeDuration without an entity, createArrow without a weapon,
 * getAttackDamageBonus(player, damage), hurtAndBreak(amount, random, player, onBreak), hasInfiniteArrows,
 * FoodData.eat(stack), FoodProperties' 1.20.6 constructor, BowlFoodItem, RecordItem (its song in the
 * world's jukebox_song registry), MobEffect.addAttributeModifier by uuid string, CustomData.update,
 * DyeColor's colour components, Item's attack modifier uuids, ItemStack.transmuteCopyIgnoreEmpty,
 * CreativeModeTab's background suffix and search tree builder, TagManager.getTagDir and
 * BlockMath.getUVLockTransform. 1.21's own entry points are reached by reflection on 1.21's methods.
 * Every check prints one "CB-USETEST ok|FAIL ..." line; the last is "CB-USETEST DONE &lt;passed&gt;/&lt;checks&gt;".
 */
public class ItemUseTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-USETEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    /** a bow one tick slower to draw than vanilla's, through its super call */
    public static class TBow extends class_1753 {
        public TBow() {
            super(new class_1792.class_1793().method_7895(384));
        }

        @Override
        public int method_7881(class_1799 stack) {
            return super.method_7881(stack) + 1;
        }

        static boolean infinite(class_1799 weapon, class_1799 ammo) {
            return method_57392(weapon, ammo, false);
        }
    }

    /** an arrow item recording who shot it */
    public static class TArrow extends class_1744 {
        static class_1309 lastShooter;

        public TArrow() {
            super(new class_1792.class_1793());
        }

        @Override
        public class_1665 method_7702(class_1937 level, class_1799 stack, class_1309 shooter) {
            lastShooter = shooter;
            return super.method_7702(level, stack, shooter);
        }
    }

    /** a weapon with 5 bonus damage over its super's */
    public static class TSword extends class_1792 {
        public TSword() {
            super(new class_1792.class_1793());
        }

        @Override
        public float method_58403(class_1657 attacker, float damage) {
            return super.method_58403(attacker, damage) + 5.0F;
        }
    }

    /** an effect adding speed under a modifier uuid */
    public static class TEffect extends class_1291 {
        public TEffect() {
            super(class_4081.field_18271, 0xFFFFFF);
            this.method_5566(class_5134.field_23719, "0b2a2a1e-0000-4000-8000-000000000001", 0.2, class_1322.class_1323.field_6328);
        }
    }

    /** a disc of the note block bell */
    public static class TDisc extends class_1813 {
        public TDisc() {
            super(7, class_3417.field_14793.comp_349(), new class_1792.class_1793().method_7889(1), 10);
        }
    }

    static TBow BOW;
    static TArrow ARROW;
    static TSword SWORD;
    static TEffect EFFECT;
    static TDisc DISC;
    static class_1756 STEW;

    @Override
    public void onInitialize() {
        BOW = class_2378.method_10230(class_7923.field_41178, class_2960.method_43902("cbitemuse", "bow"), new TBow());
        ARROW = class_2378.method_10230(class_7923.field_41178, class_2960.method_43902("cbitemuse", "arrow"), new TArrow());
        SWORD = class_2378.method_10230(class_7923.field_41178, class_2960.method_43902("cbitemuse", "sword"), new TSword());
        DISC = class_2378.method_10230(class_7923.field_41178, class_2960.method_43902("cbitemuse", "disc"), new TDisc());
        STEW = class_2378.method_10230(class_7923.field_41178, class_2960.method_43902("cbitemuse", "stew"),
            new class_1756(new class_1792.class_1793().method_19265(new class_4174(6, 0.6F, false, 1.6F, List.of()))));
        EFFECT = class_2378.method_10230(class_7923.field_41174, class_2960.method_43902("cbitemuse", "effect"), new TEffect());
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
        }, "cb-itemuse");
        t.setDaemon(true);
        t.start();
    }

    static Method method(Class<?> c, String name, Class<?>... params) throws Exception {
        Method m = c.getMethod(name, params);
        m.setAccessible(true);
        return m;
    }

    static Object field(Object o, Class<?> c, String name) throws Exception {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(o);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void run(MinecraftServer server) {
        try {
            class_3218 level = server.method_30002();
            class_1309 zombie = (class_1309) class_1299.field_6051.method_5883(level);
            zombie.method_5814(0, 100, 0);
            class_1657 player = (class_1657) Class.forName("net.fabricmc.fabric.api.entity.FakePlayer")
                .getMethod("get", class_3218.class).invoke(null, level);

            // ---- getUseDuration, getChargeDuration ----
            class_1799 bowStack = new class_1799(BOW);
            check((int) method(class_1792.class, "method_7881", class_1799.class, class_1309.class).invoke(BOW, bowStack, zombie) == 72001,
                "1.21 getUseDuration(stack, entity) reaches the mod's bow and its super");
            check(BOW.method_7881(bowStack) == 72001, "the mod's own getUseDuration(stack)");
            class_1799 crossbow = new class_1799(class_1802.field_8399);
            check(((class_1792) class_1802.field_8399).method_7881(crossbow) == 28, "1.20.6 getUseDuration on the crossbow, no entity: 28");
            check(class_1764.method_7775(crossbow) == 25, "CrossbowItem.getChargeDuration(stack): 25");
            crossbow.method_7978(class_1893.field_9098, 3);
            check(class_1764.method_7775(crossbow) == 10, "getChargeDuration with quick charge III: 10 (1.20.6's 25 - 5*3)");
            check(new class_1799(class_1802.field_8102).method_7935() == 72000, "ItemStack.getUseDuration() of a bow: 72000");

            // ---- createArrow ----
            Method create = method(class_1744.class, "method_7702", class_1937.class, class_1799.class, class_1309.class, class_1799.class);
            class_1665 arrow = (class_1665) create.invoke(ARROW, level, new class_1799(ARROW), zombie, bowStack);
            check(arrow != null && TArrow.lastShooter == zombie, "1.21 createArrow(level, ammo, shooter, weapon) reaches the mod's arrow item");
            Object weapon = field(arrow, class_1665.class, "field_51620");
            check(weapon instanceof class_1799 w && w.method_31574(BOW), "the mod's super call hands on 1.21's weapon: " + weapon);
            class_1665 plain = ((class_1744) class_1802.field_8107).method_7702(level, new class_1799(class_1802.field_8107), zombie);
            check(plain != null && field(plain, class_1665.class, "field_51620") == null, "1.20.6 createArrow(level, ammo, shooter): no weapon");

            // ---- getAttackDamageBonus ----
            Method bonus = method(class_1792.class, "method_58403", class_1297.class, float.class, class_1282.class);
            class_1282 playerAttack = player.method_48923().method_48802(player);
            check((float) bonus.invoke(SWORD, zombie, 4.0F, playerAttack) == 5.0F, "1.21 getAttackDamageBonus from a player's attack: the mod's 5 over super's 0");
            check((float) bonus.invoke(SWORD, zombie, 4.0F, zombie.method_48923().method_48812(zombie)) == 0.0F, "a mob's attack: no bonus");
            check(SWORD.method_58403(player, 4.0F) == 5.0F, "the mod's own getAttackDamageBonus(player, damage)");
            check(((class_1792) class_1802.field_8371).method_58403(player, 4.0F) == 0.0F, "1.20.6 getAttackDamageBonus on an iron sword: 0");
            check(((class_1792) class_1802.field_49814).method_58403(player, 4.0F) == 0.0F, "1.20.6 getAttackDamageBonus on a mace, not falling: 0");

            // ---- hurtAndBreak, hasInfiniteArrows ----
            class_1799 sword = new class_1799(class_1802.field_8371);
            int[] broke = {0};
            sword.method_7956(1, level.field_9229, null, () -> broke[0]++);
            check(sword.method_7919() == 1 && broke[0] == 0, "hurtAndBreak(1, random, null, onBreak): one damage");
            sword.method_7974(sword.method_7936() - 1);
            sword.method_7956(1, level.field_9229, null, () -> broke[0]++);
            check(broke[0] == 1 && sword.method_7947() == 1, "hurtAndBreak to the maximum: onBreak once, the stack not shrunk (1.20.6)");
            class_1799 infinityBow = new class_1799(BOW);
            infinityBow.method_7978(class_1893.field_9125, 1);
            check(TBow.infinite(infinityBow, new class_1799(class_1802.field_8107)) && !TBow.infinite(bowStack, new class_1799(class_1802.field_8107)),
                "ProjectileWeaponItem.hasInfiniteArrows: infinity and an arrow");

            // ---- food ----
            player.method_7344().method_7580(10);
            player.method_7344().method_7579(new class_1799(class_1802.field_8229));
            check(player.method_7344().method_7586() == 15, "FoodData.eat(bread): +5 -> " + player.method_7344().method_7586());
            class_4174 stewFood = new class_1799(STEW).method_57824(net.minecraft.class_9334.field_50075);
            check(stewFood != null && stewFood.comp_2491() == 6, "FoodProperties' 1.20.6 constructor: nutrition 6");
            check(STEW.method_7861(new class_1799(STEW), level, zombie).method_31574(class_1802.field_8428), "BowlFoodItem.finishUsingItem leaves a bowl");

            // ---- music disc ----
            class_2378 songs = server.method_30611().method_30530(class_5321.method_29180(class_2960.method_43902("minecraft", "jukebox_song")));
            class_2960 bell = class_3417.field_14793.comp_349().method_14833();
            Object song = songs.method_10223(bell);
            check(song != null, "the world's jukebox_song registry has the disc's song " + bell);
            check(song != null && (int) method(song.getClass(), "comp_2838").invoke(song) == 7, "its comparator output is the disc's 7");
            check(class_1813.method_8012(class_3417.field_14793.comp_349()) == DISC && DISC.method_44369() == 200,
                "RecordItem.getBySound and getLengthInTicks");

            // ---- effects, data, colours, uuids ----
            Map templates = (Map) field(EFFECT, class_1291.class, "field_5885");
            Object template = templates.get(class_5134.field_23719);
            Object id = template == null ? null : method(template.getClass(), "comp_2182").invoke(template);
            check(class_2960.method_43902("minecraft", "0b2a2a1e-0000-4000-8000-000000000001").equals(id),
                "MobEffect.addAttributeModifier by uuid string: 1.21 id " + id);
            Object data = class_9279.field_49302.method_57447(com.mojang.serialization.Codec.INT.fieldOf("x"), 5).result().orElse(null);
            check(data instanceof class_9279 cd && cd.method_57461().method_10550("x") == 5, "CustomData.update(encoder, value) through NbtOps");
            float[] red = class_1767.field_7964.method_7787();
            check(Math.abs(red[0] - 0xB0 / 255.0F) < 1e-6 && Math.abs(red[1] - 0x2E / 255.0F) < 1e-6 && Math.abs(red[2] - 0x26 / 255.0F) < 1e-6,
                "DyeColor.RED.getTextureDiffuseColors: 1.20.6's components");
            check(class_1792.field_8006.equals(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")), "Item.BASE_ATTACK_DAMAGE_UUID");
            check(new class_1799(class_1802.field_8371).method_56704(class_1802.field_8802, 1).method_31574(class_1802.field_8802),
                "ItemStack.transmuteCopyIgnoreEmpty");

            // ---- creative tabs, tags, block math ----
            check("item_search.png".equals(class_7706.method_47344().method_7742()), "the search tab's background suffix: " + class_7706.method_47344().method_7742());
            class_1761 tab = class_1761.method_47307(class_1761.class_7915.field_41049, 0).method_47321(class_2561.method_43470("x"))
                .method_47319("cbtest.png").method_47324();
            check("cbtest.png".equals(tab.method_7742()), "Builder.backgroundSuffix round trip: " + tab.method_7742());
            int[] built = {0};
            tab.method_47308(items -> built[0]++);
            tab.method_47314();
            check(built[0] == 1, "setSearchTreeBuilder + rebuildSearchTree");
            check("tags/block".equals(class_3505.method_40099(class_7924.field_41254)), "TagManager.getTagDir(BLOCK): " + class_3505.method_40099(class_7924.field_41254));
            check(class_4609.method_23221(class_4590.method_22931(), class_2350.field_11043, () -> "never") != null, "BlockMath.getUVLockTransform");
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            check(false, "threw " + e);
        }
        System.out.println("CB-USETEST DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}
