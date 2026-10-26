package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1321;
import net.minecraft.class_9817;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's TamableAnimal.handleLeashAtDistance, restored with 1.21.5's body (javap): sitting, it drops the leash past
 * 10 blocks and answers false; otherwise its super call, which reached PathfinderMob's (Animal and AgeableMob declare
 * none) -- that body inlined (restrictTo(holder's block, 5), true): a restored mixin body cannot name the super method
 * 1.21.6's stub lacks. 1.21.6 has no sitting rule. Priority below every mod's.
 */
@Mixin(value = class_1321.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLeashTamableRestoredMixin {

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's tickLeash asks it for an entity whose class takes it from a converted mod's "
        + "override (whose super call lands here) or when a converted mod mixed it")
    public boolean method_60956(class_1297 holder, float distance) {
        if (((class_1321) (Object) this).method_6172()) {
            if (distance > 10.0F) {
                ((class_9817) this).method_5932();
            }
            return false;
        }
        ((class_1308) (Object) this).method_18408(holder.method_24515(), 5);
        return true;
    }
}
