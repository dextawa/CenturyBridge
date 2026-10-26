package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * Piglin: 1.20.4's protected makeSound(sound) -- the sound at the mob's volume and voice pitch -- which 1.20.5 dropped
 * (the playAngrySound route: CbEntityPiglinSoundMixinHooks, default priority)
 */
@Mixin(value = net.minecraft.class_4836.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityPiglinSoundMixin extends net.minecraft.class_1309 implements CbPrioritySplit.PiglinSound {

    protected CbEntityPiglinSoundMixin(net.minecraft.class_1299<? extends net.minecraft.class_1309> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /** was makeSound(sound) */
    @CbVanillaReads("CbEntityPiglinSoundMixinHooks (cbRoute$angrySound): playAngrySound makes its sound through it when a converted mod mixes it")
    protected void method_30086(net.minecraft.class_3414 sound) {
        this.method_5783(sound, this.method_6107(), this.method_6017());
    }

    /** 1.20.4's makeSound called virtually, for the playAngrySound route (CbEntityPiglinSoundMixinHooks) */
    @Override
    public void cb$v1_20_5$makeSound(net.minecraft.class_3414 sound) {
        this.method_30086(sound);
    }
}
