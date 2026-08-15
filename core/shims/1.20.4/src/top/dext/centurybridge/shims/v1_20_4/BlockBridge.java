package top.dext.centurybridge.shims.v1_20_4;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4538;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.3 changed Block.onBreak to return the state. Two directions repaired:
 * - legacy CALL SITES: the old void overload is re-added (empty body -- the
 *   TAIL hook below routes the real behavior);
 * - legacy OVERRIDES in mod block subclasses: they override the re-added void
 *   method, and the new-shape method invokes it virtually at TAIL, so mod
 *   break logic fires again. The empty base body prevents recursion.
 * getPickStack narrowed its world parameter; the old overload downcasts
 * (runtime worlds are WorldViews in practice).
 */
@Mixin(class_2248.class)
public abstract class BlockBridge {

    /** legacy onBreak shape; intentionally empty at the base (see class doc) */
    public void method_9576(class_1937 world, class_2338 pos, class_2680 state, class_1657 player) {
    }

    @Inject(method = "method_9576", at = @At("TAIL"))
    private void cb$fireLegacyOnBreak(class_1937 world, class_2338 pos, class_2680 state,
                                      class_1657 player, CallbackInfoReturnable<class_2680> cir) {
        this.method_9576(world, pos, state, player);
    }

    /** legacy getPickStack shape (world param narrowed at 1.20.3) */
    public class_1799 method_9574(class_1922 world, class_2338 pos, class_2680 state) {
        return ((class_2248) (Object) this).method_9574((class_4538) world, pos, state);
    }
}
