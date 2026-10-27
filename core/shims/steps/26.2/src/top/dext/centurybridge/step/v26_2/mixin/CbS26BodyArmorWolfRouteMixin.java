package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_2.rt.CbS26BodyArmor;

/**
 * Wolf.mobInteract: 26.1.2 called setBodyArmorItem(stack.copyWithCount(1)) when the owner puts wolf armor on an adult
 * wolf, where 26.2 calls setItemSlotAndDropWhenKilled(BODY, ..) with the same stack (javap of both; the only
 * setItemSlotAndDropWhenKilled call in the method). The restored member is asked there only when routed
 * (rt/CbS26BodyArmor.route); otherwise 26.2's call is made unchanged. Priority 500, below a native mod's @Redirect of
 * the same call.
 */
@Mixin(value = Wolf.class, priority = 500)
public abstract class CbS26BodyArmorWolfRouteMixin {
    @Redirect(method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;setItemSlotAndDropWhenKilled(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private void cbS26$bodyArmor(Wolf self, EquipmentSlot slot, ItemStack stack) {
        CbS26BodyArmor.bodyArmorCall(self, slot, stack);
    }
}
