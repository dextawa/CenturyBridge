package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockLevel;

/**
 * The dimension type a joining (or respawning) player is told: 1.20.4's ServerPlayer.createCommonSpawnInfo
 * sent level.dimensionTypeId(), so a mod level class overriding it decided the type; 1.20.5 sends
 * level.dimensionTypeRegistration(). For such a level, the registered type of the override's key.
 */
@Mixin(net.minecraft.class_3222.class)
public abstract class CbBlockServerPlayerMixin {

    @Redirect(method = "method_52374", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3218;method_40134()Lnet/minecraft/class_6880;"))
    private net.minecraft.class_6880<net.minecraft.class_2874> cb$legacyDimensionType(net.minecraft.class_3218 level) {
        if (level instanceof CbBlockLevel.LegacyDimensionType legacy) {
            return level.method_30349().method_30530(net.minecraft.class_7924.field_41241).method_40290(legacy.method_44013());
        }
        return level.method_40134();
    }
}
