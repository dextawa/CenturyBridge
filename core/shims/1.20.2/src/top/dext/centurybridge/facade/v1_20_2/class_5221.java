package top.dext.centurybridge.facade.v1_20_2;

import net.minecraft.class_2561;

/**
 * Facade for 1.20.1 net/minecraft/class_5221 (com.mojang.realmsclient.gui.ErrorCallback), deleted in
 * 1.20.2: what reports a Realms failure. Mods' references are renamed here (classRenames); the vanilla
 * classes that were ErrorCallbacks in 1.20.1, LongRunningTask and RealmsLongRunningMcoTaskScreen,
 * implement it again (CbNetRealmsTaskBridge, CbNetRealmsTaskScreenBridge).
 */
public interface class_5221 {

    /** error(message) */
    void method_21067(class_2561 message);

    /** error(message): the message as literal text */
    default void method_27453(String message) {
        method_21067(class_2561.method_43470(message));
    }
}
