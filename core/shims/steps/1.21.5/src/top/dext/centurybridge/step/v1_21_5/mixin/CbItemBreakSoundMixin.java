package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_3414;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemSounds;

/**
 * LivingEntity.breakItem(stack) plays the sound 1.21.4's Item.getBreakingSound answers (restored,
 * CbItemMembersRestoredMixin) for an item whose class overrides it (CbItemSounds.legacy): 1.21.4's body -- the sound
 * unless the entity is silent, then the item particles. Every other item takes 1.21.5's body (its BREAK_SOUND).
 */
@Mixin(class_1309.class)
public abstract class CbItemBreakSoundMixin {

    @Inject(method = "method_6045(Lnet/minecraft/class_1799;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyBreakingSound(class_1799 stack, CallbackInfo ci) {
        if (stack.method_7960() || !CbItemSounds.legacy(stack)) {
            return;
        }
        class_1309 self = (class_1309) (Object) this;
        class_3414 sound = CbItemSounds.legacySound(stack);
        if (!((class_1297) self).method_5701()) {
            self.method_37908().method_8486(self.method_23317(), self.method_23318(), self.method_23321(), sound, self.method_5634(),
                0.8F, 0.8F + self.method_37908().field_9229.method_43057() * 0.4F, false);
        }
        self.method_6037(stack, 5);
        ci.cancel();
    }
}
