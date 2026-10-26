package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_3414;
import net.minecraft.class_4761;
import net.minecraft.class_4763;
import net.minecraft.class_4967;
import net.minecraft.class_4968;
import net.minecraft.class_5195;
import net.minecraft.class_6012;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbBiomeEffects;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's BiomeSpecialEffects.Builder setters 1.21.11 dropped (their settings are biome environment attributes now,
 * rt/CbBiomeEffects), restored with 1.21.10's bodies on the kept settings; build() hands them to the effects it builds.
 * 1.21.11's own biomes set those attributes on the Biome.Builder and never call these: a mod's mixin on them changes
 * only the biomes converted mods build.
 */
@Mixin(value = class_4763.class_4764.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbBiomeEffectsBuilderMixin {
    @Unique
    private final CbBiomeEffects.Settings cb$settings = new CbBiomeEffects.Settings();

    @Unique
    private class_4763.class_4764 cb$self() {
        return (class_4763.class_4764) (Object) this;
    }

    /** fogColor */
    public class_4763.class_4764 method_24392(int color) {
        this.cb$settings.fogColor = color;
        return this.cb$self();
    }

    /** waterFogColor */
    public class_4763.class_4764 method_24397(int color) {
        this.cb$settings.waterFogColor = color;
        return this.cb$self();
    }

    /** skyColor */
    public class_4763.class_4764 method_30820(int color) {
        this.cb$settings.skyColor = color;
        return this.cb$self();
    }

    /** ambientParticle */
    public class_4763.class_4764 method_24393(class_4761 particle) {
        this.cb$settings.particle = particle;
        return this.cb$self();
    }

    /** ambientLoopSound */
    public class_4763.class_4764 method_24942(class_6880<class_3414> sound) {
        this.cb$settings.loopSound = sound;
        return this.cb$self();
    }

    /** ambientMoodSound */
    public class_4763.class_4764 method_24943(class_4968 settings) {
        this.cb$settings.moodSound = settings;
        return this.cb$self();
    }

    /** ambientAdditionsSound */
    public class_4763.class_4764 method_24944(class_4967 settings) {
        this.cb$settings.additionsSound = settings;
        return this.cb$self();
    }

    /** backgroundMusic(music): 1.21.10 kept it as a weighted list of one (none for null) */
    public class_4763.class_4764 method_27346(class_5195 music) {
        if (music == null) {
            this.cb$settings.music = java.util.Optional.empty();
            return this.cb$self();
        }
        return this.method_65138(class_6012.method_66214(music));
    }

    /** backgroundMusic(weighted list) */
    public class_4763.class_4764 method_65138(class_6012<class_5195> music) {
        this.cb$settings.music = java.util.Optional.of(music);
        return this.cb$self();
    }

    /** backgroundMusicVolume */
    public class_4763.class_4764 method_65137(float volume) {
        this.cb$settings.musicVolume = volume;
        this.cb$settings.musicVolumeSet = true;
        return this.cb$self();
    }

    /** silenceAllBackgroundMusic: backgroundMusic(empty list).backgroundMusicVolume(0) */
    public class_4763.class_4764 method_65136() {
        this.method_65138(class_6012.method_34990());
        return this.method_65137(0.0F);
    }

    @Inject(method = "method_24391", at = @At("RETURN"))
    private void cb$build(CallbackInfoReturnable<class_4763> cir) {
        CbBiomeEffects.attach(cir.getReturnValue(), this.cb$settings);
    }
}
