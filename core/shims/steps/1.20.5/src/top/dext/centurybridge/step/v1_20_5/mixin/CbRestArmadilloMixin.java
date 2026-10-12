package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.facade.class_1310;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;

/**
 * 1.20.5's Armadillo is scared by undead entities, asking the entity's type for #undead -- the tag that
 * replaced 1.20.4's MobType.UNDEAD. An entity whose class declares a MobType (rt/CbRestMobType) is
 * undead by that declaration, as everywhere 1.20.5 asks those tags about a live entity.
 */
@Mixin(net.minecraft.class_9069.class)
public abstract class CbRestArmadilloMixin {

    @Redirect(method = "method_55721", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1299;method_20210(Lnet/minecraft/class_6862;)Z"))
    private boolean cbRest$undead(net.minecraft.class_1299<?> type, net.minecraft.class_6862<net.minecraft.class_1299<?>> tag,
                                  net.minecraft.class_1309 entity) {
        if (tag == net.minecraft.class_3483.field_46232) {
            class_1310 declared = CbRestMobType.declared(entity);
            if (declared != null) {
                return declared == class_1310.field_6289;
            }
        }
        return type.method_20210(tag);
    }
}
