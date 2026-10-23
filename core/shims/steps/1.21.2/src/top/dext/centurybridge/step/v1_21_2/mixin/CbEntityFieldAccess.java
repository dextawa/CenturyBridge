package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Entity's private 1.21.2 wasOnFire, 1.21.1's public field of the same meaning (CbEntityMisc). */
@Mixin(class_1297.class)
public interface CbEntityFieldAccess {

    @Accessor("field_54946")
    boolean cb$wasOnFire();

    @Accessor("field_54946")
    void cb$setWasOnFire(boolean value);
}
