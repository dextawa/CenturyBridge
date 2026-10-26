package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallInteract;

/**
 * 1.21.11's second interaction step on an armor stand (CbWallInteractArmorStandMixin, rt/CbWallInteract): for an
 * armor stand of a 1.21.11 class, or under a mod's call of the old interact/interactOn, 26.1's ArmorStand.interact
 * answers LivingEntity's, as 1.21.11's ArmorStand had no interact of its own. Default priority: injector into a 26.1
 * method.
 */
@Mixin(ArmorStand.class)
public abstract class CbWallInteractArmorStandReadsMixin extends LivingEntity {

    protected CbWallInteractArmorStandReadsMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void cb$secondStep(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (CbWallInteract.armorStandSecondStep((ArmorStand) (Object) this)) {
            cir.setReturnValue(super.interact(player, hand, location));
        }
    }
}
