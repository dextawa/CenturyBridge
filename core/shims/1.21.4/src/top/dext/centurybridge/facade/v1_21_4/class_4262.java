package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_4262 (AbstractGlassBlock / TransparentBlock),
 * deleted at 1.20.3. It was a thin glass base: a (Properties) constructor plus the
 * transparency overrides (getVisualShape / getShadeBrightness / propagatesSkylight)
 * that its surviving ancestor class_2373 (HalfTransparentBlock) already provides
 * identically. Corpus glass mods subclass it (botania, immersive_weathering,
 * create's ConnectedGlass, conquest, ...); `extends class_4262` fails to load once
 * the class is gone, so this facade re-supplies it as a non-final class via
 * superRenames. Extending the live HalfTransparentBlock means a subclass keeps the
 * exact glass rendering/occlusion behaviour it always inherited. Member names are
 * intermediary tokens.
 */
public abstract class class_4262 extends net.minecraft.class_2373 {

    protected class_4262(net.minecraft.class_4970.class_2251 properties) {
        super(properties);
    }
}
