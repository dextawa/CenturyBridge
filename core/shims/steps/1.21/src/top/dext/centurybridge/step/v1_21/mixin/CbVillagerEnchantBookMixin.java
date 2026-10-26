package top.dext.centurybridge.step.v1_21.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_2378;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6885;
import net.minecraft.class_7699;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.facade.class_1887;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantBookTrades;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * VillagerTrades.EnchantBookForTrades (class_3853$class_1648) and its pick. 1.20.6 kept the offerable enchantments
 * as a list built from Enchantments fields and picked one in getRandomEnchantment(random, features) (method_59891):
 * filter by the world's features, then list.get(random.nextInt(size)). 1.21 keeps an enchantment tag
 * (EnchantmentTags.TRADES_*, data/minecraft/tags/enchantment/...) and picks with Registry.getRandomElementOf(tag,
 * random). This mixin restores method_59891 over the tag (the tag's enchantments, in its order, as the 1.20.6 objects
 * mods see) and routes 1.21's pick in getOffer (method_7246) through it, so a mod's 1.20.6 mixin on the pick decides
 * the book as on 1.20.6. With nothing mixed in, the answer is 1.21's: the same tag members in the same order, one
 * random.nextInt(size) draw, none for an empty tag. The route (getOffer's pick) is CbVillagerEnchantBookMixinHooks
 * (default priority).
 */
@Mixin(targets = "net.minecraft.class_3853$class_1648", priority = CbVanillaReads.PRIORITY)
public abstract class CbVillagerEnchantBookMixin implements CbEnchantBookTrades.Explicit, CbPrioritySplit.EnchantBookPick {

    @Shadow
    @Final
    private class_6862<net.minecraft.class_1887> field_45131;

    /** the world's enchantment registry for the pick in progress (getOffer's), else the thread's world's */
    @Unique
    private class_2378<net.minecraft.class_1887> cb$registry;

    /**
     * the enchantments a 1.20.6 constructor named explicitly (EnchantBookForEmeralds(xp, enchantments...), through
     * CbEnchantBookTrades' factories), in their order; null for a trade built on 1.21's tag
     */
    @Unique
    private List<class_1887> cb$explicit;

    @Override
    public void cb$setEnchantments(List<class_1887> enchantments) {
        this.cb$explicit = List.copyOf(enchantments);
    }

    /** the tag's members as the 1.20.6 objects (CbEnchantments.cbEnchantment), in the tag's order; a 1.20.6 list, as given */
    @Unique
    private List<class_1887> cb$tradeable() {
        if (this.cb$explicit != null) {
            return this.cb$explicit;
        }
        class_2378<net.minecraft.class_1887> registry = this.cb$registry != null ? this.cb$registry : CbEnchantments.current();
        List<class_1887> out = new ArrayList<>();
        if (registry != null) {
            Optional<class_6885.class_6888<net.minecraft.class_1887>> set = registry.method_40266(this.field_45131);
            if (set.isPresent()) {
                for (int i = 0; i < set.get().method_40247(); i++) {
                    out.add(CbEnchantments.cbEnchantment(set.get().method_40240(i)));
                }
            }
        }
        return out;
    }

    /**
     * the 1.20.6 feature test of the pick: a mod's 1.20.6 enchantment is offered only where its required features are
     * enabled, as on 1.20.6; an enchantment the world's data defines is gated by nothing (1.21 has no feature flags on
     * enchantments), as on 1.21
     */
    @Unique
    private static boolean cb$enabled(class_1887 enchantment, class_7699 features) {
        return enchantment.cb$kind != class_1887.CB_MOD || enchantment.method_45382(features);
    }

    /** 1.20.6 EnchantBookForTrades.getRandomEnchantment(random, features) (class_3853$class_1648.method_59891) */
    @CbVanillaReads(value = "CbVillagerEnchantBookMixinHooks: getOffer (method_7246) takes its book's enchantment from this method "
        + "(cb$pick replaces Registry.getRandomElementOf(tag, random)); the body is 1.20.6's over the tag's members: "
        + "List.stream / Stream.filter / Stream.toList / List.size / RandomSource.nextInt / List.get stand as in 1.20.6, "
        + "the list field (1.21's is the tag) does not",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN", "Inject@INVOKE", "Redirect@INVOKE",
            "ModifyArg@INVOKE", "ModifyExpressionValue@INVOKE", "WrapOperation@INVOKE"})
    public class_1887 method_59891(class_5819 random, class_7699 features) {
        List<class_1887> list = this.cb$tradeable();
        List<class_1887> enabled = list.stream().filter(e -> cb$enabled(e, features)).toList();
        if (enabled.isEmpty()) {
            return null; // 1.21: an empty tag offers no book (1.20.6 never had an empty list)
        }
        return enabled.get(random.method_43048(enabled.size()));
    }

    /** the world's registry for the pick in progress, null after it (CbVillagerEnchantBookMixinHooks) */
    @Override
    public void cb$v1_21$pickRegistry(class_2378<net.minecraft.class_1887> registry) {
        this.cb$registry = registry;
    }

    /** 1.20.6's pick called virtually, for getOffer (CbVillagerEnchantBookMixinHooks) */
    @Override
    public class_1887 cb$v1_21$pick(class_5819 random, class_7699 features) {
        return this.method_59891(random, features);
    }
}
