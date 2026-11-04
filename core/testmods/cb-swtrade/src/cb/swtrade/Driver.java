package cb.swtrade;

import net.minecraft.class_1299;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_3218;

/**
 * cb-swtrade: a 1.20.6-era mod building VillagerTrades.EnchantBookForEmeralds and SuspiciousStewForEmerald through
 * constructor invokers and calling getRandomEnchantment (method_59891) through an invoker. For seeds 1..8 it prints the
 * book each trade offers ("CBTRADE SEQ <path> <seed> <enchantment:level> <cost>"), so the same jar climbed to 1.21.11 and
 * to 26.1 gives sequences to compare, and checks that the direct pick is the very object the villager's offer carries
 * ("CBTEST PASS|FAIL <name> : <detail>", "CBTEST SUMMARY ok/total").
 */
public final class Driver {
    private static int ok, total;

    private static void check(String name, boolean cond, String detail) {
        total++;
        if (cond) {
            ok++;
        }
        System.out.println("CBTEST " + (cond ? "PASS " : "FAIL ") + name + " : " + detail);
    }

    /** the offer's book: "id:level cost", "none" without an enchanted book */
    private static Object[] offer(class_3218 level, net.minecraft.class_3853.class_1652 trade, long seed) {
        net.minecraft.class_1646 villager = new net.minecraft.class_1646(class_1299.field_6077, level);
        try {
            net.minecraft.class_1914 o = trade.method_7246(villager, net.minecraft.class_5819.method_43049(seed));
            if (o == null) {
                return new Object[] {"null", null};
            }
            int cost = o.method_8246().method_7947();
            if (o.method_8250().method_7909() != class_1802.field_8598) {
                return new Object[] {o.method_8250().method_7909() + " " + cost, null};
            }
            net.minecraft.class_9304 e = net.minecraft.class_1890.method_57532(o.method_8250());
            StringBuilder out = new StringBuilder();
            net.minecraft.class_1887 first = null;
            for (net.minecraft.class_6880<net.minecraft.class_1887> h : e.method_57534()) {
                net.minecraft.class_1887 en = h.comp_349();
                first = first == null ? en : first;
                out.append(net.minecraft.class_7923.field_41176.method_10221(en)).append(':').append(e.method_57536(en));
            }
            return new Object[] {out + " " + cost, first};
        } finally {
            villager.method_31472();
        }
    }

    public static void run(net.minecraft.server.MinecraftServer server) {
        class_3218 level = server.method_30002();
        try {
                // the mod's own enchantment in the world's #minecraft:tradeable (the registry trades pick from), by key
                net.minecraft.class_6862<net.minecraft.class_1887> tradeable = net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41265,
                    new net.minecraft.class_2960("minecraft", "tradeable"));
                net.minecraft.class_2378<net.minecraft.class_1887> world = server.method_30611().method_30530(net.minecraft.class_7924.field_41265);
                java.util.Optional<net.minecraft.class_6885.class_6888<net.minecraft.class_1887>> set = world.method_40266(tradeable);
                boolean modIn = false;
                int size = set.map(net.minecraft.class_6885.class_6888::method_40247).orElse(-1);
                for (int i = 0; i < Math.max(size, 0); i++) {
                    modIn |= set.get().method_40240(i).method_40230().map(k -> k.method_29177().toString().equals("cb_swtrade:steady")).orElse(false);
                }
                System.out.println("CBTRADE TAG tradeable size=" + size + " mod=" + modIn);
                check("tag.modTradeable", modIn, "cb_swtrade:steady in the world's #minecraft:tradeable (" + size + " members)");
                // a datapack-defined enchantment through the 1.20.6 registry: the world's entry as the 1.20.6 object
                net.minecraft.class_1887 mending = net.minecraft.class_7923.field_41176.method_10223(new net.minecraft.class_2960("minecraft", "mending"));
                check("registry.dataEntry", mending != null && mending.method_8183() == 1, "minecraft:mending -> " + mending);
        } catch (Throwable t) {
            check("tag.exception", false, String.valueOf(t));
        }
        try {
            net.minecraft.class_3853.class_1652 listed = cb.swtrade.mixin.EnchantBookInvoker.cbswtrade$newListed(1,
                class_1893.field_9119, class_1893.field_9099, class_1893.field_9101);
            net.minecraft.class_3853.class_1652 any = cb.swtrade.mixin.EnchantBookInvoker.cbswtrade$new(1);
            int same = 0, listedOk = 0, anyOk = 0;
            for (long seed = 1; seed <= 8; seed++) {
                Object[] l = offer(level, listed, seed);
                Object[] a = offer(level, any, seed);
                System.out.println("CBTRADE SEQ listed " + seed + " " + l[0]);
                System.out.println("CBTRADE SEQ tradeable " + seed + " " + a[0]);
                listedOk += l[1] != null && (l[1] == class_1893.field_9119 || l[1] == class_1893.field_9099 || l[1] == class_1893.field_9101) ? 1 : 0;
                anyOk += a[1] != null ? 1 : 0;
                try {
                    net.minecraft.class_1887 direct = ((cb.swtrade.mixin.EnchantBookPickInvoker) listed).cbswtrade$pick(
                        net.minecraft.class_5819.method_43049(seed), level.method_45162());
                    net.minecraft.class_1887 directAny = ((cb.swtrade.mixin.EnchantBookPickInvoker) any).cbswtrade$pick(
                        net.minecraft.class_5819.method_43049(seed), level.method_45162());
                    System.out.println("CBTRADE PICK " + seed + " " + net.minecraft.class_7923.field_41176.method_10221(direct)
                        + " " + net.minecraft.class_7923.field_41176.method_10221(directAny));
                    same += direct == l[1] && directAny == a[1] ? 1 : 0;
                } catch (Throwable t) {
                    if (seed == 1) {
                        check("trade.directPickInvoker", false, String.valueOf(t));
                    }
                }
            }
            check("trade.listed", listedOk == 8, listedOk + "/8 books from the explicit list");
            check("trade.tradeable", anyOk == 8, anyOk + "/8 books from #tradeable");
            check("trade.directPickSame", same == 8, same + "/8 seeds: the direct getRandomEnchantment is the villager's pick (same object)");
            net.minecraft.class_3853.class_1652 stew = cb.swtrade.mixin.StewInvoker.cbswtrade$new(net.minecraft.class_1294.field_5904, 100, 7);
            net.minecraft.class_1646 villager = new net.minecraft.class_1646(class_1299.field_6077, level);
            net.minecraft.class_1914 so = stew.method_7246(villager, net.minecraft.class_5819.method_43049(1L));
            villager.method_31472();
            net.minecraft.class_9298 eff = so == null ? null : so.method_8250().method_57824(net.minecraft.class_9334.field_49652);
            check("trade.stew", so != null && so.method_8250().method_7909() == class_1802.field_8766 && so.method_19279() == 7
                && eff != null && eff.comp_2416().size() == 1 && eff.comp_2416().get(0).comp_1838() == net.minecraft.class_1294.field_5904,
                so == null ? "no offer" : so.method_8250() + " xp " + so.method_19279() + " effects " + eff);
        } catch (Throwable t) {
            check("trade.exception", false, String.valueOf(t));
            t.printStackTrace();
        }
        System.out.println("CBTEST SUMMARY " + ok + "/" + total);
    }
}
