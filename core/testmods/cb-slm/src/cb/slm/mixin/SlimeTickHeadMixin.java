package cb.slm.mixin;

import cb.slm.Count;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a HEAD inject on the same dropped override, alone in its mixin: it needs its own restored Slime.tick */
@Mixin(Slime.class)
public abstract class SlimeTickHeadMixin {
    @Inject(method = "tick()V", at = @At("HEAD"))
    private void cbslm$head(CallbackInfo ci) {
        Count.hit("convHead", this);
    }
}
