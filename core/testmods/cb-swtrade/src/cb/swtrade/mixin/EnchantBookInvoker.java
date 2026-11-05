package cb.swtrade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.20.6 EnchantBookForEmeralds (class_3853$class_1648, package-private) constructors, typed as the public ItemListing */
@Mixin(targets = "net.minecraft.class_3853$class_1648")
public interface EnchantBookInvoker {
    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_3853.class_1652 cbswtrade$new(int villagerXp) {
        throw new AssertionError();
    }

    @Invoker(value = "<init>", remap = false)
    static net.minecraft.class_3853.class_1652 cbswtrade$newListed(int villagerXp, net.minecraft.class_1887... enchantments) {
        throw new AssertionError();
    }
}
