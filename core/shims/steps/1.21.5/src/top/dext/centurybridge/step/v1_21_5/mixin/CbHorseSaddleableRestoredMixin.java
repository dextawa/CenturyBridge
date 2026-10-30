package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1496;
import net.minecraft.class_1799;
import net.minecraft.class_3419;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * AbstractHorse as a 1.21.4 Saddleable (facade/Saddleable) over its 1.21.5 SADDLE slot. 1.21.4's bodies (javap -c -p
 * 1.21.4 class_1496): isSaddleable = alive, not a baby and tamed, which is 1.21.5's canUseSlot(SADDLE) (javap -c 1.21.5
 * method_56991; Llama's own canUseSlot answers for llamas, as its 1.21.4 isSaddleable did); isSaddled = the saddled flag,
 * 1.21.5's Mob.isSaddled = hasItemInSlot(SADDLE); equipSaddle(stack, source) = the saddle into the horse inventory's
 * saddle slot (its listener played the saddle sound), 1.21.5 puts it in the SADDLE slot, which plays the equip sound.
 */
@Mixin(value = class_1496.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbHorseSaddleableRestoredMixin implements Saddleable {

    @Override
    @CbVanillaReads("CbSaddleHooksMixin: canUseSlot(SADDLE) answers a converted subclass's override")
    public boolean method_6765() {
        class_1496 self = (class_1496) (Object) this;
        return self.method_5805() && !self.method_6109() && self.method_6727();
    }

    @Override
    @CbVanillaReads("CbSaddleEquipHooksMixin: the saddle's Equippable and the dispenser call a converted subclass's override")
    public void method_6576(class_1799 stack, class_3419 source) {
        ((class_1309) (Object) this).method_5673(class_1304.field_55946, stack);
    }

    @Override
    @CbVanillaReads("CbSaddleHooksMixin: Mob.isSaddled answers a converted subclass's override")
    public boolean method_6725() {
        return ((class_1309) (Object) this).method_6084(class_1304.field_55946);
    }
}
