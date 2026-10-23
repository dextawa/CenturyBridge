package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.ModInitializer;

/**
 * Registrations 1.21.1 had that 1.21.2 dropped, made while the built-in registries take entries: 1.21.1's
 * PlacementModifierType "minecraft:carving_mask" (CbLevelCarving), so placed features naming it load, and its
 * block type "minecraft:moss" (facade MossBlock's codec).
 */
public final class CbLevelInit implements ModInitializer {
    @Override
    public void onInitialize() {
        CbLevelCarving.field_35751();
        net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_46591,
            net.minecraft.class_2960.method_60656("moss"), top.dext.centurybridge.step.v1_21_2.facade.MossBlock.field_46397);
    }
}
