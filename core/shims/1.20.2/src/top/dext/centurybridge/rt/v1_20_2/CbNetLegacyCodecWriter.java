package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.class_2520;

/**
 * The 1.20.1 shape of PacketByteBuf.encode(ops, codec, value), returning nothing (1.20.2's returns
 * the buffer). A mod subclass of PacketByteBuf overriding it is given this interface, which its own
 * method implements, and the 1.20.2 shape vanilla calls reaches it through CbNet.cbWriteWithCodec.
 */
public interface CbNetLegacyCodecWriter {

    void method_29172(DynamicOps<class_2520> ops, Codec<Object> codec, Object value);
}
