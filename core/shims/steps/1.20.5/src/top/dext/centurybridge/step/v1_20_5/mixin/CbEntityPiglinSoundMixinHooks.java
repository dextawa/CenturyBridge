package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * The route into 1.20.5's own Piglin.playAngrySound that goes with {@link CbEntityPiglinSoundMixin} (its restored
 * 1.20.4 makeSound stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can anchor inside it).
 */
@Mixin(net.minecraft.class_4836.class)
public abstract class CbEntityPiglinSoundMixinHooks {

    /**
     * 1.20.4's playAngrySound was makeSound(PIGLIN_ANGRY); 1.20.5's calls Entity.makeSound with the same sound, which plays
     * it at the same volume and pitch. With a converted mod mixing the restored member, the sound goes through it.
     */
    @Inject(method = "method_30238", at = @At("HEAD"), cancellable = true)
    private void cbRoute$angrySound(CallbackInfo ci) {
        if (CbRoutes.route(CbRoutes.PIGLIN_MAKE_SOUND, this)) {
            ((CbPrioritySplit.PiglinSound) (Object) this).cb$v1_20_5$makeSound(net.minecraft.class_3417.field_22272);
            ci.cancel();
        }
    }
}
