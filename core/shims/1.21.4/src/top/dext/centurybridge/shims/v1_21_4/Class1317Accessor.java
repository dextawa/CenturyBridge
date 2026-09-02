package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1317;
import net.minecraft.class_2902;
import net.minecraft.class_9168;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * SpawnRestriction.register went private in 1.20.5 (fapi reaches it through its own accessor);
 * the 1.20.1 corpus called it directly with the Location enum (see Statics.method_20637 and
 * the class_1317_class_1319 facade).
 */
@Mixin(class_1317.class)
public interface Class1317Accessor {

    @Invoker("method_20637")
    static <T extends class_1308> void cbRegister(class_1299<T> type, class_9168 location,
            class_2902.class_2903 heightmap, class_1317.class_4306<T> predicate) {
        throw new AssertionError("mixin invoker not applied");
    }
}
