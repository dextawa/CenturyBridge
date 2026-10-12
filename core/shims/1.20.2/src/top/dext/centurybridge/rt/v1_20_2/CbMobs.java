package top.dext.centurybridge.rt.v1_20_2;

import net.minecraft.class_4140;
import net.minecraft.class_4141;
import net.minecraft.class_7298;
import net.minecraft.class_7689;
import net.minecraft.class_8153;

/**
 * 1.20.1's per-class isPanicking() of Allay, Camel and Sniffer (s1misc), merged in 1.20.2 into
 * PathfinderMob.isPanicking() (method_52546), which reads the same IS_PANICKING brain memory for
 * these brain-driven mobs. A call answers through 1.20.2's method -- so a converted subclass's
 * override, which an override hook forwards that method to, still decides; a super call from such
 * an override runs 1.20.1's own body.
 */
public final class CbMobs {

    /** Allay.isPanicking() */
    public static boolean method_45342(class_7298 allay) {
        return allay.method_52546();
    }

    /** super.isPanicking() from a converted Allay subclass: 1.20.1's body */
    public static boolean super_method_45342(class_7298 allay) {
        return allay.method_18868().method_18904(class_4140.field_39408).isPresent();
    }

    /** Camel.isPanicking() */
    public static boolean method_45348(class_7689 camel) {
        return camel.method_52546();
    }

    /** super.isPanicking() from a converted Camel subclass: 1.20.1's body */
    public static boolean super_method_45348(class_7689 camel) {
        return camel.method_18868().method_18876(class_4140.field_39408, class_4141.field_18456);
    }

    /** Sniffer.isPanicking() */
    public static boolean method_49147(class_8153 sniffer) {
        return sniffer.method_52546();
    }

    /** super.isPanicking() from a converted Sniffer subclass: 1.20.1's body */
    public static boolean super_method_49147(class_8153 sniffer) {
        return sniffer.method_18868().method_18904(class_4140.field_39408).isPresent();
    }

    private CbMobs() {
    }
}
