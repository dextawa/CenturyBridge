package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.facade.v1_20_2.class_7997;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyMeleeReference;

/**
 * 1.20.1's LivingEntity.getMeleeAttackReferencePosition back (s1misc melee bridge), 1.20.1 body:
 * the entity's position, raised by its mount's shielding height when it rides a
 * RiderShieldingMount (the camel, facade class_7997). Declared public: a call from Mob's
 * restored perceived-distance method reaches it through the trampoline, which calls it virtually
 * so a converted subclass's (protected) override is selected.
 */
@Mixin(value = class_1309.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbLivingMeleeReference implements CbLegacyMeleeReference {

    @Override
    public class_243 method_47921() {
        class_1309 self = (class_1309) (Object) this;
        class_1297 vehicle = self.method_5854();
        if (vehicle instanceof class_7997 mount) {
            return self.method_19538().method_1031(0.0, mount.method_47923(), 0.0);
        }
        return self.method_19538();
    }

    @Override
    public class_243 cb$meleeReferencePosition() {
        return this.method_47921();
    }
}
