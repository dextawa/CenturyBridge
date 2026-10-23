package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_486;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EnchantmentScreen.time of 1.21.1: a public tick counter that tickBook advanced once per tick (after the
 * page-flip target update). 1.21.2's tickBook no longer counts; the field is back and counts in the same
 * place, so a mod reading it sees 1.21.1's value.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_486.class)
public abstract class CbRestEnchantmentScreenMixin {

    public int field_2915;

    @Inject(method = "method_2478", at = @At(value = "FIELD", target = "Lnet/minecraft/class_486;field_2914:F", opcode = org.objectweb.asm.Opcodes.PUTFIELD))
    private void cb$countTime(CallbackInfo ci) {
        this.field_2915++;
    }
}
