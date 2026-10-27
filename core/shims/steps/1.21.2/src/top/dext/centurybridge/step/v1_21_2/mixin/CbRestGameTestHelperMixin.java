package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_4513;
import net.minecraft.class_4516;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * GameTestHelper.assertEntityPresent / assertEntityNotPresent(type, from, to) of 1.21.1, back with their
 * bodies: the living entities of the type in the box between the two positions taken as they are, in
 * level coordinates (1.21.2's replacements take one box relative to the test and make it absolute first),
 * the failure placed at the two positions' block positions at the test's current tick, with 1.21.1's
 * messages.
 */
@Mixin(value = class_4516.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestGameTestHelperMixin {

    public void method_46224(class_1299<?> type, class_243 from, class_243 to) {
        class_4516 self = (class_4516) (Object) this;
        if (self.method_35943().method_18023(type, new class_238(from, to), class_1297::method_5805).isEmpty()) {
            throw new class_4513("Expected " + type.method_35050() + " between ", class_2338.method_49638(from),
                class_2338.method_49638(to), self.method_36045());
        }
    }

    public void method_59872(class_1299<?> type, class_243 from, class_243 to) {
        class_4516 self = (class_4516) (Object) this;
        if (!self.method_35943().method_18023(type, new class_238(from, to), class_1297::method_5805).isEmpty()) {
            throw new class_4513("Did not expect " + type.method_35050() + " between ", class_2338.method_49638(from),
                class_2338.method_49638(to), self.method_36045());
        }
    }
}
