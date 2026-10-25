package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_10124;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_3414;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * Consumable.emitParticlesAndSounds: the sound of eating / drinking as 1.21.1's LivingEntity asked the stack -- its
 * restored getEatingSound() / getDrinkingSound() (CbItemStackSoundsMixin), where that answers otherwise than 1.21.1's
 * Item body: a 1.21.1 class's override or a mod's mixin (CbItemMembers.consumeSound). Otherwise 1.21.2 reads the
 * component's sound (or an OverrideConsumeSound entity's).
 */
@Mixin(class_10124.class)
public abstract class CbItemConsumeSoundMixin {

    @Shadow
    public abstract net.minecraft.class_1839 comp_3086();

    @ModifyVariable(method = "method_62843", at = @At(value = "STORE"), ordinal = 0)
    private class_3414 cb$sound(class_3414 sound, class_5819 random, class_1309 entity, class_1799 stack, int particles) {
        class_3414 legacy = CbItemMembers.consumeSound(stack, comp_3086());
        return legacy != null ? legacy : sound;
    }
}
