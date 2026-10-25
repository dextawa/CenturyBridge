package top.dext.centurybridge.shims.v1_20_2;

import io.netty.channel.Channel;
import net.minecraft.class_2535;
import net.minecraft.class_2539;
import net.minecraft.class_2598;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * ClientConnection.setState(NetworkState) is gone from 1.20.2: the connection now keeps one protocol
 * codec per direction (two channel attributes), and vanilla moves each by itself when a packet that
 * ends a phase passes the encoder or decoder (ProtocolSwapHandler). 1.20.1's setState put the one
 * state it had in force for both directions and turned auto-read on; this does that with 1.20.2's
 * two attributes. Vanilla 1.20.2 never calls it, so a subclass overriding it is not told of the
 * phase changes vanilla makes on its own.
 */
@Mixin(value = class_2535.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetConnectionBridge {

    @Shadow
    private Channel field_11651;

    /** was setState(NetworkState) */
    public void method_10750(class_2539 state) {
        field_11651.attr(class_2535.field_45666).set(state.method_52921(class_2598.field_11941));
        field_11651.attr(class_2535.field_45667).set(state.method_52921(class_2598.field_11942));
        field_11651.config().setAutoRead(true);
    }
}
