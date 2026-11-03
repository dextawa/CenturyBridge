package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * The pick of 1.21.11's EnchantBookForEmeralds facade (facade/VillagerTrades$EnchantBookForEmeralds) on 26.1, where a
 * climbing 1.21 layer gives back 1.20.6's getRandomEnchantment (method_59891) and its explicit enchantment lists. The
 * 1.21 layer's rt/CbEnchantBookPicks and its Hook / Explicit interfaces become these at the wall (classRenames), so the
 * 1.21 layer installs its pick here with its own call (rt/CbEnchantBookPick, from its BuiltInRegistries.createContents
 * mixin). Without a 1.21 layer nothing is installed and the facade picks as 1.21.11 did (getRandomElementOf).
 */
public final class CbWallEnchantBookPicks {
    private CbWallEnchantBookPicks() {
    }

    /** the climbing 1.21 layer's pick, or null when no 1.21 layer runs */
    private static volatile Hook hook;

    /** the 1.21 layer's CbEnchantBookPicks.install, from its createContents mixin */
    public static void install(Hook pick) {
        hook = pick;
    }

    public static Hook hook() {
        return hook;
    }

    /** the 1.21 layer's CbEnchantBookPicks.Hook: 1.20.6's pick over its own enchantment objects */
    public interface Hook {
        /**
         * 1.20.6's getRandomEnchantment: one of {@code explicit} (a 1.20.6 constructor's list, in its order) or else of
         * the tag's members in the registry's order (the thread's world's registry when {@code registry} is null),
         * filtered by the features, one {@code random.nextInt(size)} draw; null for none. The answer is the 1.21
         * layer's enchantment object (top/dext/centurybridge/step/v1_21/facade/class_1887)
         */
        Object cb$pick(List explicit, TagKey<Enchantment> tag, Registry<Enchantment> registry, RandomSource random, FeatureFlagSet features);

        /**
         * 1.20.6's offerable list: {@code explicit} when given, else the tag's members in the registry's order (the
         * thread's world's registry when {@code registry} is null), as the 1.21 layer's enchantment objects
         */
        List cb$tradeable(List explicit, TagKey<Enchantment> tag, Registry<Enchantment> registry);

        /** the registry holder of a {@link #cb$pick} answer */
        Holder<Enchantment> cb$holder(Object picked);
    }

    /** the 1.21 layer's CbEnchantBookTrades.Explicit: a trade carrying a 1.20.6 constructor's enchantment list */
    public interface Explicit {
        /** the list's elements are the 1.21 layer's enchantment objects */
        void cb$setEnchantments(List enchantments);

        /** 1.20.6's offerable list (the 1.21 layer's CbEnchantBookTrades$Explicit.cb$tradeable, s121h): elements are the 1.21 layer's objects */
        List cb$tradeable();
    }
}
