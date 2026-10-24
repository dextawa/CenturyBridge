package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;

/**
 * 1.21.11's WolfSoundVariant was one set of six sounds (ambient, death, growl, hurt, pant, whine) every wolf of
 * the variant used, its steps always SoundEvents.WOLF_STEP; 26.1's has an adult and a baby set of seven (with
 * the step). A 1.21.11 variant is the six sounds and WOLF_STEP for adults and babies alike; its accessors answer
 * the adult set's.
 */
public final class CbWallWolves {

    private CbWallWolves() {
    }

    /** new WolfSoundVariant(ambient, death, growl, hurt, pant, whine) */
    public static WolfSoundVariant create(Holder<SoundEvent> ambient, Holder<SoundEvent> death, Holder<SoundEvent> growl, Holder<SoundEvent> hurt,
        Holder<SoundEvent> pant, Holder<SoundEvent> whine) {
        WolfSoundVariant.WolfSoundSet sounds = new WolfSoundVariant.WolfSoundSet(ambient, death, growl, hurt, pant, whine, SoundEvents.WOLF_STEP);
        return new WolfSoundVariant(sounds, sounds);
    }

    public static Holder<SoundEvent> ambientSound(WolfSoundVariant variant) {
        return variant.adultSounds().ambientSound();
    }

    public static Holder<SoundEvent> deathSound(WolfSoundVariant variant) {
        return variant.adultSounds().deathSound();
    }

    public static Holder<SoundEvent> growlSound(WolfSoundVariant variant) {
        return variant.adultSounds().growlSound();
    }

    public static Holder<SoundEvent> hurtSound(WolfSoundVariant variant) {
        return variant.adultSounds().hurtSound();
    }

    public static Holder<SoundEvent> pantSound(WolfSoundVariant variant) {
        return variant.adultSounds().pantSound();
    }

    public static Holder<SoundEvent> whineSound(WolfSoundVariant variant) {
        return variant.adultSounds().whineSound();
    }
}
