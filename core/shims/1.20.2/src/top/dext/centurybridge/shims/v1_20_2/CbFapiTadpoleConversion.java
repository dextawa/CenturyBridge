package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.compat.fapi.entity.event.v1.ServerLivingEntityEvents;

/** Fabric API's MOB_CONVERSION when a tadpole grows up into a frog (1.20.1's TadpoleEntityMixin; see CbFapiPigConversion). */
@Mixin(net.minecraft.class_7110.class)
abstract class CbFapiTadpoleConversion {

    @ModifyArg(method = "method_41397", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3218;method_30771(Lnet/minecraft/class_1297;)V"))
    private net.minecraft.class_1297 cb$afterGrowingUpToFrog(net.minecraft.class_1297 converted) {
        ServerLivingEntityEvents.MOB_CONVERSION.invoker().onConversion((net.minecraft.class_1308) (Object) this,
            (net.minecraft.class_1308) converted, false);
        return converted;
    }
}
