package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_4.rt.CbTextureMetadata;

/**
 * 1.21.3's SpriteContents built a sprite's animation from AnimationMetadataSection.forEachFrame (virtually); 1.21.4 reads the
 * frames component. A 1.21.3 section overriding forEachFrame is built from what its forEachFrame answers again
 * (see {@link CbTextureMetadata}).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_7764.class)
public abstract class CbTextureAnimationMixin {

    @ModifyVariable(method = "method_45812", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_1079 cb$legacyFrames(net.minecraft.class_1079 metadata) {
        return CbTextureMetadata.framesOf(metadata);
    }
}
