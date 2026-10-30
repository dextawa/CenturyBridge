package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_1799;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

/**
 * 1.21.4's Saddleable (net/minecraft/class_5146: isSaddleable, equipSaddle(stack, source), getSaddleSoundEvent
 * (default HORSE_SADDLE), isSaddled; javap -c -p 1.21.4). 1.21.5 dropped the interface: the saddle is the SADDLE
 * equipment slot and an item's Equippable component equips it (javap -p 1.21.5 class_10192, class_10205).
 *
 * classRenames: a converted mod's Saddleable is this interface. 1.21.4's vanilla Saddleables (Pig, Strider,
 * AbstractHorse) implement it again through CbSaddleableRestoredMixin / CbHorseSaddleableRestoredMixin, over their
 * 1.21.5 saddle slot, so a 1.21.4 "instanceof Saddleable" still holds for them. 1.21.4's two vanilla callers -- the
 * saddle item used on an entity and the dispenser's saddle behaviour -- reach a mod's own Saddleable through
 * CbSaddleEquipHooksMixin (rt/CbSaddles).
 */
public interface Saddleable {
    /** isSaddleable() */
    boolean method_6765();

    /** equipSaddle(stack, source) */
    void method_6576(class_1799 stack, class_3419 source);

    /** getSaddleSoundEvent(): 1.21.4's default, HORSE_SADDLE */
    default class_3414 method_45328() {
        return class_3417.field_14704.comp_349();
    }

    /** isSaddled() */
    boolean method_6725();
}
