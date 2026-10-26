package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's AbstractClientPlayer.elytraRotX / elytraRotY / elytraRotZ (field_3900, field_3899, field_3898), which 1.21.9
 * dropped: public floats that no 1.21.8 game code read or wrote (javap of every 1.21.8 class: no access outside the
 * declaration; the elytra's angles came from the render state), so they hold what a mod stores in them and change
 * nothing else, as on 1.21.8.
 */
@Mixin(value = class_742.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbElytraRotationMixin {

    public float field_3900;

    public float field_3899;

    public float field_3898;
}
