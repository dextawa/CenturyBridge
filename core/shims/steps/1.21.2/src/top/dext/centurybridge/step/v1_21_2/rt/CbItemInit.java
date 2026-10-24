package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.ModInitializer;

/** Registrations 1.21.1 had that 1.21.2 dropped (s7item's area): the recipe serializers of CbItemRecipeSerializers */
public final class CbItemInit implements ModInitializer {
    @Override
    public void onInitialize() {
        CbItemRecipeSerializers.register();
    }
}
