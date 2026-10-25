package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26Beds;

/**
 * 26.1.2's BlockEntityType.BED registered where 26.1.2 registered its block entity types: at the end of 26.2's
 * BlockEntityTypes initializer (the built-in registry's bootstrap loads it), see rt/CbS26Beds.
 */
@Mixin(BlockEntityTypes.class)
public abstract class CbS26BedTypeMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cbS26$bed(CallbackInfo ci) {
        CbS26Beds.register();
    }
}
