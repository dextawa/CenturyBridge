package top.dext.centurybridge.step.v1_21_6.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbPrioritySplit;

/**
 * The injector into 1.21.6's own CubeMap constructor that goes with {@link CbCubeMapFacesMixin} (the given-back field
 * stays there, at {@code CbVanillaReads.PRIORITY}): 1.21.5's six face ids, base.withPath(base.getPath() + "_" + i + ".png")
 * for i 0..5, from the constructor's argument, set at its return.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(net.minecraft.class_751.class)
public abstract class CbCubeMapFacesMixinHooks {

    @Inject(method = "<init>(Lnet/minecraft/class_2960;)V", at = @At("RETURN"))
    private void cb$faces(net.minecraft.class_2960 base, CallbackInfo ci) {
        net.minecraft.class_2960[] faces = new net.minecraft.class_2960[6];
        for (int i = 0; i < 6; i++) {
            faces[i] = base.method_45136(base.method_12832() + "_" + i + ".png");
        }
        ((CbPrioritySplit.CubeMapFaces) (Object) this).cb$v1_21_6$setFaces(List.of(faces));
    }
}
