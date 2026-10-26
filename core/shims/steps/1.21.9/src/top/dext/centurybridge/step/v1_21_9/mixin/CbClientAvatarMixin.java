package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_11901;
import net.minecraft.class_11902;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbAvatar;

/**
 * 1.21.8's Player cloak and bob fields (xCloak .. zCloakO, bob, oBob) for a client's player: 1.21.9 keeps them in the
 * player's ClientAvatarState (javap: moveCloak / bob updates moved there with the same arithmetic). Adds accessors
 * only; nothing of 1.21.9's runs differently.
 */
@Mixin(class_742.class)
public abstract class CbClientAvatarMixin implements CbAvatar.Cloak {

    private class_11902 cb$state() {
        return ((class_11901) this).method_74192();
    }

    @Override
    public double cb$cloak(int i) {
        class_11902 s = cb$state();
        return switch (i) {
            case 0 -> s.field_62571;
            case 1 -> s.field_62572;
            case 2 -> s.field_62573;
            case 3 -> s.field_62574;
            case 4 -> s.field_62575;
            default -> s.field_62576;
        };
    }

    @Override
    public void cb$cloak(int i, double v) {
        class_11902 s = cb$state();
        switch (i) {
            case 0 -> s.field_62571 = v;
            case 1 -> s.field_62572 = v;
            case 2 -> s.field_62573 = v;
            case 3 -> s.field_62574 = v;
            case 4 -> s.field_62575 = v;
            default -> s.field_62576 = v;
        }
    }

    @Override
    public float cb$bob(int i) {
        class_11902 s = cb$state();
        return i == 0 ? s.field_62569 : s.field_62570;
    }

    @Override
    public void cb$bob(int i, float v) {
        class_11902 s = cb$state();
        if (i == 0) {
            s.field_62569 = v;
        } else {
            s.field_62570 = v;
        }
    }
}
