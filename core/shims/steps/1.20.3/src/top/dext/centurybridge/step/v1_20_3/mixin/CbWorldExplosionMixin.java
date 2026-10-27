package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1927;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldExplosionAccess;

/**
 * 1.20.2's Explosion.getDamageSource() (gone in 1.20.3): the damage source the explosion hurts entities
 * with. It comes back with its 1.20.2 body, and explode() asks it, as 1.20.2's did (CbWorldExplosionMixinHooks,
 * default priority), so a 1.20.2 subclass overriding it decides the damage source again (1.20.3 reads the field
 * directly).
 */
@Mixin(value = class_1927.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldExplosionMixin implements CbWorldExplosionAccess {

    @Shadow
    @Final
    private class_1282 field_9193;

    public class_1282 method_8349() {
        return this.field_9193;
    }

    @Override
    public class_1282 cb$getDamageSource() {
        return this.method_8349();
    }
}
