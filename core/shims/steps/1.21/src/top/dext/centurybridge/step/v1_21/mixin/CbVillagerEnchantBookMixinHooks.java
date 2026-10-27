package top.dext.centurybridge.step.v1_21.mixin;

import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1914;
import net.minecraft.class_2378;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7699;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.facade.class_1887;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;

/**
 * The route into 1.21's own EnchantBookForTrades.getOffer that goes with {@link CbVillagerEnchantBookMixin} (its
 * restored 1.20.6 getRandomEnchantment stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.6 injectors can
 * anchor inside it): getOffer's tag pick goes through the restored pick, over the trader's world's registry and features.
 */
@Mixin(targets = "net.minecraft.class_3853$class_1648")
public abstract class CbVillagerEnchantBookMixinHooks {

    /** the trader's world's features for the pick in progress */
    @Unique
    private class_7699 cb$features;

    @Inject(method = "method_7246(Lnet/minecraft/class_1297;Lnet/minecraft/class_5819;)Lnet/minecraft/class_1914;", at = @At("HEAD"))
    private void cb$beginOffer(class_1297 trader, class_5819 random, CallbackInfoReturnable<class_1914> cir) {
        ((CbPrioritySplit.EnchantBookPick) (Object) this).cb$v1_21$pickRegistry(
            trader.method_37908().method_30349().method_30530(net.minecraft.class_7924.field_41265));
        this.cb$features = trader.method_37908().method_45162();
    }

    /** 1.21's tag pick, routed through the restored 1.20.6 pick */
    @Redirect(method = "method_7246(Lnet/minecraft/class_1297;Lnet/minecraft/class_5819;)Lnet/minecraft/class_1914;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2378;method_56159(Lnet/minecraft/class_6862;Lnet/minecraft/class_5819;)Ljava/util/Optional;"))
    private Optional<class_6880<net.minecraft.class_1887>> cb$pick(class_2378<net.minecraft.class_1887> registry, class_6862<net.minecraft.class_1887> tag,
            class_5819 random) {
        CbPrioritySplit.EnchantBookPick pick = (CbPrioritySplit.EnchantBookPick) (Object) this;
        try {
            class_1887 picked = pick.cb$v1_21$pick(random, this.cb$features != null ? this.cb$features : net.minecraft.class_7701.field_40183);
            return picked == null ? Optional.empty() : Optional.ofNullable(CbEnchantments.cbHolder(picked));
        } finally {
            pick.cb$v1_21$pickRegistry(null);
            this.cb$features = null;
        }
    }
}
