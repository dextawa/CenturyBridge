package top.dext.centurybridge.step.v26_1.facade;

/**
 * 1.21.11's FloatProvider (net/minecraft/class_5863) was an abstract class implementing SampledFloat; 26.1's is
 * an interface extending it. A 1.21.11 mod's provider class extends this instead (superRenames); as
 * {@link IntProvider} (getMinValue / getMaxValue are 26.1's min / max, getType() reconnected to codec()).
 */
public abstract class FloatProvider implements net.minecraft.util.valueproviders.FloatProvider {

    /** 1.21.11 FloatProvider() */
    public FloatProvider() {
    }
}
