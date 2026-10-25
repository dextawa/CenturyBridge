package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1320;
import net.minecraft.class_1799;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;

/**
 * Mob.canReplaceCurrentItem(new, current, slot): 1.21.1 compared two armor pieces by ArmorItem.getDefense() and
 * getToughness(); 1.21.2 compares the stacks' apparent ARMOR / ARMOR_TOUGHNESS. Each value read asks the restored
 * member (CbItemArmorMixin) and takes it where it answers otherwise than its body -- a mod's mixin, or a 1.21.1 class's
 * override (CbItemArmor.mobArmorRead); otherwise 1.21.2's value stands. Other attributes (a sword's attack damage) pass
 * through.
 */
@Mixin(class_1308.class)
public abstract class CbItemMobArmorMixin {

    @Shadow
    private double method_63632(class_1799 stack, class_6880<class_1320> attribute, class_1304 slot) {
        throw new AssertionError();
    }

    @Redirect(method = "method_5955(Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;Lnet/minecraft/class_1304;)Z",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/class_1308;method_63632(Lnet/minecraft/class_1799;Lnet/minecraft/class_6880;Lnet/minecraft/class_1304;)D"))
    private double cb$armorValue(class_1308 self, class_1799 stack, class_6880<class_1320> attribute, class_1304 slot) {
        return CbItemArmor.mobArmorRead(stack, attribute, this.method_63632(stack, attribute, slot));
    }
}
