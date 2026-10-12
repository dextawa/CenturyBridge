package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.compat.fapi.entity.event.v1.ServerLivingEntityEvents;

/** Fabric API's MOB_CONVERSION when a villager struck by lightning becomes a witch (1.20.1's VillagerEntityMixin; see CbFapiPigConversion). */
@Mixin(net.minecraft.class_1646.class)
abstract class CbFapiVillagerConversion {

    @ModifyArg(method = "method_5800", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3218;method_30771(Lnet/minecraft/class_1297;)V"))
    private net.minecraft.class_1297 cb$afterWitchConversion(net.minecraft.class_1297 converted) {
        ServerLivingEntityEvents.MOB_CONVERSION.invoker().onConversion((net.minecraft.class_1308) (Object) this,
            (net.minecraft.class_1308) converted, false);
        return converted;
    }
}
