package top.dext.centurybridge.compat.fapi;

/**
 * fabric-key-binding-api-v1's KeyBindingHelper as 1.20.x mods call it. The
 * module became fabric-key-mapping-api-v1 with a new package in the 26.x
 * era; the converter class-renames the old helper here.
 */
public final class CbKeyBindingHelper {

    private CbKeyBindingHelper() {
    }

    public static net.minecraft.client.KeyMapping registerKeyBinding(net.minecraft.client.KeyMapping mapping) {
        return net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(mapping);
    }

    public static com.mojang.blaze3d.platform.InputConstants.Key getBoundKeyOf(net.minecraft.client.KeyMapping mapping) {
        return net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.getBoundKeyOf(mapping);
    }
}
