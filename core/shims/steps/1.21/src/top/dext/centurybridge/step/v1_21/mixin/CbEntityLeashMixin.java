package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityLeash;

/**
 * Entity and leashes (CbEntityLeash): Entity.baseTick's Leashable.tickLeash hands a 1.20.6 subclass that
 * overrides tickLeash() its own tick, as 1.20.6's Mob.tick called this.tickLeash(); Entity.interact records the
 * interacting player, the one 1.20.6 passed to canBeLeashed(player) and 1.21 no longer passes to canBeLeashed().
 */
@Mixin(class_1297.class)
public abstract class CbEntityLeashMixin implements CbEntityLeash.InteractionContext {

    @Unique
    private class_1657 cb$interactingPlayer;

    @Redirect(method = "method_5670()V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9817;method_60965(Lnet/minecraft/class_1297;)V"))
    private void cb$tickLeash(class_1297 entity) {
        CbEntityLeash.baseTickLeash(entity);
    }

    @Inject(method = "method_5688(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;",
            at = @At("HEAD"))
    private void cb$interactionStarts(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        this.cb$interactingPlayer = player;
    }

    @Inject(method = "method_5688(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;",
            at = @At("RETURN"))
    private void cb$interactionEnds(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        this.cb$interactingPlayer = null;
    }

    @Override
    public class_1657 cb$interactingPlayer() {
        return this.cb$interactingPlayer;
    }
}
