package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4538;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.facade.fapi.BlockPickInteractionAware;
import top.dext.centurybridge.step.v1_21_4.facade.fapi.EntityPickInteractionAware;

/**
 * Fabric API 1.21.3's pick interaction contract on 1.21.4's server-side pick: a block or entity implementing
 * BlockPickInteractionAware / EntityPickInteractionAware gives its own stack when it answers a non-empty one,
 * otherwise vanilla's pick result stands (1.21.3's ClientPickBlockGatherCallback router did exactly this on the
 * client). The server has no hit vector; the block hit is its center facing up, the entity hit the entity.
 */
@Mixin(class_3244.class)
public abstract class CbRestFapiPickMixin {

    @Shadow
    public class_3222 field_14140;

    @Redirect(method = "method_65085", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2680;method_65171(Lnet/minecraft/class_4538;Lnet/minecraft/class_2338;Z)Lnet/minecraft/class_1799;"))
    private class_1799 cb$blockPick(class_2680 state, class_4538 level, class_2338 pos, boolean includeData) {
        if (state.method_26204() instanceof BlockPickInteractionAware aware) {
            class_1799 stack = aware.getPickedStack(state, level, pos, this.field_14140,
                new class_3965(class_243.method_24953(pos), class_2350.field_11036, pos, false));
            if (stack != null && !stack.method_7960()) {
                return stack;
            }
        }
        return state.method_65171(level, pos, includeData);
    }

    @Redirect(method = "method_12084(Lnet/minecraft/class_10371;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1297;method_31480()Lnet/minecraft/class_1799;"))
    private class_1799 cb$entityPick(class_1297 entity) {
        if (entity instanceof EntityPickInteractionAware aware) {
            class_1799 stack = aware.getPickedStack(this.field_14140, new class_3966(entity));
            if (stack != null && !stack.method_7960()) {
                return stack;
            }
        }
        return entity.method_31480();
    }
}
