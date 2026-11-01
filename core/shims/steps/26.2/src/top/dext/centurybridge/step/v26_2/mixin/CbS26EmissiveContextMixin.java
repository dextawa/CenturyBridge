package top.dext.centurybridge.step.v26_2.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26Emissive;

/**
 * 26.2's LightCoordsUtil.getLightCoords(getter, level, state, pos) is 26.1.2's LevelRenderer.getLightCoords moved, but
 * for state.emissiveRendering(level, pos) -> emissiveRendering(): the level and position no longer reach the emissive
 * test. The wrap holds them for the call (rt/CbS26Emissive.enter/leave), so a 26.1.2 StatePredicate a converted mod handed
 * Properties.emissiveRendering is asked with the level and position 26.1.2 asked it with. 26.2 predicates ignore it;
 * the body runs unchanged, native injectors into it included. Wrap only; nothing is held until a 26.1.2 predicate was
 * adapted (rt/CbS26Emissive.adapted), so the hot path stays as 26.2 has it for every mod set without one.
 */
@Mixin(LightCoordsUtil.class)
public abstract class CbS26EmissiveContextMixin {
    @WrapMethod(method = "getLightCoords(Lnet/minecraft/util/LightCoordsUtil$BrightnessGetter;Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I")
    private static int cbS26$emissiveAskedHere(LightCoordsUtil.BrightnessGetter getter, BlockAndLightGetter level,
                                               BlockState state, BlockPos pos, Operation<Integer> original) {
        if (!CbS26Emissive.adapted()) {
            return original.call(getter, level, state, pos);
        }
        Object[] outer = CbS26Emissive.enter(level, pos);
        try {
            return original.call(getter, level, state, pos);
        } finally {
            CbS26Emissive.leave(outer);
        }
    }
}
