package cb.slm.mixin;

import cb.slm.Count;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * smixin18 negative case: an anchor whose owner is not Slime (super.tick() on Mob; 26.2's AbstractCubeMob.tick calls
 * AgeableMob.tick). Mob is no subclass of AbstractCubeMob, so the owner must not be respelled to it (report: not
 * "anchor owner follows the member up" for this handler).
 */
@Mixin(Slime.class)
public abstract class SlimeTickSuperMixin {
    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;tick()V"))
    private void cbslm$super(CallbackInfo ci) {
        Count.hit("convSuper", this);
    }
}
