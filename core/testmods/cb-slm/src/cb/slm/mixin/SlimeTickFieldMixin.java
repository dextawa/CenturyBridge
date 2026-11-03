package cb.slm.mixin;

import cb.slm.Count;
import net.minecraft.world.entity.monster.Slime;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 26.1.2 mod's inject inside Slime.tick (the squish write at its head). 26.2 dropped Slime's tick override; the body
 * is AbstractCubeMob.tick, which MagmaCube (a Slime in 26.1.2) now shares without being a Slime: the handler must run
 * for slimes and not for magma cubes (reported as a loss).
 */
@Mixin(Slime.class)
public abstract class SlimeTickFieldMixin {
    @Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/Slime;squish:F", opcode = Opcodes.PUTFIELD))
    private void cbslm$squish(CallbackInfo ci) {
        Count.hit("convField", this);
    }
}
