package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.compat.fapi.entity.event.v1.ServerLivingEntityEvents;

/**
 * Fabric API's MOB_CONVERSION for the conversions that do not go through MobEntity.convertTo, as
 * 1.20.1's Pig/Tadpole/VillagerEntityMixin fired it (keepEquipment false): a pig struck by lightning
 * (zombified piglin), a tadpole growing up (frog), a villager struck by lightning (witch) -- each just
 * before the new mob is spawned.
 */
@Mixin(net.minecraft.class_1452.class)
abstract class CbFapiPigConversion {

    @ModifyArg(method = "method_5800", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3218;method_8649(Lnet/minecraft/class_1297;)Z"))
    private net.minecraft.class_1297 cb$afterPiglinConversion(net.minecraft.class_1297 converted) {
        ServerLivingEntityEvents.MOB_CONVERSION.invoker().onConversion((net.minecraft.class_1308) (Object) this,
            (net.minecraft.class_1308) converted, false);
        return converted;
    }
}
