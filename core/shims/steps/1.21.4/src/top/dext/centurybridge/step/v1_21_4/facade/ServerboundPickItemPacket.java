package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.class_2540;
import net.minecraft.class_2596;
import net.minecraft.class_2792;
import net.minecraft.class_9139;
import net.minecraft.class_9145;
import top.dext.centurybridge.step.v1_21_4.rt.CbFinPick;

/**
 * 1.21.3's ServerboundPickItemPacket (net/minecraft/class_2838, classRenames): "pick inventory slot N", which 1.21.4 removed from
 * the protocol (picking is the server's decision from ServerboundPickItemFromBlockPacket / ...FromEntityPacket). Its members are
 * 1.21.3's: the slot, the var-int stream codec, the packet type minecraft:pick_item (GamePacketTypes.SERVERBOUND_PICK_ITEM), and
 * handle(listener) = listener.handlePickItem(this). It never reaches the wire: a client sending one performs 1.21.3's pick of that
 * slot through 1.21.4's inventory container (CbFinPickSendMixin), and a server listener handed one runs 1.21.3's handler
 * (CbFinPick.method_12084).
 */
public class ServerboundPickItemPacket implements class_2596<class_2792> {

    /** STREAM_CODEC */
    public static final class_9139<class_2540, ServerboundPickItemPacket> field_48204 =
        class_2596.method_56443(ServerboundPickItemPacket::method_55985, ServerboundPickItemPacket::new);

    /** slot */
    public final int field_12908;

    public ServerboundPickItemPacket(int slot) {
        this.field_12908 = slot;
    }

    public ServerboundPickItemPacket(class_2540 buf) {
        this.field_12908 = buf.method_10816();
    }

    /** write(buf) */
    public void method_55985(class_2540 buf) {
        buf.method_10804(this.field_12908);
    }

    /** type(), 1.21.3's name */
    public class_9145<ServerboundPickItemPacket> method_55846() {
        return CbFinPick.SERVERBOUND_PICK_ITEM;
    }

    /** type(), 1.21.4's name */
    @Override
    public class_9145<ServerboundPickItemPacket> method_65080() {
        return this.method_55846();
    }

    /** handle(listener): 1.21.3's listener.handlePickItem(this) */
    public void method_12292(class_2792 listener) {
        CbFinPick.method_12084(listener, this);
    }

    @Override
    public void method_65081(class_2792 listener) {
        this.method_12292(listener);
    }

    /** getSlot() */
    public int method_12293() {
        return this.field_12908;
    }
}
