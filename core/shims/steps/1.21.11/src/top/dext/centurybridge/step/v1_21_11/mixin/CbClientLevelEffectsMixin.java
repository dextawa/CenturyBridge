package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.facade.DimensionSpecialEffects;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's ClientLevel.effects() (method_28103), restored: the DimensionSpecialEffects for the level's dimension type,
 * chosen once when the level is made, as 1.21.10's constructor did.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_638.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientLevelEffectsMixin {
    @Unique
    private DimensionSpecialEffects cb$effects;

    @Inject(method = "<init>(Lnet/minecraft/class_634;Lnet/minecraft/class_638$class_5271;Lnet/minecraft/class_5321;Lnet/minecraft/class_6880;IILnet/minecraft/class_761;ZJI)V", at = @At("RETURN"))
    private void cb$chooseEffects(CallbackInfo ci) {
        this.cb$effects = DimensionSpecialEffects.method_28111(((class_638) (Object) this).method_8597());
    }

    public DimensionSpecialEffects method_28103() {
        return this.cb$effects;
    }
}
