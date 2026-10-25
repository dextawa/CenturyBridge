package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1759;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1812;
import net.minecraft.class_1819;
import net.minecraft.class_1833;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * 1.21.2's getName(stack) overrides of CompassItem, PotionItem, TippedArrowItem and ShieldItem name the stack from
 * the restored 1.21.1 getDescriptionId(stack) (Cb{Compass,Potion,TippedArrow,Shield}DescriptionIdMixin) where it
 * answers otherwise than 1.21.1's body -- a mod's mixin on it -- or where a 1.21.1 subclass overrides it
 * (CbItemMembers.nameRead); otherwise 1.21.2's name stands.
 */
@Mixin({class_1759.class, class_1812.class, class_1833.class, class_1819.class})
public abstract class CbItemNameReadsMixin {

    @Inject(method = "method_7864", at = @At("HEAD"), cancellable = true)
    private void cb$name(class_1799 stack, CallbackInfoReturnable<class_2561> cir) {
        class_2561 name = CbItemMembers.nameRead((class_1792) (Object) this, stack);
        if (name != null) {
            cir.setReturnValue(name);
        }
    }
}
