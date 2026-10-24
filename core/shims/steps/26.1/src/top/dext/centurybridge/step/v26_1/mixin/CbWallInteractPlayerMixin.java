package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallInteract;

/**
 * 26.1's interactOn(entity, hand, location) -- what both its server (the interact packet) and its client
 * (MultiPlayerGameMode.interact) call -- first runs 1.21.11's interactAt step for an entity of a 1.21.11
 * class and answers it when it consumes the action, as 1.21.11's INTERACT_AT came before INTERACT
 * (rt/CbWallInteract.firstStep).
 */
@Mixin(Player.class)
public abstract class CbWallInteractPlayerMixin {

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    private void cb$interactAtFirst(Entity entity, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult first = CbWallInteract.firstStep((Player) (Object) this, entity, hand, location);
        if (first != null) {
            cir.setReturnValue(first);
        }
    }
}
