package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;

/** Hoglin: 1.20.4's protected makeSound(sound) -- the sound at the mob's volume and voice pitch -- which 1.20.5 dropped */
@Mixin(net.minecraft.class_4760.class)
public abstract class CbEntityHoglinSoundMixin extends net.minecraft.class_1309 {

    protected CbEntityHoglinSoundMixin(net.minecraft.class_1299<? extends net.minecraft.class_1309> type, net.minecraft.class_1937 level) {
        super(type, level);
    }

    /** was makeSound(sound) */
    protected void method_30081(net.minecraft.class_3414 sound) {
        this.method_5783(sound, this.method_6107(), this.method_6017());
    }
}
