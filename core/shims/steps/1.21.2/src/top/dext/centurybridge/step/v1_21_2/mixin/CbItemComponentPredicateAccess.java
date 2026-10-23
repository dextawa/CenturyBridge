package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import net.minecraft.class_9329;
import net.minecraft.class_9336;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** DataComponentPredicate's expected components, for the key of a 1.21.2 lock (CbItemLocks) */
@Mixin(class_9329.class)
public interface CbItemComponentPredicateAccess {

    @Accessor("field_49598")
    List<class_9336<?>> cb$expected();
}
