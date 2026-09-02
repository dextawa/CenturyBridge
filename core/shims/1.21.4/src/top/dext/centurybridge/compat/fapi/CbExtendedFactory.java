package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_2540;

/**
 * The 1.20.1 shape of fabric-screen-handler-api-v1's ExtendedScreenHandlerType.ExtendedFactory:
 * {@code create(syncId, PlayerInventory, PacketByteBuf)}. 1.20.5 made the data generic
 * ({@code create(int, Inventory, D)}, erased to Object), so a 1.20.1 lambda's SAM no longer
 * matches the real interface; the converter renames the interface here and
 * {@link CbExtendedScreenHandlerType} adapts it onto the real one with D = PacketByteBuf.
 */
@FunctionalInterface
public interface CbExtendedFactory<T extends class_1703> {
    T create(int syncId, class_1661 inventory, class_2540 buf);
}
