package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbEnchant;

/**
 * Enchantment for a converted 1.20.4 one (rt/CbEnchant): the instance keeps the 1.20.4 arguments its
 * definition was made from, and the methods 1.20.5 answers from the definition -- final there, re-opened
 * by the layer's access widener so a mod's override is the one the game calls -- answer by its 1.20.4
 * class: level range, costs, weight and anvil cost (its rarity), the items it applies to (its category)
 * and the items the enchanting table offers it for (1.20.4's table asked the category). An enchantment
 * 1.20.5 made carries nothing and keeps every 1.20.5 answer.
 */
@Mixin(net.minecraft.class_1887.class)
public abstract class CbEnchantmentMixin implements CbEnchant.Carrier {

    @Unique
    private CbEnchant.Legacy cbS4$legacy;

    @Override
    public CbEnchant.Legacy cbS4$legacy() {
        return cbS4$legacy;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cbS4$made(net.minecraft.class_1887.class_9427 definition, CallbackInfo ci) {
        cbS4$legacy = CbEnchant.claim(definition);
    }

    /** getMaxLevel() */
    @Inject(method = "method_8183", at = @At("HEAD"), cancellable = true)
    private void cbS4$maxLevel(CallbackInfoReturnable<Integer> cir) {
        if (cbS4$legacy != null) {
            cir.setReturnValue(CbEnchant.maxLevel((net.minecraft.class_1887) (Object) this));
        }
    }

    /** getMinCost(level) */
    @Inject(method = "method_8182", at = @At("HEAD"), cancellable = true)
    private void cbS4$minCost(int level, CallbackInfoReturnable<Integer> cir) {
        if (cbS4$legacy != null) {
            cir.setReturnValue(CbEnchant.minCost((net.minecraft.class_1887) (Object) this, level));
        }
    }

    /** getMaxCost(level) */
    @Inject(method = "method_20742", at = @At("HEAD"), cancellable = true)
    private void cbS4$maxCost(int level, CallbackInfoReturnable<Integer> cir) {
        if (cbS4$legacy != null) {
            cir.setReturnValue(CbEnchant.maxCost((net.minecraft.class_1887) (Object) this, level));
        }
    }

    /** getWeight() */
    @Inject(method = "method_58445", at = @At("HEAD"), cancellable = true)
    private void cbS4$weight(CallbackInfoReturnable<Integer> cir) {
        if (cbS4$legacy != null) {
            cir.setReturnValue(CbEnchant.weight((net.minecraft.class_1887) (Object) this));
        }
    }

    /** getAnvilCost() */
    @Inject(method = "method_58446", at = @At("HEAD"), cancellable = true)
    private void cbS4$anvilCost(CallbackInfoReturnable<Integer> cir) {
        if (cbS4$legacy != null) {
            cir.setReturnValue(CbEnchant.anvilCost((net.minecraft.class_1887) (Object) this));
        }
    }

    /** canEnchant(stack): reached only where the class leaves it alone, or by super.canEnchant */
    @Inject(method = "method_8192", at = @At("HEAD"), cancellable = true)
    private void cbS4$canEnchant(net.minecraft.class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        if (cbS4$legacy != null) {
            cir.setReturnValue(CbEnchant.canEnchant((net.minecraft.class_1887) (Object) this, stack));
        }
    }

    /** isPrimaryItem(stack) */
    @Inject(method = "method_58444", at = @At("HEAD"), cancellable = true)
    private void cbS4$primary(net.minecraft.class_1799 stack, CallbackInfoReturnable<Boolean> cir) {
        if (cbS4$legacy != null) {
            cir.setReturnValue(CbEnchant.isPrimaryItem((net.minecraft.class_1887) (Object) this, stack));
        }
    }
}
