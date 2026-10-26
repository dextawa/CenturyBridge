package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_2.rt.CbS26BodyArmor;

/**
 * AbstractHorse.equipBodyArmor: 26.1.2 called setBodyArmorItem(stack.consumeAndReturn(1, player)) where 26.2 calls
 * setItemSlotAndDropWhenKilled(BODY, ..) with the same stack (javap of both). The restored member is asked there only
 * when routed (rt/CbS26BodyArmor.route); otherwise 26.2's call is made unchanged. Priority 500, below a native
 * mod's @Redirect of the same call (that one wins and the route stands down for it).
 */
@Mixin(value = AbstractHorse.class, priority = 500)
public abstract class CbS26BodyArmorHorseRouteMixin {
    @Redirect(method = "equipBodyArmor(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/AbstractHorse;setItemSlotAndDropWhenKilled(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private void cbS26$bodyArmor(AbstractHorse self, EquipmentSlot slot, ItemStack stack) {
        CbS26BodyArmor.bodyArmorCall(self, slot, stack);
    }
}
