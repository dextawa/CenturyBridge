package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/** Piglin: 1.20.4's protected makeSound(sound) -- the sound at the mob's volume and voice pitch -- which 1.20.5 dropped */
@Mixin(net.minecraft.class_4836.class)
public abstract class CbEntityPiglinSoundMixin extends net.minecraft.class_1309 {

    protected CbEntityPiglinSoundMixin(net.minecraft.class_1299<? extends net.minecraft.class_1309> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /** was makeSound(sound) */
    @CbVanillaReads("CbEntityPiglinSoundMixin (cbRoute$angrySound): playAngrySound makes its sound through it when a converted mod mixes it")
    protected void method_30086(net.minecraft.class_3414 sound) {
        this.method_5783(sound, this.method_6107(), this.method_6017());
    }

    /**
     * 1.20.4's playAngrySound was makeSound(PIGLIN_ANGRY); 1.20.5's calls Entity.makeSound with the same sound, which plays
     * it at the same volume and pitch. With a converted mod mixing the restored member, the sound goes through it.
     */
    @Inject(method = "method_30238", at = @At("HEAD"), cancellable = true)
    private void cbRoute$angrySound(CallbackInfo ci) {
        if (CbRoutes.route(CbRoutes.PIGLIN_MAKE_SOUND, this)) {
            this.method_30086(net.minecraft.class_3417.field_22272);
            ci.cancel();
        }
    }
}
