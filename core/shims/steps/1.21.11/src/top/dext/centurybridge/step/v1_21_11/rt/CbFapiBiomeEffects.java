package top.dext.centurybridge.step.v1_21_11.rt;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.class_12190;
import net.minecraft.class_12197;
import net.minecraft.class_12199;
import net.minecraft.class_12206;
import net.minecraft.class_12212;
import net.minecraft.class_1959;
import net.minecraft.class_3414;
import net.minecraft.class_4761;
import net.minecraft.class_4967;
import net.minecraft.class_4968;
import net.minecraft.class_5195;
import net.minecraft.class_6012;
import net.minecraft.class_6880;

/**
 * Fabric API 0.138's (1.21.10) BiomeModificationContext.EffectsContext setters that 0.141 (1.21.11) removed with the
 * BiomeSpecialEffects fields they wrote: the ambient particle, the ambient loop / mood / additions sounds and the
 * background music. 1.21.11 keeps them as the biome's environment attributes (AMBIENT_PARTICLES, AMBIENT_SOUNDS,
 * BACKGROUND_MUSIC), where 0.141's own EffectsContext writes fog, water fog, sky color and music volume (through its
 * AttributesContext into Biome.attributes). Each restored setter writes the attribute that stands for 0.138's field:
 * <ul>
 * <li>particle: 0.138's Optional -> a list of that one particle, or none (an empty Optional: the biome had none);</li>
 * <li>sounds: 0.138 set each of the three on its own; AMBIENT_SOUNDS holds the three together, so the other two keep the
 * biome's current value, and the additions become a list of the one (or none);</li>
 * <li>music: 0.138's weighted list -> BackgroundMusic as CbBiomeEffects.music does for the builder; an empty Optional
 * (0.138: the biome names no music, the game's situational default plays) takes BACKGROUND_MUSIC off the biome, so the
 * default plays again.</li>
 * </ul>
 * The default methods (Optional.of / Optional.empty / WeightedList.of(music)) are 0.138's bodies, calling the abstract
 * shape. A mod's own EffectsContext implementation (LegacyEffects, through adapterHooks) answers the abstract shapes
 * itself, as on 0.138.
 */
public final class CbFapiBiomeEffects {
    private static final Map<String, Field> FIELDS = new ConcurrentHashMap<>();

    private CbFapiBiomeEffects() {
    }

    /** 0.138's abstract shapes, as a 1.21.10 implementation of EffectsContext declares them */
    public interface LegacyEffects {
        void setParticleConfig(Optional<class_4761> particleConfig);

        void setAmbientSound(Optional<class_6880<class_3414>> sound);

        void setMoodSound(Optional<class_4968> sound);

        void setAdditionsSound(Optional<class_4967> sound);

        void setMusic(Optional<class_6012<class_5195>> music);

        // 0.138's default shapes, with 0.138's bodies: a 1.21.10 implementation's own overrides of them override these

        default void setParticleConfig(class_4761 particleConfig) {
            setParticleConfig(Optional.of(particleConfig));
        }

        default void clearParticleConfig() {
            setParticleConfig(Optional.empty());
        }

        default void setAmbientSound(class_6880<class_3414> sound) {
            setAmbientSound(Optional.of(sound));
        }

        default void clearAmbientSound() {
            setAmbientSound(Optional.empty());
        }

        default void setMoodSound(class_4968 sound) {
            setMoodSound(Optional.of(sound));
        }

        default void clearMoodSound() {
            setMoodSound(Optional.empty());
        }

        default void setAdditionsSound(class_4967 sound) {
            setAdditionsSound(Optional.of(sound));
        }

        default void clearAdditionsSound() {
            setAdditionsSound(Optional.empty());
        }

        default void setMusic(class_6012<class_5195> music) {
            setMusic(Optional.of(music));
        }

        default void setMusic(class_5195 music) {
            setMusic(class_6012.method_66214(music));
        }

        default void clearMusic() {
            setMusic(Optional.empty());
        }
    }

