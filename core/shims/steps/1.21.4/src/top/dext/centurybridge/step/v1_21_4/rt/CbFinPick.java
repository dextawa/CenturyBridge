package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_10370;
import net.minecraft.class_10371;
import net.minecraft.class_10387;
import net.minecraft.class_1661;
import net.minecraft.class_2598;
import net.minecraft.class_2600;
import net.minecraft.class_2735;
import net.minecraft.class_2792;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_9145;
import top.dext.centurybridge.step.v1_21_4.facade.ServerboundPickItemPacket;

/**
 * 1.21.3's pick-inventory-slot request on 1.21.4 (area s9fin), server side and shared parts.
 * <ul>
 * <li>GamePacketTypes.SERVERBOUND_PICK_ITEM: 1.21.3's type, serverbound minecraft:pick_item.</li>
 * <li>ServerGamePacketListener.handlePickItem(packet): a 1.21.3 listener's own handler (adapter LegacyPickItem), else 1.21.3's
 *     ServerGamePacketListenerImpl body -- on the server thread, Inventory.pickSlot(slot) (unchanged in 1.21.4: the slot swaps with
 *     the suitable hotbar slot, which becomes selected), then both slots and the selected hotbar slot sent to the player.</li>
 * <li>Handlers 1.21.4's ServerGamePacketListener added (pick from block, pick from entity, player loaded): a 1.21.3 listener
 *     implementation declares none of them; the packets did not exist in 1.21.3, whose connection failed on an id it did not know,
 *     so each fails the same way, naming the packet (as rt/CbNetListeners of 1.20.5 does for that step's new packets).</li>
 * </ul>
 */
public final class CbFinPick {

    /** GamePacketTypes.SERVERBOUND_PICK_ITEM */
    public static final class_9145<ServerboundPickItemPacket> SERVERBOUND_PICK_ITEM =
        new class_9145<>(class_2598.field_11941, class_2960.method_60656("pick_item"));

    private CbFinPick() {
    }

    /** a 1.21.3 ServerGamePacketListener (implementation or ServerGamePacketListenerImpl subclass) declaring handlePickItem */
    public interface LegacyPickItem {
        void method_12084(ServerboundPickItemPacket packet);
    }

    /** was GamePacketTypes.SERVERBOUND_PICK_ITEM (field_48096) */
    public static class_9145<ServerboundPickItemPacket> field_48096() {
        return SERVERBOUND_PICK_ITEM;
    }

    /** was ServerGamePacketListener.handlePickItem(packet) (method_12084): the listener's own, else 1.21.3's */
    public static void method_12084(class_2792 listener, ServerboundPickItemPacket packet) {
        if (listener instanceof LegacyPickItem own) {
            own.method_12084(packet);
        } else if (listener instanceof class_3244 impl) {
            superPickItem(impl, packet);
        } else {
            throw new AbstractMethodError(listener.getClass().getName() + " does not implement 1.21.3's handlePickItem");
        }
    }

    /** was ServerGamePacketListenerImpl.handlePickItem(packet) (method_12084) */
    public static void method_12084(class_3244 listener, ServerboundPickItemPacket packet) {
        method_12084((class_2792) listener, packet);
    }

    /** ServerGamePacketListenerImpl.handlePickItem(packet), 1.21.3's body */
    public static void superPickItem(class_3244 impl, ServerboundPickItemPacket packet) {
        class_3222 player = impl.field_14140;
        class_2600.method_11073(packet, (class_2792) impl, player.method_51469());
        class_1661 inventory = player.method_31548();
        inventory.method_7365(packet.method_12293());
        int selected = inventory.field_7545;
        player.field_13987.method_14364(inventory.method_61497(selected));
        player.field_13987.method_14364(inventory.method_61497(packet.method_12293()));
        player.field_13987.method_14364(new class_2735(selected));
    }

    private static IllegalStateException unknown(Object listener, Object packet) {
        return new IllegalStateException("1.21.3 packet listener " + listener.getClass().getName() + " cannot handle "
            + packet.getClass().getSimpleName() + ": the packet did not exist in 1.21.3");
    }

    /** handlePickItemFromEntity, new in 1.21.4 */
    public static void pickItemFromEntity(class_2792 listener, class_10371 packet) {
        throw unknown(listener, packet);
    }

    /** handlePickItemFromBlock, new in 1.21.4 */
    public static void pickItemFromBlock(class_2792 listener, class_10370 packet) {
        throw unknown(listener, packet);
    }

    /** handleAcceptPlayerLoad, new in 1.21.4 */
    public static void playerLoaded(class_2792 listener, class_10387 packet) {
        throw unknown(listener, packet);
    }
}
