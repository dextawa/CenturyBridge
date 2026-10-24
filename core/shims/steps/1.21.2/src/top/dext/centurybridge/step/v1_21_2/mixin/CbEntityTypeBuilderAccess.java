package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Optional;
import net.minecraft.class_10162;
import net.minecraft.class_1299;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EntityType.Builder's key functions, re-applied to the real key of a type built for 1.21.1's build(String) (CbEntityTypes). */
@Mixin(class_1299.class_1300.class)
public interface CbEntityTypeBuilderAccess {

    @Accessor("field_53973")
    class_10162<class_1299<?>, String> cb$descriptionIdFn();

    @Accessor("field_53972")
    class_10162<class_1299<?>, Optional<class_5321<class_52>>> cb$lootTableFn();
}
