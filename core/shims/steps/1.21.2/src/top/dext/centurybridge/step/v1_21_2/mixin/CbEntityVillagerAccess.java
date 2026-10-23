package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1646;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Villager's chasing flag, private in 1.21.2 (CbEntityTail). */
@Mixin(class_1646.class)
public interface CbEntityVillagerAccess {

    @Accessor("field_30612")
    boolean cb$chasing();

    @Accessor("field_30612")
    void cb$setChasing(boolean chasing);
}
