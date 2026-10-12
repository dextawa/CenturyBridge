package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.facade.class_1310;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;

/**
 * 1.20.4 WitherBoss: it ignored damage from a living entity of its own MobType (UNDEAD) and targeted
 * every living entity that was not UNDEAD. 1.20.5 asks the entity's type for #wither_friends; where the
 * attacker or target (or the wither itself) is of a class declaring a MobType (rt/CbRestMobType), the
 * 1.20.4 comparison decides.
 */
@Mixin(net.minecraft.class_1528.class)
public abstract class CbRestWitherBossMixin {

    @Redirect(method = "method_5643", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1299;method_20210(Lnet/minecraft/class_6862;)Z"))
    private boolean cbRest$friendlyAttacker(net.minecraft.class_1299<?> type, net.minecraft.class_6862<net.minecraft.class_1299<?>> tag,
                                            net.minecraft.class_1282 source, float amount) {
        if (tag == net.minecraft.class_3483.field_48292) {
            net.minecraft.class_1297 attacker = source.method_5529();
            net.minecraft.class_1297 self = (net.minecraft.class_1297) (Object) this;
            if (attacker != null && (CbRestMobType.declared(attacker) != null || CbRestMobType.declared(self) != null)) {
                return attacker instanceof net.minecraft.class_1309 && !(attacker instanceof net.minecraft.class_1657)
                    && CbRestMobType.mobType(attacker) == CbRestMobType.mobType(self);
            }
        }
        return type.method_20210(tag);
    }

    @Redirect(method = "method_6873", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1299;method_20210(Lnet/minecraft/class_6862;)Z"))
    private static boolean cbRest$friendlyTarget(net.minecraft.class_1299<?> type, net.minecraft.class_6862<net.minecraft.class_1299<?>> tag,
                                                 net.minecraft.class_1309 target) {
        if (tag == net.minecraft.class_3483.field_48292) {
            class_1310 declared = CbRestMobType.declared(target);
            if (declared != null) {
                return declared == class_1310.field_6289;
            }
        }
        return type.method_20210(tag);
    }
}
