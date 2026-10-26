package top.dext.centurybridge.step.v1_21_6.mixin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.minecraft.class_1297;
import net.minecraft.class_1321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_6.rt.CbLeash;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's TamableAnimal.handleLeashAtDistance, restored with 1.21.5's body (javap): sitting, it drops the leash past
 * 10 blocks and answers false; otherwise its super call (invokespecial Animal.handleLeashAtDistance, which reached
 * PathfinderMob's: Animal and AgeableMob declare none). That super call runs PathfinderMob's restored member
 * (CbLeashPathfinderRestoredMixin) with invokespecial semantics, so a converted mod's mixin on PathfinderMob's member
 * runs on this path as in 1.21.5. 1.21.6's stub has no such member to name in a super call, and an @Invoker dispatches
 * virtually (back into this override), so the call goes through a MethodHandles.findSpecial handle looked up with this
 * class's own Lookup (the merged method's caller is TamableAnimal; CbLeash.tamableSuper). The other calls keep 1.21.5's
 * owners, so a 1.21.5 mixin's anchors on them match. 1.21.6 has no sitting rule. Priority below every mod's.
 */
@Mixin(value = class_1321.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLeashTamableRestoredMixin {

    /** PathfinderMob's restored handleLeashAtDistance, called as TamableAnimal's super call (invokespecial) */
    @Unique
    private static MethodHandle cb$superLeash;

    @CbVanillaReads("CbLeashRouteMixin: 1.21.6's tickLeash asks it for an entity whose class takes it from a converted mod's "
        + "override (whose super call lands here) or when a converted mod mixed it or PathfinderMob's member its super call reaches")
    public boolean method_60956(class_1297 holder, float distance) {
        class_1321 self = (class_1321) (Object) this;
        if (self.method_6172()) {
            if (distance > 10.0F) {
                self.method_5932();
            }
            return false;
        }
        MethodHandle sup = cb$superLeash;
        if (sup == null) {
            cb$superLeash = sup = CbLeash.tamableSuper(MethodHandles.lookup());
        }
        return CbLeash.callTamableSuper(sup, self, holder, distance);
    }
}
