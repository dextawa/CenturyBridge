package cb.enchtest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1882;
import net.minecraft.class_1887;
import net.minecraft.class_1889;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1900;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3489;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7699;
import net.minecraft.class_7701;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.6-era server mod (intermediary names, Fabric Loader API only) for the enchantment bridge of the
 * 1.20.6 -> 1.21 step. At start-up it registers two Enchantment subclasses in
 * BuiltInRegistries.ENCHANTMENT, as a 1.20.6 mod did: "cbenchtest:test" (a sword enchantment whose
 * getDamageBonus is 3 per level, incompatible with sharpness, that also accepts a stick, logs its
 * doPostAttack and names itself literally) and "cbenchtest:guard" (a ProtectionEnchantment subclass
 * whose getDamageProtection is 4 per level). Once the server runs it checks, on the server thread:
 * the 1.20.6 API as the mod calls it (stack.enchant, EnchantmentHelper's statics, the registry, the
 * vanilla enchantment objects and their classes), and 1.21's own enchantment code -- reached by
 * reflection on 1.21's methods, which no converter touches: what it sees is what 1.21 code sees --
 * answering with the mod's overrides (modifyDamage, getDamageProtection, areCompatible, getFullname,
 * canEnchant, the enchanting-table tag and selection, doPostAttackEffects). Every check prints one
 * "CB-ENCHTEST ok|FAIL ..." line; the last line is "CB-ENCHTEST DONE &lt;passed&gt;/&lt;checks&gt;".
 *
 * <p>Build against 1.20.6 (any fabric-loader jar for the API):
 * <pre>
 *   javac --release 21 -cp "client-1.20.6-intermediary.jar:fabric-loader.jar:&lt;1.20.6 libraries&gt;" -d out \
 *         core/testmods/cb-enchtest/src/cb/enchtest/*.java
 *   cp core/testmods/cb-enchtest/res/fabric.mod.json out/ &amp;&amp; (cd out &amp;&amp; jar cf cb-enchtest.jar .)
 * </pre>
 */
