package top.dext.centurybridge.step.v1_20_5.compat.fapi.screenhandler.v1;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.screenhandler.CbScreenHandlers;

/**
 * Fabric API 0.97.3's (1.20.4) ScreenHandlerRegistry (deprecated there, removed in 1.20.5): registering a
 * simple screen handler type (1.20.4's body: a vanilla type over the factory, registered under the id) or an
 * extended one (an ExtendedScreenHandlerType whose opening data is the 1.20.4 buffer, CbScreenHandlers.RAW).
 */
@Deprecated
public final class ScreenHandlerRegistry {

    private ScreenHandlerRegistry() {
    }

    @Deprecated
    public static <T extends net.minecraft.class_1703> net.minecraft.class_3917<T> registerSimple(net.minecraft.class_2960 id,
                                                                                                SimpleClientHandlerFactory<T> factory) {
        net.minecraft.class_3917<T> type = CbScreenHandlers.simpleType((syncId, inventory) -> factory.create(syncId, inventory));
        return net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41187, id, type);
    }

    @Deprecated
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends net.minecraft.class_1703> net.minecraft.class_3917<T> registerExtended(net.minecraft.class_2960 id,
                                                                                                  ExtendedClientHandlerFactory<T> factory) {
        net.minecraft.class_3917<T> type = new ExtendedScreenHandlerType(
            (ExtendedScreenHandlerType.ExtendedFactory) (syncId, inventory, data) -> factory.create(syncId, inventory, (net.minecraft.class_2540) data),
            CbScreenHandlers.RAW);
        return net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41187, id, type);
    }

    @Deprecated
    public interface SimpleClientHandlerFactory<T extends net.minecraft.class_1703> {
        T create(int syncId, net.minecraft.class_1661 inventory);
    }

    /** 1.20.4: an ExtendedScreenHandlerType.ExtendedFactory; here the same 1.20.4 shape, adapted where it is registered */
    @Deprecated
    public interface ExtendedClientHandlerFactory<T extends net.minecraft.class_1703> {
        T create(int syncId, net.minecraft.class_1661 inventory, net.minecraft.class_2540 buf);
    }
}
