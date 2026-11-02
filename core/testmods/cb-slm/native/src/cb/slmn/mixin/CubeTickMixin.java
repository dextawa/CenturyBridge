package cb.slmn.mixin;

import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** the native 26.2 control: the same squish write, in AbstractCubeMob.tick; fires for every cube mob, layer or not */
@Mixin(AbstractCubeMob.class)
public abstract class CubeTickMixin {
    @Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;squish:F", opcode = Opcodes.PUTFIELD))
    private void cbslmn$squish(CallbackInfo ci) {
        System.getProperties().merge("cb.slm.native." + this.getClass().getSimpleName(), 1, (a, b) -> (Integer) a + (Integer) b);
    }
}