public class EnchTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;
    static volatile int postAttacks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-ENCHTEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    /** a sword enchantment: 3 damage per level, not with sharpness, also on a stick, a literal name */
    public static class TestEnchantment extends class_1887 {
        public TestEnchantment() {
            super(class_1887.method_58442(class_3489.field_48304, 10, 3, class_1887.method_58441(1, 10), class_1887.method_58441(51, 10), 1,
                class_1304.field_6173));
        }

        @Override
        public float method_8196(int level, class_1299<?> type) {
            return level * 3.0F;
        }

        @Override
        protected boolean method_8180(class_1887 other) {
            return super.method_8180(other) && other != class_1893.field_9118;
        }

        @Override
        public boolean method_8192(class_1799 stack) {
            return super.method_8192(stack) || stack.method_31574(class_1802.field_8600);
        }

        @Override
        public void method_8189(class_1309 attacker, class_1297 target, int level) {
            postAttacks++;
            System.out.println("CB-ENCHTEST postAttack level " + level);
        }

        @Override
        public class_2561 method_8179(int level) {
            return class_2561.method_43470("CBTEST-NAME-" + level);
        }
    }

    /** an armor protection: 4 per level against everything */
    public static class GuardEnchantment extends class_1900 {
        public GuardEnchantment() {
            super(class_1887.method_58442(class_3489.field_48303, 5, 4, class_1887.method_58441(1, 11), class_1887.method_58441(12, 11), 1,
                class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166), class_1900.class_1901.field_9140);
        }

        @Override
        public int method_8181(int level, class_1282 source) {
            return level * 4;
        }
    }

    static class_1887 TEST;
    static class_1887 GUARD;

    @Override
    public void onInitialize() {
        TEST = (class_1887) class_2378.method_10230(class_7923.field_41176, class_2960.method_43902("cbenchtest", "test"), new TestEnchantment());
        GUARD = (class_1887) class_2378.method_10230(class_7923.field_41176, class_2960.method_43902("cbenchtest", "guard"), new GuardEnchantment());
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
        }, "cb-enchtest");
        t.setDaemon(true);
        t.start();
    }

    static Method method(Class<?> c, String name, int params) {
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == params) {
                m.setAccessible(true);
                return m;
            }
        }
        throw new IllegalStateException("no " + c.getName() + "." + name + "/" + params);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void run(MinecraftServer server) {
        try {
            class_3218 level = server.method_30002();
            // ---- the 1.20.6 API ----
            check(class_7923.field_41176.method_10221(TEST).equals(class_2960.method_43902("cbenchtest", "test")), "registry name of the mod enchantment");
            check(class_1893.field_9118 instanceof class_1882, "Enchantments.SHARPNESS is a DamageEnchantment");
            check(class_1893.field_9118.method_8183() == 5, "Enchantments.SHARPNESS.getMaxLevel() == 5");
            check(!TEST.method_8188(class_1893.field_9118), "test incompatible with sharpness (1.20.6 isCompatibleWith)");
            check(TEST.method_8188(class_1893.field_9121), "test compatible with knockback");
            class_1799 sword = new class_1799(class_1802.field_8802);
            sword.method_7978(TEST, 2);
            check(class_1890.method_8225(TEST, sword) == 2, "EnchantmentHelper.getItemEnchantmentLevel == 2");
            check(sword.method_58657().method_57536(TEST) == 2, "ItemEnchantments.getLevel == 2");
            boolean found = false;
            for (Object h : sword.method_58657().method_57534()) {
                found |= ((class_6880) h).comp_349() == TEST;
            }
            check(found, "ItemEnchantments.keySet() holds the mod's object");
            check(class_1890.method_8218(sword, class_1299.field_6051) == 6.0F, "EnchantmentHelper.getDamageBonus == 6");
            check(new class_1889(TEST, 1).field_9093 == TEST, "EnchantmentInstance.enchantment");
            class_7699 flags = class_7701.field_40182;
            boolean offered = false;
            for (class_1889 inst : class_1890.method_8229(flags, 30, new class_1799(class_1802.field_8802), false)) {
                offered |= inst.field_9093 == TEST;
            }
            check(offered, "1.20.6 getAvailableEnchantmentResults offers the mod enchantment on a sword");

            // ---- 1.21's own code, reached by reflection ----
            class_2378 world = server.method_30611().method_30530(class_7924.field_41265);
            class_2960 id = class_2960.method_43902("cbenchtest", "test");
            check(world.method_10250(id), "the world's enchantment registry has cbenchtest:test");
            Object holder = ((Optional) world.method_40264(class_5321.method_29179(class_7924.field_41265, id))).get();
            Object sharpness = ((Optional) world.method_40264(class_5321.method_29179(class_7924.field_41265,
                class_2960.method_43902("minecraft", "sharpness")))).get();
            Object knockback = ((Optional) world.method_40264(class_5321.method_29179(class_7924.field_41265,
                class_2960.method_43902("minecraft", "knockback")))).get();
            Class<?> enchantment = Class.forName("net.minecraft.class_1887");
            Class<?> helper = Class.forName("net.minecraft.class_1890");
            Method areCompatible = method(enchantment, "method_60033", 2);
            check(!(boolean) areCompatible.invoke(null, holder, sharpness), "1.21 areCompatible(test, sharpness) == false");
            check((boolean) areCompatible.invoke(null, holder, knockback), "1.21 areCompatible(test, knockback) == true");
            Object name = method(enchantment, "method_8179", 2).invoke(null, holder, 2);
            check(((class_2561) name).getString().equals("CBTEST-NAME-2"), "1.21 getFullname answers the mod's name: " + ((class_2561) name).getString());
            Object record = ((class_6880) holder).comp_349();
            check((boolean) method(enchantment, "method_8192", 1).invoke(record, new class_1799(class_1802.field_8600)),
                "1.21 canEnchant(stick) answers the mod's override");
            Field inTable = Class.forName("net.minecraft.class_9636").getField("field_51547");
            check((boolean) method(class_6880.class, "method_40220", 1).invoke(holder, inTable.get(null)), "in #minecraft:in_enchanting_table");
            Optional tag = (Optional) world.method_40266((net.minecraft.class_6862) inTable.get(null));
            Stream tableStream = tag.isPresent() ? java.util.stream.StreamSupport.stream(((Iterable) tag.get()).spliterator(), false) : Stream.empty();
            List results = (List) method(helper, "method_8229", 3).invoke(null, 30, new class_1799(class_1802.field_8802), tableStream);
            boolean tableOffers = false;
            for (Object r : results) {
                Field f = r.getClass().getField("field_9093");
                tableOffers |= f.get(r) == holder;
            }
            check(tableOffers, "1.21 enchanting selection offers cbenchtest:test on a sword");

            class_1309 attacker = (class_1309) class_1299.field_6051.method_5883(level);
            class_1309 target = (class_1309) class_1299.field_6051.method_5883(level);
            attacker.method_5814(0, 100, 0);
            target.method_5814(1, 100, 0);
            attacker.method_5673(class_1304.field_6173, sword);
            class_1282 source = attacker.method_48923().method_48812(attacker);
            float damage = (float) method(helper, "method_60120", 5).invoke(null, level, sword, target, source, 0.0F);
            check(damage == 6.0F, "1.21 modifyDamage adds the mod's bonus: " + damage);
            int before = postAttacks;
            method(helper, "method_60107", 3).invoke(null, level, target, source);
            check(postAttacks > before, "1.21 doPostAttackEffects runs the mod's doPostAttack (" + (postAttacks - before) + "x)");
            class_1799 chest = new class_1799(class_1802.field_8058);
            chest.method_7978(GUARD, 2);
            target.method_5673(class_1304.field_6174, chest);
            float protection = (float) method(helper, "method_8219", 3).invoke(null, level, target, source);
            check(protection == 8.0F, "1.21 getDamageProtection adds the mod's protection: " + protection);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            check(false, "threw " + e);
        }
        System.out.println("CB-ENCHTEST DONE " + (checks - FAILS.size()) + "/" + checks);
    }
}
