package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2540;

/**
 * 1.20.4's EntityDataSerializer.write / read, on a 1.20.4 mod's own serializer class (the converter
 * adds this interface where a class declares both): its 1.20.5 codec() is made from them
 * (CbNetEntityData.cbLegacyCodec), and a 1.20.4 caller writing or reading through any serializer
 * reaches them directly.
 */
public interface CbNetLegacySerializer {

    /** was EntityDataSerializer.write(FriendlyByteBuf, value) */
    void method_12715(class_2540 buf, Object value);

    /** was EntityDataSerializer.read(FriendlyByteBuf) */
    Object method_12716(class_2540 buf);
}
