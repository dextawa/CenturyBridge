package top.dext.centurybridge.compat.fapi;

/**
 * Fabric API's RegistryAttribute.PERSISTED is gone in the 1.20.2 registry sync;
 * MODDED -- which FabricRegistryBuilder adds to every registry it builds anyway --
 * stands in, so attribute(PERSISTED) stays a harmless call.
 */
public final class CbRegistryAttributes {

    public static final net.fabricmc.fabric.api.event.registry.RegistryAttribute PERSISTED =
        net.fabricmc.fabric.api.event.registry.RegistryAttribute.MODDED;

    private CbRegistryAttributes() {
    }
}
