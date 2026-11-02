package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_12190;
import net.minecraft.class_12194;
import net.minecraft.class_12197;
import net.minecraft.class_12206;
import net.minecraft.class_3414;
import net.minecraft.class_4761;
import net.minecraft.class_4763;
import net.minecraft.class_4967;
import net.minecraft.class_4968;
import net.minecraft.class_5195;
import net.minecraft.class_6010;
import net.minecraft.class_6012;
import net.minecraft.class_6880;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.21.10's BiomeSpecialEffects.Builder settings 1.21.11 moved to the biome's environment attributes
 * (world/attribute/EnvironmentAttributes): fog, water fog and sky color (FOG_COLOR, WATER_FOG_COLOR, SKY_COLOR: the
 * same RGB int), the ambient particle (AMBIENT_PARTICLES, a list of one), the ambient loop / mood / additions sounds
 * (AMBIENT_SOUNDS), the background music (BACKGROUND_MUSIC) and its volume (MUSIC_VOLUME). The restored setters
 * (CbBiomeEffectsBuilderMixin) keep what a 1.21.10 builder set, the BiomeSpecialEffects it builds carries it (by
 * identity), and the Biome.Builder it is given to sets the attributes (CbBiomeBuilderMixin), as 1.21.11's own biomes
 * set them on the Biome.Builder.
 */
public final class CbBiomeEffects {
    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/biome");
    private static final Map<class_4763, Settings> BUILT = new MapMaker().weakKeys().makeMap();

    private CbBiomeEffects() {
    }

    /** what a 1.21.10 builder set beyond 1.21.11's BiomeSpecialEffects (1.21.10's defaults: unset, volume 1) */
    public static final class Settings {
        public Integer fogColor;
        public Integer waterFogColor;
        public Integer skyColor;
        public class_4761 particle;
        public class_6880<class_3414> loopSound;
        public class_4968 moodSound;
        public class_4967 additionsSound;
        public Optional<class_6012<class_5195>> music = Optional.empty();
        public float musicVolume = 1.0F;
        public boolean musicVolumeSet;

        boolean any() {
            return this.fogColor != null || this.waterFogColor != null || this.skyColor != null || this.particle != null
                || this.loopSound != null || this.moodSound != null || this.additionsSound != null || this.music.isPresent()
                || this.musicVolumeSet;
        }
    }

    /** a BiomeSpecialEffects.Builder with 1.21.10's setters restored (CbBiomeEffectsBuilderMixin): what they set */
    public interface Builder {
        Settings cb$v1_21_11$settings();
    }

    public static void attach(class_4763 effects, Settings s) {
        if (s != null && s.any()) {
            BUILT.put(effects, s);
        }
    }

    /** the attributes a 1.21.10 builder's settings stand for, in the order 1.21.11's biomes set them */
    public static List<Object[]> attributes(class_4763 effects) {
        Settings s = BUILT.get(effects);
        List<Object[]> out = new ArrayList<>();
        if (s == null) {
            return out;
        }
        // 1.21.10's builders took RGB (OverworldBiomes: fogColor 0xC0D8FF); 1.21.11's colour attributes hold ARGB and its
        // sky renderer hands all four channels to the shader's colour modulator (SkyRenderer: ARGB.toVector4f(skyColor),
        // alpha included; 1.21.11's own OverworldBiomes.calculateSkyColor ORs 0xFF000000 where 1.21.10's did not; javap):
        // the colour is made opaque, as 1.21.10 drew it (ARGB.opaque, class_9848.method_61334)
        add(out, class_12206.field_63742, opaque(s.fogColor));
        add(out, class_12206.field_63744, opaque(s.waterFogColor));
        add(out, class_12206.field_63746, opaque(s.skyColor));
        if (s.particle != null) {
            add(out, class_12206.field_63750, List.of(s.particle));
        }
        if (s.loopSound != null || s.moodSound != null || s.additionsSound != null) {
            add(out, class_12206.field_63753, new class_12190(Optional.ofNullable(s.loopSound), Optional.ofNullable(s.moodSound),
                s.additionsSound == null ? List.of() : List.of(s.additionsSound)));
        }
        s.music.ifPresent(list -> add(out, class_12206.field_63751, music(list)));
        if (s.musicVolumeSet) {
            add(out, class_12206.field_63752, s.musicVolume);
        }
        return out;
    }

    private static Integer opaque(Integer rgb) {
        return rgb == null ? null : net.minecraft.class_9848.method_61334(rgb);
    }

    private static void add(List<Object[]> out, class_12197<?> attribute, Object value) {
        if (value != null) {
            out.add(new Object[]{attribute, value});
        }
    }

    /**
     * 1.21.10's weighted music list as 1.21.11's BackgroundMusic: none (silenceAllBackgroundMusic) -> EMPTY; one
     * entry -> that music. 1.21.11's BackgroundMusic holds one music, not a weighted choice: a list of several takes
     * the heaviest entry (the first among equals) and says so once.
     */
    static class_12194 music(class_6012<class_5195> list) {
        List<class_6010<class_5195>> entries = list.method_34994();
        if (entries.isEmpty()) {
            return class_12194.field_63700;
        }
        class_6010<class_5195> best = entries.get(0);
        for (class_6010<class_5195> e : entries) {
            if (e.comp_2543() > best.comp_2543()) {
                best = e;
            }
        }
        if (entries.size() > 1) {
            LOGGER.warn("biome background music: 1.21.10 chose among {} weighted entries; 1.21.11's BackgroundMusic holds one, "
                + "the heaviest is kept", entries.size());
        }
        return new class_12194(best.comp_2542());
    }
}
