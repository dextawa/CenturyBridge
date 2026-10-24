package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

/**
 * 1.21.11's PerlinNoise.getValue(x, y, z, yScale, yMax, useFixedY): 26.1 kept it without the last flag, as
 * getValue(x, y, z, yScale, yMax) (the flag's false branch; getValue(x, y, z) still calls it with zero scale and
 * max). With the flag, 1.21.11 sampled every octave at its own fixed y (-yo) instead of the scaled y: that body is
 * kept here over 26.1's fields (the layer's access widener opens them). A 1.21.11 subclass overriding the six-argument
 * method is reached through 26.1's five-argument one (override hook, LegacyPerlinNoise), as 1.21.11's
 * getValue(x, y, z) reached it.
 */
public final class CbWallNoise {

    private CbWallNoise() {
    }

    /** a 1.21.11 subclass's own getValue(x, y, z, yScale, yMax, useFixedY) */
    public interface LegacyPerlinNoise {
        double getValue(double x, double y, double z, double yScale, double yMax, boolean useFixedY);
    }

    /** 26.1's getValue(x, y, z, yScale, yMax) of such a subclass: its own method, the flag off */
    public static double bridge(LegacyPerlinNoise noise, double x, double y, double z, double yScale, double yMax) {
        return noise.getValue(x, y, z, yScale, yMax, false);
    }

    /** 1.21.11 PerlinNoise.getValue(x, y, z, yScale, yMax, useFixedY) called on a noise (a subclass's override answers) */
    public static double getValue(PerlinNoise noise, double x, double y, double z, double yScale, double yMax, boolean useFixedY) {
        return useFixedY ? superGetValue(noise, x, y, z, yScale, yMax, true) : noise.getValue(x, y, z, yScale, yMax);
    }

    /** a 1.21.11 subclass's super.getValue(x, y, z, yScale, yMax, useFixedY): 1.21.11's body (26.1's five-argument one without the flag) */
    public static double superGetValue(PerlinNoise noise, double x, double y, double z, double yScale, double yMax, boolean useFixedY) {
        double value = 0.0;
        double factor = noise.lowestFreqInputFactor;
        double valueFactor = noise.lowestFreqValueFactor;
        for (int i = 0; i < noise.noiseLevels.length; i++) {
            ImprovedNoise level = noise.noiseLevels[i];
            if (level != null) {
                double n = level.noise(PerlinNoise.wrap(x * factor), useFixedY ? -level.yo : PerlinNoise.wrap(y * factor),
                    PerlinNoise.wrap(z * factor), yScale * factor, yMax * factor);
                value += noise.amplitudes.getDouble(i) * n * valueFactor;
            }
            factor *= 2.0;
            valueFactor /= 2.0;
        }
        return value;
    }
}
