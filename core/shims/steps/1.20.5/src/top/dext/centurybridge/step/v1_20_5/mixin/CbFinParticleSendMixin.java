package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_2394;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinParticles;

/** ServerLevel.sendParticles of a 1.20.4 entity effect particle type: rt/CbFinParticles.send */
@Mixin(class_3218.class)
public abstract class CbFinParticleSendMixin {

    @Inject(method = "method_14199", at = @At("HEAD"), cancellable = true)
    private <T extends class_2394> void cb$legacyEffect(T options, double x, double y, double z, int count, double dx, double dy, double dz,
                                                        double speed, CallbackInfoReturnable<Integer> cir) {
        if (options instanceof CbFinParticles.LegacyEffect effect) {
            cir.setReturnValue(CbFinParticles.send((class_3218) (Object) this, effect, x, y, z, count, dx, dy, dz, speed));
        }
    }

    @Inject(method = "method_14166", at = @At("HEAD"), cancellable = true)
    private <T extends class_2394> void cb$legacyEffect(class_3222 player, T options, boolean force, double x, double y, double z, int count,
                                                        double dx, double dy, double dz, double speed, CallbackInfoReturnable<Boolean> cir) {
        if (options instanceof CbFinParticles.LegacyEffect effect) {
            cir.setReturnValue(CbFinParticles.send((class_3218) (Object) this, player, effect, force, x, y, z, count, dx, dy, dz, speed));
        }
    }
}
