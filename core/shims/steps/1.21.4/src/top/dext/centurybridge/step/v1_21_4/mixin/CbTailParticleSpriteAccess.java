package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderTail;

/** TextureSheetParticle.setSprite (protected): a 1.21.3 BreakingItemParticle's sprite, chosen as 1.21.3 chose it */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4003.class)
public interface CbTailParticleSpriteAccess extends CbRenderTail.CbSpriteParticle {
    @Override
    @Invoker("method_18141")
    void cb$setSprite(net.minecraft.class_1058 sprite);
}
