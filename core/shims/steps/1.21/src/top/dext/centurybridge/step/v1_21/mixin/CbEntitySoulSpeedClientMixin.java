package top.dext.centurybridge.step.v1_21.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbEntitySoulSpeed;

/**
 * LocalPlayer: marks the class whose 1.20.6 canSpawnSoulSpeedParticle() also refused while flying, so a 1.20.6
 * caller's call on the local player answers as it did (CbEntitySoulSpeed.canSpawnSoulSpeedParticle).
 */
@Environment(EnvType.CLIENT)
@Mixin(class_746.class)
public abstract class CbEntitySoulSpeedClientMixin implements CbEntitySoulSpeed.LocalPlayerParticles {
}
