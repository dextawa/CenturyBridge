package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.collect.MapMaker;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.class_12190;
import net.minecraft.class_12194;
import net.minecraft.class_12197;
import net.minecraft.class_12206;
import net.minecraft.class_1959;
import net.minecraft.class_3414;
import net.minecraft.class_4761;
import net.minecraft.class_4763;
import net.minecraft.class_4967;
import net.minecraft.class_4968;
import net.minecraft.class_5195;
import net.minecraft.class_6012;
import net.minecraft.class_6880;

/**
 * 1.21.10's BiomeSpecialEffects reads on 1.21.11 (javap of both code jars): 1.21.11's BiomeSpecialEffects is a record of
 * the water / foliage / dry foliage / grass colours and the grass modifier; fog, water fog and sky colour, the ambient
 * particle, the ambient sounds, the background music and its volume moved to the biome's environment attributes
 * (FOG_COLOR, WATER_FOG_COLOR, SKY_COLOR, AMBIENT_PARTICLES, AMBIENT_SOUNDS, BACKGROUND_MUSIC, MUSIC_VOLUME).
 * <ul>
 * <li>The getters 1.21.11 dropped (fogColor method_24387, waterFogColor method_24389, skyColor method_30810,
 *     getAmbientParticleSettings method_24390, getAmbientLoopSoundEvent method_24939, getAmbientMoodSettings
 *     method_24940, getAmbientAdditionsSettings method_24941, getBackgroundMusic method_27345, getBackgroundMusicVolume
 *     method_65135) answer from the attributes of the biome the effects belong to (Biome's constructor records it,
 *     mixin/CbBiomeEffectsOwnerMixinHooks), in 1.21.10's form: colours as RGB (1.21.11's attributes hold ARGB), one
 *     particle (the first of the list; none when the list is empty or unset), the three sounds, the music as 1.21.10's
 *     weighted list (silence -> an empty list, one music -> a list of it, unset -> none). Effects a 1.21.10 builder
 *     built and no biome holds yet answer what that builder set (rt/CbBiomeEffects.Settings); effects no biome holds and
 *     no 1.21.10 builder built answer the attributes' defaults.</li>
 * <li>Reads of 1.21.10's private fields (through a mod's access widener) are the same getters; the fields 1.21.11's
 *     record keeps under its components (waterColor, foliage / grass colour overrides, grass modifier) read those.</li>
 * <li>Reads and writes of the Builder's private fields 1.21.11 dropped (fogColor, waterFogColor, skyColor, particle,
 *     sounds, music) go to what the restored setters keep (CbBiomeEffects.Settings), so build() hands them on.</li>
 * </ul>
 */
public final class CbBiomeEffectsLegacy {
    private CbBiomeEffectsLegacy() {
    }

    /** effects -> the biome built with them (identity; a biome holds its effects for life) */
    private static final Map<class_4763, class_1959> OWNER = new MapMaker().weakKeys().weakValues().makeMap();

    public static void owner(class_4763 effects, class_1959 biome) {
        if (effects != null) {
            OWNER.put(effects, biome);
        }
    }

    private static <T> T attr(class_4763 effects, class_12197<T> attribute, T legacy) {
        class_1959 biome = OWNER.get(effects);
        if (biome != null) {
            return biome.method_75734().method_75663(attribute, attribute.method_75650());
        }
        return legacy != null ? legacy : attribute.method_75650();
    }

    /** whether the biome (or the 1.21.10 builder's settings) sets the attribute at all */
    private static boolean set(class_4763 effects, class_12197<?> attribute, Object legacy) {
        class_1959 biome = OWNER.get(effects);
        return biome != null ? biome.method_75734().method_75667(attribute) : legacy != null;
    }

    private static CbBiomeEffects.Settings settings(class_4763 effects) {
        return CbBiomeEffects.settings(effects);
    }

    private static int rgb(int argb) {
        return argb & 0xFFFFFF;
    }

    // ---- BiomeSpecialEffects getters (and the private fields they read)

