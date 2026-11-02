package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * 1.20.6's EnchantBookForTrades.getRandomEnchantment(random, features) (class_3853$class_1648.method_59891) over 1.21's
 * enchantment tag: the explicit list a 1.20.6 constructor named (CbEnchantBookTrades), else the tag's members in the
 * registry's order as the 1.20.6 objects mods see; filtered by the features; one random.nextInt(size) draw; null for
 * none. CbVillagerEnchantBookMixin's restored method_59891 makes the same draw inline (a mod's 1.20.6 INVOKE injectors
 * anchor on its List / Stream / RandomSource calls) over {@link #tradeable} and {@link #enabled}; past 1.21.11 the 26.1 layer's
 * EnchantBookForEmeralds facade calls it through {@link CbEnchantBookPicks.Hook}.
 */
public final class CbEnchantBookPick implements CbEnchantBookPicks.Hook {
    public static final CbEnchantBookPick INSTANCE = new CbEnchantBookPick();

    private CbEnchantBookPick() {
    }

    /** the pick; {@code registry} null = the thread's world's (CbEnchantments.current) */
    @SuppressWarnings("unchecked")
    public static class_1887 pick(List<?> explicit, net.minecraft.class_6862<net.minecraft.class_1887> tag,
                                  net.minecraft.class_2378<net.minecraft.class_1887> registry, net.minecraft.class_5819 random,
                                  net.minecraft.class_7699 features) {
        List<class_1887> list = explicit != null ? (List<class_1887>) explicit : tradeable(tag, registry);
        List<class_1887> enabled = list.stream().filter(e -> enabled(e, features)).toList();
        if (enabled.isEmpty()) {
            return null; // 1.21: an empty tag offers no book (1.20.6 never had an empty list)
        }
        return enabled.get(random.method_43048(enabled.size()));
    }

    /** the tag's members as the 1.20.6 objects (CbEnchantments.cbEnchantment), in the tag's order */
    public static List<class_1887> tradeable(net.minecraft.class_6862<net.minecraft.class_1887> tag,
                                              net.minecraft.class_2378<net.minecraft.class_1887> registry) {
        net.minecraft.class_2378<net.minecraft.class_1887> r = registry != null ? registry : CbEnchantments.current();
        List<class_1887> out = new ArrayList<>();
        if (r != null) {
            Optional<net.minecraft.class_6885.class_6888<net.minecraft.class_1887>> set = r.method_40266(tag);
            if (set.isPresent()) {
                for (int i = 0; i < set.get().method_40247(); i++) {
                    out.add(CbEnchantments.cbEnchantment(set.get().method_40240(i)));
                }
            }
        }
        return out;
    }

    /**
     * the 1.20.6 feature test: a mod's 1.20.6 enchantment is offered only where its required features are enabled, as
     * on 1.20.6; an enchantment the world's data defines is gated by nothing (1.21 has no feature flags on enchantments)
     */
    public static boolean enabled(class_1887 enchantment, net.minecraft.class_7699 features) {
        return enchantment.cb$kind != class_1887.CB_MOD || enchantment.method_45382(features);
    }

    @Override
    public Object cb$pick(List explicit, net.minecraft.class_6862<net.minecraft.class_1887> tag,
                          net.minecraft.class_2378<net.minecraft.class_1887> registry, net.minecraft.class_5819 random,
                          net.minecraft.class_7699 features) {
        return pick(explicit, tag, registry, random, features);
    }

    @Override
    public net.minecraft.class_6880<net.minecraft.class_1887> cb$holder(Object picked) {
        return CbEnchantments.cbHolder((class_1887) picked);
    }
}
