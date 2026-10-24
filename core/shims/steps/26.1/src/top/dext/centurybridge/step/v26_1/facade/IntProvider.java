package top.dext.centurybridge.step.v26_1.facade;

/**
 * 1.21.11's IntProvider (net/minecraft/class_6017) was an abstract class; 26.1's is an interface. A 1.21.11 mod's
 * provider class extends this instead (superRenames), which implements 26.1's interface: its sample /
 * getMinValue / getMaxValue overrides are 26.1's sample / minInclusive / maxInclusive under the wall's names, and
 * its getType() is reconnected to codec() (overrideHooks: the codec of the type it answers). Every other
 * reference to IntProvider stays 26.1's interface.
 */
public abstract class IntProvider implements net.minecraft.util.valueproviders.IntProvider {

    /** 1.21.11 IntProvider() */
    public IntProvider() {
    }
}
