package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * Route for 1.20.4's Inventory.hurtArmor(source, damage, slots) (restored by CbEntityInventoryMixin): 1.20.4's
 * Player.hurtArmor / hurtHelmet called it with the armor slot ids ([0, 1, 2, 3] / [3], Inventory's ALL_ARMOR_SLOTS /
 * HELMET_SLOT_ONLY, still in 1.20.5); 1.20.5's call LivingEntity.doHurtEquipment with the same equipment slots. With a
 * converted mod mixing the restored member, they call it instead; unmixed they run as 1.20.5 has them.
 */
@Mixin(value = net.minecraft.class_1657.class, priority = CbRoutes.ROUTE_PRIORITY)
public abstract class CbRouteArmorMixin {

    @Inject(method = "method_6105", at = @At("HEAD"), cancellable = true)
    private void cbRoute$hurtArmor(net.minecraft.class_1282 source, float damage, CallbackInfo ci) {
        if (CbRoutes.route(CbRoutes.INVENTORY_HURT_ARMOR, this)) {
            ((CbRoutes.InventoryHurtArmor) ((net.minecraft.class_1657) (Object) this).method_31548())
                .method_7375(source, damage, net.minecraft.class_1661.field_33767);
            ci.cancel();
        }
    }

    @Inject(method = "method_36977", at = @At("HEAD"), cancellable = true)
    private void cbRoute$hurtHelmet(net.minecraft.class_1282 source, float damage, CallbackInfo ci) {
        if (CbRoutes.route(CbRoutes.INVENTORY_HURT_ARMOR, this)) {
            ((CbRoutes.InventoryHurtArmor) ((net.minecraft.class_1657) (Object) this).method_31548())
                .method_7375(source, damage, net.minecraft.class_1661.field_33768);
            ci.cancel();
        }
    }
}
