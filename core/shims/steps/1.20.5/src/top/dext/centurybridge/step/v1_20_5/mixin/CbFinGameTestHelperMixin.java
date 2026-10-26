package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_4516;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinGameTests;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * GameTestHelper: 1.20.4's makeMockSurvivalPlayer(), removed in 1.20.5 (whose makeMockPlayer takes the game
 * type). 1.20.4's body: a player in the helper's level, neither spectator nor creative (rt/CbFinGameTests).
 */
@Mixin(value = class_4516.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFinGameTestHelperMixin {

    /** was makeMockSurvivalPlayer() */
    public class_1657 method_46228() {
        return CbFinGameTests.mockSurvivalPlayer(((class_4516) (Object) this).method_35943());
    }
}
