package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.compat.fapi.entity.event.v1.ServerLivingEntityEvents;

/**
 * Fabric API's MOB_CONVERSION (1.20.1's MobEntityMixin): fired as MobEntity.convertTo spawns the
 * converted mob, just before it enters the world, with convertTo's keepEquipment. 1.20.1's Fabric API
 * modified the spawned entity argument and read keepEquipment from the method's arguments; plain
 * Mixin gives a handler the enclosing method's arguments only through a redirect of the call, which
 * here spawns the entity exactly as the call did.
 */
@Mixin(net.minecraft.class_1308.class)
abstract class CbFapiMobConversion {

    @Redirect(method = "method_29243", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1937;method_8649(Lnet/minecraft/class_1297;)Z"))
    private boolean cb$afterEntityConverted(net.minecraft.class_1937 world, net.minecraft.class_1297 converted,
                                            net.minecraft.class_1299<?> type, boolean keepEquipment) {
        ServerLivingEntityEvents.MOB_CONVERSION.invoker().onConversion((net.minecraft.class_1308) (Object) this,
            (net.minecraft.class_1308) converted, keepEquipment);
        return world.method_8649(converted);
    }
}