    /** fogColor() */
    public static int method_24387(class_4763 e) {
        CbBiomeEffects.Settings s = settings(e);
        return rgb(attr(e, class_12206.field_63742, s == null || s.fogColor == null ? null : s.fogColor | 0xFF000000));
    }

    /** waterFogColor() */
    public static int method_24389(class_4763 e) {
        CbBiomeEffects.Settings s = settings(e);
        return rgb(attr(e, class_12206.field_63744, s == null || s.waterFogColor == null ? null : s.waterFogColor | 0xFF000000));
    }

    /** skyColor() */
    public static int method_30810(class_4763 e) {
        CbBiomeEffects.Settings s = settings(e);
        return rgb(attr(e, class_12206.field_63746, s == null || s.skyColor == null ? null : s.skyColor | 0xFF000000));
    }

    /** getAmbientParticleSettings() */
    public static Optional<class_4761> method_24390(class_4763 e) {
        CbBiomeEffects.Settings s = settings(e);
        List<class_4761> legacy = s == null || s.particle == null ? null : List.of(s.particle);
        List<class_4761> list = attr(e, class_12206.field_63750, legacy);
        return list == null || list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    private static class_12190 sounds(class_4763 e) {
        CbBiomeEffects.Settings s = settings(e);
        class_12190 legacy = s == null || (s.loopSound == null && s.moodSound == null && s.additionsSound == null) ? null
            : new class_12190(Optional.ofNullable(s.loopSound), Optional.ofNullable(s.moodSound),
                s.additionsSound == null ? List.of() : List.of(s.additionsSound));
        return attr(e, class_12206.field_63753, legacy);
    }

    /** getAmbientLoopSoundEvent() */
    public static Optional<class_6880<class_3414>> method_24939(class_4763 e) {
        class_12190 s = sounds(e);
        return s == null ? Optional.empty() : s.comp_5125();
    }

    /** getAmbientMoodSettings() */
    public static Optional<class_4968> method_24940(class_4763 e) {
        class_12190 s = sounds(e);
        return s == null ? Optional.empty() : s.comp_5126();
    }

    /** getAmbientAdditionsSettings() */
    public static Optional<class_4967> method_24941(class_4763 e) {
        class_12190 s = sounds(e);
        return s == null || s.comp_5127().isEmpty() ? Optional.empty() : Optional.of(s.comp_5127().get(0));
    }

    /** getBackgroundMusic(): unset -> none; BackgroundMusic.EMPTY (silence) -> an empty list; a music -> a list of it */
    public static Optional<class_6012<class_5195>> method_27345(class_4763 e) {
        CbBiomeEffects.Settings s = settings(e);
        if (OWNER.get(e) == null && s != null) {
            return s.music;
        }
        if (!set(e, class_12206.field_63751, null)) {
            return Optional.empty();
        }
        class_12194 music = attr(e, class_12206.field_63751, null);
        if (music == null || music.equals(class_12194.field_63700) || music.comp_5133().isEmpty()) {
            return Optional.of(class_6012.method_34990());
        }
        return Optional.of(class_6012.method_66214(music.comp_5133().get()));
    }

    /** getBackgroundMusicVolume() */
    public static float method_65135(class_4763 e) {
        CbBiomeEffects.Settings s = settings(e);
        return attr(e, class_12206.field_63752, s == null || !s.musicVolumeSet ? null : s.musicVolume);
    }

    // the private record fields 1.21.10 had, read through a mod's access widener
    public static int field_22067(class_4763 e) {
        return method_24387(e);
    }

    public static int field_22068(class_4763 e) {
        return e.comp_5169();
    }

    public static int field_22069(class_4763 e) {
        return method_24389(e);
    }

    public static int field_26418(class_4763 e) {
        return method_30810(e);
    }

    public static Optional<Integer> field_26419(class_4763 e) {
        return e.comp_5170();
    }

    public static Optional<Integer> field_26420(class_4763 e) {
        return e.comp_5172();
    }

    public static class_4763.class_5486 field_26421(class_4763 e) {
        return e.comp_5173();
    }

    public static Optional<class_4761> field_22070(class_4763 e) {
        return method_24390(e);
    }

    public static Optional<class_6880<class_3414>> field_22491(class_4763 e) {
        return method_24939(e);
    }

    public static Optional<class_4968> field_22492(class_4763 e) {
        return method_24940(e);
    }

    public static Optional<class_4967> field_22493(class_4763 e) {
        return method_24941(e);
    }

    public static Optional<class_6012<class_5195>> field_24113(class_4763 e) {
        return method_27345(e);
    }

    /** dryFoliageColorOverride: 1.21.11's record keeps it (comp_5171) */
    public static Optional<Integer> field_57099(class_4763 e) {
        return e.comp_5171();
    }

    /** backgroundMusicVolume */
    public static float field_55050(class_4763 e) {
        return method_65135(e);
    }

    /** Builder.backgroundMusicVolume (1.21.10 default 1.0): what setBackgroundMusicVolume keeps */
    public static float field_55051(class_4763.class_4764 b) {
        return of(b).musicVolume;
    }

    public static void field_55051(class_4763.class_4764 b, float v) {
        CbBiomeEffects.Settings s = of(b);
        s.musicVolume = v;
        s.musicVolumeSet = true;
    }

    // ---- BiomeSpecialEffects.Builder private fields 1.21.11 dropped: what the restored setters keep

    private static CbBiomeEffects.Settings of(class_4763.class_4764 b) {
        return ((CbBiomeEffects.Builder) b).cb$v1_21_11$settings();
    }

    private static OptionalInt opt(Integer v) {
        return v == null ? OptionalInt.empty() : OptionalInt.of(v);
    }

    private static Integer val(OptionalInt v) {
        return v == null || v.isEmpty() ? null : v.getAsInt();
    }

    public static OptionalInt field_22071(class_4763.class_4764 b) {
        return opt(of(b).fogColor);
    }

    public static void field_22071(class_4763.class_4764 b, OptionalInt v) {
        of(b).fogColor = val(v);
    }

    public static OptionalInt field_22073(class_4763.class_4764 b) {
        return opt(of(b).waterFogColor);
    }

    public static void field_22073(class_4763.class_4764 b, OptionalInt v) {
        of(b).waterFogColor = val(v);
    }

    public static OptionalInt field_26422(class_4763.class_4764 b) {
        return opt(of(b).skyColor);
    }

    public static void field_26422(class_4763.class_4764 b, OptionalInt v) {
        of(b).skyColor = val(v);
    }

    public static Optional<class_4761> field_22074(class_4763.class_4764 b) {
        return Optional.ofNullable(of(b).particle);
    }

    public static void field_22074(class_4763.class_4764 b, Optional<class_4761> v) {
        of(b).particle = v == null ? null : v.orElse(null);
    }

    public static Optional<class_6880<class_3414>> field_22494(class_4763.class_4764 b) {
        return Optional.ofNullable(of(b).loopSound);
    }

    public static void field_22494(class_4763.class_4764 b, Optional<class_6880<class_3414>> v) {
        of(b).loopSound = v == null ? null : v.orElse(null);
    }

    public static Optional<class_4968> field_22495(class_4763.class_4764 b) {
        return Optional.ofNullable(of(b).moodSound);
    }

    public static void field_22495(class_4763.class_4764 b, Optional<class_4968> v) {
        of(b).moodSound = v == null ? null : v.orElse(null);
    }

    public static Optional<class_4967> field_22496(class_4763.class_4764 b) {
        return Optional.ofNullable(of(b).additionsSound);
    }

    public static void field_22496(class_4763.class_4764 b, Optional<class_4967> v) {
        of(b).additionsSound = v == null ? null : v.orElse(null);
    }

    public static Optional<class_6012<class_5195>> field_24114(class_4763.class_4764 b) {
        return of(b).music;
    }

    public static void field_24114(class_4763.class_4764 b, Optional<class_6012<class_5195>> v) {
        of(b).music = v == null ? Optional.empty() : v;
    }
}
