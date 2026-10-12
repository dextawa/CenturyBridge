package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.facade.class_1310;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;

/**
 * 1.20.4 AbstractIllager.isAlliedTo(entity): allied to an entity of MobType ILLAGER (when neither is on
 * a team). 1.20.5 asks the entity's type for #illager_friends; an entity whose class declares a MobType
 * (rt/CbRestMobType) is asked that instead.
 */
@Mixin(net.minecraft.class_1543.class)
public abstract class CbRestAbstractIllagerMixin {

    @Redirect(method = "method_5722", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1299;method_20210(Lnet/minecraft/class_6862;)Z"))
    private boolean cbRest$illagerFriend(net.minecraft.class_1299<?> type, net.minecraft.class_6862<net.minecraft.class_1299<?>> tag,
                                         net.minecraft.class_1297 other) {
        if (tag == net.minecraft.class_3483.field_48293) {
            class_1310 declared = CbRestMobType.declared(other);
            if (declared != null) {
                return declared == class_1310.field_6291;
            }
        }
        return type.method_20210(tag);
    }
}
