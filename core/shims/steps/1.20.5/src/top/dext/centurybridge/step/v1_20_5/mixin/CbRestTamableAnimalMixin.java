package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 TamableAnimal.reassessTameGoals(): a protected hook, empty on TamableAnimal, that every
 * setTame(tame) ran at its end -- Cat re-registered its avoid-players goal there, mods their own goals.
 * 1.20.5 removed it from TamableAnimal (Cat keeps its own and calls it from its setTame override) and
 * runs applyTamingSideEffects() instead, only when the caller asks for side effects (not when an entity
 * loads). The hook comes back with its 1.20.4 body, so a mod's override binds to it and a mod's
 * super.reassessTameGoals() reaches it, and 1.20.5's setTame(tame, sideEffects) runs it at its end as
 * 1.20.4's setTame did -- except on a cat, whose own setTame override already runs it
 * (CbRestTamableAnimalMixinHooks, default priority).
 */
@Mixin(value = net.minecraft.class_1321.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestTamableAnimalMixin implements CbPrioritySplit.TameGoals {

    /** was reassessTameGoals(): 1.20.4's body */
    protected void method_6175() {
    }

    /** 1.20.4's reassessTameGoals called virtually, for 1.20.5's setTame (CbRestTamableAnimalMixinHooks) */
    @Override
    public void cb$v1_20_5$reassessTameGoals() {
        this.method_6175();
    }
}
