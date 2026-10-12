package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted WritableRegistry implementation with its own 1.20.1
 * registerMapping(rawId, key, value, lifecycle) (method_10273): 1.20.2 dropped the method from the
 * interface; a call reaches the implementation again (CbRegistries).
 */
public interface CbLegacyWritableRegistry {

    /** 1.20.1 registerMapping(rawId, key, value, lifecycle) */
    net.minecraft.class_6880 method_10273(int rawId, net.minecraft.class_5321 key, Object value,
                                          com.mojang.serialization.Lifecycle lifecycle);
}
