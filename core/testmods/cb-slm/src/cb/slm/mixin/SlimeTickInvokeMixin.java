package cb.slm.mixin;

import cb.slm.Count;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** an inject at a call inside Slime.tick (getDimensions, which AbstractCubeMob.tick keeps): the guarded-injector path */
@Mixin(Slime.class)
public abstract class SlimeTickInvokeMixin {
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"))
    private void cbslm$dims(CallbackInfo ci) {
        Count.hit("convInvoke", this);
    }
}
