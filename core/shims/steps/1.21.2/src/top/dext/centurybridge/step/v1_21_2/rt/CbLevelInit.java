package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.ModInitializer;

/**
 * Registrations 1.21.1 had that 1.21.2 dropped, made while the built-in registries take entries: 1.21.1's
 * PlacementModifierType "minecraft:carving_mask" (CbLevelCarving), so placed features naming it load.
 */
public final class CbLevelInit implements ModInitializer {
    @Override
    public void onInitialize() {
        CbLevelCarving.field_35751();
    }
}
