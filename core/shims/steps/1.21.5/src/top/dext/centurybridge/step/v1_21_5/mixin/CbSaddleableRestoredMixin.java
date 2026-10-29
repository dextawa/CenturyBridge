package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1452;
import net.minecraft.class_1799;
import net.minecraft.class_3419;
import net.minecraft.class_4985;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.facade.Saddleable;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * Pig and Strider as 1.21.4 Saddleables (facade/Saddleable) over their 1.21.5 SADDLE slot. 1.21.4's bodies (javap -c -p
 * 1.21.4 class_1452 / class_4985): isSaddleable = alive and not a baby, which is 1.21.5's canUseSlot(SADDLE) for both
 * (javap -c 1.21.5 method_56991); isSaddled = the steering's saddle flag, 1.21.5's Mob.isSaddled = hasItemInSlot(SADDLE);
 * equipSaddle(stack, source) = set the flag and play PIG_SADDLE / STRIDER_SADDLE at the source, 1.21.5 puts the stack in
 * the SADDLE slot, which plays the entity's saddle equip sound (its own sound source).
 */
@Mixin(value = {class_1452.class, class_4985.class}, priority = CbVanillaReads.PRIORITY)
public abstract class CbSaddleableRestoredMixin implements Saddleable {

    @Override
    @CbVanillaReads("CbSaddleHooksMixin: canUseSlot(SADDLE) answers a converted subclass's override")
    public boolean method_6765() {
        class_1309 self = (class_1309) (Object) this;
        return self.method_5805() && !self.method_6109();
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
