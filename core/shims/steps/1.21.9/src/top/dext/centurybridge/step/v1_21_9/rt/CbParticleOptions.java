package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.function.Supplier;
import net.minecraft.class_11978;
import net.minecraft.class_11979;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2398;
import net.minecraft.class_2400;
import net.minecraft.class_9381;

/**
 * Four particle types that were their own options on 1.21.8 (SimpleParticleType) and take options of their own on
 * 1.21.9: DRAGON_BREATH (PowerParticleOption), EFFECT and INSTANT_EFFECT (SpellParticleOption), FLASH
 * (ColorParticleOption). 1.21.8 code reads the field as a SimpleParticleType and passes it as ParticleOptions; it
 * gets a {@link Legacy} type standing for the 1.21.8 particle, and wherever 1.21.9 takes particle options from mods
 * (the client's particle creation, the level particles packet, an area effect cloud's particle: the step layer's
 * mixins) a Legacy becomes the options 1.21.9 spells the 1.21.8 particle with. What 1.21.8's providers drew (javap,
 * both releases): dragon breath unscaled (power 1, the provider's scale(power)), spell particles white and unscaled
 * (1.21.8's SpellParticle kept its default colour; 1.21.9's provider sets the option's colour and scale), the flash
 * white and opaque (the default colour; 1.21.9 sets the option's ARGB colour). A 1.21.9 value is never a Legacy, so
 * vanilla's own options pass unchanged.
 */
public final class CbParticleOptions {
    private CbParticleOptions() {
    }

    /** a 1.21.8 particle type that was its own options, standing for the 1.21.9 options it is spelled with now */
    public static final class Legacy extends class_2400 {
        private final Supplier<class_2394> current;

        Legacy(class_2396<?> now, Supplier<class_2394> current) {
            super(now.method_10299());
            this.current = current;
        }

        /** the 1.21.9 options of the 1.21.8 particle */
        public class_2394 current() {
            return this.current.get();
        }
    }

    public static final class_2400 DRAGON_BREATH = new Legacy(class_2398.field_11216, () -> class_11978.method_74410(class_2398.field_11216, 1.0F));
    public static final class_2400 EFFECT = new Legacy(class_2398.field_11245, () -> class_11979.method_74418(class_2398.field_11245, 1.0F, 1.0F, 1.0F, 1.0F));
    public static final class_2400 INSTANT_EFFECT = new Legacy(class_2398.field_11213, () -> class_11979.method_74418(class_2398.field_11213, 1.0F, 1.0F, 1.0F, 1.0F));
    public static final class_2400 FLASH = new Legacy(class_2398.field_17909, () -> class_9381.method_58256(class_2398.field_17909, -1));

    // ------------------------------------------------------------------ 1.21.8 reads of the fields

    public static class_2400 field_11216() {
        return DRAGON_BREATH;
    }

    public static class_2400 field_11245() {
        return EFFECT;
    }

    public static class_2400 field_11213() {
        return INSTANT_EFFECT;
    }

    public static class_2400 field_17909() {
        return FLASH;
    }

    // ------------------------------------------------------------------ where 1.21.9 takes options

    /** the options 1.21.9 takes for what a mod passed: a Legacy's 1.21.9 options, anything else unchanged */
    public static class_2394 current(class_2394 options) {
        return options instanceof Legacy legacy ? legacy.current() : options;
    }

    // ------------------------------------------------------------------ the three providers whose options changed

    /** DragonBreathParticle.Provider.createParticle(options, ..): 1.21.8 passed the type itself; its particle, unscaled */
    public static class_11978 power(class_2400 options) {
        return current(options) instanceof class_11978 o ? o : class_11978.method_74410(class_2398.field_11216, 1.0F);
    }

    /** SpellParticle.InstantProvider.createParticle(options, ..): white and unscaled, as 1.21.8 drew it */
    public static class_11979 spell(class_2400 options) {
        return current(options) instanceof class_11979 o ? o : class_11979.method_74418(class_2398.field_11213, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** FireworkParticles.FlashProvider.createParticle(options, ..): white and opaque, as 1.21.8 drew it */
    public static class_9381 flash(class_2400 options) {
        return current(options) instanceof class_9381 o ? o : class_9381.method_58256(class_2398.field_17909, -1);
    }

    /** an override of such a provider's 1.21.8 createParticle answers 1.21.9's: the type 1.21.8 passed */
    public static class_2400 legacyPower(class_11978 options) {
        return DRAGON_BREATH;
    }

    public static class_2400 legacySpell(class_11979 options) {
        return options.method_10295() == class_2398.field_11245 ? EFFECT : INSTANT_EFFECT;
    }

    public static class_2400 legacyFlash(class_9381 options) {
        return FLASH;
    }
}
