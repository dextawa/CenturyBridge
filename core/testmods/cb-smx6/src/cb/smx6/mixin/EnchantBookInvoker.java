package cb.smx6.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * A 1.20.6 mod building VillagerTrades.EnchantBookForEmeralds (class_3853$class_1648, package-private) through its
 * constructors, the way such a mod adds trades: constructor invokers, typed as the public ItemListing it is.
 */
@Mixin(targets = "net.minecraft.class_3853$class_1648")
public interface EnchantBookInvoker {
    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_3853.class_1652 cbsmx6$new(int villagerXp) {
        throw new AssertionError();
    }

    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_3853.class_1652 cbsmx6$newListed(int villagerXp, net.minecraft.class_1887... enchantments) {
        throw new AssertionError();
    }

    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_3853.class_1652 cbsmx6$newRanged(int villagerXp, int minLevel, int maxLevel, net.minecraft.class_1887... enchantments) {
        throw new AssertionError();
    }
}
