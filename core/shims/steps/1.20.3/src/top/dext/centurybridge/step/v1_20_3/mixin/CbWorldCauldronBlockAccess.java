package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_5546;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** CauldronBlock.shouldHandlePrecipitation (protected static), for the 1.20.2 cauldron fill (CbWorldLayeredCauldronMixin) */
@Mixin(class_5546.class)
public interface CbWorldCauldronBlockAccess {

    @Invoker("method_31636")
    static boolean cb$shouldHandlePrecipitation(class_1937 level, class_1959.class_1963 precipitation) {
        throw new AssertionError("mixin invoker");
    }
}
