package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1308;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * PathNavigation.isClearForMovementBetween (protected static): the second half of 1.20.1's
 * AmphibiousPathNavigation.canMoveDirectly (CbNavigationLegacy).
 */
@Mixin(net.minecraft.class_1408.class)
public interface CbPathNavigationInvoker {

    @Invoker("method_43394")
    static boolean cb$isClearForMovementBetween(class_1308 mob, class_243 from, class_243 to, boolean nearlyOnly) {
        throw new AssertionError();
    }
}
