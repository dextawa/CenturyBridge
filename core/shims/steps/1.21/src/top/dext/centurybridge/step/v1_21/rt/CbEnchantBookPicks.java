package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;

/**
 * Where this layer's 1.20.6 enchanted-book pick ({@link CbEnchantBookPick}) is installed for a later layer that serves
 * VillagerTrades.EnchantBookForEmeralds itself. Up to 1.21.11 this layer's own mixins on class_3853$class_1648 pick
 * (CbVillagerEnchantBookMixin) and nothing reads the holder. At 1.21.11 -> 26.1 the class is gone and the 26.1 layer's
 * facade serves it: this class and {@link Hook} retire there and the wall's classRenames send this layer's references
 * to the 26.1 layer's rt/CbWallEnchantBookPicks and its Hook, so {@link #install}'s call reaches the 26.1 holder
 * directly (same members, same descriptors once converted).
 */
public final class CbEnchantBookPicks {
    private CbEnchantBookPicks() {
    }

    private static volatile Hook hook;

    public static void install(Hook pick) {
        hook = pick;
    }

    public static Hook hook() {
        return hook;
    }

    /** 1.20.6's pick over this layer's enchantment objects (26.1: CbWallEnchantBookPicks.Hook) */
    public interface Hook {
        Object cb$pick(List explicit, net.minecraft.class_6862<net.minecraft.class_1887> tag,
                       net.minecraft.class_2378<net.minecraft.class_1887> registry, net.minecraft.class_5819 random,
                       net.minecraft.class_7699 features);

        net.minecraft.class_6880<net.minecraft.class_1887> cb$holder(Object picked);
    }
}
