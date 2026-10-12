package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2540;

/**
 * 1.20.4's Packet.write(FriendlyByteBuf), which 1.20.5 took out of the Packet interface (area s4net).
 *
 * Every vanilla packet whose 1.20.4 class wrote itself gets the method back through a generated
 * mixin (CbNetWrite*, core/tools/gen_packet_bridges.py) that implements this interface; a 1.20.4
 * mod's own packet class -- a Packet implementation, or a subclass of an abstract vanilla packet --
 * gets the interface added by the converter (adapterHooks), its own write implementing it. A call of
 * write through the Packet interface (CbNetPacketWrites.method_11052) reaches the packet's write this way.
 */
public interface CbNetLegacyPacket {

    /** was Packet.write(FriendlyByteBuf): the packet's fields, as the running release writes them */
    void method_11052(class_2540 buf);
}
