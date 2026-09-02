package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_1703;

/**
 * The 1.20.1 ScreenHandlerRegistry.ExtendedClientHandlerFactory: an empty sub-interface of
 * ExtendedScreenHandlerType.ExtendedFactory (a lambda's SAM is the inherited
 * {@code create(int, PlayerInventory, PacketByteBuf)}), kept as one here so a value of this type
 * still passes where the old code handed it to {@code new ExtendedScreenHandlerType<>(...)}.
 * See CbScreenHandlerRegistry.
 */
@FunctionalInterface
public interface CbExtendedClientHandlerFactory<T extends class_1703> extends CbExtendedFactory<T> {
}
