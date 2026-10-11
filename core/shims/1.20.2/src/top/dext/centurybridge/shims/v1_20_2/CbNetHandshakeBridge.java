package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2539;
import net.minecraft.class_2889;
import org.spongepowered.asm.mixin.Mixin;

/**
 * HandshakeC2SPacket: a class in 1.20.1, a record in 1.20.2 whose intent is a ClientIntent rather than
 * a NetworkState. The 1.20.1 accessors answer from the record -- the intended state is the protocol
 * the intent leads to (the record's own method_52273). For converted calls and a subclass's super
 * calls (extendable again); vanilla 1.20.2 reads the components.
 */
@Mixin(class_2889.class)
public abstract class CbNetHandshakeBridge {

    /** was getIntendedState() */
    public class_2539 method_12573() {
        return ((class_2889) (Object) this).method_52273();
    }

    /** was getProtocolVersion() */
    public int method_12574() {
        return ((class_2889) (Object) this).comp_1563();
    }

    /** was getAddress() */
    public String method_36174() {
        return ((class_2889) (Object) this).comp_1564();
    }

    /** was getPort() */
    public int method_36175() {
        return ((class_2889) (Object) this).comp_1565();
    }
}
