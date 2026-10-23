package cb.mixintest.mixin;

import cb.mixintest.T;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.class_2248.class)
public abstract class BlockMixin {
    /** getCloneItemStack(BlockGetter, ...): 1.20.3 takes a LevelReader, a BlockGetter (step 2 override hook) */
    @Inject(method = "getCloneItemStack", at = @At("HEAD"))
    private void cbtest$onClone(net.minecraft.class_1922 level, net.minecraft.class_2338 pos, net.minecraft.class_2680 state,
                                CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        T.ran("inject-subtype-hook", "pos=" + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260()
            + " level=" + level.getClass().getSimpleName());
    }
}
