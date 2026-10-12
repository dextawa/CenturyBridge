package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** CompositeLootItemCondition's protected terms (an array in 1.20.1), for the serializer facade and field reads. */
@Mixin(net.minecraft.class_186.class)
public interface CbLootCompositeConditionTerms {

    @Accessor("field_1246")
    List<net.minecraft.class_5341> cb$terms();
}