    public static void setParticleConfig(BiomeModificationContext.EffectsContext context, Optional<class_4761> particleConfig) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setParticleConfig(particleConfig);
            return;
        }
        put(biome(context), class_12206.field_63750, particleConfig.<List<class_4761>>map(List::of).orElse(List.of()));
    }

    public static void setParticleConfig(BiomeModificationContext.EffectsContext context, class_4761 particleConfig) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setParticleConfig(particleConfig);
            return;
        }
        setParticleConfig(context, Optional.of(particleConfig));
    }

    /** a 1.21.10 implementation's super.setParticleConfig(..): 0.138's default body */
    public static void superSetParticleConfig(BiomeModificationContext.EffectsContext context, class_4761 particleConfig) {
        setParticleConfig(context, Optional.of(particleConfig));
    }

    public static void clearParticleConfig(BiomeModificationContext.EffectsContext context) {
        if (context instanceof LegacyEffects legacy) {
            legacy.clearParticleConfig();
            return;
        }
        setParticleConfig(context, Optional.empty());
    }

    /** a 1.21.10 implementation's super.clearParticleConfig(..): 0.138's default body */
    public static void superClearParticleConfig(BiomeModificationContext.EffectsContext context) {
        setParticleConfig(context, Optional.empty());
    }

    public static void setAmbientSound(BiomeModificationContext.EffectsContext context, Optional<class_6880<class_3414>> sound) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setAmbientSound(sound);
            return;
        }
        class_1959 biome = biome(context);
        class_12190 now = current(biome, class_12206.field_63753);
        put(biome, class_12206.field_63753, new class_12190(sound, now.comp_5126(), now.comp_5127()));
    }

    public static void setAmbientSound(BiomeModificationContext.EffectsContext context, class_6880<class_3414> sound) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setAmbientSound(sound);
            return;
        }
        setAmbientSound(context, Optional.of(sound));
    }

    /** a 1.21.10 implementation's super.setAmbientSound(..): 0.138's default body */
    public static void superSetAmbientSound(BiomeModificationContext.EffectsContext context, class_6880<class_3414> sound) {
        setAmbientSound(context, Optional.of(sound));
    }

    public static void clearAmbientSound(BiomeModificationContext.EffectsContext context) {
        if (context instanceof LegacyEffects legacy) {
            legacy.clearAmbientSound();
            return;
        }
        setAmbientSound(context, Optional.empty());
    }

    /** a 1.21.10 implementation's super.clearAmbientSound(..): 0.138's default body */
    public static void superClearAmbientSound(BiomeModificationContext.EffectsContext context) {
        setAmbientSound(context, Optional.empty());
    }

    public static void setMoodSound(BiomeModificationContext.EffectsContext context, Optional<class_4968> sound) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setMoodSound(sound);
            return;
        }
        class_1959 biome = biome(context);
        class_12190 now = current(biome, class_12206.field_63753);
        put(biome, class_12206.field_63753, new class_12190(now.comp_5125(), sound, now.comp_5127()));
    }

    public static void setMoodSound(BiomeModificationContext.EffectsContext context, class_4968 sound) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setMoodSound(sound);
            return;
        }
        setMoodSound(context, Optional.of(sound));
    }

    /** a 1.21.10 implementation's super.setMoodSound(..): 0.138's default body */
    public static void superSetMoodSound(BiomeModificationContext.EffectsContext context, class_4968 sound) {
        setMoodSound(context, Optional.of(sound));
    }

    public static void clearMoodSound(BiomeModificationContext.EffectsContext context) {
        if (context instanceof LegacyEffects legacy) {
            legacy.clearMoodSound();
            return;
        }
        setMoodSound(context, Optional.empty());
    }

    /** a 1.21.10 implementation's super.clearMoodSound(..): 0.138's default body */
    public static void superClearMoodSound(BiomeModificationContext.EffectsContext context) {
        setMoodSound(context, Optional.empty());
    }

    public static void setAdditionsSound(BiomeModificationContext.EffectsContext context, Optional<class_4967> sound) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setAdditionsSound(sound);
            return;
        }
        class_1959 biome = biome(context);
        class_12190 now = current(biome, class_12206.field_63753);
        put(biome, class_12206.field_63753, new class_12190(now.comp_5125(), now.comp_5126(),
            sound.<List<class_4967>>map(List::of).orElse(List.of())));
    }

    public static void setAdditionsSound(BiomeModificationContext.EffectsContext context, class_4967 sound) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setAdditionsSound(sound);
            return;
        }
        setAdditionsSound(context, Optional.of(sound));
    }

    /** a 1.21.10 implementation's super.setAdditionsSound(..): 0.138's default body */
    public static void superSetAdditionsSound(BiomeModificationContext.EffectsContext context, class_4967 sound) {
        setAdditionsSound(context, Optional.of(sound));
    }

    public static void clearAdditionsSound(BiomeModificationContext.EffectsContext context) {
        if (context instanceof LegacyEffects legacy) {
            legacy.clearAdditionsSound();
            return;
        }
        setAdditionsSound(context, Optional.empty());
    }

    /** a 1.21.10 implementation's super.clearAdditionsSound(..): 0.138's default body */
    public static void superClearAdditionsSound(BiomeModificationContext.EffectsContext context) {
        setAdditionsSound(context, Optional.empty());
    }

    public static void setMusic(BiomeModificationContext.EffectsContext context, Optional<class_6012<class_5195>> music) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setMusic(music);
            return;
        }
        class_1959 biome = biome(context);
        if (music.isPresent()) {
            put(biome, class_12206.field_63751, CbBiomeEffects.music(music.get()));
        } else {
            remove(biome, class_12206.field_63751);
        }
    }

    public static void setMusic(BiomeModificationContext.EffectsContext context, class_6012<class_5195> music) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setMusic(music);
            return;
        }
        setMusic(context, Optional.of(music));
    }

    /** a 1.21.10 implementation's super.setMusic(..): 0.138's default body */
    public static void superSetMusic(BiomeModificationContext.EffectsContext context, class_6012<class_5195> music) {
        setMusic(context, Optional.of(music));
    }

    public static void setMusic(BiomeModificationContext.EffectsContext context, class_5195 music) {
        if (context instanceof LegacyEffects legacy) {
            legacy.setMusic(music);
            return;
        }
        setMusic(context, class_6012.method_66214(music));
    }

    /** a 1.21.10 implementation's super.setMusic(..): 0.138's default body */
    public static void superSetMusic(BiomeModificationContext.EffectsContext context, class_5195 music) {
        setMusic(context, class_6012.method_66214(music));
    }

    public static void clearMusic(BiomeModificationContext.EffectsContext context) {
        if (context instanceof LegacyEffects legacy) {
            legacy.clearMusic();
            return;
        }
        setMusic(context, Optional.empty());
    }

    /** a 1.21.10 implementation's super.clearMusic(..): 0.138's default body */
    public static void superClearMusic(BiomeModificationContext.EffectsContext context) {
        setMusic(context, Optional.empty());
    }

    /**
     * 0.141's setDryFoliageColor(Optional) on a 1.21.10 implementation: 0.138's EffectsContext had no dry foliage color, so
     * the mod's implementation has nothing to set it on
     */
    public static void setDryFoliageColor(LegacyEffects context, Optional<Integer> color) {
        throw new UnsupportedOperationException(context.getClass().getName()
            + " implements Fabric API 0.138's BiomeModificationContext.EffectsContext, which had no dry foliage color");
    }

    /** 0.138's BiomeModificationContext abstract shapes, as a 1.21.10 implementation of it declares them */
    public interface LegacyContext {
        BiomeModificationContext.WeatherContext getWeather();

        BiomeModificationContext.EffectsContext getEffects();

        BiomeModificationContext.GenerationSettingsContext getGenerationSettings();

        BiomeModificationContext.SpawnSettingsContext getSpawnSettings();
    }

    /**
     * 0.141's getAttributes() on a 1.21.10 implementation of BiomeModificationContext: 0.138's context had no attributes
     * (its effects carried them), so the mod's implementation has none to give
     */
    public static BiomeModificationContext.AttributesContext getAttributes(LegacyContext context) {
        throw new UnsupportedOperationException(context.getClass().getName()
            + " implements Fabric API 0.138's BiomeModificationContext, which had no attributes context");
    }

    /** the biome a 0.141 EffectsContextImpl modifies (its enclosing BiomeModificationContextImpl's) */
    private static class_1959 biome(BiomeModificationContext.EffectsContext context) {
        return (class_1959) field(field(context, "this$0"), "biome");
    }

    private static <T> T current(class_1959 biome, class_12197<T> attribute) {
        return biome.method_75734().method_75663(attribute, attribute.method_75650());
    }

    /** 0.141's AttributesContext.set: the biome's attributes with this one set */
    private static <T> void put(class_1959 biome, class_12197<T> attribute, T value) {
        biome.field_63809 = class_12199.method_75661().method_75675(biome.method_75734()).method_75674(attribute, value).method_75672();
    }

    /** the biome's attributes without this one */
    private static void remove(class_1959 biome, class_12197<?> attribute) {
        class_12199 now = biome.method_75734();
        class_12199.class_12200 builder = class_12199.method_75661();
        for (class_12197<?> a : now.method_75666()) {
            if (a != attribute) {
                copy(builder, now, a);
            }
        }
        biome.field_63809 = builder.method_75672();
    }

    @SuppressWarnings("unchecked")
    private static <T, A> void copy(class_12199.class_12200 builder, class_12199 from, class_12197<T> attribute) {
        class_12199.class_12201<T, A> entry = (class_12199.class_12201<T, A>) from.method_75662(attribute);
        builder.method_75673(attribute, (class_12212<T, A>) entry.comp_5141(), entry.comp_5140());
    }

    private static Object field(Object owner, String name) {
        Field f = FIELDS.computeIfAbsent(owner.getClass().getName() + "#" + name, k -> {
            try {
                Field found = owner.getClass().getDeclaredField(name);
                found.setAccessible(true);
                return found;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Fabric API's " + owner.getClass().getName() + " has no " + name, e);
            }
        });
        try {
            return f.get(owner);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
