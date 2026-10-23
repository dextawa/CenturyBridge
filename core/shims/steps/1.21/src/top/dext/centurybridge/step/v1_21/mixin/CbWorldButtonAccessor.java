package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_2269;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ButtonBlock members 1.20.6's press body reaches that 1.21 keeps private. */
@Mixin(class_2269.class)
public interface CbWorldButtonAccessor {

    @Invoker("method_9713")
    void cb$updateNeighbours(class_2680 state, class_1937 level, class_2338 pos);

    @Accessor("field_40298")
    int cb$ticksToStayPressed();
}
