package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2540;
import org.spongepowered.asm.mixin.Mixin;

/**
 * PacketByteBuf.getWrittenBytes() is gone from 1.20.2. 1.20.1's body, verbatim: every byte from
 * index 0 to the writer index, whatever has been read already.
 */
@Mixin(class_2540.class)
public abstract class CbNetFriendlyByteBufBridge {

    /** was getWrittenBytes() */
    public byte[] method_36132() {
        class_2540 self = (class_2540) (Object) this;
        byte[] out = new byte[self.writerIndex()];
        self.getBytes(0, out);
        return out;
    }
}
