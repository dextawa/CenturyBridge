package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * 1.21.11's RandomPatchConfiguration (net/minecraft/class_4638), which 26.1 removed with the random_patch,
 * flower and no_bonemeal_flower features (its patches are placement modifiers around a simple block now): the
 * record's surface and its codec as 1.21.11 had them (tries 128, xz_spread 7, y_spread 3 by default; the
 * placed feature to try), configuring this layer's RandomPatchFeature. Members keep 1.21.11's intermediary
 * names; the component fields are public (access wideners never reach this class).
 */
public final class RandomPatchConfiguration implements FeatureConfiguration {

    /** 1.21.11 RandomPatchConfiguration.CODEC */
    public static final Codec<RandomPatchConfiguration> field_24902 = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(RandomPatchConfiguration::comp_149),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(RandomPatchConfiguration::comp_150),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(RandomPatchConfiguration::comp_151),
            PlacedFeature.CODEC.fieldOf("feature").forGetter(RandomPatchConfiguration::comp_155))
        .apply(i, RandomPatchConfiguration::new));

    public final int comp_149;
    public final int comp_150;
    public final int comp_151;
    public final Holder<PlacedFeature> comp_155;

    /** 1.21.11 RandomPatchConfiguration(tries, xzSpread, ySpread, feature) */
    public RandomPatchConfiguration(int tries, int xzSpread, int ySpread, Holder<PlacedFeature> feature) {
        this.comp_149 = tries;
        this.comp_150 = xzSpread;
        this.comp_151 = ySpread;
        this.comp_155 = feature;
    }

    /** tries() */
    public int comp_149() {
        return this.comp_149;
    }

    /** xzSpread() */
    public int comp_150() {
        return this.comp_150;
    }

    /** ySpread() */
    public int comp_151() {
        return this.comp_151;
    }

    /** feature() */
    public Holder<PlacedFeature> comp_155() {
        return this.comp_155;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof RandomPatchConfiguration c && this.comp_149 == c.comp_149 && this.comp_150 == c.comp_150
            && this.comp_151 == c.comp_151 && Objects.equals(this.comp_155, c.comp_155);
    }

    @Override
    public int hashCode() {
        return ((Integer.hashCode(this.comp_149) * 31 + Integer.hashCode(this.comp_150)) * 31 + Integer.hashCode(this.comp_151)) * 31
            + Objects.hashCode(this.comp_155);
    }

    @Override
    public String toString() {
        return "RandomPatchConfiguration[tries=" + this.comp_149 + ", xzSpread=" + this.comp_150 + ", ySpread=" + this.comp_151
            + ", feature=" + this.comp_155 + "]";
    }
}
