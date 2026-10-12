package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ItemEntity's private thrower id and cached thrower, for 1.20.2's setThrower(UUID) (CbWorldEntities) */
@Mixin(class_1542.class)
public interface CbWorldItemEntityAccess {

    @Accessor("field_7200")
    void cb$setThrowerId(UUID thrower);

    @Accessor("field_46653")
    void cb$setCachedThrower(class_1297 thrower);
}
