package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_3414;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's ItemStack.getDrinkingSound() / getEatingSound(), restored with 1.21.1's bodies: the item's
 * getDrinkingSound() / getEatingSound() (restored on Item by CbItemMembersRestoredMixin; called through
 * CbItemMembers.ItemSounds, the compile stub's Item having no such method). Consumable.emitParticlesAndSounds plays
 * them where they answer otherwise than 1.21.1's Item body (CbItemConsumeSoundMixin). Priority below every mod's
 * (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1799.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemStackSoundsMixin implements CbItemMembers.StackSounds {

    /** getDrinkingSound() */
    @CbVanillaReads("CbItemConsumeSoundMixin: Consumable.emitParticlesAndSounds plays it for a drinking animation where it answers "
        + "otherwise than 1.21.1's Item body")
    @Override
    public class_3414 method_21832() {
        return ((CbItemMembers.ItemSounds) (Object) ((class_1799) (Object) this).method_7909()).method_21831();
    }

    /** getEatingSound() */
    @CbVanillaReads("CbItemConsumeSoundMixin: Consumable.emitParticlesAndSounds plays it for an eating animation where it answers "
        + "otherwise than 1.21.1's Item body")
    @Override
    public class_3414 method_21833() {
        return ((CbItemMembers.ItemSounds) (Object) ((class_1799) (Object) this).method_7909()).method_21830();
    }
}
