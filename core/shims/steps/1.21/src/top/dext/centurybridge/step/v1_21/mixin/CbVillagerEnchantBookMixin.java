package top.dext.centurybridge.step.v1_21.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1914;
import net.minecraft.class_2378;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7699;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.facade.class_1887;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * VillagerTrades.EnchantBookForTrades (class_3853$class_1648) and its pick. 1.20.6 kept the offerable enchantments
 * as a list built from Enchantments fields and picked one in getRandomEnchantment(random, features) (method_59891):
 * filter by the world's features, then list.get(random.nextInt(size)). 1.21 keeps an enchantment tag
 * (EnchantmentTags.TRADES_*, data/minecraft/tags/enchantment/...) and picks with Registry.getRandomElementOf(tag,
 * random). This mixin restores method_59891 over the tag (the tag's enchantments, in its order, as the 1.20.6 objects
 * mods see) and routes 1.21's pick in getOffer (method_7246) through it, so a mod's 1.20.6 mixin on the pick decides
 * the book as on 1.20.6. With nothing mixed in, the answer is 1.21's: the same tag members in the same order, one
 * random.nextInt(size) draw, none for an empty tag.
 */
@Mixin(targets = "net.minecraft.class_3853$class_1648", priority = 1)
public abstract class CbVillagerEnchantBookMixin {

    @Shadow
    @Final
    private class_6862<net.minecraft.class_1887> field_45131;

    /** the world's enchantment registry for the pick in progress (getOffer's), else the thread's world's */
    @Unique
    private class_2378<net.minecraft.class_1887> cb$registry;

    /** the tag's members as the 1.20.6 objects (CbEnchantments.cbEnchantment), in the tag's order */
    @Unique
    private List<class_1887> cb$tradeable() {
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
    @CbVanillaReads(value = "CbVillagerEnchantBookMixin: getOffer (method_7246) takes its book's enchantment from this method "
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

    @Inject(method = "method_7246(Lnet/minecraft/class_1297;Lnet/minecraft/class_5819;)Lnet/minecraft/class_1914;", at = @At("HEAD"))
    private void cb$beginOffer(class_1297 trader, class_5819 random, CallbackInfoReturnable<class_1914> cir) {
        this.cb$registry = trader.method_37908().method_30349().method_30530(net.minecraft.class_7924.field_41265);
        this.cb$features = trader.method_37908().method_45162();
    }

    @Unique
    private class_7699 cb$features;

    /** 1.21's tag pick, routed through the restored 1.20.6 pick */
    @Redirect(method = "method_7246(Lnet/minecraft/class_1297;Lnet/minecraft/class_5819;)Lnet/minecraft/class_1914;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2378;method_56159(Lnet/minecraft/class_6862;Lnet/minecraft/class_5819;)Ljava/util/Optional;"))
    private Optional<class_6880<net.minecraft.class_1887>> cb$pick(class_2378<net.minecraft.class_1887> registry, class_6862<net.minecraft.class_1887> tag,
            class_5819 random) {
        try {
            class_1887 picked = this.method_59891(random, this.cb$features != null ? this.cb$features : net.minecraft.class_7701.field_40183);
            return picked == null ? Optional.empty() : Optional.ofNullable(CbEnchantments.cbHolder(picked));
        } finally {
            this.cb$registry = null;
            this.cb$features = null;
        }
    }
}
